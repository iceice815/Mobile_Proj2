package com.example.login;

import com.example.clas.Student;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class loginactivity extends Activity {
	
	private TextView tv_username,tv_password,tv_sex;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getback);
		tv_username=(TextView) this.findViewById(R.id.tv_username);
		tv_password=(TextView) this.findViewById(R.id.tv_password);
		tv_sex=(TextView) this.findViewById(R.id.tv_sex);
		Intent intent=this.getIntent();
		Student student=(Student) intent.getSerializableExtra("student");
		
		
		tv_username.setText(student.getUser());
		tv_password.setText(student.getPassword());
		tv_sex.setText(student.getSex());
		
		
	}

}
