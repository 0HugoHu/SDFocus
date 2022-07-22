package com.sdzx.news;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.UpdatePasswordCallback;

import java.util.List;

public class ForgetPassActivity extends AppCompatActivity {

    TextView phoneTv,passTv,passAgainTv,codeTv;
    AVUser noeUsr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.forgetToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setTitle("忘记密码");

        phoneTv=(TextView)findViewById(R.id.forgetlayoutEditText1phone);
        passTv=(TextView)findViewById(R.id.forgetlayoutEditText1password);
        codeTv=(TextView)findViewById(R.id.forgetlayoutEditText1code);
        passAgainTv=(TextView)findViewById(R.id.forgetlayoutEditText1passwordAgain);
    }


    public void onSignUpClick(View v){
        String passStr=passTv.getText().toString();
        String passAgainStr=passAgainTv.getText().toString();
        String codeStr=codeTv.getText().toString();

        if (noeUsr==null){
            Toast.makeText(ForgetPassActivity.this,"用户不存在",Toast.LENGTH_SHORT).show();
        }else if (codeStr.length()==0){
            Toast.makeText(ForgetPassActivity.this,"请输入验证码",Toast.LENGTH_SHORT).show();
        }else if (passStr.length()<6){
            Toast.makeText(ForgetPassActivity.this,"密码长度不能小于6字符",Toast.LENGTH_SHORT).show();
        }else if (!passStr.equals(passAgainStr)){
            Toast.makeText(ForgetPassActivity.this,"两次输入密码不相同",Toast.LENGTH_SHORT).show();
        }else {
            noeUsr.resetPasswordBySmsCodeInBackground(codeStr, passStr, new UpdatePasswordCallback() {
                @Override
                public void done(AVException e) {
                    if(e == null){
                        //密码更改成功了！
                        Toast.makeText(ForgetPassActivity.this,"密码更改成功，请牢记新密码",Toast.LENGTH_SHORT).show();
                    } else Toast.makeText(ForgetPassActivity.this,"失败"+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    public void onSendCodeClick(View v){


        final String phoneStr=phoneTv.getText().toString();
        if (phoneStr.length()!=0) {

            AVQuery<AVUser> query = AVUser.getQuery();
            query.whereEqualTo("mobilePhoneNumber", phoneStr);
            query.findInBackground(new FindCallback<AVUser>() {
                public void done(List<AVUser> objects, AVException e) {
                    if (e == null) {
                        // 查询成功
                        noeUsr=objects.get(0);
                        noeUsr.requestPasswordResetBySmsCodeInBackground(phoneStr, new RequestMobileCodeCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    //发送成功了
                                    Toast.makeText(ForgetPassActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(ForgetPassActivity.this, "发送失败 " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        // 查询出错
                        Toast.makeText(ForgetPassActivity.this,"用户不存在"+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }else Toast.makeText(ForgetPassActivity.this,"请输入手机号码",Toast.LENGTH_SHORT).show();
    }
}
