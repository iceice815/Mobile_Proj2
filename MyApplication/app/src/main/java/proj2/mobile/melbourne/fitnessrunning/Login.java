package proj2.mobile.melbourne.fitnessrunning;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;

public class Login extends AppCompatActivity {
    private Button register;
    private Button Login;
    private EditText UserName;
    private EditText PassWord;

    private MobileServiceClient mClient;
    private MobileServiceTable<UserInfo> mToDoTable;

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

//            mToDoTable = mClient.getTable(UserInfo.class);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

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
                LoginJudgement();
            }
        });

    }
//获取两个edit的内容，然后获取table的内容进行比对，如果是true就跳转
    public void LoginJudgement(){



    }
}
