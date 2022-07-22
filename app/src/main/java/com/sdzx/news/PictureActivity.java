package com.sdzx.news;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.sdzx.tools.ApplicationHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class PictureActivity extends SwipeBackActivity {

	private String cid;
	private int i_final;
	private ImageView imageView;
	private ApplicationHelper appHelper;
	private ProgressBar progressBar;
	private Bitmap newBmp;
	
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_layout);
		
		imageView=(ImageView)findViewById(R.id.pictureActivityImageView1);

		progressBar=(ProgressBar)findViewById(R.id.progressBar1);

		appHelper=new ApplicationHelper();
		progressBar.setProgress(20);
		
		Intent intent = getIntent();
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
			getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
		cid=intent.getStringExtra("cid");
		
		if (appHelper.fileIsExists("/SDFocus/cache/"+cid+".cache")){
			new Thread(){
				@Override
				public void run(){
					Bitmap mBitmap;
					mBitmap=appHelper.convertToBitmap("/SDFocus/cache/"+cid+".cache",80);
					imageView.setImageBitmap(mBitmap);
					progressBar.setAlpha(0);
					newBmp=mBitmap;

				}
			}.run();

		} else{
			
		
		AVQuery<AVObject> query = new AVQuery<AVObject>("images");
		if (!appHelper.isNetworkConnected(this))
			query.setCachePolicy(AVQuery.CachePolicy.CACHE_ONLY);
		else{
			query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		}
		query.getInBackground(cid, new GetCallback<AVObject>() {
			
			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1==null) {
					AVFile picFile = arg0.getAVFile("File");
					if (picFile != null) picFile.getDataInBackground(new GetDataCallback() {
						@Override
						public void done(byte[] arg0, AVException arg1) {
							// TODO Auto-generated method stub
							newBmp = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
							imageView.setImageBitmap(newBmp);
							progressBar.setAlpha(0);
						}
					}, new ProgressCallback() {
						@Override
						public void done(Integer arg0) {
							// TODO Auto-generated method stub

						}
					});
				}
			}
		});
		}
	}
	
	public void onSaveClick(View v){
		if (newBmp!=null){
		
		appHelper.makeRootDirectory("/SDFocus/");
		appHelper.makeRootDirectory("/SDFocus/pictures/");
		
		appHelper.saveMyBitmap(newBmp, "/SDFocus/pictures/"+cid,true);
		Toast.makeText(this, "文件已保存为"+"/SDFocus/pictures/"+cid+".png", Toast.LENGTH_LONG).show();
	}
		else Toast.makeText(this, "文件保存失败", Toast.LENGTH_LONG).show();
		
	}
	
	
}
