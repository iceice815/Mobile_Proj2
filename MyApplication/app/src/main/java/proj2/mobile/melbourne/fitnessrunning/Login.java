package proj2.mobile.melbourne.fitnessrunning;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.*;

public class Login extends AppCompatActivity {
    private Button register;
    private Button login;
    private EditText username;
    private EditText password;

    private MobileServiceClient mClient;
    private MobileServiceTable<UserInfo> mUserInfoTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.LogInButton);

        register = (Button) findViewById(R.id.RegisterButton);

        username = (EditText) findViewById(R.id.UserName);

        password = (EditText) findViewById(R.id.Password);

        try {
            //get a client of the referred database
            mClient = new MobileServiceClient("https://fitnessrunning.azurewebsites.net", this);
            //get the table of the database
            mUserInfoTable = mClient.getTable(UserInfo.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the user click register button, then transfer to the register interface
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
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

        final String Username = username.getText().toString();
        final String Password = password.getText().toString();

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //get all items which has a same username with the user input value
                    final List<UserInfo> results = get_items_from_table(Username);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(UserInfo info :results)
                            {
                                if (info.getmPassword().equals(Password)) {
                                    //if the password is correct, then transfer to the operation interface
                                    Intent intent1 = new Intent(Login.this, RunningTrack.class);
                                    intent1.putExtra("username",Username);
                                    startActivity(intent1);


                                } else {
                                    username.setText("");
                                    password.setText("");
                                }
                            }
                            finish();
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
