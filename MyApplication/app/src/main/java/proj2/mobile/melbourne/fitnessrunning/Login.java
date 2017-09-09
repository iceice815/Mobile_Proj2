package proj2.mobile.melbourne.fitnessrunning;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    private Button Login;
    private EditText UserName;
    private EditText PassWord;

    private MobileServiceClient mClient;
    private MobileServiceTable<UserInfo> mUserInfoTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Login = (Button) findViewById(R.id.LogInButton);

        register = (Button) findViewById(R.id.RegisterButton);

        UserName = (EditText) findViewById(R.id.UserName);

        PassWord = (EditText) findViewById(R.id.Password);

        try {
            mClient = new MobileServiceClient("https://fitnessrunning.azurewebsites.net", this);

            mUserInfoTable = mClient.getTable(UserInfo.class);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

//        mToDoTable = mClient.getTable(UserInfo.class);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LoginJudgement();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }
//获取两个edit的内容，然后获取table的内容进行比对，如果是true就跳转
    public void LoginJudgement() throws ExecutionException, InterruptedException {

        final String Username = UserName.getText().toString();
        final String Password = PassWord.getText().toString();

        new AsyncTask<Void, Void, Void>(){


            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<UserInfo> results = refreshItemsFromMobileServiceTable(Username);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(UserInfo info :results)
                            {

                                if (info.getmPassword().equals(Password)) {
                                    Intent intent1 = new Intent(Login.this, Operation.class);
                                    startActivity(intent1);
                                } else {
//                                    Intent intent1 = new Intent(Login.this, Operation.class);
//                                    startActivity(intent1);
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

    private List<UserInfo> refreshItemsFromMobileServiceTable(String Username) throws ExecutionException, InterruptedException {

        return mUserInfoTable.where().field("username").
                eq(val(Username)).execute().get();            //将所有field值为complete的记录返回出来
    }


}
