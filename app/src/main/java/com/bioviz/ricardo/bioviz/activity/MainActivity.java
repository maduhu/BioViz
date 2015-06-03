package com.bioviz.ricardo.bioviz.activity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.bioviz.ricardo.bioviz.AppController;
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.fragment.SettingsFragment;
import com.bioviz.ricardo.bioviz.fragment.AboutFragment;
import com.bioviz.ricardo.bioviz.fragment.Home;
import com.bioviz.ricardo.bioviz.fragment.NavigationDrawerFragment;
import com.bioviz.ricardo.bioviz.fragment.OccurrenceList;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private Integer colorStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        AppController.enableHttpResponseCache();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment;
        final Integer colorTo;
        ValueAnimator colorAnimation;

        if (colorStatus == null) {
            colorStatus = R.color.primary;
        }

        switch (position) {
            case 0:
                fragment = Home.newInstance();
                colorTo = getResources().getColor(R.color.primary);
                break;
            case 1:
                fragment = OccurrenceList.newInstance();
                colorTo = getResources().getColor(R.color.tab_blue);
                break;
            case 2:
                fragment = SettingsFragment.newInstance();
                colorTo = getResources().getColor(R.color.tab_green);
                break;
            case 3:
                fragment = AboutFragment.newInstance();
                colorTo = getResources().getColor(R.color.tab_red);
                break;
            default:
                return;
        }

        colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorStatus, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                colorStatus = colorTo;
                if (Build.VERSION.SDK_INT >= 21) {
                    getWindow().setStatusBarColor(colorTo);
                }

                if (getActionBar() != null) {
                    getActionBar().setBackgroundDrawable(new ColorDrawable((Integer) animator.getAnimatedValue()));
                }
            }
        });
        colorAnimation.start();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
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

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        //getMenuInflater().inflate(R.menu.home, menu);
        //restoreActionBar();
        return !mNavigationDrawerFragment.isDrawerOpen() || super.onCreateOptionsMenu(menu);
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

}
