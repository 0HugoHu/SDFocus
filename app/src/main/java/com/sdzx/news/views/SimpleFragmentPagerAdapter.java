package com.sdzx.news.views;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Administrator on 2015/7/30.
 */
public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
    private int PAGE_COUNT;
    private String tabTitles[];
    private String tabIds[];
    private Context context;
    public SimpleFragmentPagerAdapter(FragmentManager fm,String tabs[],String tabsId[],Context context) {
        super(fm);
        tabTitles=tabs;
        PAGE_COUNT=tabTitles.length;
        tabIds=tabsId;
        this.context = context;
    }
    @Override
    public Fragment getItem(int position) {
        return PageFragment1.newInstance(position,tabIds);
    }
    @Override
    public int getCount() {
        PAGE_COUNT=tabTitles.length;
        return PAGE_COUNT;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        if (position<PAGE_COUNT)
            return tabTitles[position];
        else return "";
    }
}
