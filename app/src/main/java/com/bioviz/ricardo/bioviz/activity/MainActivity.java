package com.bioviz.ricardo.bioviz.activity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.fragment.AboutFragment;
import com.bioviz.ricardo.bioviz.fragment.FragmentDrawer;
import com.bioviz.ricardo.bioviz.fragment.Home;
import com.bioviz.ricardo.bioviz.fragment.OccurrenceList;
import com.bioviz.ricardo.bioviz.fragment.SettingsFragment;


public class MainActivity extends ActionBarActivity
        implements FragmentDrawer.FragmentDrawerListener  {

    private CharSequence mTitle;

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    private Integer colorStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        drawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        displayView(0);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.section_home);
                break;
            case 2:
                mTitle = getString(R.string.section_occurrence_list);
                break;
            case 3:
                mTitle = getString(R.string.section_settings);
                break;
            case 4:
                mTitle = getString(R.string.section_about);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the MainActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {displayView(position);}

    private void displayView(int position) {
        android.support.v4.app.Fragment fragment = null;
        final Integer colorTo;

        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new Home();
                title = getString(R.string.section_home);
                colorTo = getResources().getColor(R.color.primary);
                break;
            case 1:
                fragment = new OccurrenceList();
                title = getString(R.string.section_occurrence_list);
                colorTo = getResources().getColor(R.color.tab_blue);
                break;
            case 2:
                fragment = new SettingsFragment();
                title = getString(R.string.section_settings);
                colorTo = getResources().getColor(R.color.tab_red);
                break;
            case 3:
                fragment = new AboutFragment();
                title = getString(R.string.section_about);
                colorTo = getResources().getColor(R.color.tab_green);
                break;
            default:
                colorTo = null;
                break;
        }

        ValueAnimator colorAnimation;

        if (colorStatus == null) {
            colorStatus = R.color.primary;
        }

        if (colorTo == null) {
            return;
        }

        colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorStatus, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                colorStatus = colorTo;
                if (Build.VERSION.SDK_INT >= 21) {
                    //getWindow().setStatusBarColor(colorTo);
                    mToolbar.setBackgroundColor(colorTo);
                }
            }
        });
        colorAnimation.start();

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();

        // set the toolbar title
        getSupportActionBar().setTitle(title);
    }
}
