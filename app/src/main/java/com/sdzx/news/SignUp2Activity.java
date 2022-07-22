package com.sdzx.news;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RefreshCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SignUpCallback;

public class SignUp2Activity extends AppCompatActivity {

    String userStr,passStr;
    EditText codeEt,phoneEt;
    AVUser thisAvu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        userStr=getIntent().getStringExtra("user");
        passStr=getIntent().getStringExtra("pass");

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.signup2Toolbar);
        setSupportActionBar(toolbar);
        setTitle("验证手机号");

        codeEt=(EditText)findViewById(R.id.signuplayoutEditText1code);
        phoneEt=(EditText)findViewById(R.id.signuplayoutEditText1phone);
    }

    public void onSendCodeClick(View v){
        thisAvu=new AVUser();
        thisAvu.setUsername(userStr);
        thisAvu.setPassword(passStr);
        thisAvu.setMobilePhoneNumber(phoneEt.getText().toString());
        thisAvu.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    thisAvu.requestMobilePhoneVerifyInBackgroud(phoneEt.getText().toString(), new RequestMobileCodeCallback() {
                        @Override
                        public void done(AVException p1) {
                            // TODO: Implement this method
                            if (p1 == null)
                                Toast.makeText(SignUp2Activity.this, "发送成功  " + phoneEt.getText().toString(), Toast.LENGTH_SHORT).show();
                            else Toast.makeText(SignUp2Activity.this, "发送失败  " + phoneEt.getText().toString() + p1.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else Toast.makeText(SignUp2Activity.this,e.toString(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onSignUpClick(View v){
        thisAvu.verifyMobilePhoneInBackground(codeEt.getText().toString(), new AVMobilePhoneVerifyCallback() {
            @Override
            public void done(AVException e) {
                // TODO Auto-generated method stub
                if (e == null) {
                    Toast.makeText(SignUp2Activity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent();
                    i.setClass(SignUp2Activity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else Toast.makeText(SignUp2Activity.this, "注册失败  " + e.toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }

}
