package com.sdzx.news;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.*;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.*;
import android.widget.AbsListView.*;
import android.widget.AdapterView.*;
import com.avos.avoscloud.*;

import java.util.*;

import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;

import com.avos.avoscloud.feedback.FeedbackAgent;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.sdzx.news.views.MyTouchDisableViewPager;
import com.sdzx.news.views.BmpDataClass;
import com.sdzx.news.views.PageListViewAdapter;
import com.sdzx.news.views.SimpleFragmentPagerAdapter;
import com.sdzx.tools.*;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;
import com.tencent.open.utils.Util;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener
{
	private ListView listView;
	private PageListViewAdapter adapter;
	private ArrayList<NewsItemDataClass> list;
	private boolean ifLoadImg;
	private ApplicationHelper appHelper;
	private boolean ifLoading=true;//是否在加载中
	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;
    Bundle BD_OLDBundle;
	private DrawerLayout mDrawerLayout;
	private EditText nameEditText,messagEditText;
	private ImageView userImgView;
	private TextView idTv,scoreTv;
    //private LinearLayout pageLayout;
	private MyTouchDisableViewPager mViewPager;

	private RelativeLayout allLayout;
	private ImageView initImgv;
	private FeedbackAgent agent;
	boolean ifHasBeenLongClick=false;
	private PagerAdapter mViewPagerAdapter;
	private ArrayList<View> mViews= new ArrayList<View>();
	private FloatingActionButton fab;
	private String className="main";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i("ACT", "onCreate");

		setContentView(R.layout.mdactivity_main);
		BD_OLDBundle = savedInstanceState;

		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		editor=sharedPref.edit();


		allLayout=(RelativeLayout)findViewById(R.id.all_view);
		initImgv=(ImageView)findViewById(R.id.initImageView);
		initImgv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		new Thread(new Runnable() {
			@Override
			public void run() {
				/*
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Animation alphaAnimation=new AlphaAnimation(1, 0);
						alphaAnimation.setDuration(500);//设置动画持续时间为3秒
						alphaAnimation.setFillAfter(true);//设置动画结束后保持当前的位置（即不返回到动画开始前的位置）
						initImgv.startAnimation(alphaAnimation);
					}
				});
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				*/
				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						allLayout.removeView(initImgv);
					}
				});

				if (!sharedPref.getBoolean("MAIN_NOTE", true)) {

					//************************************************
					//显示帮助提示
					//************************************************
					boolean main_help_1 = sharedPref.getBoolean("MAIN_HELP_1", true);
					if (main_help_1) {
						editor.putBoolean("MAIN_HELP_1", false);
						editor.commit();
						Intent intent = new Intent();
						intent.setClass(MainActivity.this, HelpActivity.class);
						intent.putExtra("num", 0);
						startActivity(intent);
					}
				}

			}
		}).start();




		//************************************************
		//显示用户协议
		//************************************************
		if (sharedPref.getBoolean("MAIN_NOTE", true)) {

			android.support.v7.app.AlertDialog.Builder a = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
			a.setTitle("用户协议");
			a.setCancelable(false);
			a.setMessage(getResources().getString(R.string.yong_hu_xie_yi));
			a.setPositiveButton("我已阅读并会遵守以上条款", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					editor.putBoolean("MAIN_NOTE", false);
					editor.commit();

					//************************************************
					//显示帮助提示
					//************************************************
					boolean main_help_1 = sharedPref.getBoolean("MAIN_HELP_1", true);
					if (main_help_1) {
						editor.putBoolean("MAIN_HELP_1", false);
						editor.commit();
						Intent intent = new Intent();
						intent.setClass(MainActivity.this, HelpActivity.class);
						intent.putExtra("num", 0);
						startActivity(intent);
					}
				}
			});
			a.show();
		} else {
		}


		AVQuery<AVObject> ableQue = new AVQuery<AVObject>("Value");
		ableQue.whereEqualTo("ValueName", "IfLogInAble");
		ableQue.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> list, AVException e) {
				if (e == null) {
					if (list.get(0).getString("Value").equals("0")) {
						Intent intent = new Intent();
						intent.setClass(MainActivity.this, InitActivity.class);
						startActivity(intent);
					}
				} else Log.e("AVE",e.toString());
			}
		});


		if (AVUser.getCurrentUser()!=null){
			agent = new FeedbackAgent(this);
			agent.sync();
		}

		AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
			public void done(AVException e) {
				if (e == null) {
					// 保存成功
					String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
					AVUser thisAvu=AVUser.getCurrentUser();
					Log.i("INS","done "+installationId);
					if (thisAvu!=null) {
						thisAvu.put("InstaId", installationId);
						thisAvu.saveInBackground(new SaveCallback() {
							@Override
							public void done(AVException e) {
								if (e == null)
									Log.i("INS", "done");
								else Log.i("INS", e.toString());
							}
						});
					}
					// 关联  installationId 到用户表等操作……
				} else {
					// 保存失败，输出错误信息
					Log.i("INS",e.toString());
				}
			}
		});
		PushService.setDefaultPushCallback(this, UserActivity.class);

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			//申请WRITE_EXTERNAL_STORAGE权限
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
		}

		android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (AVUser.getCurrentUser() == null) {
					Snackbar.make(fab, "请先登录", Snackbar.LENGTH_SHORT).setAction("立即登录", new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							intent.setClass(MainActivity.this, InitActivity.class);
							startActivity(intent);
						}
					}).show();
				} else {
					if (mViewPager.getCurrentItem() != feedbackIndex) {
						Intent intent = new Intent();
						intent.setClass(MainActivity.this, EditActivity.class);
						startActivity(intent);
					} else {
						onSendFeedbackClick();
					}
				}
			}
		});

		onShareEverydayCheck();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		mDrawerLayout.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		mViewPager = (MyTouchDisableViewPager) findViewById(R.id.myMainPagerViewPager);
		mViewPagerAdapter = new MyViewPagerAdatper(mViews);
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setOffscreenPageLimit(1);

		View drawHeadView = navigationView.getHeaderView(0);

		userImgView = (ImageView) drawHeadView.findViewById(R.id.userImageView);
		ImageView exitImgView = (ImageView) drawHeadView.findViewById(R.id.exitImg);
		exitImgView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onLogOutClick(null);
			}
		});
		userImgView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.this.onUserClick(null);
			}
		});

		idTv = (TextView) drawHeadView.findViewById(R.id.usernameTv);
		scoreTv = (TextView) drawHeadView.findViewById(R.id.userScoreTv);

		getUserImg();
		int type = 2;
		if (AVUser.getCurrentUser()!=null){
		
			if (AVUser.getCurrentUser().getInt("level") == 0) type = 0;
			else if (AVUser.getCurrentUser().getInt("level") != 2) type = 1;
			else type = 2;
		}
		ApplicationHelper.checkForUpdates(MainActivity.this, false, type);
		
		
		appHelper = new ApplicationHelper();
		appHelper.makeRootDirectory("/SDFocus/");
		appHelper.makeRootDirectory("/SDFocus/pictures/");
		appHelper.makeRootDirectory("/SDFocus/updates/");
		appHelper.makeRootDirectory("/SDFocus/cache/");
		appHelper.makeRootDirectory("/SDFocus/upload/");


		setTitle("精选");
		ifLoadImg = sharedPref.getBoolean("IfLoadImg", true);
		onFeedBackClick(null);
		onFenleiClick(null);
		onMainClick(null);
		onShetuanClick();
		doRefresh();
		if (!appHelper.isNetworkConnected(MainActivity.this)){
			onFenleiClick(null);
		}
	}


	public void getUserImg(){
		if (AVUser.getCurrentUser()!=null)
		AVUser.getCurrentUser().refreshInBackground(new RefreshCallback() {
			@Override
			public void done(AVObject avObject, AVException e) {
				if (e == null) {
					final int yunVersion=avObject.getInt("VersionCode");
					String verstr=MainActivity.this.getResources().getString(R.string.version_code);
					int currentVersion=0;
					if (verstr!=null)
						if (verstr.length()>0){
							currentVersion=Integer.valueOf(verstr);
						}
					final int verfnl=currentVersion;

					if (yunVersion<currentVersion) avObject.put("VersionCode",currentVersion);
					avObject.saveInBackground(new SaveCallback() {
						@Override
						public void done(AVException e) {
							Log.i("SAV",yunVersion+"-->"+verfnl);
						}
					});

					
					idTv.setText(AVUser.getCurrentUser().getUsername());
					scoreTv.setText("积分: " + AVUser.getCurrentUser().getNumber("score"));

					AVFile imgFile = avObject.getAVFile("Image");
					if (imgFile != null) {
						imgFile.getDataInBackground(new GetDataCallback() {
							@Override
							public void done(byte[] bytes, AVException e) {
								if (e == null) {
									userImgView.setImageBitmap(ApplicationHelper.Bytes2Bimap(bytes));
									userImgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
								} else Log.i("USR", e.toString());
							}
						});
					}
				} else Log.i("USR", e.toString());
			}
		});
	}

	@Override
	protected void onResume(){
		super.onResume();
		Log.i("ACT", "onResume");
		if (mViewPager.getCurrentItem()==feedbackIndex)
			fab.setImageResource(R.drawable.ic_done_white_48dp);
		ifLoadImg = sharedPref.getBoolean("IfLoadImg", true);
		if (AVUser.getCurrentUser()!=null) getUserImg();
	}

	@Override
	protected void onStop(){
		super.onStop();
		Log.i("ACT", "onStop");
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		Log.i("ACT", "onDestroy");
	}

	@Override
	protected void onPause(){
		super.onPause();
		Log.i("ACT", "onPause");
	}

	@Override
	protected void onStart(){
		super.onStart();
		Log.i("ACT", "onStart");
	}
	@Override
	protected void onRestart(){
		super.onRestart();
		Log.i("ACT", "onRestart");
	}

	public void onUserClick(View v){
		if (AVUser.getCurrentUser()==null){
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, InitActivity.class);
			startActivity(intent);
		}else {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, UserActivity.class);
			intent.putExtra("type",0);
			startActivity(intent);
		}
	}
/*
	private class ShowSnackTask extends AsyncTask<Void, Void, String[]>
	{
        protected String[] doInBackground(Void... params)
		{
            try{
                Thread.sleep(1000);
            }
			catch (InterruptedException e){ }
            return null;
        }
        protected void onPostExecute(String[] result)
		{
			if (AVUser.getCurrentUser()!=null)
			Snackbar.make(fab, "欢迎来到树德Focus\n" + AVUser.getCurrentUser().getUsername() + "已登陆,积分：" + AVUser.getCurrentUser().getInt("score"), Snackbar.LENGTH_LONG)
				.setAction("好的", new OnClickListener(){

					@Override
					public void onClick(View p1)
					{
						// TODO: Implement this method
					}
				}).show();
			else Snackbar.make(fab, "欢迎来到树德Focus，请登录",Snackbar.LENGTH_SHORT).
					setAction("立即登录", new OnClickListener(){
						@Override
						public void onClick(View p1)
						{
							Intent intent = new Intent();
							intent.setClass(MainActivity.this, InitActivity.class);
							startActivity(intent);
						}
					}).show();
		    super.onPostExecute(result);
        }
    }*/
	/*
	private class HideFabTask extends AsyncTask<Void, Void, String[]>
	{
		protected String[] doInBackground(Void... params)
		{
			try{
				Thread.sleep(100);
			}
			catch (InterruptedException e){ }
			return null;
		}
		protected void onPostExecute(String[] result)
		{
			super.onPostExecute(result);
			fab.hide();
		}
	}
*/

	boolean ifMainHasBeenLoad=false,ifFeedbackHasBeenLoad=false,ifFenleiHasBeenLoad=false,ifShetuanHasBeenLoad=false;
	View pageViewFeedbackView;
	View pageViewFenleiView;
	View pageViewShetuanView;
	View pageViewMainThisView;
	SwipeRefreshLayout pageViewMainThisViewPullListView;

	public void onFeedBackClick(View v)
	{
		mDrawerLayout.closeDrawers();
		if (!ifFeedbackHasBeenLoad)
		{
			pageViewFeedbackView = LayoutInflater.from(this).inflate(R.layout.activity_main_pager_3, null);
			nameEditText = (EditText)pageViewFeedbackView.findViewById(R.id.editTextName);
			messagEditText = (EditText)pageViewFeedbackView.findViewById(R.id.editTextMessage);
			ifFeedbackHasBeenLoad = true;
			mViews.add(pageViewFeedbackView);
			mViewPagerAdapter.notifyDataSetChanged();
			feedbackIndex = mViews.size()-1;
		}
		if (tabLayout!=null) {
			apl.removeView(tabLayout);
			ifTabIsExsist=false;
		}
		if (shetuanTabLayout!=null) {
			apl.removeView(shetuanTabLayout);
			ifShetuanTabIsExsist=false;
		}
		mViewPager.setCurrentItem(feedbackIndex);
		mViewPagerAdapter.notifyDataSetChanged();
	}

	ViewPager viewPager;
	ViewPager shetuanViewPager;
	TabLayout shetuanTabLayout;
	TabLayout tabLayout;
	AppBarLayout apl;
	boolean ifTabIsExsist=false;
	boolean ifShetuanTabIsExsist=false;



	public void onFenleiClick(View v)
	{
		mDrawerLayout.closeDrawers();

		if (!ifFenleiHasBeenLoad){
				pageViewFenleiView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main_pager_2, null);

				apl=(AppBarLayout)findViewById(R.id.appBar);

				viewPager = (ViewPager) pageViewFenleiView.findViewById(R.id.fenleiviewpager);
				viewPager.setAdapter(new SimpleFragmentPagerAdapter(MainActivity.this.getSupportFragmentManager(),getResources().getStringArray(R.array.categories_name),getResources().getStringArray(R.array.categories_id),MainActivity.this));
				tabLayout = new TabLayout(MainActivity.this);
				tabLayout.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, ApplicationHelper.dip2px(MainActivity.this, 50)));
				tabLayout.setupWithViewPager(viewPager);
				tabLayout.setTabTextColors(Color.parseColor("#DDDDDD"), Color.parseColor("#FFFFFF"));
				tabLayout.setTabMode(TabLayout.MODE_FIXED);
				ifFenleiHasBeenLoad = true;
				mViews.add(pageViewFenleiView);
				mViewPagerAdapter.notifyDataSetChanged();
				fenleiIndex = mViews.size()-1;

		}
		else{
			if (!ifTabIsExsist) {
				apl.addView(tabLayout,new AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, ApplicationHelper.dip2px(MainActivity.this, 50)));
				ifTabIsExsist=true;
			}
			if (shetuanTabLayout!=null) {
				apl.removeView(shetuanTabLayout);
				ifShetuanTabIsExsist=false;
			}
			mViewPager.setCurrentItem(fenleiIndex);
			mViewPagerAdapter.notifyDataSetChanged();
		}

	}

	public void onShetuanClick(){
		mDrawerLayout.closeDrawers();
		if (!ifShetuanHasBeenLoad){
			pageViewShetuanView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main_pager_4, null);

			shetuanViewPager = (ViewPager) pageViewShetuanView.findViewById(R.id.shetuanviewpager);
			shetuanViewPager.setAdapter(new SimpleFragmentPagerAdapter(getSupportFragmentManager(),getResources().getStringArray(R.array.shetuan_name),getResources().getStringArray(R.array.shetuan_id),MainActivity.this));
			shetuanTabLayout = new TabLayout(MainActivity.this);
			shetuanTabLayout.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, ApplicationHelper.dip2px(MainActivity.this, 50)));
			shetuanTabLayout.setupWithViewPager(shetuanViewPager);
			shetuanTabLayout.setTabTextColors(Color.parseColor("#DDDDDD"), Color.parseColor("#FFFFFF"));
			shetuanTabLayout.setTabMode(TabLayout.MODE_FIXED);
			ifShetuanHasBeenLoad = true;
			mViews.add(pageViewShetuanView);
			mViewPagerAdapter.notifyDataSetChanged();
			shetuanIndex = mViews.size()-1;

		}
		else{
			if (!ifShetuanTabIsExsist){
				apl.addView(shetuanTabLayout,new AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, ApplicationHelper.dip2px(MainActivity.this, 50)));
				ifShetuanTabIsExsist=true;
			}
			if (tabLayout != null) {
				apl.removeView(tabLayout);
				ifTabIsExsist=false;
			}
			mViewPager.setCurrentItem(shetuanIndex);
			mViewPagerAdapter.notifyDataSetChanged();
		}

	}

	public void onMainClick(View v)
	{
		className = "main";
		ifNeedReload = true;
		HandlerThread handlerThread = new  HandlerThread("backgroundThread");
		handlerThread.start();   
		MyHandler myHandler = new  MyHandler(handlerThread.getLooper());   
		Message msg = myHandler.obtainMessage();
		//....此处对msg进行赋值，可以创建一个Bundle进行承载
		msg.sendToTarget();
		mDrawerLayout.closeDrawers();
		ifLoading = true;
		if (!ifMainHasBeenLoad)
		{
			pageViewMainThisView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main_pager_1, null);

			RelativeLayout mainRelativeLayout=(RelativeLayout)pageViewMainThisView.findViewById(R.id.main1Relative);
			list = new ArrayList<NewsItemDataClass>();
			pageViewMainThisViewPullListView = new SwipeRefreshLayout(this);

			listView = new ListView(this);
			pageViewMainThisViewPullListView.setLayoutParams(new SwipeRefreshLayout.LayoutParams(SwipeRefreshLayout.LayoutParams.MATCH_PARENT, SwipeRefreshLayout.LayoutParams.MATCH_PARENT));
			pageViewMainThisViewPullListView.addView(listView);
			pageViewMainThisViewPullListView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					ifNeedReload = true;
					pageNum = 0;
					//new DoneRefreshTask().execute();
					MainActivity.this.loadItemOfListView();
				}
			});
			listView.setDivider(null);
			listView.setFastScrollEnabled(false);
			listView.setFastScrollAlwaysVisible(false);
			adapter = new PageListViewAdapter(MainActivity.this, list);
			listView.setAdapter(adapter);
			listView.setOnScrollListener(new OnScrollListener() {
				public void onScrollStateChanged(AbsListView view, int scrollState) {

				}

				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					int pos = listView.getLastVisiblePosition();
					nowScollPosition = listView.getFirstVisiblePosition();
					if (nowScollPosition - lastScollPosition >= 1) fab.hide();
					else if (nowScollPosition - lastScollPosition <= -1) fab.show();
					lastScollPosition = nowScollPosition;
					if (ifLoading == true) {
						if (listView.getFirstVisiblePosition() != 0) if (pos == list.size() - 1) {
							fab.show();
							new Thread() {
								public void run() {
									MainActivity.this.loadItemOfListView();
								}
							}.run();
						}
					}
				}
			});
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
					if (!ifHasBeenLongClick)
						if (p3 < list.size()) {
							Intent intent = new Intent();
							intent.putExtra("cid", (list.get(p3).getCid()) + "");
							intent.putExtra("ClassName", list.get(p3).getClassname());
							intent.setClass(MainActivity.this, ReaderActivity.class);
							startActivity(intent);
						}
				}
			});

			final String itemsStr[]={"从精选删除","删除本文"};

			listView.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
					ifHasBeenLongClick = true;
					android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
							.setTitle("操作")
							.setItems(itemsStr, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									ifHasBeenLongClick = false;
									if (which == 0) {
										if (AVUser.getCurrentUser()!=null) {
											if ((AVUser.getCurrentUser().getInt("level") == 0) || (AVUser.getCurrentUser().getInt("level") == 3) || (AVUser.getCurrentUser().getObjectId().equals(list.get(position).getUser().getObjectId()))) {

												String fCid = list.get(position).getFenleiCid();
												AVQuery<AVObject> newQ = new AVQuery<AVObject>("main");
												newQ.getInBackground(fCid, new GetCallback<AVObject>() {
													@Override
													public void done(AVObject avObject, AVException e) {
														avObject.deleteInBackground(new DeleteCallback() {
															@Override
															public void done(AVException e) {
																if (e == null) {
																	Snackbar.make(fab, "移除成功", Snackbar.LENGTH_SHORT).show();
																	doRefresh();
																} else
																	Snackbar.make(fab, "移除失败", Snackbar.LENGTH_SHORT).show();
															}
														});
													}
												});
											} else
												Snackbar.make(fab, "没有权限", Snackbar.LENGTH_SHORT).show();
										}else Snackbar.make(fab, "请先登录", Snackbar.LENGTH_SHORT).setAction("立即登录", new OnClickListener() {
											@Override
											public void onClick(View v) {
												Intent intent = new Intent();
												intent.setClass(MainActivity.this, InitActivity.class);
												startActivity(intent);
											}
										}).show();
									} else if (which == 1) {

										android.support.v7.app.AlertDialog dialog1 = new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
												.setTitle("确定？")
												.setMessage("真的要删除吗？")
												.setPositiveButton("是的", new DialogInterface.OnClickListener() {
													@Override
													public void onClick(DialogInterface dialog, int which) {

														if (AVUser.getCurrentUser()!=null) {
															if ((AVUser.getCurrentUser().getInt("level") == 0) || (AVUser.getCurrentUser().getInt("level") == 3)) {

																list.get(position).getObj().deleteInBackground(new DeleteCallback() {
																	@Override
																	public void done(AVException e) {
																		if (e == null) {
																			Snackbar.make(fab, "删除成功", Snackbar.LENGTH_SHORT).show();
																			doRefresh();
																		} else
																			Snackbar.make(fab, "删除失败", Snackbar.LENGTH_SHORT).show();
																	}
																});
															} else
																Snackbar.make(fab, "没有权限", Snackbar.LENGTH_SHORT).show();
														}else Snackbar.make(fab, "请先登录", Snackbar.LENGTH_SHORT).setAction("立即登录", new OnClickListener() {
															@Override
															public void onClick(View v) {
																Intent intent = new Intent();
																intent.setClass(MainActivity.this, InitActivity.class);
																startActivity(intent);
															}
														}).show();
													}
												}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
													@Override
													public void onClick(DialogInterface dialog, int which) {
													}
												}).show();

									}
								}
							}).show();
					dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							ifHasBeenLongClick = false;
						}
					});

					return false;
				}
			});



			pageViewMainThisViewPullListView.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
			ifMainHasBeenLoad = true;
			mainRelativeLayout.removeAllViews();
			pageViewMainThisViewPullListView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mainRelativeLayout.addView(pageViewMainThisViewPullListView);
			mViews.add(pageViewMainThisView);
			mViewPagerAdapter.notifyDataSetChanged();
			mainIndex = mViews.size()-1;
		}

		onMainNotNewViewClick();
		mViewPager.setCurrentItem(mainIndex);
	}

	int mainIndex,feedbackIndex,fenleiIndex,shetuanIndex;
	int lastScollPosition=0,nowScollPosition=0;



	public void doRefresh(){

		ifNeedReload = true;
		pageNum = 0;
		pageViewMainThisViewPullListView.setRefreshing(true);
		MainActivity.this.loadItemOfListView();
	}



	public void onTestNotNewViewClick(){
		mViewPager.setCurrentItem(mainIndex);
	}

	public void onMainNotNewViewClick(){
		if (tabLayout!=null) {
			apl.removeView(tabLayout);
			ifTabIsExsist=false;
		}
		if (shetuanTabLayout!=null) {
			apl.removeView(shetuanTabLayout);
			ifShetuanTabIsExsist=false;
		}
		className = "main";
		mViewPager.setCurrentItem(mainIndex);
	}



	boolean ifNeedReload=false;

	public void onLogOutClick(View v)
	{
		AVUser currentUser = AVUser.getCurrentUser();
		currentUser.logOut();
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, InitActivity.class);
		startActivity(intent);
	}

	public void onSearchClick(View v)
	{
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, MySearchActivity.class);
		startActivity(intent);

	}

	int looper_number=0;
	int pageNum=0;
	boolean ifSavingBmp=false;

	public void loadItemOfListView()
	{

		new Thread(new Runnable() {
			@Override
			public void run() {


		ifLoading = false;
		looper_number++;
		AVQuery<AVObject> query = new AVQuery<AVObject>(className);
		if (!appHelper.isNetworkConnected(MainActivity.this))
			query.setCachePolicy(AVQuery.CachePolicy.CACHE_ONLY);
		else {
			query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		}
		query.setSkip(pageNum * 10);
		query.setLimit(10);
		pageNum++;
		query.orderByDescending("updatedAt");
		query.findInBackground(new FindCallback<AVObject>()
		{
			public void done(List<AVObject> avObjects, AVException e)
			{
				if (ifNeedReload){
					list.clear();
					ifNeedReload = false;
				}
				if (e == null)
				{
					final List<AVObject> avofnl=avObjects;

					int max;
					if (avObjects.size() >= 10) max = 10;
					else max = avObjects.size();

					final int maxFnl=max;

					for (int i=0;i < max;i++)
					{
						final int ifnl1=i;


						final List<AVObject> avObjectsFnl=avObjects;
						String thisClassName=avofnl.get(i).getString("class");
						String thisId=avofnl.get(i).getString("idstring");
						AVQuery<AVObject> thisQuery = new AVQuery<AVObject>(thisClassName);
						if (!appHelper.isNetworkConnected(MainActivity.this))
							thisQuery.setCachePolicy(AVQuery.CachePolicy.CACHE_ONLY);
						else {
							thisQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
						}
						thisQuery.getInBackground(thisId, new GetCallback<AVObject>() {
							@Override
							public void done(AVObject avObject, AVException e) {
								if (e == null) {
									String summaryStr = avObject.getString("Summary").toString();
									if (summaryStr.length() > 45)
										summaryStr = summaryStr.substring(0, 44) + "…";


									String titleStr = avObject.getString("Title").toString();
									if (titleStr.length() > 45)
										titleStr = titleStr.substring(0, 44) + "…";
									list.add(new NewsItemDataClass(avObject.getClassName(), avObject.getObjectId(), titleStr, "", summaryStr, avObject));
									final int thisIndex = list.size() - 1;
									list.get(thisIndex).setFenleiCid(avObjectsFnl.get(ifnl1).getObjectId());
									adapter.notifyDataSetChanged();

									if (ifnl1==maxFnl-1) {
										ProgressWheel pb=(ProgressWheel)MainActivity.this.findViewById(R.id.progressBarLoading);
										pb.setAlpha(0);
									}

									AVObject thisavo = avObject.getAVObject("user");
									if (thisavo != null)
										thisavo.fetchIfNeededInBackground(new GetCallback() {
											@Override
											public void done(AVObject p1, AVException p2) {
												// TODO: Implement this method
												if (p2 == null) {
													if (list.size() > thisIndex){
														list.get(thisIndex).setUser(p1);
														adapter.notifyDataSetChanged();
													}
												}
											}
										});

									final AVObject thisAvoFnl = avObject;

									ifLoading = true;
									if (ifLoadImg) {
										final AVFile avFile = avObject.getAVFile("Thumb");

										if (appHelper.fileIsExists("/SDFocus/cache/" + thisAvoFnl.getObjectId() + ".cache")) {

											new Thread() {

												public void run() {
													Bitmap mBitmap;
													mBitmap = appHelper.convertToBitmap("/SDFocus/cache/" + thisAvoFnl.getObjectId() + ".cache", 80);
													if (list.size() > thisIndex){
														list.get(thisIndex).setBmp(mBitmap);
														adapter.notifyDataSetChanged();
													}
												}
											}.run();
										} else {
											if (avFile!=null) avFile.getDataInBackground(new GetDataCallback() {
												public void done(byte[] arg0, AVException arg1) {
													if (arg1 == null) {
														Bitmap bitmap = BitmapUtils.compressByKByte(arg0, 200, 200, 200);
														if (list.size() > thisIndex){
															list.get(thisIndex).setBmp(bitmap);
															adapter.notifyDataSetChanged();
														}
														bmpToSave.add(new BmpDataClass(bitmap, "/SDFocus/cache/" + thisAvoFnl.getObjectId()));

													}
												}
											});
										}
									}
								}
								if (ifMainHasBeenLoad) pageViewMainThisViewPullListView.setRefreshing(false);
							}
						});
					}
				}
			}
		});



			}
		}).start();
	}




	private ArrayList<BmpDataClass> bmpToSave=new ArrayList<BmpDataClass>();


	class  MyHandler  extends  Handler
	{
		public  MyHandler()
		{}
		public  MyHandler(Looper looper){
			super(looper);
		}
		public   void  handleMessage(Message msg)
		{
			new Thread(new Runnable(){
				public void run()
				{
					// TODO: Implement this method
					int index=0;
					while (true)
					{
						while (bmpToSave.size() <= index)
						{
							try
							{
								Thread.sleep(50);
							}
							catch (InterruptedException e)
							{}
						}
						if (bmpToSave.size() > index) appHelper.saveMyBitmap(appHelper.compressImage(bmpToSave.get(index).bitmap), bmpToSave.get(index).path+".cache",false);
						if (bmpToSave.size() > index) bmpToSave.get(index).delete();
						index++;
					}
				}
			}).run();
		}
	}

	public void onSendFeedbackClick()
	{
		String name=nameEditText.getText().toString();
		String message=messagEditText.getText().toString();

		if (name.equals(""))
		{
			Snackbar.make(fab, "名字不能为空", Snackbar.LENGTH_SHORT).show();
			return;
		}

		if (message.length() < 5)
		{
			Snackbar.make(fab, "请至少填写5个字", Snackbar.LENGTH_SHORT).show();
			return;
		}

    	AVObject avo=new AVObject("feedback");
		avo.put("Name", name);
		avo.put("Message", message);
		if (AVUser.getCurrentUser()!=null)
			avo.put("user",AVObject.createWithoutData("_User", AVUser.getCurrentUser().getObjectId()));
		avo.saveInBackground(new SaveCallback() {
			public void done(AVException arg0) {
				// TODO Auto-generated method stub
				Snackbar.make(fab, "发送成功，感谢您的反馈", Snackbar.LENGTH_SHORT).show();

				}
			});
	}


	public void onInformationClick(View v)
	{
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, InformationActivity.class);
		startActivity(intent);
	}

	public void onSetClick(View v)
	{
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, SettingsActivity.class);
		startActivity(intent);
	}




	public class ListViewAdapter extends BaseAdapter 
	{
		private Context context;
		private ArrayList<NewsItemDataClass> list;

		public ListViewAdapter(Context context, ArrayList<NewsItemDataClass> list) 
		{
			this.context = context;
			this.list = list;
		}
		public int getCount() 
		{
			return list.size();
		}

		public Object getItem(int arg0) 
		{
			return null;
		}

		public long getItemId(int arg0) 
		{
			return 0;
		}
		Type_1_Holder type_1_view = null;
		public View getView(final int position, View convertView, ViewGroup arg2) 
		{
			if (convertView==null) {

				type_1_view = new Type_1_Holder();
				convertView = LayoutInflater.from(context).inflate(R.layout.user_center_list_item, null);
				type_1_view.list_text = (TextView) convertView.findViewById(R.id.user_center_item_txt);
				type_1_view.list_img = (ImageView) convertView.findViewById(R.id.user_center_item_img);
				type_1_view.userlevelTextView = (TextView) convertView.findViewById(R.id.usercenterlistitemUserlevelTextView1);
				type_1_view.sumText = (TextView) convertView.findViewById(R.id.itemMainSummary);
				type_1_view.classView = (TextView) convertView.findViewById(R.id.usercenterlistitemUserClassTextView1);
				type_1_view.timeText = (TextView) convertView.findViewById(R.id.mainlayoutitemTextView1);
				type_1_view.userTextView = (TextView) convertView.findViewById(R.id.usercenterlistitemUserIdTextView1);
				type_1_view.userLayout=(RelativeLayout)convertView.findViewById(R.id.userCenterlinearLayout);

				convertView.setTag(type_1_view);

			} else {
				type_1_view = (Type_1_Holder) convertView.getTag();
			}

			if (position < list.size()) {
				int idx=ApplicationHelper.findIndexOfClasses(list.get(position).getClassname(),MainActivity.this);
				if (idx!=-1) type_1_view.classView.setText(MainActivity.this.getResources().getStringArray(R.array.categories_name)[idx]);
				//type_1_view.classView.setBackgroundColor(Color.parseColor(MainActivity.this.getResources().getStringArray(R.array.categories_color)[idx]));
				type_1_view.sumText.setText(list.get(position).getSummary());
				type_1_view.timeText.setText(list.get(position).getObj().getUpdatedAt().toLocaleString());
				type_1_view.list_text.setText(list.get(position).getCatname());
				type_1_view.list_img.setImageBitmap(list.get(position).getBmp());
				AVUser thisAvu = (AVUser) list.get(position).getUser();
				if (thisAvu != null) {
					type_1_view.userLayout.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

							Intent intent = new Intent();
							intent.setClass(MainActivity.this, UserActivity.class);
							intent.putExtra("cid",list.get(position).getUser().getObjectId());
							intent.putExtra("type",1);
							startActivity(intent);
						}
					});

					type_1_view.userTextView.setText(thisAvu.getUsername());
					int sco = thisAvu.getInt("score");
					int flg = thisAvu.getInt("level");
					flg = flg % 4;

					if (flg!=2) {
						type_1_view.userlevelTextView.setText(MainActivity.this.getResources().getStringArray(R.array.user_level_list)[flg]);
						type_1_view.userlevelTextView.setBackgroundColor(Color.parseColor(MainActivity.this.getResources().getStringArray(R.array.user_level_color)[flg]));
						type_1_view.userlevelTextView.setTextColor(Color.parseColor("#ffffff"));
					} else {
						int userScore = thisAvu.getInt("score");
						int userScoreLevel = ApplicationHelper.getUserScoreLevel(userScore);
						type_1_view.userlevelTextView.setText(MainActivity.this.getResources().getStringArray(R.array.user_score_level_list)[userScoreLevel]);
						type_1_view.userlevelTextView.setBackgroundColor(Color.parseColor(MainActivity.this.getResources().getStringArray(R.array.user_score_level_color)[userScoreLevel]));
						type_1_view.userlevelTextView.setTextColor(Color.parseColor("#ffffff"));
					}
				} else type_1_view.userTextView.setText(" ");

				listView.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
						if (!ifHasBeenLongClick)
							if (p3 < list.size()) {
								if (list.get(p3) != null) {
									Intent intent = new Intent();
									intent.putExtra("cid", (list.get(p3).getCid()) + "");
									intent.putExtra("ClassName", list.get(p3).getClassname());
									intent.setClass(MainActivity.this, ReaderActivity.class);
									startActivity(intent);
								}
							}
					}
				});
				if (list.get(position).getCatname().equals(""))
					return LayoutInflater.from(context).inflate(R.layout.viewpager_item, null);
			}
			return convertView;
		}

		class Type_1_Holder
		{
			TextView list_text;
			TextView sumText;
			ImageView list_img;
			TextView timeText;
			Button onclick;
			TextView userTextView;
			TextView classView;
			TextView userlevelTextView;
			RelativeLayout userLayout;
		}

	}
	
	
	public void onShareEverydayClick(){
		long lastTime = sharedPref.getLong("ShareTime", 0);
			Log.i("TIM", System.currentTimeMillis() + "/" + lastTime);
			if ((System.currentTimeMillis() - lastTime >= 24 *60*60 * 1000)) {

				Tencent mTencent = Tencent.createInstance("1105167874", MainActivity.this.getApplicationContext());
				final Bundle params = new Bundle();
				params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
				params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, getResources().getString(R.string.share_text));
				/*ArrayList<String> imgs=new ArrayList<String>();
				imgs.add("https://www.baidu.com/img/bd_logo1.png");
				params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgs);*/
				mTencent.publishToQzone(MainActivity.this, params, qZoneShareListener);
			}else Snackbar.make(fab, "您今天已经分享过了，再等"+(24*60-((System.currentTimeMillis() - lastTime)/1000/60))+"分钟吧~", Snackbar.LENGTH_SHORT).show();
	
	}



	public void onShareEverydayCheck(){
		if (sharedPref.getBoolean("SHOW_SHARE_NOTE", true)) {
			long lastTime = sharedPref.getLong("ShareTime", 0);
			Log.i("TIM", System.currentTimeMillis() + "/" + lastTime);
			if ((System.currentTimeMillis() - lastTime >= 24 *60*60 * 1000)) {
				Snackbar.make(fab,"现在分享本应用到QQ空间可以获得积分哦！(可在设置中关闭本提示)",Snackbar.LENGTH_LONG).setAction("分享", new OnClickListener() {
					@Override
					public void onClick(View v) {
						MainActivity.this.onShareEverydayClick();
					}
				}).show();
			}
		};




	}



	@Override
	public void onBackPressed()
	{
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START))
		{
			drawer.closeDrawer(GravityCompat.START);
		}
		else
		{
			Snackbar.make(fab,"真的要退出？",Snackbar.LENGTH_LONG).setAction("是的", new OnClickListener() {
				@Override
				public void onClick(View v) {
					MainActivity.this.finish();
				}
			}).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
		if (id == R.id.action_settings)
		{
			onSetClick(null);
			return true;
		}else if (id == R.id.action_search){
			//onSearchClick(null);
			getSupportFragmentManager()
					.beginTransaction()
					.add(android.R.id.content, new FragmentRevealExample(), "fragment_my")
					.addToBackStack("fragment:reveal")
					.commit();
			fab.show();
			return true;
		}else if (id==R.id.action_share){
			onShareEverydayClick();

		}

		return super.onOptionsItemSelected(item);
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_QZONE_SHARE) {
			Tencent.onActivityResultData(requestCode, resultCode, data, qZoneShareListener);
		}
	}




	IUiListener qZoneShareListener = new IUiListener() {

		@Override
		public void onCancel() {
			Log.i("SRE", "onCancel:test ");

		}

		@Override
		public void onError(UiError e) {
			// TODO Auto-generated method stub
			Log.i("SRE", "onError: " + e.errorMessage);
			Snackbar.make(fab, "错误" + e.toString(), Snackbar.LENGTH_SHORT).show();

		}

		@Override
		public void onComplete(Object response) {
			// TODO Auto-generated method stub
			Log.i("SRE", "onComplete: " + response.toString());

			AVUser currentUser=AVUser.getCurrentUser();
			if (currentUser!=null) {
				currentUser.put("score", currentUser.getInt("score") + 5);
				currentUser.saveInBackground(new SaveCallback() {
					@Override
					public void done(AVException p11) {
						// TODO: Implement this method
						if (p11 == null) {
							Snackbar.make(fab, "分享成功,积分+5", Snackbar.LENGTH_SHORT).show();
							SharedPreferences.Editor editor = sharedPref.edit();
							editor.putLong("ShareTime", System.currentTimeMillis());
							editor.commit();

						} else {
							Snackbar.make(fab, "网络错误" + p11.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
						}
					}
				});
			}
		}

	};


	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item)
	{
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		if (id == R.id.main_menu){
			onMainNotNewViewClick();
			setTitle("精选");
			fab.show();
			fab.setImageResource(R.drawable.ic_edit_white_3x);
		}
		else if (id == R.id.fenlei_menu){
			onFenleiClick(null);
			setTitle("分类");
			fab.show();
			fab.setImageResource(R.drawable.ic_edit_white_3x);
		} else if (id == R.id.setting_menu){
			onSetClick(null);
			fab.show();
		}
		else if (id == R.id.info_menu){
			onInformationClick(null);
			fab.show();
			fab.setImageResource(R.drawable.ic_edit_white_3x);
		}
		else if (id == R.id.shetuan_menu){
			setTitle("社团与部门");
			onShetuanClick();
			fab.show();
			fab.setImageResource(R.drawable.ic_edit_white_3x);
		}else if (id==R.id.conversation_menu){
			if (AVUser.getCurrentUser()!=null) {
				agent.startDefaultThreadActivity();
			}else {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, InitActivity.class);
				startActivity(intent);

			}
		}
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}



	private class MyViewPagerAdatper extends PagerAdapter
	{

		private ArrayList<View> mViews;

		public MyViewPagerAdatper(ArrayList<View> pViews)
		{
			super();
			if (pViews != null)
			{
				mViews = pViews;
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return MainActivity.this.getResources().getStringArray(R.array.categories_name)[position];
		}
		@Override
		public void destroyItem(ViewGroup pContainer, int pPosition,
								Object pObject)
		{
			pContainer.removeView((View)pObject);
		}

		@Override
		public int getCount()
		{
			return mViews.size();
		}

		@Override
		public Object instantiateItem(ViewGroup pContainer, int pPosition)
		{
			pContainer.addView(mViews.get(pPosition));
			return mViews.get(pPosition);
		}

		@Override
		public boolean isViewFromObject(View pView, Object pObject)
		{
			return pView == (View)pObject;
		}
	}


}
