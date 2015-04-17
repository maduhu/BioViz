package com.bioviz.ricardo.bioviz.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.activity.MainActivity;
import com.bioviz.ricardo.bioviz.utils.Values;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Initial screen
 */
public class Home extends Fragment implements ViewSwitcher.ViewFactory, View.OnClickListener {

    private TextSwitcher switcher;
    private int mIndex = 0;
    ArrayList<String> homeMessages;
    ArrayList<String> homeDescriptions;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment Home.
     */
    public static Home newInstance() {
        Home fragment = new Home();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public Home() {
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        switcher = (TextSwitcher) rootView.findViewById(R.id.home_switcher);
        switcher.setFactory(this);

        Animation in = AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.slide_out_right);


        switcher.setInAnimation(in);
        switcher.setOutAnimation(out);
        switcher.setOnClickListener(this);
        switcher.setText("Welcome to the BioViz application! BioViz is focused on making it easy to find specific details about different species, allowing you to get in touch with the surrounding biodiversity.");

        homeMessages = new ArrayList<String>() {{
            add("I will be your guide throughout this application. I would like you to know that my developer is very eager to get your feedback! If you want to know more about him, tap here.");
            add("BioViz gets all its data from two main biodiversity sources: GBIF and iNaturalist. These are often used by biologists and biodiversity specialists to share data about different observations and sightseeings.");
            add("You can customize the results shown in this application. This customization can go from requiring observations with images attached, to restricting network data to wifi-based connections.");
        }};

        homeDescriptions = new ArrayList<String>() {{
            add("Hey! My name is Ricardo, I am the developer of this application. I am kinda new to this mobile application world so be patient if something goes wrong. I enjoy getting info about species around me and explore different locations' habitats. With that in mind, I started developing this application and focused on searching for biodiversity exchange points to extract such data and show it to you in the application.");
            add("BioViz is this. iNaturalist is like a wikipedia-bioviz everyone can submit, no one cares about curating resources");
            add("If you want a more visually-intense experience, you can request that the search results include images of the observation. This is sometimes difficult to happen when getting results from GBIF, but it is still worth the try. iNaturalist is often richer in terms of related media and will certainly get all the results with images. Nevertheless, if a specific observation has no pictures associated with it, it is still possible to get pictures from the associated species by clicking on it.");
        }};



        Timer T=new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switcher.setText(homeMessages.get(mIndex));
                            if (mIndex == homeMessages.size()-1) {
                                mIndex = 0;
                            } else {
                                mIndex++;
                            }

                        }
                    });
                }


            }
        }, 5000, 5000);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(Values.section_home_id);
    }

    @Override
    public View makeView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        return inflater.inflate(R.layout.switcher_textview, null);
    }

    @Override
    public void onClick(View view) {
        new AlertDialog.Builder(getActivity())
                .setMessage(homeDescriptions.get(mIndex))
                .setIcon(R.drawable.ic_yok_angry)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }
}
