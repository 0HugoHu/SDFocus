package com.sdzx.news;
import android.R.integer;
import android.app.*;
import android.os.*;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.content.*;
import android.net.*;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;

import java.io.*;

import android.widget.*;
import android.database.*;
import android.provider.*;

import java.util.*;

import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenImages;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.kbeanie.imagechooser.exceptions.ChooserException;
import com.sdzx.tools.ApplicationHelper;
import com.sdzx.tools.BitmapUtils;
import com.avos.avoscloud.*;
import com.sdzx.tools.FileSizeUtils;


public class EditActivity extends AppCompatActivity implements ImageChooserListener
{

	private FloatingActionButton fab;
	ApplicationHelper helper=new ApplicationHelper();
	private String thumbBmpString;
	LinearLayout editLayout;
	Vector <String> pathStrings=new Vector<String>();
	Vector <TextView> textViews=new Vector<TextView>();
	int thisi=0;
	private SharedPreferences.Editor editor;
	private SharedPreferences sharedPref;
	boolean havePic=false;
	EditText titleEt,messageEt,summaryEt;
	int numberOfItems=3;
	boolean flag=true;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_layout);

		android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.edittoolbar);
		setSupportActionBar(toolbar);
		setTitle("发帖");

		sharedPref = this.getSharedPreferences("SDnews", 0);
		editor = sharedPref.edit();
		fab = (FloatingActionButton) findViewById(R.id.editfab);

		//************************************************
		//显示帮助提示
		//************************************************
		boolean edit_help=sharedPref.getBoolean("EDIT_HELP", true);
		if (edit_help)
		{
			editor.putBoolean("EDIT_HELP", false);
			editor.commit();
			Intent intent = new Intent();
			intent.setClass(this, HelpActivity.class);
			intent.putExtra("num", 4);
			startActivity(intent);
		}

		editLayout = (LinearLayout)findViewById(R.id.editlayoutLinearLayout1);

		titleEt = (EditText)findViewById(R.id.titleEditText1);
		summaryEt = (EditText)findViewById(R.id.summaryEditText1);
		messageEt = (EditText)findViewById(R.id.messageEditText1);

	}
	int ifnl=0;

	@Override
	public void onBackPressed()
	{

		Snackbar.make(fab, "这会丢失数据，真的要退出？", Snackbar.LENGTH_LONG).setAction("是的", new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					EditActivity.this.finish();
				}
			}).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit, menu);
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
		if (id == R.id.action_image)
		{
			onGetImgClick(null);
		}

		return super.onOptionsItemSelected(item);
	}



	public void onSendClick(View v)
	{

		if ((!messageEt.getText().toString().equals("")) && (!titleEt.getText().toString().equals("")))
		{

			if (havePic)
			{

				if (summaryEt.getText().toString().equals(""))
				{
					String summaryWithoutPicStr = "";
					String totStr = messageEt.getText().toString();
					String messageStrings[] = totStr.split("\\^");
					int lenOfMes = messageStrings.length;
					for (int j = 0; j < lenOfMes; j++)
					{
						if (!messageStrings[j].startsWith("pic="))
						{
							summaryWithoutPicStr = summaryWithoutPicStr + messageStrings[j];
						}
					}
					if (summaryWithoutPicStr.length() > 61)
						summaryWithoutPicStr = summaryWithoutPicStr.substring(0, 55);
					summaryEt.setText(summaryWithoutPicStr);
				}

				if (summaryEt.getText().toString().length() >= 5)
				{


					final String ids[] = ApplicationHelper.concat(EditActivity.this.getResources().getStringArray(R.array.categories_id), EditActivity.this.getResources().getStringArray(R.array.shetuan_id));
					final String names[] = ApplicationHelper.concat(EditActivity.this.getResources().getStringArray(R.array.categories_name), EditActivity.this.getResources().getStringArray(R.array.shetuan_name));

					AVQuery<AVObject> newAvo = new AVQuery<AVObject>("Value");
					newAvo.whereEqualTo("ValueName", "IfEditAble");
					newAvo.findInBackground(new FindCallback<AVObject>() {
							@Override
							public void done(List<AVObject> list, AVException e)
							{
								if (e == null)
								{
									if (list.get(0).get("Value").equals("1"))
									{
										if (AVUser.getCurrentUser().getBoolean("IfEditAble"))
										{

											android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(EditActivity.this)
												.setTitle("选择分类")
												.setItems(names, new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int which)
													{
														dialog.dismiss();

														//if ((AVUser.getCurrentUser().getInt("level") == 0) || (AVUser.getCurrentUser().getInt("level") == 3) || (which == 1))
														//{

															long lastTime = sharedPref.getLong("EditTime", 0);
															Log.i("TIM", System.currentTimeMillis() + "/" + lastTime);
															if ((System.currentTimeMillis() - lastTime >= 300 * 1000) || (AVUser.getCurrentUser().getInt("level") == 0))
															{

																final AVObject postb = new AVObject(ids[which]);
																postb.put("Title", titleEt.getText().toString());
																postb.put("Summary", summaryEt.getText().toString());
																postb.put("Message", messageEt.getText().toString());
																postb.put("user", AVObject.createWithoutData("_User", AVUser.getCurrentUser().getObjectId()));
																postb.put("Number", numberOfItems);

																AVFile file = null;
																try
																{
																	file = AVFile.withAbsoluteLocalPath(thumbBmpString, thumbBmpString);
																}
																catch (FileNotFoundException ex)
																{
																	// TODO Auto-generated catch block
																	ex.printStackTrace();
																	Snackbar.make(fab, "提交失败" + ex.toString(), Snackbar.LENGTH_SHORT).show();
																}
																postb.put("Thumb", file);
																postb.saveInBackground(new SaveCallback() {
																		@Override
																		public void done(AVException arg0)
																		{
																			// TODO Auto-generated method stub
																			if (arg0 == null)
																			{
																				AVUser currentUser = AVUser.getCurrentUser();
																				currentUser.put("score", currentUser.getInt("score") + 8);
																				currentUser.saveInBackground(new SaveCallback() {
																						@Override
																						public void done(AVException p11)
																						{
																							// TODO: Implement this method
																							if (p11 == null)
																							{

																								SharedPreferences.Editor editor = sharedPref.edit();
																								editor.putLong("EditTime", System.currentTimeMillis());
																								editor.commit();


																								Toast.makeText(EditActivity.this, "提交成功，积分+8", Toast.LENGTH_SHORT).show();
																								finish();
																							}
																							else
																							{
																								Snackbar.make(fab, "提交失败：   " + p11.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
																							}
																						}
																					});


																			}
																			else
																			{
																				Snackbar.make(fab, "提交失败" + arg0.toString(), Snackbar.LENGTH_SHORT).show();
																			}
																		}
																	});
															}
															else
																Snackbar.make(fab, "发贴过于频繁，先休息" + (300 - (System.currentTimeMillis() - lastTime) / 1000) + "秒吧亲~", Snackbar.LENGTH_SHORT).show();
														//}
														//else
															//Snackbar.make(fab, "您没有权限在此发帖", Snackbar.LENGTH_SHORT).show();
													}
												}
											).create();
											dialog.show();
										}
										else
											Snackbar.make(fab, "您正在被禁言中", Snackbar.LENGTH_SHORT).show();
									}
									else
										Snackbar.make(fab, "现在不允许发贴", Snackbar.LENGTH_SHORT).show();
								}
								else
									Snackbar.make(fab, "网络出错", Snackbar.LENGTH_SHORT).show();
							}
						}
					);

				}
				else Snackbar.make(fab, "摘要太短了", Snackbar.LENGTH_SHORT).show();
				

			}
			else Snackbar.make(fab, "至少上传一张图片", Snackbar.LENGTH_SHORT).show();

		}
		else Snackbar.make(fab, "内容不能为空", Snackbar.LENGTH_SHORT).show();
	}
	private void reinitializeImageChooser()
	{
		imageChooserManager = new ImageChooserManager(this, chooserType, true);
		imageChooserManager.setImageChooserListener(this);
		imageChooserManager.reinitialize(filePath);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.i("ICA", "OnActivityResult");
		Log.i("ICA", "File Path : " + filePath);
		Log.i("ICA", "Chooser Type: " + chooserType);
		if (resultCode == RESULT_OK && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE))
		{
			if (imageChooserManager == null)
			{
				reinitializeImageChooser();
			}
			imageChooserManager.submit(requestCode, data);
		}
	}

    @Override
    public void onImageChosen(final ChosenImage image)
	{
		runOnUiThread(new Runnable() {

				@Override
				public void run()
				{

					String oriPathStr = "";
					if (image == null)
					{
						return;
					}

					Log.i("ICA", "Chosen Image: O - " + image.getFilePathOriginal());
					Log.i("ICA", "Chosen Image: T - " + image.getFileThumbnail());
					Log.i("ICA", "Chosen Image: Ts - " + image.getFileThumbnailSmall());
					oriPathStr = image.getFilePathOriginal();

					final String pathString;
					if (FileSizeUtils.getFileOrFilesSize(oriPathStr, 2) > 500)
						pathString = image.getFileThumbnail();
					else pathString = oriPathStr;

					Toast.makeText(EditActivity.this, pathString, Toast.LENGTH_LONG).show();

					if (thisi == 0)
					{
						thumbBmpString = ApplicationHelper.getRootPath() + "/SDFocus/upload/" + "thumbMainTest.t";
						BitmapUtils.compressByKByte(oriPathStr, 30, 180, 180, thumbBmpString);
					}


					File thisFile = new File(pathString);
					final ImageView thisImg = new ImageView(EditActivity.this);
					thisImg.setImageBitmap(BitmapFactory.decodeFile(pathString));
					thisImg.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v)
							{
								Intent i = new Intent();
								i.setClass(EditActivity.this, PictureYulanActivity.class);
								i.putExtra("path", pathString);
								startActivity(i);
							}
						});
					thisImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
					thisImg.setLayoutParams(new LinearLayout.LayoutParams(ApplicationHelper.dip2px(EditActivity.this, 100), ApplicationHelper.dip2px(EditActivity.this, 100)));
					editLayout.addView(thisImg);
					pathStrings.add(pathString);
					havePic = true;
					thisi++;
					final AVObject postimg = new AVObject("images");
					EditActivity.this.setTitle("开始上传图片 ");

					final AVFile avFile = new AVFile(pathString, helper.getBytesFromFile(thisFile));
					avFile.saveInBackground(new SaveCallback() {
							@Override
							public void done(AVException e)
							{
								if (e == null)
								{
									String objectId = avFile.getObjectId();
									postimg.put("File", avFile);
									postimg.saveInBackground(new SaveCallback() {
											@Override
											public void done(AVException e)
											{
												if (e != null)
												{
													EditActivity.this.setTitle("上传失败");

												}
												else
												{
													String cidd = postimg.getObjectId();
													EditActivity.this.setTitle("上传成功");

													String preString = messageEt.getText().toString();
													messageEt.setText(preString.substring(0, index) + "^pic=" + cidd + "^" + preString.substring(index));
													messageEt.setSelection(index);
												}
											}
										});
								}
								else
								{
									Snackbar.make(fab, "提交失败" + e.toString(), Snackbar.LENGTH_SHORT).show();

								}
								setProgressBarIndeterminateVisibility(false);
							}
						}

						, new ProgressCallback() {
							@Override
							public void done(Integer percentDone)
							{
								LogUtil.log.d("uploading: " + percentDone);
							}
						});
				}
			});

    }


	private final int GET_IMG_CODE=1;
	private final int GET_THUMB_CODE=2;


	int index;

	public void onGetImgClick(View v)
	{
		if (messageEt.getSelectionStart() >= 0)
		{
			index = messageEt.getSelectionStart();
			/*
			 Intent intent = new Intent();
			 intent.setClass(EditActivity.this, ScanImgMainActivity.class);
			 startActivityForResult(intent, GET_IMG_CODE);
			 */
			Toast.makeText(this, "图片在" + index, Toast.LENGTH_SHORT).show();

			final String itemsStr[]={"从相册选择","拍摄照片"};
			android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(EditActivity.this)
				.setTitle("插入图片")
				.setItems(itemsStr, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						if (which == 0)
						{
							chooserType = ChooserType.REQUEST_PICK_PICTURE;
						}
						else
						{
							chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
						}
						imageChooserManager = new ImageChooserManager(EditActivity.this, chooserType, true);
						imageChooserManager.setImageChooserListener(EditActivity.this);
						imageChooserManager.clearOldFiles();
						try
						{
							filePath = imageChooserManager.choose();
						}
						catch (ChooserException e)
						{
							e.printStackTrace();
						}
					}
				}).show();

		}
		else
		{
			Snackbar.make(fab, "请点击要上传的位置", Snackbar.LENGTH_SHORT).show();
		}

	}
	ImageChooserManager imageChooserManager;
	int chooserType;
	String filePath;

    private byte[] readFile(File file)
	{
        RandomAccessFile rf = null;
        byte[] data = null;
        try
		{
            rf = new RandomAccessFile(file, "r");
            int length = (int)rf.length();
            if (length >= 5 * 1024 * 1024)
			{
                return null;
            }
            data = new byte[length];
            rf.readFully(data);
        }
		catch (Exception exception)
		{
            exception.printStackTrace();
        }
        return data;
    }



	@Override
	public void onError(String s)
	{

	}

	@Override
	public void onImagesChosen(ChosenImages chosenImages)
	{

	}


}
