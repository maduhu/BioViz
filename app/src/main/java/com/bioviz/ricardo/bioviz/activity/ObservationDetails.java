package com.bioviz.ricardo.bioviz.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bioviz.ricardo.bioviz.AppController;
import com.bioviz.ricardo.bioviz.Interface.OnItemClickListener;
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.fragment.iNatDescriptionFragment;
import com.bioviz.ricardo.bioviz.fragment.iNatMediaFragment;
import com.bioviz.ricardo.bioviz.model.iNatResponses.iNatObservation;
import com.bioviz.ricardo.bioviz.model.iNatResponses.iNatSpecies;
import com.bioviz.ricardo.bioviz.utils.Values;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;


public class ObservationDetails  extends Activity implements ActionBar.TabListener, Response.ErrorListener, OnItemClickListener, Response.Listener<JSONArray> {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    private iNatDescriptionFragment descriptionFragment;
    private iNatMediaFragment mediaFragment;

    private iNatObservation observationItem;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gbifoccurrence_view);


        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }


        String itemString;

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            itemString = intent.getStringExtra("item");
        } else {
            itemString = savedInstanceState.getString("item");
        }

        observationItem = new Gson().fromJson(itemString, iNatObservation.class);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        descriptionFragment = iNatDescriptionFragment.newInstance(observationItem);
        mediaFragment = iNatMediaFragment.newInstance(observationItem);

        /**
         * Deploy network request
         */
        //http://www.inaturalist.org/taxa/search.json?q=merriam kangaroo rat
        String request = Values.iNATBaseAddr + "taxa/search.json?q=" + observationItem.getSpecies_guess();

        //lookup species with speciesKey
        Log.e("REQUEST", request);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(request,
                this, this);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonArrayRequest, "inat_species_description_lookup");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_occurrence_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(JSONArray jsonArray) {
        descriptionFragment.onResponse(jsonArray);
        mediaFragment.onResponse(jsonArray);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Log.e("VOLLEY", volleyError.toString());
        Toast.makeText(this, "Something went wrong :| ", Toast.LENGTH_SHORT).show();
        loadErrorView();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    /**
     * Loads the view showing that there are no descriptions available
     */
    private void loadErrorView() {
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.view_woops, null);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        (v.findViewById(R.id.tvKittenMode)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.google.pt/search?q=kittens&source=lnms&tbm=isch&sa=X");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });


        LinearLayout llDetails = (LinearLayout) findViewById(R.id.ll_occurrence_details);
        llDetails.removeAllViews();
        llDetails.addView(v, params);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
    /**
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
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
            if (position == 0) {
                return descriptionFragment;
            } else if (position == 1) {
                return mediaFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Observation descriptions";
                case 1:
                    return "Media";
            }
            return null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("item", new Gson().toJson(observationItem));
    }
}