package com.sdzx.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.*;
import android.os.*;
import android.preference.PreferenceManager;
import android.util.Log;
import java.io.*;
import java.net.*;
import java.util.List;

import android.text.*;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.sdzx.news.R;
import com.sdzx.news.HelpActivity;

public class ApplicationHelper
{

	public static void shareMsg(Context context, String activityTitle, String msgTitle, String msgText,
								String imgPath) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		if (imgPath == null || imgPath.equals("")) {
			intent.setType("text/plain"); // 纯文本
		} else {
			File f = new File(imgPath);
			if (f != null && f.exists() && f.isFile()) {
				intent.setType("image/png");
				Uri u = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, u);
			}else intent.setType("text/plain"); // 纯文本
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
		intent.putExtra(Intent.EXTRA_TEXT, msgText);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(Intent.createChooser(intent, activityTitle));
	}

	static public Bitmap saveViewBitmap(View view) {
// get current view bitmap
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache(true);
		Bitmap bitmap = view.getDrawingCache(true);

		Bitmap bmp = duplicateBitmap(bitmap);
		if (bitmap != null && !bitmap.isRecycled()) { bitmap.recycle(); bitmap = null; }
		// clear the cache
		view.setDrawingCacheEnabled(false);
		return bmp;
	}

	public static Bitmap duplicateBitmap(Bitmap bmpSrc)
	{
		if (null == bmpSrc)
		{ return null; }

		int bmpSrcWidth = bmpSrc.getWidth();
		int bmpSrcHeight = bmpSrc.getHeight();

		Bitmap bmpDest = Bitmap.createBitmap(bmpSrcWidth, bmpSrcHeight, Bitmap.Config.ARGB_8888);
		if (null != bmpDest) { Canvas canvas = new Canvas(bmpDest); final Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);

		canvas.drawBitmap(bmpSrc, rect, rect, null); }

		return bmpDest;
	}

	static public void showHelpNote(SharedPreferences sharedPref,Context ctx,int key){
		SharedPreferences.Editor editor;
		editor=sharedPref.edit();

		boolean main_help_1=sharedPref.getBoolean("MAIN_HELP_1",true);
		if (main_help_1) {
			editor.putBoolean("MAIN_HELP_1", false);
			editor.commit();
			Intent intent = new Intent();
			intent.setClass(ctx, HelpActivity.class);
			ctx.startActivity(intent);
		}
	}


	static public int ifTooWaterring(Context ctx,int frc){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		long lastTime=sharedPref.getLong("EditTime", 0);
		Log.i("TIM",System.currentTimeMillis()+"/"+lastTime);
		if (System.currentTimeMillis()-lastTime>=frc*1000) {
			return -1;
		}else return (frc-((int)(System.currentTimeMillis()-lastTime)/1000));
	}


	static public int getUserScoreLevel(int score){
		if (score<=10) return 0;
		else if (score<=20) return 1;
		else if (score<=40) return 2;
		else if (score<=80) return 3;
		else if (score<=160) return 4;
		else if (score<=320) return 5;
		else if (score<=640) return 6;
		else if (score<=1280) return 7;
		else if (score<=2560) return 8;
		else if (score<=5120) return 9;
		else return 10;
	}

	static public Bitmap Bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	public static String[] concat(String[] a, String[] b) {
		String[] c= new String[a.length+b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}
	public static int findIndexOfClasses(String classId,Context ctx){
		String classIdStrs[]=ctx.getResources().getStringArray(R.array.categories_id);
		int len=classIdStrs.length;
		for (int i=0;i<len;i++){
			if (classIdStrs[i].equals(classId)) return i;
		}
		return -1;
	}


	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	static public void checkForUpdates(Context ctx,boolean ifShowToast,int type)  //type:0:BUILD 1:内测 2:公开
	{
		/********************************************************************/
		/*********检查更新***************************************************/

		final Context ctxfnl=ctx;
		final boolean ifShowToastfnl=ifShowToast;
		AVQuery<AVObject> query0 = new AVQuery<AVObject>("update");
		query0.whereEqualTo("Latest", "Latest");
		if (type==0) query0.whereGreaterThanOrEqualTo("type",0);
		else if (type==1) query0.whereGreaterThanOrEqualTo("type",1);
		else query0.whereGreaterThanOrEqualTo("type",2);
		query0.findInBackground(new FindCallback<AVObject>() {
			public void done(List<AVObject> avObjects1, AVException e) {
				if ((e == null)&&(avObjects1.size()>0)) {
					final AVFile avFile = avObjects1.get(0).getAVFile("APK");
					final AVObject avoffnml = avObjects1.get(0);
					int thisCode = Integer.valueOf(ctxfnl.getString(R.string.version_code));
					int latestCode = avObjects1.get(0).getInt("VersionCode");
					final View thisview;
					thisview = LayoutInflater.from(ctxfnl).inflate(R.layout.update_dialog, null);
					if (latestCode > thisCode)
					{
						Dialog alertDialog = new AlertDialog.Builder(ctxfnl).
								setTitle("有新版本可以更新哦！").
								setView(thisview)
								.show();
						TextView logTView = (TextView) thisview.findViewById(R.id.textViewVersionLog);
						TextView nameTView = (TextView) thisview.findViewById(R.id.textViewVersionName);
						logTView.setText(avoffnml.getString("Log"));
						nameTView.setText(avoffnml.getString("Version") + "  (" + latestCode + ")");
						final ProgressBar percBar = (ProgressBar) thisview.findViewById(R.id.progressBar1percent);
						Button yesButton = (Button) thisview.findViewById(R.id.yesbutton1);
						yesButton.setOnClickListener(new View.OnClickListener() {

							public void onClick(View arg0)
							{
								// TODO Auto-generated method stub
								avFile.getDataInBackground(new GetDataCallback() {
									public void done(byte[] data, AVException e)
									{
										File apkFile = ApplicationHelper.getFileFromBytes(data, "/SDFocus/updates/" + avoffnml.getString("Version") + ".apk");

										Intent intent = new Intent(Intent.ACTION_VIEW);
										intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
										ctxfnl.startActivity(intent);
									}
								}, new ProgressCallback() {

									public void done(Integer percentDone)
									{
										//打印进度
										percBar.setProgress(percentDone);
									}
								});
							}
						});
					}
					else if (ifShowToastfnl) Toast.makeText(ctxfnl,"已是最新版本",Toast.LENGTH_SHORT).show();
				}
				else if (ifShowToastfnl) Toast.makeText(ctxfnl,"无可用更新",Toast.LENGTH_SHORT).show();
			}
		});


		/********************************************************************/
	}


	public String[] cateNameId={"yuedu","huodong","shetuan","tongzhi","xinwen"};

	public static Bitmap compressImage(Bitmap image)
	{

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while ((baos.toByteArray().length / 1024 > 100) && (options > 0))
		{	//循环判断如果压缩后图片是否大于200kb,大于继续压缩		
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.PNG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;//每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;
	}


	public static Bitmap cQuality(Bitmap bitmap)
	{
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        int beginRate = 100;
        //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bOut);
        while (bOut.size() / 1024> 100)
		{  //如果压缩后大于100Kb，则提高压缩率，重新压缩
            beginRate -= 10;
            bOut.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, beginRate, bOut);
        }
        ByteArrayInputStream bInt = new ByteArrayInputStream(bOut.toByteArray());
        Bitmap newBitmap = BitmapFactory.decodeStream(bInt);
        if (newBitmap != null)
		{
            return newBitmap;
        }
		else
		{
            return bitmap;
		}
	}


	public static Bitmap getImageAndCompressByByte(byte[] dataBt)
	{
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeByteArray(dataBt, 0, dataBt.length, newOpts);//此时返回bm为空
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		//现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 1920f;//这里设置高度为800f
		float ww = 1080f;//这里设置宽度为480f
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;//be=1表示不缩放
		if (w > h && w > ww)
		{//如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		}
		else if (w < h && h > hh)
		{//如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置缩放比例
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeByteArray(dataBt, 0, dataBt.length, newOpts);
		return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
	}



    /** *//**
     * 文件转化为字节数组

     */
    public static byte[] getBytesFromFile(File f)
	{
        if (f == null)
		{
            return null;
        }
        try
		{
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        }
		catch (IOException e)
		{
        }
        return null;
    }



	public static String getRootPath()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}


	/** 
     * 把字节数组保存为一个文件  
     * @EditTime 2007-8-13 上午11:45:56  
     */   
    public static File getFileFromBytes(byte[] b, String outputFile)
	{   
        BufferedOutputStream stream = null;   
        File file = null;   
        try
		{   
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + outputFile);   
            FileOutputStream fstream = new FileOutputStream(file);   
            stream = new BufferedOutputStream(fstream);   
            stream.write(b);   

        }
		catch (Exception e)
		{   
            e.printStackTrace();   
        }
		finally
		{   
            if (stream != null)
			{   
                try
				{   
                    stream.close();   
                }
				catch (IOException e1)
				{   
                    e1.printStackTrace();   
                }   
            }   
        }   
        return file;   
    }   


	/**
	 * 从网络获取并压缩图片
	 * urll：图片url，size：大小，一般是几十到几千
	 * **/
	public static Bitmap revitionImageSize(String urll, int size) throws IOException
	{
		URL bmpurl=new URL(urll);
		InputStream temp=bmpurl.openStream();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(temp, null, options);
		temp.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true)
		{
			if ((options.outWidth >> i <= size)
				&& (options.outHeight >> i <= size))
			{
			    temp = bmpurl.openStream();
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(temp, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}

	// 生成文件夹
	public static void makeRootDirectory(String filePath)
	{
	    File file = null;
	    try
		{
	        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + filePath);
	        if (!file.exists())
			{
	            file.mkdir();
	        }
	    }
		catch (Exception e)
		{
	        Log.i("error:", e + "");
	    }
	}

	public static void recycleImageView(View view){
		if(view==null) return;
		if(view instanceof ImageView){
			Drawable drawable=((ImageView) view).getDrawable();
			if(drawable instanceof BitmapDrawable){
				Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();
				if (bmp != null && !bmp.isRecycled()){
					((ImageView) view).setImageBitmap(null);
					bmp.recycle();
					bmp=null;
				}
			}
		}
	}



	/**
	 * 保存Bitmap到SD卡
	 * mBitmap：保存的对象，bitName：路径（已加SD卡目录）
	 * ***/
	public static void saveMyBitmap(Bitmap mBitmap, String bitName,boolean ifAddPng)
	{
		File f;
		if (ifAddPng)
        	f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + bitName + ".png");
		else f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + bitName);
		FileOutputStream fOut = null;
        try 
        {
        	fOut = new FileOutputStream(f);
        } 
        catch (FileNotFoundException e) 
        {
        	e.printStackTrace();
        }
        if ((mBitmap!=null)&&(fOut!=null)) mBitmap.compress(Bitmap.CompressFormat.PNG, 10, fOut);
		else return;
        try 
        {
        	fOut.flush();
        } 
        catch (IOException e) 
        {
        	e.printStackTrace();
        }
        try
		{
        	fOut.close();
        } 
        catch (IOException e) 
        {
        	e.printStackTrace();
        }
	}


	
	
	public static Bitmap compressImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;//只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(getRootPath()+ srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;//
		float ww = 480f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置采样率

		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
//		return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
		//其实是无效的,大家尽管尝试
		return bitmap;
	}
	
	
	public static Bitmap compressImageFromBytes(byte data[]) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;//只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeByteArray(data,1,data.length,newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;//
		float ww = 480f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置采样率

		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收

		bitmap = BitmapFactory.decodeByteArray(data,1,data.length,newOpts);
//		return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
		//其实是无效的,大家尽管尝试
		return bitmap;
	}
	
	
	
	
	/***
	 * 检查网络状况
	 * 返回true：有网络连接aaaaaa
	 * **/
	public static boolean isNetworkConnected(Context context)
	{  
	    if (context != null)
		{  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
				.getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
	        if (mNetworkInfo != null)
			{  
	            return mNetworkInfo.isAvailable();  
	        }  
	    }  
	    return false;  
	}


	public static  void filewriter(String path, String str)
	{
    	try
		{
			FileWriter fw=new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + path);
			BufferedWriter bWriter=new BufferedWriter(fw);
			bWriter.write(str);
			bWriter.flush();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


	public static Bitmap convertToBitmap(String path, int quality)
	{
		BitmapFactory.Options option = new BitmapFactory.Options();
		//option.inSampleSize = quality;
		String pathName=Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + path;
		Bitmap bm = BitmapFactory.decodeFile(pathName, option);
        // bm.setPixel(720, 1280, Color.parseColor("#00000000"));

		return bm;
	}



	/**  
     * 删除指定目录下文件及目录   
     * @param deleteThisPath  
     * @param filePath
     * @return   
     */   
    public static void deleteFolderFile(String filePath, boolean deleteThisPath)
	{   
        if (!TextUtils.isEmpty(filePath))
		{   
            try
			{
				File file = new File(filePath);   
				if (file.isDirectory())
				{// 处理目录   
				    File files[] = file.listFiles();   
				    for (int i = 0; i < files.length; i++)
					{   
				        deleteFolderFile(files[i].getAbsolutePath(), true);   
				    }    
				}   
				if (deleteThisPath)
				{   
				    if (!file.isDirectory())
					{// 如果是文件，删除   
				        file.delete();   
				    }
					else
					{// 目录   
						if (file.listFiles().length == 0)
						{// 目录下没有文件或者目录，删除   
				            file.delete();   
				        }   
				    }   
				}
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
        }   
    }  



	public static void saveStringStr(Context ctx, String key, String save_str)

	{

		SharedPreferences settings = ctx.getSharedPreferences("SDnews", 0); 
		SharedPreferences.Editor editor = settings.edit();

		editor.putString(key, save_str);
		editor.commit();
	}

	public static boolean fileIsExists(String path)
	{
    	System.out.print("get");
        File f=new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + path);
        if (!f.exists())
		{
			return false;
        }
        return true;
	}




	public static String SPgetString(Context ctx, String key)
	{
		SharedPreferences settings = ctx.getSharedPreferences("SDne", 0); // 获取一个SharedPreferences对象
		String getsave_str = settings.getString(key, "");
		return getsave_str;
	}





}
