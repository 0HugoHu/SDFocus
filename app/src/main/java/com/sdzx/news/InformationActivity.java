package com.sdzx.news;
import java.io.File;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.RefreshCallback;
import com.avos.avoscloud.SaveCallback;
import com.sdzx.tools.ApplicationHelper;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.*;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class InformationActivity extends SwipeBackActivity
{

	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;
	private ApplicationHelper appHelper;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_layout);

		android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.infoToolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		toolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		setTitle("关于");


		sharedPref = this.getSharedPreferences("SDnews", 0);
		editor = sharedPref.edit();

		//************************************************
		//显示帮助提示
		//************************************************
		boolean info_help=sharedPref.getBoolean("INFO_HELP",true);
		if (info_help) {
			editor.putBoolean("INFO_HELP", false);
			editor.commit();
			Intent intent = new Intent();
			intent.setClass(this, HelpActivity.class);
			intent.putExtra("num", 5);
			startActivity(intent);
		}

		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
			getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

		appHelper=new ApplicationHelper();
		//安卓5.0只能通过此种方式隐藏ActionBar
	}
	
	public void onLisenceClick(View v){

		android.support.v7.app.AlertDialog.Builder a = new android.support.v7.app.AlertDialog.Builder(InformationActivity.this);
		a.setTitle("开放源代码许可");
		a.setCancelable(false);
		a.setMessage(getResources().getString(R.string.lisence));
		a.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		a.show();
	}
	
	int type = 2;
	public void check(View v){
		if (AVUser.getCurrentUser()!=null) {
			AVUser.getCurrentUser().refreshInBackground(new RefreshCallback<AVObject>() {
				@Override
				public void done(AVObject avObject, AVException e) {
					
					if (AVUser.getCurrentUser().getInt("level") == 0) type = 0;
					else if (AVUser.getCurrentUser().getInt("level") != 2) type = 1;
					else type = 2;
					ApplicationHelper.checkForUpdates(InformationActivity.this, true, type);
					
				}
			});
		}else ApplicationHelper.checkForUpdates(InformationActivity.this, true, type);
		
	}





	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.info, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_update){
			check(null);
		}
		return super.onOptionsItemSelected(item);
	}


}
