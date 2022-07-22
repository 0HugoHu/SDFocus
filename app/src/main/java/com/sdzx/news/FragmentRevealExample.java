package com.sdzx.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.search.AVSearchQuery;
import com.sdzx.news.utils.PdUtil;
import com.sdzx.tools.NewsItemDataClass;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class FragmentRevealExample extends Fragment implements View.OnClickListener{

    private TextView resultTextView;
    private EditText searEditText;
    private View content;
    private ArrayList<NewsItemDataClass> list;
//    private SupportAnimator mRevealAnimator;
    private EditText edit_search;
    private int centerX;
    private int centerY;

    private ListView listView;
    private int size=3;
    private ListViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my, container, false);
        rootView.setOnClickListener(this);
        content = rootView.findViewById(R.id.content);
        edit_search = (EditText) rootView.findViewById(R.id.SearchEditTextView1);
        final ImageView img_search = (ImageView) rootView.findViewById(R.id.img_search);
        final View edit_lay = rootView.findViewById(R.id.edit_lay);
        final View items = rootView.findViewById(R.id.items);


        resultTextView=(TextView)rootView.findViewById(R.id.resultSearchlayoutTextView);
        searEditText=(EditText)rootView.findViewById(R.id.SearchEditTextView1);

        searEditText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                FragmentRevealExample.this.onSearchItClick(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });


        list = new ArrayList<NewsItemDataClass>();
        listView = (ListView) rootView.findViewById(R.id.searchListView1);
        listView.setFastScrollEnabled(false);
        listView.setFastScrollAlwaysVisible(false);
        adapter = new ListViewAdapter(getActivity(), list);
        listView.setAdapter(adapter);


        edit_lay.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                edit_lay.getViewTreeObserver().removeOnPreDrawListener(this);
                items.setVisibility(View.INVISIBLE);
                items.setOnClickListener(FragmentRevealExample.this);
                edit_lay.setVisibility(View.INVISIBLE);

                centerX = img_search.getLeft()+img_search.getWidth()/2;
                centerY = img_search.getTop()+img_search.getHeight()/2;

                SupportAnimator mRevealAnimator = ViewAnimationUtils.createCircularReveal(edit_lay, centerX, centerY, 20, PdUtil.hypo(edit_lay.getWidth(), edit_lay.getHeight()));
                mRevealAnimator.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {
                        edit_lay.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationEnd() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                items.setVisibility(View.VISIBLE);
                                edit_search.requestFocus();
                                if (getActivity()!=null) {
                                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(edit_search, InputMethodManager.SHOW_IMPLICIT);
                                }
                            }
                        }, 100);
                    }
                    @Override
                    public void onAnimationCancel() {
                    }
                    @Override
                    public void onAnimationRepeat() {
                    }
                });
                mRevealAnimator.setDuration(200);
                mRevealAnimator.setStartDelay(100);
                mRevealAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                mRevealAnimator.start();
                return true;
            }
        });

        return rootView;
    }



    public void onSearchItClick(View v){

        list.clear();
        adapter.notifyDataSetChanged();
        String searchString="";
        searchString=searEditText.getText().toString();

        AVSearchQuery search = new AVSearchQuery(searchString);
        search.setLimit(30);

        search.findInBackgroud(new FindCallback<AVObject>() {

            @Override
            public void done(List<AVObject> objects, AVException exception) {
                if (exception == null) {
                    //你可以使用 objects来展现自己的UI
                    if (objects.size() == 30)
                        resultTextView.setText("查询到30+条符合条件的数据");
                    else resultTextView.setText("查询到" + objects.size() + "条符合条件的数据");
                    for (int i = 0; i < objects.size(); i++) {
                        if ((objects.get(i).getString("Title")!=null)&& (objects.get(i).getString("Message")!=null)&& (objects.get(i).getString("Summary")!=null)) {
                            list.add(new NewsItemDataClass(objects.get(i).getClassName(), objects.get(i).getObjectId(), objects.get(i).getString("Title"), objects.get(i).getString("Message"), objects.get(i).getString("Summary"), objects.get(i)));
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    //Exception happened
                    resultTextView.setText("无数据");
                }

            }
        });
    }



    public boolean onBackPressed() {
        edit_search.clearFocus();
        SupportAnimator mRevealAnimator = ViewAnimationUtils.createCircularReveal(content, centerX, centerY, 20, PdUtil.hypo(content.getWidth(), content.getHeight()));
        mRevealAnimator = mRevealAnimator.reverse();
        if (mRevealAnimator==null) return false;
        mRevealAnimator.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {
                content.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd() {
                content.setVisibility(View.INVISIBLE);
                if (getActivity()!=null)
                    getActivity().getSupportFragmentManager().popBackStack();
            }
            @Override
            public void onAnimationCancel() {
            }
            @Override
            public void onAnimationRepeat() {
            }
        });
        mRevealAnimator.setDuration(200);
        mRevealAnimator.setStartDelay(100);
        mRevealAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mRevealAnimator.start();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.root:
                onBackPressed();
                break;
        }
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
            if (position<list.size()){
                type_1_view = new Type_1_Holder();

                convertView = LayoutInflater.from(context).inflate(R.layout.search_item, null);
                type_1_view.list_text = (TextView) convertView.findViewById(R.id.user_center_item_txt);
                type_1_view.sumText= (TextView) convertView.findViewById(R.id.itemMainSummary);
                type_1_view.sumText.setText(list.get(position).getSummary());
                type_1_view.list_text.setText(list.get(position).getCatname());
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4){

                        Intent intent = new Intent();
                        intent.putExtra("cid",(list.get(p3).getCid())+"");
                        intent.putExtra("ClassName", list.get(p3).getClassname());
                        intent.setClass(FragmentRevealExample.this.getActivity(), ReaderActivity.class);
                        startActivity(intent);
                    }
                });



            }

            return convertView;
        }

        class Type_1_Holder
        {
            TextView list_text;
            TextView sumText;
            Button onclick;
        }

    }



}
