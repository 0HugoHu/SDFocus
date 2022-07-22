package com.sdzx.news;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.RefreshCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.search.AVSearchQuery;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenImages;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.kbeanie.imagechooser.exceptions.ChooserException;
import com.sdzx.news.views.PageListViewAdapter;
import com.sdzx.tools.ApplicationHelper;
import com.sdzx.tools.BitmapUtils;
import com.sdzx.tools.NewsItemDataClass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class UserActivity extends SwipeBackActivity implements ImageChooserListener {

    private FloatingActionButton fab;
    private ImageView userImg;
    private ListView userListView;
    private PageListViewAdapter adapter;
    private TextView userLevelTv,userScoreLevelTv,userScoreTv;
    private int type=0;
    private SharedPreferences sharedPref;
    private boolean ifLoadImg;
    private String cid="";
    private boolean ifLoading=true;//是否在加载中
    boolean ifHasBeenLongClick=false;
    private AVObject thisUser;
    private ArrayList<NewsItemDataClass> list;
    private TextView userNameTv;
    AVSearchQuery search;
    private int numOfItems=0;
    private SharedPreferences.Editor editor;
    private boolean ifUserHasBeenRefreshed=false;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        type=getIntent().getIntExtra("type",0);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        ifLoadImg = sharedPref.getBoolean("IfLoadImg", true);
        editor=sharedPref.edit();


        boolean main_help_1=sharedPref.getBoolean("LEVEL_NOTE",true);
        if (main_help_1) {
            editor.putBoolean("LEVEL_NOTE", false);
            editor.commit();
            onLevelClick(null);
        }

        //************************************************
        //显示帮助提示
        //************************************************
        boolean user_help=sharedPref.getBoolean("USER_HELP",true);
        if (user_help) {
            editor.putBoolean("USER_HELP", false);
            editor.commit();
            Intent intent = new Intent();
            intent.setClass(this, HelpActivity.class);
            intent.putExtra("num", 2);
            startActivity(intent);
        }

        UserActivity.this.setTitle("用户");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
            getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        userNameTv=(TextView)findViewById(R.id.userActivityUsernameTv);
        userImg=(ImageView)findViewById(R.id.userIvImage);
        userLevelTv=(TextView)findViewById(R.id.UserActivityUserlevelTextView1);
        userScoreLevelTv=(TextView)findViewById(R.id.UserActivityUserScorelevelTextView1);
        userScoreTv=(TextView)findViewById(R.id.userActivityScoreTextView);
        userListView=(ListView)findViewById(R.id.UserActivityListView);
        scrollView=(NestedScrollView)findViewById(R.id.userScrollview2);

        if (type == 0) {
			if  (AVUser.getCurrentUser()!=null)
            AVUser.getCurrentUser().refreshInBackground(new RefreshCallback<AVObject>() {
                @Override
                public void done(final AVObject avObject, AVException e) {
                    loadUser(avObject);
                    AVSearchQuery search1 = new AVSearchQuery(avObject.getObjectId());
                    search1.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> list, AVException e) {
							if (e==null&&list!=null){
                            search=new AVSearchQuery(avObject.getObjectId());
                            numOfItems = list.size();
                            search.orderByDescending("updatedAt");
                            ifUserHasBeenRefreshed=true;
                            loadData();
							}
                        }
                    });

                }
            });
        } else {
            AVQuery<AVObject> que = new AVQuery<AVObject>("_User");
            que.getInBackground(getIntent().getStringExtra("cid"), new GetCallback<AVObject>() {
                @Override
                public void done(final AVObject avObject, AVException e) {
                    loadUser(avObject);
                    AVSearchQuery search1 = new AVSearchQuery(avObject.getObjectId());
                    search1.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> list, AVException e) {
                            search=new AVSearchQuery(avObject.getObjectId());
                            numOfItems = list.size();
                            search.orderByDescending("updatedAt");
                            ifUserHasBeenRefreshed=true;
                            loadData();
                        }
                    });
                }
            });
        }
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type==0) {
                    final String itemsStr[]={"从相册选择","拍摄照片"};
                    android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(UserActivity.this)
                            .setTitle("更换头像")
                            .setItems(itemsStr, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0) {
                                        chooserType = ChooserType.REQUEST_PICK_PICTURE;
                                    } else {
                                        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
                                    }
                                    imageChooserManager = new ImageChooserManager(UserActivity.this, chooserType, true);
                                    imageChooserManager.setImageChooserListener(UserActivity.this);
                                    imageChooserManager.clearOldFiles();
                                    try {
                                        filePath = imageChooserManager.choose();
                                    } catch (ChooserException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).show();
                }else Snackbar.make(fab, "您并不能更换别人的头像", Snackbar.LENGTH_SHORT).show();
            }
        });
        list = new ArrayList<NewsItemDataClass>();

        userListView.setDivider(null);
        userListView.setFastScrollEnabled(false);
        userListView.setFastScrollAlwaysVisible(false);
        adapter = new PageListViewAdapter(this, list);
        userListView.setAdapter(adapter);

        //loadData();

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                if (!ifHasBeenLongClick)
                    if (p3 < list.size()) {
                        Intent intent = new Intent();
                        intent.putExtra("cid", (list.get(p3).getCid()) + "");
                        intent.putExtra("ClassName", list.get(p3).getClassname());
                        intent.setClass(UserActivity.this, ReaderActivity.class);
                        startActivity(intent);
                    }
            }
        });

    }

    ImageChooserManager imageChooserManager;
    int chooserType;
    String filePath;


    private void loadUser(AVObject nowUser){
        userNameTv.setText(nowUser.getString("username"));
        userScoreTv.setText(nowUser.getInt("score")+"");
        int userLevelInt=nowUser.getInt("level");
        userLevelInt=userLevelInt%4;

        userLevelTv.setText(UserActivity.this.getResources().getStringArray(R.array.user_level_list)[userLevelInt]);
        userLevelTv.setBackgroundColor(Color.parseColor(UserActivity.this.getResources().getStringArray(R.array.user_level_color)[userLevelInt]));
        userLevelTv.setTextColor(Color.parseColor("#ffffff"));

        int userScore=nowUser.getInt("score");
        int userScoreLevel=ApplicationHelper.getUserScoreLevel(userScore);
        userScoreLevelTv.setText(UserActivity.this.getResources().getStringArray(R.array.user_score_level_list)[userScoreLevel]);
        userScoreLevelTv.setBackgroundColor(Color.parseColor(UserActivity.this.getResources().getStringArray(R.array.user_score_level_color)[userScoreLevel]));
        userScoreLevelTv.setTextColor(Color.parseColor("#ffffff"));

        AVFile imgFile = nowUser.getAVFile("Image");
        if (imgFile!=null) {
            imgFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, AVException e) {
                    if (e == null) {
                        userImg.setImageBitmap(ApplicationHelper.Bytes2Bimap(bytes));
                    } else Log.i("USR", e.toString());
                }
            });
        }
    }


    int pageNum=0;

    public void loadData(){
        pageNum++;
        ifLoading = false;
        search.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avobjects, AVException e) {
                Log.i("HIT", numOfItems + "");
                if (e==null) {
                    //if ((pageNum-1)*10<=numOfItems)
                    for (int i = 0; i < avobjects.size(); i++) {

                        String summaryStr = avobjects.get(i).getString("Summary").toString();
                        if (summaryStr.length() > 45)
                            summaryStr = summaryStr.substring(0, 44) + "…";

                        String titleStr = avobjects.get(i).getString("Title").toString();
                        if (titleStr.length() > 45)
                            titleStr = titleStr.substring(0, 44) + "…";
                        list.add(new NewsItemDataClass(avobjects.get(i).getClassName(), avobjects.get(i).getObjectId(), titleStr, "", summaryStr, avobjects.get(i)));

                        final int thisIndex = list.size() - 1;
                        adapter.notifyDataSetChanged();

                        ifLoading = true;
                        if (ifLoadImg) {

                            final String nowId = avobjects.get(i).getObjectId();
                            if (ApplicationHelper.fileIsExists("/SDFocus/cache/" + nowId + ".cache")) {
                                new Thread() {
                                    public void run() {
                                        Bitmap mBitmap;
                                        mBitmap = ApplicationHelper.convertToBitmap("/SDFocus/cache/" + nowId + ".cache", 80);
                                        if (list.size() > thisIndex)
                                            list.get(thisIndex).setBmp(mBitmap);
                                    }
                                }.run();
                            } else {
                                AVQuery<AVObject> findFileQue = new AVQuery<AVObject>(avobjects.get(i).getClassName());
                                findFileQue.getInBackground(avobjects.get(i).getObjectId(), new GetCallback<AVObject>() {
                                    @Override
                                    public void done(AVObject avObject, AVException e) {

                                        final AVFile avFile = avObject.getAVFile("Thumb");

                                        if (avFile != null)
                                            avFile.getDataInBackground(new GetDataCallback() {
                                                public void done(final byte[] arg0, AVException arg1) {
                                                    if (arg1 == null) {
                                                        final Bitmap bitmap = BitmapUtils.compressByKByte(arg0, 50, 200, 200);
                                                        if ((list.size() > thisIndex) && (thisIndex != -1)) {
                                                            list.get(thisIndex).setBmp(bitmap);
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            });
                                    }
                                });
                            }

                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                LinearLayout allLayout=(LinearLayout)findViewById(R.id.userLinear);
                ProgressBar loadProgress=(ProgressBar)findViewById(R.id.userprogressBar);
                allLayout.removeView(loadProgress);
            }
        });
    }



    private void reinitializeImageChooser() {
        imageChooserManager = new ImageChooserManager(this, chooserType, true);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.reinitialize(filePath);
    }

    public void onLevelClick(View v){
        android.support.v7.app.AlertDialog.Builder a = new android.support.v7.app.AlertDialog.Builder(UserActivity.this);
        a.setTitle("这是什么");
        a.setMessage(getResources().getString(R.string.level_note));
        a.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        a.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("ICA", "OnActivityResult");
        Log.i("ICA", "File Path : " + filePath);
        Log.i("ICA", "Chooser Type: " + chooserType);
        if (resultCode == RESULT_OK && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (imageChooserManager == null) {
                reinitializeImageChooser();
            }
            imageChooserManager.submit(requestCode, data);
        }
    }

    @Override
    public void onImageChosen(final ChosenImage image) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final AVFile avFile = AVFile.withAbsoluteLocalPath(image.getFileThumbnailSmall(),image.getFileThumbnailSmall());
                    avFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e==null) {
                                AVQuery<AVObject> userImgQuery = new AVQuery<AVObject>("_User");
                                userImgQuery.getInBackground(AVUser.getCurrentUser().getObjectId(), new GetCallback<AVObject>() {
                                    @Override
                                    public void done(AVObject avObject, AVException e) {
                                        if (e==null){
                                            avObject.put("Image",avFile);
                                            avObject.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(AVException e) {
                                                    if (e==null){
                                                        Snackbar.make(fab,"上传成功",Snackbar.LENGTH_SHORT).show();
                                                        userImg.setImageBitmap(ApplicationHelper.convertToBitmap(image.getFileThumbnailSmall(),100));
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onError(String s) {

    }

    @Override
    public void onImagesChosen(ChosenImages chosenImages) {

    }


}
