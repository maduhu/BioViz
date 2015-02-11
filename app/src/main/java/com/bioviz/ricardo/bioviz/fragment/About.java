package com.bioviz.ricardo.bioviz.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.activity.MainActivity;
import com.bioviz.ricardo.bioviz.utils.Values;

/**
 * Fragment to show additional info about the author and the involved technologies
 */
public class About extends Fragment {


    private Button btSaveConf;
    private Switch swData;
    private Switch swPics;
    private Switch swLang;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment About.
     */
    public static About newInstance() {
        About fragment = new About();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public About() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        swData = (Switch) rootView.findViewById(R.id.conf_sw_data);
        swLang = (Switch) rootView.findViewById(R.id.conf_sw_language);
        swPics = (Switch) rootView.findViewById(R.id.conf_sw_pictures);


        btSaveConf = (Button) rootView.findViewById(R.id.conf_bt_save);
        btSaveConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applySettings(swData.isChecked(), swPics.isChecked(), swLang.isChecked());
                btSaveConf.setText("Saved");
                btSaveConf.setEnabled(false);
            }
        });

        boolean[] states = getStates();
        swData.setChecked(states[0]);
        swPics.setChecked(states[1]);
        swLang.setChecked(states[2]);

        // Inflate the layout for this fragment
        return rootView;
    }

    private boolean[] getStates() {

        SharedPreferences preferences = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if (!preferences.contains("use_data") ||
                !preferences.contains("require_images") ||
                !preferences.contains("require_language")) {

           applySettings(false, true, false);
           return new boolean[]{false, true, false};
        }

        return new boolean[]{
                preferences.getBoolean("use_data", false),
                preferences.getBoolean("require_images", true),
                preferences.getBoolean("require_language", false)};
    }

    private void applySettings(boolean data, boolean pictures, boolean language) {

        SharedPreferences preferences = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor edit= preferences.edit();

        if (preferences.contains("use_data") ||
                preferences.contains("require_images") ||
                preferences.contains("require_language")) {
            edit.remove("use_data");
            edit.remove("require_images");
            edit.remove("require_language");
        }

        edit.putBoolean("use_data", data);
        edit.putBoolean("require_images", pictures);
        edit.putBoolean("require_language", language);
        edit.apply();

        swData.setChecked(data);
        swPics.setChecked(pictures);
        swLang.setChecked(language);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(Values.section_about_id);
    }
}
