package com.sdzx.news;
import com.sdzx.tools.ApplicationHelper;

import android.support.v4.app.*;
import android.app.*;
import android.os.*;
import android.content.*;
import android.support.v7.app.AppCompatActivity;
import android.widget.*;
import android.widget.CompoundButton.*;
import android.view.*;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class SetMainActivity extends SwipeBackActivity
{
	private ApplicationHelper appHelper;
	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;

	private CheckBox loadImgCheckBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setmain_layout);
		
		loadImgCheckBox=(CheckBox)findViewById(R.id.setmainlayoutCheckBoxDisplayImg);
		sharedPref = this.getSharedPreferences("SDnews", 0);
		appHelper=new ApplicationHelper();

		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

		editor=sharedPref.edit();
		loadImgCheckBox.setChecked(!sharedPref.getBoolean("IfLoadImg",true));
		
		loadImgCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				public void onCheckedChanged(CompoundButton p1, boolean p2)
				{
					// TODO: Implement this method
					editor.putBoolean("IfLoadImg",(!p2));
					editor.commit();
					
				}

			
		});
		
		
		
		
	}
	
	
	public void onClearImgClick(View v){
	appHelper.deleteFolderFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"SDFocus/cache",true);
	Toast.makeText(SetMainActivity.this,"已删除所有缓存",Toast.LENGTH_SHORT).show();
	appHelper.makeRootDirectory("/SDFocus/cache/");

}
	
}
