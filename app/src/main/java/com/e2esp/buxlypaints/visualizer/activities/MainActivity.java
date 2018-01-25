package com.e2esp.buxlypaints.visualizer.activities;

import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.e2esp.buxlypaints.visualizer.R;
import com.e2esp.buxlypaints.visualizer.fragments.ColorsFragment;
import com.e2esp.buxlypaints.visualizer.fragments.ProductsFragment;
import com.e2esp.buxlypaints.visualizer.fragments.RoomsFragment;
import com.e2esp.buxlypaints.visualizer.interfaces.OnFragmentInteractionListener;
import com.e2esp.buxlypaints.visualizer.models.Room;
import com.e2esp.buxlypaints.visualizer.models.SecondaryColor;

/**
 *
 * Created by Zain on 1/30/2017.
 */

public class MainActivity extends FragmentActivity implements OnFragmentInteractionListener {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.vertical_slide_in, R.anim.vertical_slide_out);
        setContentView(R.layout.activity_main);

        setUpViewPager();
    }

    private void setUpViewPager() {
        // A ViewPager with touch events blocked
        LinearLayout viewContainerTabStrip = findViewById(R.id.viewContainerTabStrip);
        viewPager = findViewById(R.id.viewPagerMain);

        CustomPagerAdapter pagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        /*viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });*/

        PagerSlidingTabStrip tabStrip = new PagerSlidingTabStrip(this);
        tabStrip.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        tabStrip.setShouldExpand(true);
        tabStrip.setAllCaps(true);
        tabStrip.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_medium));
        tabStrip.setTextColor(Color.WHITE);
        tabStrip.setDividerColor(ContextCompat.getColor(this, R.color.soft_green));
        tabStrip.setDividerPadding(getResources().getDimensionPixelSize(R.dimen.margin_small));
        tabStrip.setIndicatorColor(Color.WHITE);
        tabStrip.setUnderlineColor(ContextCompat.getColor(this, R.color.soft_green));
        viewContainerTabStrip.addView(tabStrip);
        tabStrip.setViewPager(viewPager);
    }

    private boolean backPressed = false;
    @Override
    public void onBackPressed() {
        if (backPressed) {
            super.onBackPressed();
            return;
        }

        if (ColorsFragment.backPressed()) {
            return;
        }
        if (viewPager != null) {
            int currentPage = viewPager.getCurrentItem();
            if (currentPage > 0) {
                viewPager.setCurrentItem(currentPage - 1);
                return;
            }
        }

        backPressed = true;
        Toast.makeText(this, getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backPressed = false;
            }
        }, 2000);
    }

    @Override
    public void onInteraction(int type, Object obj) {
        switch (type) {
            case DEFAULT_ROOM_SELECTED:
                final Room room = (Room) obj;
                viewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ColorsFragment.setSelectedRoom(room, true);
                    }
                }, 1000);
                break;
            case ROOM_SELECTED:
                ColorsFragment.setSelectedRoom((Room) obj, false);
                break;
            case ROOMS_NEXT_CLICK:
                ColorsFragment.setSelectedRoom((Room) obj, false);
                viewPager.setCurrentItem(1);
                break;
            case COLOR_SELECTED:
                ProductsFragment.setSelectedColor((SecondaryColor) obj);
                break;
            case COLORS_NEXT_CLICK:
                //ProductsFragment.setSelectedColor((SecondaryColor) obj);
                viewPager.setCurrentItem(2);
                break;
            case PRODUCT_VIEW_COLORS_CLICK:
                ColorsFragment.showColorsOfProduct((String) obj);
                viewPager.setCurrentItem(1);
                break;
        }
    }

    public class CustomPagerAdapter extends FragmentStatePagerAdapter {

        CustomPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = RoomsFragment.newInstance();
                    break;
                case 1:
                    fragment = ColorsFragment.newInstance();
                    break;
                case 2:
                    fragment = ProductsFragment.newInstance();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int stringRes = R.string.app_name;
            switch (position) {
                case 0:
                    stringRes = R.string.rooms;
                    break;
                case 1:
                    stringRes = R.string.colors;
                    break;
                case 2:
                    stringRes = R.string.products;
                    break;
            }
            return getString(stringRes);
        }

    }

}
