package ptra.hacc.cc.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Hale Yang on 2017/8/16.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {

    private ViewPager mContainer;
    private TabLayout mTabLayout;
    private List<BaseFragment> mFragments;
    private int mCurrentPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContainer = (ViewPager) findViewById(R.id.container);
        mTabLayout = (TabLayout) findViewById(R.id.tab);
        mTabLayout.addOnTabSelectedListener(this);
        findViewById(R.id.testButton).setOnClickListener(this);
        initFragments();
        initUI();
    }

    private void initFragments(){
        mFragments = new ArrayList<>();
        BaseFragment recyclerFragment = (BaseFragment) Fragment.instantiate(this, RecyclerViewFragment.class.getName());
        BaseFragment scrollFragment = (BaseFragment) Fragment.instantiate(this, NestedScrollViewFragment.class.getName());
        mFragments.add(0, recyclerFragment);
        mFragments.add(1, scrollFragment);
    }

    private void initUI(){
        mContainer.setAdapter(new MineFragmentPagerAdapter(getSupportFragmentManager()));
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.setupWithViewPager(mContainer);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.testButton:
                mFragments.get(mCurrentPosition).clearDatas();
                Toast.makeText(this, "datas are all cleared", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mCurrentPosition = tab.getPosition();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


   private class MineFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private MineFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) return getString(R.string.title_recyclerview);
            else return getString(R.string.title_nested_scrollview);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
