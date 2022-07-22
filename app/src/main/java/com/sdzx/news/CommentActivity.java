package com.sdzx.news;

import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.*;
import com.sdzx.news.R;
import com.sdzx.tools.ApplicationHelper;
import java.util.ArrayList;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import com.avos.avoscloud.*;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.*;
import android.content.*;
import android.content.res.Resources;
import android.graphics.*;
import android.support.design.widget.*;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class CommentActivity extends SwipeBackActivity implements SwipeRefreshLayout.OnRefreshListener
{
	private int pageNum;
	private boolean mIsStart = true;
    private int mCurIndex = 0;
    private static final int mLoadDataCount = 100;
	private ListView listView;
	private int size;
	private ListViewAdapter adapter;
	private ArrayList<CommentClass> list;
	private int numberOfItems=19;
	private ApplicationHelper appHelper;
	private boolean ifLoading=true;
	private SharedPreferences sharedPref;
	private String titleStr;
	private SharedPreferences.Editor editor;
	private String cid;
	private String className;
	private AVUser currentUser;
	private SwipeRefreshLayout pageViewMainThisViewPullListView;
	private FloatingActionButton fab;
	private String THE_INSTALLATION_ID="0";
	int lastScollPosition=0,nowScollPosition=0;

	private class CommentClass
	{
		String commentMessage;
		String time;
		AVObject user;
		Bitmap userBmp;
		private AVObject avo;
		int num;

		public CommentClass(String mMessage, String mTime, AVObject thisUser)
		{
			commentMessage = mMessage;
			time = mTime;
			user = thisUser;
		}

		public void setNum(int n)
		{
			num = n;
		}

		public int getNum()
		{
			return this.num;
		}

		public AVObject getObj()
		{
			return avo;
		}
		public void setObj(AVObject newAvo)
		{
			this.avo = newAvo;
		}

		public Bitmap getUserImg()
		{return userBmp;}

		public void setUserImg(Bitmap newUsrImg)
		{userBmp = newUsrImg;}

		public AVObject getUser()
		{
			return user;
		}

		public void setUser(AVObject newUsr)
		{
			user = newUsr;
		}

		public String getMessage()
		{
			return commentMessage;
		}
		public String getTime()
		{
			return time;
		}
	}

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_layout);

		AVOSCloud.initialize(this, "rTL0VObhRrmgAPdG7fWrd0lr", "xjLm6hYPJHxxtkdEfJkpPomD");

		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		Intent intent = getIntent();
		cid = intent.getStringExtra("cid");
		titleStr = intent.getStringExtra("title");
		size = numberOfItems;
		THE_INSTALLATION_ID = "0";
		className = intent.getStringExtra("ClassName");

		if (AVUser.getCurrentUser() != null) THE_INSTALLATION_ID = AVUser.getCurrentUser().getString("InstaId");

		AVQuery<AVObject> getCid=new AVQuery<AVObject>(className);
		getCid.getInBackground(cid, new GetCallback<AVObject>() {
				@Override
				public void done(AVObject avObject, AVException e)
				{
					if (e == null)
					{
						AVObject thisavo = avObject.getAVObject("user");
						if (thisavo != null)
							thisavo.fetchIfNeededInBackground(new GetCallback() {
									@Override
									public void done(AVObject p1, AVException p2)
									{
										// TODO: Implement this method
										if (p2 == null)
										{
											THE_INSTALLATION_ID = p1.getString("InstaId");
										}
										else Log.i("ERR", p2.toString());
									}
								});
					}
					else Log.i("ERR", e.toString());
				}
			});
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

		currentUser = AVUser.getCurrentUser();
		appHelper = new ApplicationHelper();
		sharedPref = this.getSharedPreferences("SDnews", 0);
		editor = sharedPref.edit();
		list = new ArrayList<CommentClass>();

		//************************************************
		//显示帮助提示
		//************************************************
		boolean comment_help=sharedPref.getBoolean("COMMENT_HELP", true);
		if (comment_help)
		{
			editor.putBoolean("COMMENT_HELP", false);
			editor.commit();
			Intent intent1 = new Intent();
			intent1.setClass(this, HelpActivity.class);
			intent1.putExtra("num", 3);
			startActivity(intent1);
		}

		android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.commenttoolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					finish();
				}
			});
		fab = (FloatingActionButton)findViewById(R.id.commentfab);
		fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)
				{
					addcommentClick(null);
				}
			});


		pageViewMainThisViewPullListView = new SwipeRefreshLayout(this);

		listView = new ListView(this);

		pageViewMainThisViewPullListView.setLayoutParams(new SwipeRefreshLayout.LayoutParams(SwipeRefreshLayout.LayoutParams.MATCH_PARENT, SwipeRefreshLayout.LayoutParams.WRAP_CONTENT));
		pageViewMainThisViewPullListView.addView(listView);
		pageViewMainThisViewPullListView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh()
				{
					pageNum = 0;
					ifreload = true;
					CommentActivity.this.loadItemOfListView();
				}
			});


	    listView.setFastScrollEnabled(false);
	    listView.setFastScrollAlwaysVisible(false);
		adapter = new ListViewAdapter(this, list);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new OnScrollListener() {
				public void onScrollStateChanged(AbsListView p1, int p2)
				{

				}

				public void onScroll(AbsListView p1, int p2, int p3, int p4)
				{

					if (!ifOver)
					{
						int pos = listView.getLastVisiblePosition();
						nowScollPosition = listView.getFirstVisiblePosition();
						if (nowScollPosition - lastScollPosition >= 1) fab.hide();
						else if (nowScollPosition - lastScollPosition <= -1) fab.show();
						lastScollPosition = nowScollPosition;
						if (ifLoading == true)
						{
							if (pos >= list.size() - 1)
							{
								CommentActivity.this.loadItemOfListView();
							}
						}
					}
				}
			});

		final String itemsStr[]={"删除","查看用户","回复评论","复制文本"};
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
				{
					android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(CommentActivity.this)
						.setTitle("操作")
						.setItems(itemsStr, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								if (which == 0)
								{
									if ((AVUser.getCurrentUser().getInt("level") == 0) || (AVUser.getCurrentUser().getInt("level") == 3) || (AVUser.getCurrentUser().getObjectId().equals(list.get(position).getUser().getObjectId())))
									{
										list.get(position).getObj().deleteInBackground(new DeleteCallback() {
												@Override
												public void done(AVException e)
												{
													if (e == null)
													{
														Snackbar.make(fab, "删除成功", Snackbar.LENGTH_SHORT).show();
														doRefresh();
													}
													else
														Snackbar.make(fab, "删除失败", Snackbar.LENGTH_SHORT).show();
												}
											});
									}
									else
										Snackbar.make(fab, "没有权限", Snackbar.LENGTH_SHORT).show();
								}
								else if (which == 1)
								{
									Intent intent = new Intent();
									intent.setClass(CommentActivity.this, UserActivity.class);
									intent.putExtra("cid", list.get(position).getUser().getObjectId());
									intent.putExtra("type", 1);
									startActivity(intent);
								}
								else if (which == 2)
								{
									addComment("回复#" + list.get(position).getNum() + "楼:\n\n");
								}else if (which == 3)
								{
									ClipboardManager cmb = (ClipboardManager)CommentActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
									cmb.setText(list.get(position).getMessage());
									Toast.makeText(CommentActivity.this, "已复制", Toast.LENGTH_SHORT).show();
								}

							}
						}).show();
				}
			});



		pageViewMainThisViewPullListView.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
		LinearLayout fatherLayout = (LinearLayout) findViewById(R.id.swipe_container);
		fatherLayout.addView(pageViewMainThisViewPullListView);

		onLoadMore();

	}

	public void doRefresh()
	{
		ifreload = true;
		pageNum = 0;
		pageViewMainThisViewPullListView.setRefreshing(true);
		CommentActivity.this.loadItemOfListView();
	}


	boolean ifreload = false;


	public void addcommentClick(View v)
	{
		addComment("");
	}

	public void addComment(final String addToStart)
	{

		if (currentUser != null)
		{
			final View thisview;
			String ttlStr="";
			if (addToStart.length() > 1) ttlStr = "回复评论";
			else ttlStr = "添加评论";

			thisview = LayoutInflater.from(CommentActivity.this).inflate(R.layout.comment_dialog, null);
			android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(CommentActivity.this).
				setTitle(ttlStr).
				setView(thisview).
				setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which)
					{// TODO Auto-generated method stub
						final EditText ett = (EditText) thisview.findViewById(R.id.commentedittext1);
						final String textstring = addToStart + ett.getText().toString();

						long lastTime=sharedPref.getLong("EditTime", 0);
						Log.i("TIM", System.currentTimeMillis() + "/" + lastTime);
						if (System.currentTimeMillis() - lastTime >= 30 * 1000)
						{

							AVObject post = new AVObject(className + "_Comment");
							post.put("Title", textstring);
							post.put("user", AVObject.createWithoutData("_User", AVUser.getCurrentUser().getObjectId()));
							post.put("Point", AVObject.createWithoutData(className, cid));
							if (textstring.length() > 0)
							{
								post.saveInBackground(new SaveCallback() {
										@Override
										public void done(AVException p1)
										{
											// TODO: Implement this method
											if (p1 == null)
											{
												currentUser.put("score", currentUser.getInt("score") + 1);
												currentUser.saveInBackground(new SaveCallback() {
														@Override
														public void done(AVException p11)
														{
															// TODO: Implement this method
															if (p11 == null)
															{
																Snackbar.make(fab, "提交成功,积分+1：   " + textstring, Snackbar.LENGTH_SHORT).show();
																list.add(new CommentClass(textstring, "Just Now", AVUser.getCurrentUser()));
																adapter.notifyDataSetChanged();

																SharedPreferences.Editor editor = sharedPref.edit();
																editor.putLong("EditTime", System.currentTimeMillis());
																editor.commit();

																AVQuery pushQuery = AVInstallation.getQuery();
																pushQuery.whereEqualTo("installationId", THE_INSTALLATION_ID);
																if (THE_INSTALLATION_ID.length() != 0)
																	AVPush.sendMessageInBackground("您的帖子" + "\"" + titleStr + "\"" + "有新的评论", pushQuery, new SendCallback() {
																			@Override
																			public void done(AVException e)
																			{
																				if (e == null)
																					Log.i("MSG", "推送成功");
																				else
																					Log.i("ERR", e.toString());
																			}
																		});

																CommentActivity.this.doRefresh();
															}
															else
															{
																Snackbar.make(fab, "提交失败：   " + p11.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
															}
														}
													});
											}
											else
												Snackbar.make(fab, "提交失败：   " + p1.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();

										}


									});
							}
							else
								Snackbar.make(fab, "内容为空，提交失败", Snackbar.LENGTH_SHORT).show();
						}
						else Snackbar.make(fab, "发表评论过于频繁，先休息" + (30 - (System.currentTimeMillis() - lastTime) / 1000) + "秒吧亲~", Snackbar.LENGTH_SHORT).show();
					}
				}).
				setNegativeButton("取消", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which)
					{
						// TODO Auto-generated method stub
					}
				}).show();
		}
		else
		{

			Snackbar.make(fab, "请先登录", Snackbar.LENGTH_SHORT).setAction("立即登录", new View.OnClickListener() {
					@Override
					public void onClick(View v)
					{
						Intent intent = new Intent();
						intent.setClass(CommentActivity.this, InitActivity.class);
						startActivity(intent);
					}
				}).show();
		}
	}

	public void onLoadMore()
	{

		ifLoading = false;
		new Handler().postDelayed(new Runnable() {
				public void run()
				{
					looper_number = 0;
					loadItemOfListView();
				}
			}, 100);
	}

	int looper_number = 0;
	boolean ifOver = false;

	public void loadItemOfListView()
	{
		size--;
		looper_number++;

		AVQuery<AVObject> query = new AVQuery<AVObject>(className + "_Comment");
		query.whereEqualTo("Point", AVObject.createWithoutData(className, cid));
		query.orderByDescending("updatedAt");
		if (!appHelper.isNetworkConnected(this))
			query.setCachePolicy(AVQuery.CachePolicy.CACHE_ONLY);
		else
		{
			query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		}
		query.setSkip(pageNum * 10);
		query.setLimit(10);
		pageNum++;
		query.findInBackground(new FindCallback<AVObject>() {
				@Override
				public void done(List<AVObject> avObjects, AVException e)
				{
					if (e == null) 
						if (avObjects != null)
						{

							for (int i = 0; i <= avObjects.size() - 1; i++)
							{
								if (ifreload)
								{
									list.clear();
									ifreload = false;
								}

								list.add(new CommentClass(avObjects.get(i).getString("Title"), avObjects.get(i).getUpdatedAt().toLocaleString(), null));
								final int thisIndex = list.size() - 1;
								list.get(thisIndex).setObj(avObjects.get(i));
								list.get(thisIndex).setNum(avObjects.size() - i);
								AVObject thisavo = avObjects.get(i).getAVObject("user");
								thisavo.fetchIfNeededInBackground(new GetCallback() {

										@Override
										public void done(AVObject p1, AVException p2)
										{
											// TODO: Implement this method
											if (p2 == null)
											{
												list.get(thisIndex).setUser(p1);
												AVFile userImg = null;
												userImg = p1.getAVFile("Image");
												if (userImg != null)
													userImg.getDataInBackground(new GetDataCallback() {
															@Override
															public void done(final byte[] bytes, AVException e)
															{
																new Thread(new Runnable() {
																		@Override
																		public void run()
																		{
																			list.get(thisIndex).setUserImg(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
																		}
																	}).start();
															}

														});
												adapter.notifyDataSetChanged();
											}
										}

									});
							}
							adapter.notifyDataSetChanged();
							ifLoading = true;
						}
						else
						{
							ifOver = true;
						}

					pageViewMainThisViewPullListView.setRefreshing(false);
				}
			});
	}


	public class ListViewAdapter extends BaseAdapter
	{
		private Context context;
		private ArrayList<CommentClass> list;

		public ListViewAdapter(Context context, ArrayList<CommentClass> list)
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
			type_1_view = new Type_1_Holder();

			if (position < list.size())
			{
				if (convertView == null)
				{

					convertView = LayoutInflater.from(context).inflate(R.layout.comments_layout_item, null);
					type_1_view.list_text = (TextView) convertView.findViewById(R.id.comment_textView1);
					type_1_view.userText = (TextView) convertView.findViewById(R.id.commentslayoutitemUserNameTextView2);
					type_1_view.userlevelTextView = (TextView) convertView.findViewById(R.id.commentslayoutitemUserlevelTextView2);
					type_1_view.timeText = (TextView) convertView.findViewById(R.id.commentslayoutitemTextView1);
					type_1_view.userLayout = (RelativeLayout) convertView.findViewById(R.id.commentUserLayout);
					type_1_view.userImgView = (ImageView) convertView.findViewById(R.id.commentUserImageView);
					type_1_view.numberTv = (TextView)convertView.findViewById(R.id.commentslayoutitemTextViewNumber2);

					convertView.setTag(type_1_view);
				}
				else
				{
					type_1_view = (Type_1_Holder) convertView.getTag();
				}
				type_1_view.list_text.setText(list.get(position).getMessage());
				type_1_view.timeText.setText(list.get(position).getTime());
				type_1_view.numberTv.setText("#" + list.get(position).getNum());
				AVUser thisAvuser = (AVUser) list.get(position).getUser();
				if (thisAvuser != null)
				{
					if (list.get(position).getUserImg() != null)
					{
						type_1_view.userImgView.setImageBitmap(list.get(position).getUserImg());
					}
					type_1_view.userText.setText(thisAvuser.getUsername());
					int sco = thisAvuser.getInt("score");
					int flg = thisAvuser.getInt("level");
					flg = flg % 4;

					if (flg != 2)
					{
						type_1_view.userlevelTextView.setText(CommentActivity.this.getResources().getStringArray(R.array.user_level_list)[flg]);
						type_1_view.userlevelTextView.setBackgroundColor(Color.parseColor(CommentActivity.this.getResources().getStringArray(R.array.user_level_color)[flg]));
						type_1_view.userlevelTextView.setTextColor(Color.parseColor("#ffffff"));
					}
					else
					{

						int userScore = thisAvuser.getInt("score");
						int userScoreLevel = ApplicationHelper.getUserScoreLevel(userScore);
						type_1_view.userlevelTextView.setText(CommentActivity.this.getResources().getStringArray(R.array.user_score_level_list)[userScoreLevel]);
						type_1_view.userlevelTextView.setBackgroundColor(Color.parseColor(CommentActivity.this.getResources().getStringArray(R.array.user_score_level_color)[userScoreLevel]));
						type_1_view.userlevelTextView.setTextColor(Color.parseColor("#ffffff"));

					}

				}
			}
			else type_1_view.userText.setText(" ");



			return convertView;
		}

		class Type_1_Holder
		{
			TextView list_text;
			TextView timeText;
			TextView userText;
			TextView userlevelTextView;
			ImageView userImgView;
			RelativeLayout userLayout;
			TextView numberTv;
		}

	}


	public void onRefresh()
	{
		// TODO Auto-generated method stub

	}
}
