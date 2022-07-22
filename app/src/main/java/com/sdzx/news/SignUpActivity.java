package com.sdzx.news;
import com.avos.avoscloud.*;

import android.content.Intent;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;

public class SignUpActivity extends AppCompatActivity
{
	EditText passwordAgainEt,passwordEt,userNameEt;
	AVUser newAvu;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_layout);

		android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.signupToolbar);
		setSupportActionBar(toolbar);
		setTitle("注册");

		AVOSCloud.initialize(this, "rTL0VObhRrmgAPdG7fWrd0lr", "xjLm6hYPJHxxtkdEfJkpPomD");

		passwordEt=(EditText)findViewById(R.id.signuplayoutEditText1password);
		passwordAgainEt=(EditText)findViewById(R.id.signuplayoutEditText1passwordAgain);
		userNameEt=(EditText)findViewById(R.id.signuplayoutEditText1username);
		
	}
	

	
	public void onNextClick(View v){
		String userStr=userNameEt.getText().toString();
		String passStr=passwordEt.getText().toString();
		if ((userStr.length()==0)||(userStr.length()>10)) {
			Toast.makeText(SignUpActivity.this, "用户名不能超过10个字符", Toast.LENGTH_SHORT).show();
		}else if (!passStr.equals(passwordAgainEt.getText().toString())){
			Toast.makeText(SignUpActivity.this, "两次密码输入不相同", Toast.LENGTH_SHORT).show();
		}else if ((passStr.length()==0)||(passStr.length()<6)) {
			Toast.makeText(SignUpActivity.this, "密码不能少于6个字符", Toast.LENGTH_SHORT).show();
		}else {
			Intent i = new Intent();
			i.putExtra("user", userStr);
			i.putExtra("pass", passStr);
			i.setClass(SignUpActivity.this, SignUp2Activity.class);
			startActivity(i);
		}
	}
	
}
