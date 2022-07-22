package com.sdzx.news.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.sdzx.news.R;
import com.sdzx.news.ReaderActivity;
import com.sdzx.news.UserActivity;
import com.sdzx.tools.ApplicationHelper;
import com.sdzx.tools.NewsItemDataClass;

import java.util.ArrayList;

/**
 * Created by ww on 2016/2/14.
 */

public class PageListViewAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<NewsItemDataClass> list;

    public PageListViewAdapter(Context context, ArrayList<NewsItemDataClass> list)
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
    boolean ifHasBeenLongClick=false;
    public View getView(final int position, View convertView, ViewGroup arg2)
    {
        if (position<0) return convertView;
        if (convertView==null) {

            type_1_view = new Type_1_Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.user_center_list_item, null);
            type_1_view.list_text = (TextView) convertView.findViewById(R.id.user_center_item_txt);
            type_1_view.list_img = (ImageView) convertView.findViewById(R.id.user_center_item_img);
            type_1_view.userlevelTextView = (TextView) convertView.findViewById(R.id.usercenterlistitemUserlevelTextView1);
            type_1_view.sumText = (TextView) convertView.findViewById(R.id.itemMainSummary);
            type_1_view.timeText = (TextView) convertView.findViewById(R.id.mainlayoutitemTextView1);
            type_1_view.userTextView = (TextView) convertView.findViewById(R.id.usercenterlistitemUserIdTextView1);
            type_1_view.classView = (TextView) convertView.findViewById(R.id.usercenterlistitemUserClassTextView1);
            type_1_view.userLayout=(RelativeLayout)convertView.findViewById(R.id.userCenterlinearLayout);

            convertView.setTag(type_1_view);

        } else {
            type_1_view = (Type_1_Holder) convertView.getTag();
        }

        if (position < list.size()) {
            type_1_view.timeText.setText(list.get(position).getObj().getUpdatedAt().toLocaleString());
            type_1_view.sumText.setText(list.get(position).getSummary().replace('\n',' '));
            type_1_view.list_text.setText(list.get(position).getCatname().replace('\n',' '));
            type_1_view.list_img.setImageBitmap(list.get(position).getBmp());
            final AVUser thisAvu = (AVUser) list.get(position).getUser();
            if (thisAvu != null) {

                type_1_view.userLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.setClass(context, UserActivity.class);
                        intent.putExtra("cid", thisAvu.getObjectId());
                        intent.putExtra("type",1);
                        context.startActivity(intent);
                    }
                });

                final int idx= ApplicationHelper.findIndexOfClasses(list.get(position).getClassname(), context);
                if (idx!=-1) type_1_view.classView.setText(context.getResources().getStringArray(R.array.categories_name)[idx]);
                //type_1_view.classView.setBackgroundColor(Color.parseColor(PageFragment1.this.getContext().getResources().getStringArray(R.array.categories_color)[idx]));

                type_1_view.userTextView.setText(thisAvu.getUsername());
                int sco = thisAvu.getInt("score");
                int flg = thisAvu.getInt("level");
                flg = flg % 4;
                Log.i("CAT", "" + flg);

                if (flg!=2) {
                    type_1_view.userlevelTextView.setText(context.getResources().getStringArray(R.array.user_level_list)[flg]);
                    type_1_view.userlevelTextView.setBackgroundColor(Color.parseColor(context.getResources().getStringArray(R.array.user_level_color)[flg]));
                    type_1_view.userlevelTextView.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    int userScore = thisAvu.getInt("score");
                    int userScoreLevel = ApplicationHelper.getUserScoreLevel(userScore);
                    type_1_view.userlevelTextView.setText(context.getResources().getStringArray(R.array.user_score_level_list)[userScoreLevel]);
                    type_1_view.userlevelTextView.setBackgroundColor(Color.parseColor(context.getResources().getStringArray(R.array.user_score_level_color)[userScoreLevel]));
                    type_1_view.userlevelTextView.setTextColor(Color.parseColor("#ffffff"));
                }
            } else type_1_view.userTextView.setText(" ");


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
        TextView classView;
        Button onclick;
        TextView userTextView;
        TextView userlevelTextView;
        RelativeLayout userLayout;
    }

}
