package com.sdzx.news;
import android.app.*;
import android.content.*;
import android.database.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import java.io.*;

import com.avos.avoscloud.*;

import java.util.*;

public class UpdateApkActivity extends AppCompatActivity
{
	TextView thisTv;
	EditText codenameTv,codeTv,logTv,pathTv;
	private static final int FILE_SELECT_CODE = 0X111;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_layout);
		
		codenameTv=(EditText)findViewById(R.id.updatelayoutEditText1codename);
		codeTv=(EditText)findViewById(R.id.updatelayoutEditText1code);
		logTv=(EditText)findViewById(R.id.updatelayoutEditText1log);
		pathTv=(EditText)findViewById(R.id.editText1path);
		thisTv=(TextView)findViewById(R.id.updatelayoutTextView1);
	}
	
	public void getFile(View v){
		
	}
	
	public void updateClick(View v){
		
		
	
		AVQuery<AVObject> query = new AVQuery<AVObject>("update");
		query.whereEqualTo("Latest","Latest");

		query.findInBackground(new FindCallback<AVObject>() {
				@Override
				public void done(List<AVObject> avObjects1, AVException e) 
				{
					if (e==null)
					{
						AVObject AVO= avObjects1.get(0);
						AVO.put("Latest","");
						AVO.saveInBackground();

						thisTv.setText("已替换");
					}
				}
			});


		thisTv.setText("开始上传");
		String pathStrings=Environment.getExternalStorageDirectory().getAbsolutePath()+pathTv.getText().toString();
		AVFile file = null;
		try {
			file = AVFile.withAbsoluteLocalPath(codenameTv.getText().toString()+".apk", pathStrings);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			thisTv.setText("文件未找到");
		}
		
		if (file != null){ 
			AVObject post = new AVObject("update");
			post.put("Version", codenameTv.getText().toString());
			post.put("Log", logTv.getText().toString());
			post.put("VersionCode", Integer.valueOf(codeTv.getText().toString()));
			post.put("Latest", "Latest");
			post.put("APK", file);
			thisTv.setText("上传中......");
			post.saveInBackground(new SaveCallback() {
				
				@Override
				public void done(AVException arg0) {
					// TODO Auto-generated method stub
					if (arg0==null)
						thisTv.setText("上传成功");
					else thisTv.setText("上传失败");
				}
			});
			
		}

		/********************************************************************/
	}
	
	
	
	
	
	
}
