package com.sdzx.news;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by lenovo on 2016/2/16.
 */
public class HelpActivity extends Activity {
    private LinearLayout L;
    private int num;
    private int ids[]={
            R.drawable.main_help,
            R.drawable.read_help,
            R.drawable.user_help,
            R.drawable.comment_help,
            R.drawable.edit_help,
            R.drawable.info_help
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_help);

        num=getIntent().getIntExtra("num",-1);
        if (num!=-1) {
            L = (LinearLayout) findViewById(R.id.main_help);
            L.setBackgroundResource(ids[num]);
            L.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HelpActivity.this.finish();
                }
            });
        }
    }
}
