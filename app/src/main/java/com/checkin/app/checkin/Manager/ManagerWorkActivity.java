package com.checkin.app.checkin.Manager;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Account.AccountModel;
import com.checkin.app.checkin.Account.BaseAccountActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManagerWorkActivity extends BaseAccountActivity {

    public static final String KEY_RESTAURANT_PK = "manager.restaurant_pk";

    @BindView(R.id.pager_manager_work)
    DynamicSwipableViewPager pagerManager;
    @BindView(R.id.tabs_manager_work)
    TabLayout tabLayout;

    private ManagerWorkViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_work);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Live Orders");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_grey);
        }

        mViewModel = ViewModelProviders.of(this).get(ManagerWorkViewModel.class);
        mViewModel.fetchActiveTables(getIntent().getLongExtra(KEY_RESTAURANT_PK, 0L));

        pagerManager.setEnabled(false);
        pagerManager.setAdapter(new ManagerFragmentAdapter(getSupportFragmentManager()));
        pagerManager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (actionBar != null) {
                    if (position == 0){
                        actionBar.setTitle("Live Orders");
                    }else if (position == 1){
                        actionBar.setTitle("Statistics");
                    }
                }
            }
        });
        tabLayout.setupWithViewPager(pagerManager);

        if (tabLayout.getTabCount() == 2) {

            View vLiveOrder = LayoutInflater.from(this).inflate(R.layout.manager_statistics_tab,null);
            TextView tvLiveOrder = vLiveOrder.findViewById(R.id.tv_tab);
            ImageView ivLiveOrder = vLiveOrder.findViewById(R.id.iv_tab);

            tvLiveOrder.setText("Live Orders");
            ivLiveOrder.setImageResource(R.drawable.ic_orders_list_toggle);
            tabLayout.getTabAt(0).setCustomView(vLiveOrder);

            View vStatics = LayoutInflater.from(this).inflate(R.layout.manager_statistics_tab,null);
            TextView tvStatics = vStatics.findViewById(R.id.tv_tab);
            ImageView ivStatics = vStatics.findViewById(R.id.iv_tab);

            tvStatics.setText("Stats");
            ivStatics.setImageResource(R.drawable.ic_stats_toggle);
            tabLayout.getTabAt(1).setCustomView(vStatics);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected int getNavMenu() {
        return R.menu.menu_manager_work;
    }

    @Override
    protected <T extends AccountHeaderViewHolder> T getHeaderViewHolder() {
        return (T) new AccountHeaderViewHolder(this, R.layout.layout_header_account);
    }

    @Override
    protected AccountModel.ACCOUNT_TYPE[] getAccountTypes() {
        return new AccountModel.ACCOUNT_TYPE[]{AccountModel.ACCOUNT_TYPE.RESTAURANT_MANAGER};
    }

    static class ManagerFragmentAdapter extends FragmentStatePagerAdapter {
        public ManagerFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ManagerTablesFragment.newInstance();
                case 1:
                    return ManagerStatsFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

//        @Nullable
//        @Override
//        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return "Live Orders";
//                case 1:
//                    return "Stats";
//            }
//            return null;
//        }
    }
}
