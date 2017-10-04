package proj2.mobile.melbourne.elderfitness;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import proj2.mobile.melbourne.elderfitness.dto.UserInfo;
import proj2.mobile.melbourne.elderfitness.util.InitializeTable;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

public class Register extends AppCompatActivity implements InitializeTable {

    private MobileServiceClient mClient;
    private MobileServiceTable<UserInfo> mUserTable;

    private Button sign_up;
    private Button sign_up_cancel;

    private EditText mUsername;
    private EditText mPassword;
    private EditText mConfirm_password;
    private EditText mEmergency_Contact;

    private ProgressBar mProgressBar;
    private ProgressDialog mProgressDialog;
    private AlertDialog.Builder mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Button initialization
        sign_up = (Button)findViewById(R.id.Sign_up);
        sign_up_cancel = (Button)findViewById(R.id.Sing_up_cancel);
        // EditText initialization
        mUsername = (EditText)findViewById(R.id.Username);
        mPassword = (EditText)findViewById(R.id.Password);
        mEmergency_Contact =(EditText)findViewById(R.id.Emergency_Contact_Number);

        mConfirm_password = (EditText)findViewById(R.id.Confirm_password);
        // Dialog initialization
        mProgressDialog = initial_ProgressDialog();
        mAlertDialog = initial_AlertDialog();

       init_table();

        //sign_up button event
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                sign_up_action();
            }
        });

        //sign_up_cancel event
        sign_up_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back_to_login = new Intent(getApplicationContext(), Login.class);
                startActivity(back_to_login);
                finish();
            }
        });
    }

    /**
     * verify existent user code here:
     */
    private void sign_up_action(){

        final String input_user = mUsername.getText().toString();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final List<UserInfo> all_users = mUserTable.execute().get();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            boolean exist = false;
                            for (UserInfo user : all_users){
                                // logic if Multiple Registration:
                                if (user.getmUsername().equals(input_user)){
                                    mProgressDialog.dismiss();
                                    mAlertDialog.show();
                                    mUsername.setText("");
                                    mPassword.setText("");
                                    mConfirm_password.setText("");
                                    mEmergency_Contact.setText("");
                                    exist = true;
                                    break;
                                }
                            }
                            // logic if entire new user:
                            if (!exist){
                                // Add a toast to show register successfully
                                Toast toast =Toast.makeText(Register.this,
                                        "Registration completed!",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                                add_user();
                            }
                        }
                    });
                } catch (Exception e) {
                    createAndShowDialog(e, "Error");
                }
                return null;
            }
        }.execute();
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
        userInfo.setmEmergencyNumber(mEmergency_Contact.getText().toString());
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
                                // Add a toast to show register successfully
                                Intent back_to_login =
                                        new Intent(getApplicationContext(), Login.class);
                                startActivity(back_to_login);
                            }
                        });
                    } catch (Exception e) {
                        createAndShowDialog(e, "Error");
                    }
                    return null;
                }
            }.execute();
        }
        else{
            mUsername.setText("");
            mPassword.setText("");
            mConfirm_password.setText("");
            mEmergency_Contact.setText("");
            Toast toast =Toast.makeText(Register.this,
                    "Your username and password are not correct!",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
            toast.show();
        }
        finish();
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

    /**
     * Creates a ProgressDialog and return it
     */
    private ProgressDialog initial_ProgressDialog(){

        ProgressDialog progressDialog = new ProgressDialog(Register.this);

        progressDialog.setTitle("Registering your account");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        return progressDialog;
    }

    /**
     * Creates a AlertDialog.Builder and return it
     */
    private AlertDialog.Builder initial_AlertDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(Register.this);

        dialog.setTitle("Multiple Registration");
        dialog.setMessage("The username is existed, please choose another one!");
        dialog.setCancelable(false);

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    /**
     * initilize table
     */
    @Override
    public void init_table() {
        try {
            // Create the Mobile Service Client instance,
            // using the provided Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://elderfitness.azurewebsites.net",
                    this);

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    mProgressDialog.dismiss();       return client;
                }
            });

            // Get the Mobile Service Table instance to use
            mUserTable = mClient.getTable(UserInfo.class);

        }catch (MalformedURLException e) {
            createAndShowDialog(new Exception
                    ("Error in creating Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            createAndShowDialog(e, "Error");
        }
    }
}
