package com.zncm.lovetuer.modules.note.zone;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.MenuItem;
import com.zncm.lovetuer.R;
import com.zncm.lovetuer.global.GlobalConstants;
import com.zncm.lovetuer.modules.base.BaseHomeActivity;
import com.zncm.lovetuer.modules.note.NoteIndexF;

import java.util.ArrayList;

public class ZoneTabsPager extends BaseHomeActivity {

    private ViewPager mViewPager;
    private MyTabsAdapter mMyTabsAdapter;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_zone_bar);
        mViewPager = (ViewPager) this.findViewById(R.id.view_pager);
        actionBar = this.getSupportActionBar();
        actionBar.setTitle("个人主页");
        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        BarWeekFragment barWeekFragment = new BarWeekFragment();
        NoteIndexF indexF = new NoteIndexF();
        mMyTabsAdapter = new MyTabsAdapter(this, mViewPager);
//        mMyTabsAdapter.addTab(actionBar.newTab().setText("近七日"), BarWeekFragment.class, null, barWeekFragment);
        Bundle bundle = new Bundle();
        bundle.putString(GlobalConstants.KEY_ID, getIntent().getExtras().getString(GlobalConstants.KEY_ID));
        mMyTabsAdapter.addTab(actionBar.newTab().setText("个人主页"), NoteIndexF.class, bundle, indexF);
        mViewPager.setOffscreenPageLimit(mMyTabsAdapter.getCount() - 1);
    }

    public static class MyTabsAdapter extends FragmentStatePagerAdapter implements ActionBar.TabListener,
            ViewPager.OnPageChangeListener {

        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
        private final ArrayList<Fragment> mFrag = new ArrayList<Fragment>();

        static final class TabInfo {
            @SuppressWarnings("unused")
            private final Class<?> clss;
            @SuppressWarnings("unused")
            private final Bundle args;

            TabInfo(Class<?> _class, Bundle _args) {
                clss = _class;
                args = _args;
            }

            public Bundle getArgs() {
                return args;
            }

            public Class<?> getClss() {
                return clss;
            }
        }

        public MyTabsAdapter(ZoneTabsPager activity, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mActionBar = activity.getSupportActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args, Fragment frag) {
            TabInfo info = new TabInfo(clss, args);
            tab.setTag(info);
            tab.setTabListener(this);
            mTabs.add(info);
            mFrag.add(frag);
            mActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            mFrag.get(position).setArguments(mTabs.get(position).getArgs());
            return mFrag.get(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            Object tag = tab.getTag();
            for (int i = 0; i < mTabs.size(); i++) {
                if (mTabs.get(i) == tag) {
                    mViewPager.setCurrentItem(i);
                }
            }
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return false;
    }
}