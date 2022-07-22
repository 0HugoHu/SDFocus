package com.sdzx.news;
import android.content.*;
import android.graphics.*;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.*;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.*;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.avos.avoscloud.*;
import com.sdzx.news.*;
import com.sdzx.tools.*;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;
import com.tencent.open.utils.Util;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import com.pnikosis.materialishprogress.*;


public class ReaderActivity extends SwipeBackActivity {
	private String cid,className;
	private TextView titleView,timeView,userNameView,numOfCommentTv;
	private ImageView readerImage[],userImg;
	private Bitmap bmp;
	private String userString="0";
	private int picId;
	private NestedScrollView scollView;
	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;
	private LinearLayout readerLinearLayout;
	private boolean ifLoadImg;
	private String fileName[]= new String[40];
	private String messageMainString;
	private AVObject readAvo;
	private ApplicationHelper appHelper;
	private ImageView toolbarIv;
	int lastScollPosition=0,nowScollPosition=0;
	private boolean haveLoadImg=false;
	private FloatingActionButton fab;
	private AVUser thisWriterAvuer;
	private String allText="";
	private String imgShareStr="";

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reader_layout);



		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		editor=sharedPref.edit();


		//************************************************
		//显示帮助提示
		//************************************************
		boolean reader_help=sharedPref.getBoolean("READER_HELP",true);
		if (reader_help) {
			editor.putBoolean("READER_HELP", false);
			editor.commit();
			Intent intent = new Intent();
			intent.setClass(this, HelpActivity.class);
			intent.putExtra("num", 1);
			startActivity(intent);
		}


		readerImage=new ImageView[20];
		appHelper=new ApplicationHelper();
		toolbarIv=(ImageView)findViewById(R.id.readerIvImage);
		scollView=(NestedScrollView)findViewById(R.id.readerlayoutScollView1);
		scollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
			@Override
			public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
				nowScollPosition = scrollY;
				//Log.i("SCL", "" + nowScollPosition + "/" + lastScollPosition);
				if (nowScollPosition - lastScollPosition > 20) fab.hide();
				else if (nowScollPosition - lastScollPosition <= -20) fab.show();
				lastScollPosition = nowScollPosition;
			}
		});

		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
			getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);


		android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.readertoolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		toolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});


		fab = (FloatingActionButton) findViewById(R.id.readerfab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onReadComment(null);
			}
		});
		fab.hide();
		ifLoadImg=sharedPref.getBoolean("IfLoadImg", true);

		titleView=(TextView)findViewById(R.id.readerlayoutTitleTextView1);
		numOfCommentTv=(TextView)findViewById(R.id.readerlayoutNumOfCommentsTextView2);
		timeView=(TextView)findViewById(R.id.readerlayoutitemTextView1);
		readerLinearLayout=(LinearLayout)findViewById(R.id.readerActivityLinearLayout);
		userNameView=(TextView)findViewById(R.id.readerlayoutitemUserNameTextView2);
		userImg=(ImageView)findViewById(R.id.readerUserImageView);

		titleView.setTextIsSelectable(true);

		Intent intent = getIntent();
		cid = intent.getStringExtra("cid");
		className=intent.getStringExtra("ClassName");
		final int idx=ApplicationHelper.findIndexOfClasses(className,this);
		if (idx!=-1) setTitle(getResources().getStringArray(R.array.categories_name)[idx]);
		else setTitle("详情");



		AVQuery<AVObject> numQue = new AVQuery<AVObject>(className + "_Comment");
		numQue.whereEqualTo("Point", AVObject.createWithoutData(className, cid));
		numQue.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> list, AVException e) {
				if (e==null) {
					numOfCommentTv.setText("" + list.size());
				}
			}
		});



		AVQuery<AVObject> query = new AVQuery<AVObject>(className);
		if (!appHelper.isNetworkConnected(this)){
			query.setCachePolicy(AVQuery.CachePolicy.CACHE_ONLY);
			}
		else{
			query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		}

		query.getInBackground(cid,new GetCallback<AVObject>()
		{
			@Override
			public void done(AVObject avObject1, AVException e) {
				if (e == null)
				{
					readAvo=avObject1;
					titleView.setText(avObject1.getString("Title"));
					allText=avObject1.getString("Title")+"\n\n";
					allText+="本文分享自"+ReaderActivity.this.getResources().getString(R.string.share_text)+"\n\n";
					AVObject thisavo = avObject1.getAVObject("user");
					if (thisavo != null)
						thisavo.fetchIfNeededInBackground(new GetCallback() {
							@Override
							public void done(AVObject p1, AVException p2) {
								// TODO: Implement this method
								if (p2 == null) {
									thisWriterAvuer=(AVUser)p1;
									userString=thisWriterAvuer.getObjectId();
									userNameView.setText(""+thisWriterAvuer.getUsername());
									AVFile imgFile=p1.getAVFile("Image");
									if (imgFile!=null) imgFile.getDataInBackground(new GetDataCallback() {
										@Override
										public void done(byte[] bytes, AVException e) {
											userImg.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
										}
									});
								} else Log.i("ERR",p2.toString());
							}
						});

					fab.show();
					timeView.setText(avObject1.getUpdatedAt().toLocaleString());
					String messageString=avObject1.getString("Message");
					messageMainString=messageString;
					String messageStrings[]=messageString.split("\\^");
					int lenOfMes=messageStrings.length;
					for (int j=0;j<lenOfMes;j++){
						if (messageStrings[j].startsWith("pic=")){
							if (ifLoadImg){
								final int jfnl=j;
								final String imgId=messageStrings[j].substring(4);

								final ImageView newImgView=new ImageView(ReaderActivity.this);
								newImgView.setScaleType(ImageView.ScaleType.FIT_XY);
								newImgView.setAdjustViewBounds(true);
								newImgView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
								final View pbView=LayoutInflater.from(ReaderActivity.this).inflate(R.layout.reader_text, null);
								readerLinearLayout.addView(pbView);
								readerLinearLayout.addView(newImgView);

								if (appHelper.fileIsExists("/SDFocus/cache/"+imgId+".cache")){
								new Thread(){
									@Override
									public void run(){
										Bitmap mBitmap;
										mBitmap=appHelper.convertToBitmap("/SDFocus/cache/"+imgId+".cache",80);
										if (imgShareStr.length()==0) imgShareStr=ApplicationHelper.getRootPath()+ "/SDFocus/cache/"+imgId+".cache";
										newImgView.setImageBitmap(mBitmap);
										readerLinearLayout.removeView(pbView);
										if (haveLoadImg==false) {
											toolbarIv.setImageBitmap(mBitmap);
											haveLoadImg=true;
										}
										newImgView.setOnClickListener(new OnClickListener() {

											public void onClick(View v) {
												// TODO Auto-generated method stub

												Intent intent = new Intent();
												intent.putExtra("cid", imgId);
												intent.setClass(ReaderActivity.this, PictureActivity.class);
												startActivity(intent);
											}
										});

									}
								}.run();

							} else{

								AVQuery<AVObject> query = new AVQuery<AVObject>("images");
								query.getInBackground(imgId, new GetCallback<AVObject>() {

									@Override
									public void done(AVObject arg0, AVException arg1) {
										// TODO Auto-generated method stub
										AVFile avoFile=arg0.getAVFile("File");
										avoFile.getDataInBackground(new GetDataCallback() {

											@Override
											public void done(byte[] arg0, AVException arg1) {
												// TODO Auto-generated method stub
												Bitmap newBmp=BitmapUtils.compressByKByte(arg0, 200, 500, 500);
												newImgView.setImageBitmap(newBmp);
												readerLinearLayout.removeView(pbView);
												if (haveLoadImg==false) {
													toolbarIv.setImageBitmap(newBmp);
													haveLoadImg=true;
												}
												final byte[] fnlData=arg0;
												class  MySaveHandler  extends  Handler{
											        public  MySaveHandler() {}
											         public  MySaveHandler(Looper looper){
											             super (looper);
											        }

											         public   void  handleMessage(Message msg){
											        	 //....这里运行耗时的过程
											        	 appHelper.saveMyBitmap(BitmapUtils.compressByKByte(fnlData, 200, 1500, 1500),"/SDFocus/cache/"+imgId+".cache",false);

											        }
											    }

												HandlerThread handlerThread = new  HandlerThread( "backgroundThread" );
												handlerThread.start();
												MySaveHandler myHandler = new  MySaveHandler(handlerThread.getLooper());
												 Message msg = myHandler.obtainMessage();
												//....此处对msg进行赋值，可以创建一个Bundle进行承载
												msg.sendToTarget();

												newImgView.setOnClickListener(new OnClickListener() {

													public void onClick(View v) {
														// TODO Auto-generated method stub
														Intent intent = new Intent();
														intent.putExtra("cid", imgId);
														intent.setClass(ReaderActivity.this, PictureActivity.class);
														startActivity(intent);
													}
												});

											}
										}, new ProgressCallback() {

											@Override
											public void done(Integer arg0) {
												// TODO Auto-generated method stub

											}
										});


									}
								});
							}
							}
						}
						else {
							TextView newTv=new TextView(ReaderActivity.this);
							newTv.setTextIsSelectable(false);
							newTv.setTextSize(15);
							newTv.setTextColor(Color.parseColor("#404040"));
							newTv.setLineSpacing(2, (float) 1.5);
							newTv.setText(messageStrings[j]);

							allText+=messageStrings[j];

							newTv.setOnLongClickListener(new OnLongClickListener() {
								@Override
								public boolean onLongClick(View v) {

									final String itemsStr[]={"复制全文","分享"};
									android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(ReaderActivity.this)
											.setTitle("操作")
											.setItems(itemsStr, new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which)
												{
													if (which == 0)
													{
														ClipboardManager cmb = (ClipboardManager)ReaderActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
														cmb.setText(allText);

														Snackbar.make(fab,"已复制",Snackbar.LENGTH_SHORT).show();
													}else if (which==1){
														onShareClick(null);
													}
												}
											}).show();

									return false;
								}
							});
							readerLinearLayout.addView(newTv);
						}
					}
				}
			}
		});
	}


	public void onShareClick(View v){
		ApplicationHelper.shareMsg(ReaderActivity.this,"分享",allText,allText,imgShareStr);
	}

	public void onThisUserClick(View v){
		if (!userString.equals("0")) {
			Intent intent = new Intent();
			intent.setClass(ReaderActivity.this, UserActivity.class);
			intent.putExtra("cid", userString);
			intent.putExtra("type", 1);
			startActivity(intent);
		}
	}

	public void onReadComment(View v){

		Intent intent = new Intent();
		intent.putExtra("cid",cid+"");
		intent.putExtra("ClassName", className);
		String ttl=titleView.getText().toString();
		if (ttl.length()<=5)
			intent.putExtra("title", ttl + "");
		else intent.putExtra("title", ttl.substring(0,4) + "...");
		intent.setClass(ReaderActivity.this, CommentActivity.class);
		startActivity(intent);

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reader, menu);
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
		if (id == R.id.action_share){
			onShareClick(null);
		}else if (id==R.id.action_delete_reader){

			android.support.v7.app.AlertDialog dialog1 = new android.support.v7.app.AlertDialog.Builder(ReaderActivity.this)
					.setTitle("确定？")
					.setMessage("真的要删除吗？")
					.setPositiveButton("是的", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {


							if ((AVUser.getCurrentUser().getInt("level") == 0) || (AVUser.getCurrentUser().getInt("level") == 3) || (AVUser.getCurrentUser().getObjectId().equals(thisWriterAvuer.getObjectId()))) {

								AVQuery<AVObject> deleteQuery = new AVQuery<AVObject>("main");
								deleteQuery.whereEndsWith("idstring", cid);
								deleteQuery.findInBackground(new FindCallback<AVObject>() {
									@Override
									public void done(List<AVObject> list, AVException e) {
										if (e == null)
											if (list.size() > 0)
												list.get(0).deleteInBackground(new DeleteCallback() {
													@Override
													public void done(AVException e2) {
														if (e2 != null)
															Snackbar.make(fab, "删除未完成", Snackbar.LENGTH_SHORT).show();
													}
												});
									}
								});
								if (readAvo != null)
									readAvo.deleteInBackground(new DeleteCallback() {
										@Override
										public void done(AVException e) {
											if (e == null) {
												Toast.makeText(ReaderActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
												ReaderActivity.this.finish();
											} else
												Snackbar.make(fab, "删除失败", Snackbar.LENGTH_SHORT).show();
										}
									});


							} else Snackbar.make(fab, "没有权限", Snackbar.LENGTH_SHORT).show();


						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).show();
		}else if (id==R.id.action_jingxuan_reader){
			if ((AVUser.getCurrentUser().getInt("level") == 0) || (AVUser.getCurrentUser().getInt("level") == 3)) {

				AVObject postb = new AVObject("main");
				postb.put("idstring", cid);
				postb.put("class", className);
				postb.saveInBackground(new SaveCallback() {
				@Override
				public void done(AVException e) {
						if (e == null) {
							Snackbar.make(fab, "成功！", Snackbar.LENGTH_SHORT).show();
						} else
							Snackbar.make(fab, "出错啦！", Snackbar.LENGTH_SHORT).show();
					}
				});
			} else Snackbar.make(fab, "没有权限", Snackbar.LENGTH_SHORT).show();
		}else if (id==R.id.action_move_reader){
			final String ids[]=ApplicationHelper.concat(ReaderActivity.this.getResources().getStringArray(R.array.categories_id),ReaderActivity.this.getResources().getStringArray(R.array.shetuan_id));
			final String names[]=ApplicationHelper.concat(ReaderActivity.this.getResources().getStringArray(R.array.categories_name),ReaderActivity.this.getResources().getStringArray(R.array.shetuan_name));
			if (AVUser.getCurrentUser()!=null)
			if ((AVUser.getCurrentUser().getInt("level") == 0) || (AVUser.getCurrentUser().getInt("level") == 3)) {
				android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(ReaderActivity.this)
					.setTitle("选择分类")
					.setItems(names, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();

									if (readAvo != null) {
										final AVObject postb = new AVObject(ids[which]);
										postb.put("Title", readAvo.getString("Title"));
										postb.put("Summary", readAvo.getString("Summary"));
										postb.put("Message", readAvo.getString("Message"));
										postb.put("user", readAvo.getAVObject("user"));

										AVFile file;
										file = readAvo.getAVFile("Thumb");
										postb.put("Thumb", file);
										postb.saveInBackground(new SaveCallback() {
											@Override
											public void done(AVException arg0) {
												// TODO Auto-generated method stub
												if (arg0 == null) {
													readAvo.deleteInBackground(new DeleteCallback() {
														@Override
														public void done(AVException e) {
															if (e == null) {
																Toast.makeText(ReaderActivity.this, "转移成功", Toast.LENGTH_SHORT).show();
																ReaderActivity.this.finish();
															} else {
																Snackbar.make(fab, "提交失败" + e.toString(), Snackbar.LENGTH_SHORT).show();
															}
														}
													});

													AVQuery<AVObject> deleteQuery = new AVQuery<AVObject>("main");
													deleteQuery.whereEndsWith("idstring",cid);
													deleteQuery.findInBackground(new FindCallback<AVObject>() {
														@Override
														public void done(List<AVObject> list, AVException e) {
															if (e == null)
																if (list.size() > 0)
																	list.get(0).deleteInBackground(new DeleteCallback() {
																		@Override
																		public void done(AVException e2) {
																			if (e2 != null)
																				Snackbar.make(fab, "删除未完成", Snackbar.LENGTH_SHORT).show();
																		}
																	});
														}
													});

												} else {
													Snackbar.make(fab, "提交失败" + arg0.toString(), Snackbar.LENGTH_SHORT).show();
												}
											}
										});

									}
								}
							}
					).create();
				dialog.show();
			} else
				Snackbar.make(fab, "没有权限", Snackbar.LENGTH_SHORT).show();
		}
		return super.onOptionsItemSelected(item);
	}


}
