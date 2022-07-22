package com.sdzx.news;

import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.sdzx.tools.ApplicationHelper;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class PictureYulanActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_yulan);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
            getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        String path=getIntent().getStringExtra("path");
        ImageView img=(ImageView)findViewById(R.id.yulanImageView);

        img.setImageBitmap(BitmapFactory.decodeFile(path));

    }

}
