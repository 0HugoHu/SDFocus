package com.sdzx.news.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.SaveCallback;
import com.sdzx.news.InitActivity;
import com.sdzx.news.R;
import com.sdzx.news.ReaderActivity;
import com.sdzx.tools.ApplicationHelper;
import com.sdzx.tools.BitmapUtils;
import com.sdzx.tools.NewsItemDataClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/30.
 */
public class PageFragment1 extends Fragment {

    private String tabClasses[];
    private boolean ifLoading=true;//是否在加载中
    boolean ifNeedReload=false;
    private ListView listView;
    private PageListViewAdapter adapter;
    boolean ifHasBeenLongClick=false;
    private ArrayList<NewsItemDataClass> list;
    SwipeRefreshLayout pageViewMainThisViewPullListView;
    View pageViewMainThisView;
    boolean ifMainHasBeenLoad=false;
    int looper_number=0;
    int pageNum=0;
    private FloatingActionButton fab;
    private PagerAdapter mViewPagerAdapter;
    private ArrayList<View> mViews= new ArrayList<View>();
    boolean ifSavingBmp=false;
    private String className="Test";
    boolean ifLoadImg=true;

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;

    public static PageFragment1 newInstance(int page,String newTabClasses[]) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putStringArray("classes",newTabClasses);
        PageFragment1 pageFragment = new PageFragment1();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        tabClasses=getArguments().getStringArray("classes");
    }

    @Override
    public void onDetach(){
        Log.i("FRG", "onDetach");
        super.onDetach();
        getActivity().finish();
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i("FRG", "onDestroy");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.i("FRG", "onPause");
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i("FRG", "onStart");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i("FRG", "onResume");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        className=tabClasses[mPage];

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

        ifLoadImg = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("IfLoadImg", true);
        ifNeedReload = true;
        HandlerThread handlerThread = new  HandlerThread("backgroundThread");
        handlerThread.start();
        MyHandler myHandler = new  MyHandler(handlerThread.getLooper());
        Message msg = myHandler.obtainMessage();
        //....此处对msg进行赋值，可以创建一个Bundle进行承载
        msg.sendToTarget();
        ifLoading = true;
        if (!ifMainHasBeenLoad)
        {
            pageViewMainThisView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main_pager_1, null);

            RelativeLayout mainRelativeLayout=(RelativeLayout)pageViewMainThisView.findViewById(R.id.main1Relative);
            list = new ArrayList<NewsItemDataClass>();
            pageViewMainThisViewPullListView = new SwipeRefreshLayout(getContext());

            listView = new ListView(getContext());
            pageViewMainThisViewPullListView.setLayoutParams(new SwipeRefreshLayout.LayoutParams(SwipeRefreshLayout.LayoutParams.MATCH_PARENT, SwipeRefreshLayout.LayoutParams.MATCH_PARENT));
            pageViewMainThisViewPullListView.addView(listView);
            pageViewMainThisViewPullListView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    ifNeedReload = true;
                    pageNum = 0;
                    PageFragment1.this.loadItemOfListView();
                }
            });
            listView.setDivider(null);
            listView.setFastScrollEnabled(false);
            listView.setFastScrollAlwaysVisible(false);
            adapter = new PageListViewAdapter(getContext(), list);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                    if (!ifHasBeenLongClick)
                        if (p3 < list.size()) {
                            Intent intent = new Intent();
                            intent.putExtra("cid", (list.get(p3).getCid()) + "");
                            intent.putExtra("ClassName", className);
                            intent.setClass(PageFragment1.this.getContext(), ReaderActivity.class);
                            startActivity(intent);
                        }
                }
            });
            final String itemsStr[]={"删除","加入到精选"};
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    ifHasBeenLongClick = true;
                    final int pofnl = position;
                    android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(getContext())
                            .setTitle("操作")
                            .setItems(itemsStr, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ifHasBeenLongClick = false;
                                    if (which == 1) {

                                        if (AVUser.getCurrentUser() != null) {
                                            if ((AVUser.getCurrentUser().getInt("level") == 0) || (AVUser.getCurrentUser().getInt("level") == 3)) {

                                                AVObject postb = new AVObject("main");
                                                postb.put("idstring", list.get(pofnl).getCid());
                                                postb.put("class", list.get(pofnl).getClassname());
                                                postb.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(AVException e) {
                                                        if (e == null) {
                                                            Snackbar.make(fab, "成功！", Snackbar.LENGTH_SHORT).show();
                                                            doRefresh();
                                                        } else
                                                            Snackbar.make(fab, "出错啦！", Snackbar.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else
                                                Snackbar.make(fab, "没有权限", Snackbar.LENGTH_SHORT).show();
                                        } else
                                            Snackbar.make(fab, "请先登录", Snackbar.LENGTH_SHORT).setAction("立即登录", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent();
                                                    intent.setClass(PageFragment1.this.getActivity(), InitActivity.class);
                                                    startActivity(intent);
                                                }
                                            }).show();

                                    } else if (which == 0)

                                    {

                                        android.support.v7.app.AlertDialog dialog1 = new android.support.v7.app.AlertDialog.Builder(PageFragment1.this.getActivity())
                                                .setTitle("确定？")
                                                .setMessage("真的要删除吗？")
                                                .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {


                                                        if (AVUser.getCurrentUser() != null) {
                                                            if ((AVUser.getCurrentUser().getInt("level") == 0) || (AVUser.getCurrentUser().getInt("level") == 3) || (AVUser.getCurrentUser().getObjectId().equals(list.get(position).getUser().getObjectId()))) {

                                                                AVQuery<AVObject> deleteQuery = new AVQuery<AVObject>("main");
                                                                deleteQuery.getInBackground(list.get(position).getObj().getObjectId(), new GetCallback<AVObject>() {
                                                                    @Override
                                                                    public void done(AVObject avObject, AVException e1) {
                                                                        if (e1 == null) {
                                                                            avObject.deleteInBackground(new DeleteCallback() {
                                                                                @Override
                                                                                public void done(AVException e2) {
                                                                                    if (e2 != null)
                                                                                        Snackbar.make(fab, "删除未完成", Snackbar.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });

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
                                                        } else
                                                            Snackbar.make(fab, "请先登录", Snackbar.LENGTH_SHORT).setAction("立即登录", new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Intent intent = new Intent();
                                                                    intent.setClass(PageFragment1.this.getActivity(), InitActivity.class);
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


                    listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                        public void onScrollStateChanged(AbsListView p1, int p2) {
                        }

                        public void onScroll(AbsListView p1, int p2, int p3, int p4) {
                            int pos = listView.getLastVisiblePosition();
                            nowScollPosition = listView.getFirstVisiblePosition();
                            if (nowScollPosition - lastScollPosition >= 1) fab.hide();
                            else if (nowScollPosition - lastScollPosition <= -1) fab.show();
                            lastScollPosition = nowScollPosition;
                            if (ifLoading == true) {
                                if (pos == list.size() - 1) {
                                    new Thread() {
                                        public void run() {
                                            fab.show();
                                            PageFragment1.this.loadItemOfListView();
                                        }
                                    }.run();

                                }
                            }
                        }
                    });
                    pageViewMainThisViewPullListView.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
                    ifMainHasBeenLoad = true;
                    mainRelativeLayout.removeAllViews();
                    pageViewMainThisViewPullListView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    mainRelativeLayout.addView(pageViewMainThisViewPullListView);
                }

                return pageViewMainThisView;
            }

            int lastScollPosition=0,nowScollPosition=0;

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
                        if ((bmpToSave.size() > index)&&(bmpToSave.get(index).bitmap!=null)) ApplicationHelper.saveMyBitmap(ApplicationHelper.compressImage(bmpToSave.get(index).bitmap), bmpToSave.get(index).path+".cache",false);
                        if (bmpToSave.size() > index) bmpToSave.get(index).delete();
                        index++;
                    }
                }


            }).run();
        }
    }


    public void loadItemOfListView()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {


        ifLoading = false;
        looper_number++;
        AVQuery<AVObject> query = new AVQuery<AVObject>(className);
        if (!ApplicationHelper.isNetworkConnected(getContext()))
            query.setCachePolicy(AVQuery.CachePolicy.CACHE_ONLY);
        else
        {
            query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
        }
        query.setSkip(pageNum * 10);
        query.setLimit(10);
        pageNum++;
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<AVObject>() {

            public void done(List<AVObject> avObjects, AVException e) {
                if (ifNeedReload) {
                    list.clear();
                    ifNeedReload = false;
                }
                if (e == null) {
                    final List<AVObject> avofnl = avObjects;

                    int max;
                    if (avObjects.size() >= 10) max = 10;
                    else max = avObjects.size();

                    for (int i = 0; i < max; i++) {
                        final AVObject avobjetFnl1 = avofnl.get(i);
                        String summaryStr = avofnl.get(i).getString("Summary").toString();
                        if (summaryStr.length() > 45)
                            summaryStr = summaryStr.substring(0, 44) + "…";


                        final int ifnl1 = i;
                        String titleStr = avofnl.get(i).getString("Title").toString();
                        if (titleStr.length() > 45) titleStr = titleStr.substring(0, 44) + "…";
                        list.add(new NewsItemDataClass(avofnl.get(i).getClassName(), avofnl.get(i).getObjectId(), titleStr, "", summaryStr, avofnl.get(i)));
                        final int thisIndex = list.size() - 1;
                        //adapter.notifyDataSetChanged();

                        final int maxfnl = max;

                        AVObject thisavo = avObjects.get(i).getAVObject("user");
                        if (thisavo != null) thisavo.fetchIfNeededInBackground(new GetCallback() {

                            @Override
                            public void done(AVObject p1, AVException p2) {
                                // TODO: Implement this method
                                if (p2 == null) {
                                    if (list.size() > thisIndex) list.get(thisIndex).setUser(p1);
                                    if (ifnl1 == maxfnl - 1) adapter.notifyDataSetChanged();
                                }

                            }


                        });

                        ifLoading = true;
                        if (ifLoadImg) {
                            final AVFile avFile = avObjects.get(i).getAVFile("Thumb");
                             if (ApplicationHelper.fileIsExists("/SDFocus/cache/" + avobjetFnl1.getObjectId() + ".cache")) {
                                new Thread() {
                                    public void run() {
                                        Bitmap mBitmap;
                                        mBitmap = ApplicationHelper.convertToBitmap("/SDFocus/cache/" + avobjetFnl1.getObjectId() + ".cache", 80);
                                        if (list.size() > thisIndex)
                                            list.get(thisIndex).setBmp(mBitmap);
                                    }
                                }.run();
                            } else {
                                 if (avFile!=null) avFile.getDataInBackground(new GetDataCallback() {

                                    public void done(byte[] arg0, AVException arg1) {
                                        if (arg1 == null) {
                                            Bitmap bitmap = BitmapUtils.compressByKByte(arg0, 50, 200, 200);
                                            if (list.size() > thisIndex)
                                                list.get(thisIndex).setBmp(bitmap);
                                            //adapter.notifyDataSetChanged();
                                            bmpToSave.add(new BmpDataClass(bitmap, "/SDFocus/cache/" + avobjetFnl1.getObjectId()));

                                        }
                                    }
                                });

                            }

                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                pageViewMainThisViewPullListView.setRefreshing(false);
            }
        });


            }
        }).start();
    }


    public void doRefresh(){
        ifNeedReload = true;
        pageNum = 0;
        pageViewMainThisViewPullListView.setRefreshing(true);
        PageFragment1.this.loadItemOfListView();
    }

    private ArrayList<BmpDataClass> bmpToSave=new ArrayList<BmpDataClass>();
}