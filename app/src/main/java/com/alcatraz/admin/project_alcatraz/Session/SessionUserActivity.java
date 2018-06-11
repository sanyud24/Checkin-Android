package com.alcatraz.admin.project_alcatraz.Session;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Typeface;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.DynamicSwipableViewPager;
import com.alcatraz.admin.project_alcatraz.Utility.Util;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

public class SessionUserActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private DynamicSwipableViewPager mViewPager;
    private Toolbar mToolbar;
    private MaterialSearchView mSearchView;
    private ImageButton mFilterToggle;
    private View mFilterContainer;
    private View mDarkFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_user);

        mToolbar = findViewById(R.id.session_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mFilterToggle = findViewById(R.id.action_filter_toggle);
        mFilterContainer = findViewById(R.id.filter_container);
        mDarkFilter = findViewById(R.id.darkfilter);
        mDarkFilter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    hideFilter();
                return true;
            }
        });

        final TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        final TabLayout.ViewPagerOnTabSelectedListener tabSelectedListener = new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                switch (tab.getPosition()) {
                    case 0:
                        findViewById(R.id.action_finish).setVisibility(View.VISIBLE);
                        findViewById(R.id.action_search).setVisibility(View.VISIBLE);
                        mFilterToggle.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        findViewById(R.id.action_finish).setVisibility(View.INVISIBLE);
                        findViewById(R.id.action_search).setVisibility(View.INVISIBLE);
                        mFilterToggle.setVisibility(View.GONE);
                        break;
                }
            }
        };

        tabLayout.addOnTabSelectedListener(tabSelectedListener);

        mSearchView = findViewById(R.id.search_view);
        mSearchView.setVoiceSearch(true);
        mSearchView.setStartFromRight(false);
        mSearchView.setCursorDrawable(R.drawable.color_cursor_white);
        mSearchView.setSuggestions(getResources().getStringArray(R.array.menu_items));

        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Snackbar.make(findViewById(R.id.main_content), "Query: " + query, Snackbar.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO: Do some magic!
                return false;
            }

            @Override
            public boolean onQueryClear() {
                return false;
            }
        });
        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                tabLayout.animate()
                        .alpha(1.0f)
                        .setDuration(100)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                tabLayout.setVisibility(View.VISIBLE);
                            }
                        });
                mViewPager.setEnabled(true);
            }
        });
        ImageButton btn_search = findViewById(R.id.action_search);
        btn_search.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabLayout.animate()
                        .alpha(0.0f)
                        .setDuration(100)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                tabLayout.setVisibility(View.GONE);
                            }
                        });
                mViewPager.setEnabled(false);
                mSearchView.showSearch();
            }
        });
        Typeface tf = ResourcesCompat.getFont(this, R.font.arial_rounded_mt_bold);
        Util.setTabsFont(tabLayout, tf);

        mFilterToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDarkFilter.getVisibility() == View.VISIBLE)
                    hideFilter();
                else
                    showFilter();
            }
        });
    }

    private void showFilter() {
        mDarkFilter.animate()
                .alpha(0.67f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mDarkFilter.setVisibility(View.VISIBLE);
                    }
                });
        mFilterContainer.animate()
                .rotationBy(-180)
                .alpha(1.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mFilterContainer.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mFilterContainer.setRotation(0);
                    }
                });
    }

    private void hideFilter() {
        mFilterContainer.animate()
                .rotationBy(-180)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mFilterContainer.setVisibility(View.GONE);
                        mFilterContainer.setRotation(180);
                    }
                });

        mDarkFilter.animate()
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mDarkFilter.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = (Fragment) mSectionsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
        if (mSearchView.isSearchOpen())
            mSearchView.closeSearch();
        else if (mDarkFilter.getVisibility() == View.VISIBLE)
            hideFilter();
        else if (fragment instanceof MenuUserFragment && ((MenuUserFragment) fragment).isGroupExpanded()) {
            ((MenuUserFragment) fragment).onBackPressed();
        }
        else
            super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position) {
                case 0:
                    return MenuUserFragment.newInstance();
                case 1:
                    return SessionDetailFragment.newInstance("abc", "xyz");
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    mSearchView.setQuery(searchWrd, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
