package com.example.login;

import com.example.clas.Student;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends Activity {
	private EditText username;
	private EditText password1;
	private RadioButton man,woman;
	private Button submit;
	private ImageView imgchoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username=(EditText) this.findViewById(R.id.username);
        password1=(EditText) this.findViewById(R.id.password1);
        man=(RadioButton) this.findViewById(R.id.man);
        woman=(RadioButton) this.findViewById(R.id.woman);
        submit=(Button) this.findViewById(R.id.submit);
        
        submit.setOnClickListener(new loginOnClickListener());
    }
    public void chooseimg(View v){
    	Intent intent=new Intent();
    	intent.setClass(this, ImgActivity.class);
    	startActivityForResult(intent, 1);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
    	if(requestCode==1 && requestCode==200){
    		int photoid=data.getIntExtra("photo", R.drawable.ic_launcher);
    		imgchoose.setImageResource(photoid);
    	}
    }
  //内部类实现监听
  	private class loginOnClickListener implements OnClickListener{
  		//监听
  		public void onClick(View arg0) {
  			String user=username.getText().toString();
  			String password=password1.getText().toString();
  			String sex="男";
  			if(man.isChecked()){
  				sex="男";
  			}else if(woman.isChecked()){ 
  				sex="女";
  			}
  			
  			Student student=new Student(user, password, sex);
  			
  			Intent intent=new Intent();
  			intent.setClass(MainActivity.this, loginactivity.class);
  			intent.putExtra("student", student);
  			startActivity(intent);
  		}		
  	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
