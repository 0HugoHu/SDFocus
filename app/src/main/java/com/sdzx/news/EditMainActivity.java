package com.sdzx.news;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.*;

import java.util.*;
import android.content.*;

public class EditMainActivity extends AppCompatActivity
{
	TextView tvclass,tv11,tv12,tv21,tv22,tv31,tv32,tv41,tv42;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_activity_main);
		
		tvclass=(TextView)findViewById(R.id.activitymainEditTextclass);
		tv11=(TextView)findViewById(R.id.activitymainEditText11);
		tv12=(TextView)findViewById(R.id.activitymainEditText12);
		tv21=(TextView)findViewById(R.id.activitymainEditText21);
		tv22=(TextView)findViewById(R.id.activitymainEditText22);
		tv31=(TextView)findViewById(R.id.activitymainEditText31);
		tv32=(TextView)findViewById(R.id.activitymainEditText32);
		tv41=(TextView)findViewById(R.id.activitymainEditText41);
		tv42=(TextView)findViewById(R.id.activitymainEditText42);
		
		
		
		
		
		AVOSCloud.initialize(this, "rTL0VObhRrmgAPdG7fWrd0lr", "xjLm6hYPJHxxtkdEfJkpPomD");
		
	}

	public void onsetClick(View v){
		if (tvclass.getText().toString().length()!=0){
			AVObject post = new AVObject(tvclass.getText().toString());
			if (tv11.getText().toString().length()!=0)
				post.put(tv11.getText().toString(), Integer.valueOf(tv12.getText().toString()));
			if (tv21.getText().toString().length()!=0) 
				post.put(tv21.getText().toString(), tv22.getText().toString());
			if (tv31.getText().toString().length()!=0) 
				post.put(tv31.getText().toString(), tv32.getText().toString());
			if (tv41.getText().toString().length()!=0) 
				post.put(tv41.getText().toString(), tv42.getText().toString());
		
			post.saveInBackground(new SaveCallback() {
				
				@Override
				public void done(AVException arg0) {
					// TODO Auto-generated method stub
					Toast.makeText(EditMainActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
					
				}
			});
		
			}
		}
		
		public void onEditClick(View v){
			Intent intent = new Intent();
			intent.setClass(EditMainActivity.this, EditActivity.class);
			startActivity(intent);
		}
	
		
	
	public void onUpdateClick(View v){
		Intent intent = new Intent();
		intent.setClass(EditMainActivity.this, UpdateApkActivity.class);
		startActivity(intent);
		
	}
	
}



