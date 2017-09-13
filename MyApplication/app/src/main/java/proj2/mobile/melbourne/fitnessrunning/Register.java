package proj2.mobile.melbourne.fitnessrunning;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;
/**
 * Created by iceice on 9/9/17.
 */
public class Register extends AppCompatActivity {

    private MobileServiceClient mClient;

    private MobileServiceTable<UserInfo> mUserTable;

    private Button sign_up;
    private Button sign_up_cancel;
    private EditText mUsername;
    private EditText mPassword;
    private EditText mConfirm_password;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sign_up = (Button)findViewById(R.id.Sign_up);
        sign_up_cancel = (Button)findViewById(R.id.Sing_up_cancel);
        mUsername = (EditText)findViewById(R.id.Username);
        mPassword = (EditText)findViewById(R.id.Password);
        mConfirm_password = (EditText)findViewById(R.id.Confirm_password);


        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://fitnessrunning.azurewebsites.net",
                    this);

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });

            // Get the Mobile Service Table instance to use

            mUserTable = mClient.getTable(UserInfo.class);
        }catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            createAndShowDialog(e, "Error");
        }
        //sign_up button event
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_user();
            }
        });

        //sign_up_cancel event
        sign_up_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backto_login = new Intent(getApplicationContext(), Login.class);
                startActivity(backto_login);
            }
        });


    }

    /**
     * add user to userinfo table
     */
    private void add_user(){
        mProgressBar = (ProgressBar)findViewById(R.id.ProgressBar);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        if(mClient == null){
            return;
        }
        final UserInfo userInfo = new UserInfo();
        userInfo.setmUsername(mUsername.getText().toString());
        userInfo.setmPassword(mPassword.getText().toString());
        if(mConfirm_password.getText().toString().equals(mPassword.getText().toString())) {
            Log.d("Debug","AsyncJob");

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        mUserTable.insert(userInfo).get();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent backto_login = new Intent(getApplicationContext(), Login.class);
                                startActivity(backto_login);
                            }
                        });
                    } catch (Exception e) {
                        createAndShowDialog(e, "Error");
                    }
                    return null;
                }
            }.execute();
        }else{
            mUsername.setText("");
            mPassword.setText("");
            mConfirm_password.setText("");
            Toast toast =Toast.makeText(Register.this, "Your username and password are not correct!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
            toast.show();
        }
    }

    /*********************************************************************************
     ********************************you can ignore it********************************
     * *******************************************************************************
     */

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }


}
