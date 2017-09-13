package proj2.mobile.melbourne.fitnessrunning;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.*;

public class Login extends AppCompatActivity {
    private Button mRegister;
    private Button mLogin;
    private EditText mUsername;
    private EditText mPassword;
    private ProgressBar mProgressBar;

    private MobileServiceClient mClient;
    private MobileServiceTable<UserInfo> mUserInfoTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLogin = (Button) findViewById(R.id.LogInButton);

        mRegister = (Button) findViewById(R.id.RegisterButton);

        mUsername = (EditText) findViewById(R.id.UserName);

        mPassword = (EditText) findViewById(R.id.Password);

        mProgressBar = (ProgressBar) findViewById(R.id.LoginProgressBar);

        try {
            //get a client of the referred database
            mClient = new MobileServiceClient("https://fitnessrunning.azurewebsites.net", this);
            //get the table of the database
            mUserInfoTable = mClient.getTable(UserInfo.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the user click register button, then transfer to the register interface
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);

            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //judge if the username and password are correct
                    login_judgement();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void login_judgement() throws ExecutionException, InterruptedException {
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        final String username = mUsername.getText().toString();
        final String password = mPassword.getText().toString();

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //get all items which has a same username with the user input value
                    final List<UserInfo> results = get_items_from_table(username);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(UserInfo info :results)
                            {
                                if (info.getmPassword().equals(password)) {
                                    //if the password is correct, then transfer to the operation interface
                                    Intent intent1 = new Intent(Login.this, Operation.class);
                                    startActivity(intent1);
                                    finish();
                                } else {
                                    mUsername.setText("");
                                    mPassword.setText("");
                                }
                            }
                        }
                    });

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();


    }

    private List<UserInfo> get_items_from_table(String Username) throws ExecutionException, InterruptedException {
        //return a item list which has the same username with the user input
        return mUserInfoTable.where().field("username").
                eq(val(Username)).execute().get();
    }


}
