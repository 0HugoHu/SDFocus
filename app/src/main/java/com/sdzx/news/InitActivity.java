package com.sdzx.news;

import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import android.content.*;
import android.app.*;
import android.view.*;
import com.avos.avoscloud.*;
import android.widget.*;
import android.widget.Toolbar;

public class InitActivity extends AppCompatActivity
{
	EditText phoneEt,passwordEt;
	ImageView userImg;
	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		editor=sharedPref.edit();


		android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.inittoolbar);
		setSupportActionBar(toolbar);
		setTitle("");
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		passwordEt=(EditText)findViewById(R.id.initlayoutEditText1pass);
		phoneEt=(EditText)findViewById(R.id.initlayoutEditText1phone);
		userImg=(ImageView)findViewById(R.id.loginUserImageView);

		//userImg.startAnimation(AnimationUtils.loadAnimation(this,R.anim.init_anim));

		final AVUser currentUser = AVUser.getCurrentUser();

		phoneEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String phoneStr=phoneEt.getText().toString();
				if (phoneStr.length()==11){
					AVQuery<AVObject> findUserQ=new AVQuery<AVObject>("_User");
					findUserQ.whereEqualTo("mobilePhoneNumber",phoneStr);
					findUserQ.findInBackground(new FindCallback<AVObject>() {
						@Override
						public void done(List<AVObject> list, AVException e) {
							if ((e==null))
								if (list.size()>0){
									AVFile userImgAVf=list.get(0).getAVFile("Image");
									if (userImgAVf!=null) userImgAVf.getDataInBackground(new GetDataCallback() {
										@Override
										public void done(byte[] bytes, AVException e) {
											if (e==null) {
												userImg.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
											}
										}
									});
							}
						}
					});

				}
			}
		});
	}
	
	
	public void onLogInClick(View v){
		
		if ((phoneEt.getText().toString().length()!=0)&&(passwordEt.getText().toString().length()!=0)){
		
		AVUser.loginByMobilePhoneNumberInBackground(phoneEt.getText().toString(), passwordEt.getText().toString(), new LogInCallback() {

			@Override
			public void done(AVUser p1, AVException p2) {
				// TODO: Implement this method
				if (p2 == null) {
						AVQuery<AVObject> ableQue = new AVQuery<AVObject>("Value");
						ableQue.whereEqualTo("ValueName", "IfLogInAble");
						ableQue.findInBackground(new FindCallback<AVObject>() {
							@Override
							public void done(List<AVObject> list, AVException e) {
								if (e == null) {
									if (list.get(0).getString("Value").equals("1")) {

										Intent intent = new Intent();
										intent.setClass(InitActivity.this, MainActivity.class);
										startActivity(intent);
										finish();

									} else {
										Toast.makeText(InitActivity.this, "不允许登陆", Toast.LENGTH_SHORT).show();
									}
								} else
									Toast.makeText(InitActivity.this, "不允许登陆:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
							}
						});

				} else
					Toast.makeText(InitActivity.this, "登陆失败  " + p2.getLocalizedMessage(), Toast.LENGTH_SHORT).show();


			}


		});
		
		}
		else Toast.makeText(InitActivity.this,"请输入用户名和密码",Toast.LENGTH_SHORT).show();
		
		
	}

	public void onForgetPasswordClick(View v){
		Intent intent = new Intent();
		intent.setClass(InitActivity.this, ForgetPassActivity.class);
		startActivity(intent);
	}

	public void onSignUpClick(View v){
		Intent intent = new Intent();
		intent.setClass(InitActivity.this, SignUpActivity.class);
		startActivity(intent);
		
	}
	
	public void jumpClick(View v){
		Intent intent = new Intent();
		intent.setClass(InitActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
}
