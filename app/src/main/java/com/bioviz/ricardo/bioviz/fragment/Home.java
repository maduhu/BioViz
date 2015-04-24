package com.bioviz.ricardo.bioviz.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.ViewSwitcher;

import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.activity.MainActivity;
import com.bioviz.ricardo.bioviz.utils.Values;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Initial screen
 */
public class Home extends Fragment implements ViewSwitcher.ViewFactory, View.OnClickListener {

    private TextSwitcher switcher;
    private int mIndex = 0;
    private Timer mTimer;
    private String[] homeMessages;
    private String[] homeDescriptions;

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
                android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.fade_out);

        switcher.setInAnimation(in);
        switcher.setOutAnimation(out);
        switcher.setOnClickListener(this);


        homeMessages = getResources().getStringArray(R.array.home_messages);
        homeDescriptions = getResources().getStringArray(R.array.home_expanded_messages);

        switcher.setText(homeMessages[mIndex]);

        mTimer =new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           dispatchNextMessage();
                        }
                    });
                }
            }
        }, 10000, 10000);

        rootView.findViewById(R.id.switcher_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimer != null) {
                    mTimer.cancel();
                }
                dispatchNextMessage();
            }
        });

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

        ObjectAnimator animation = ObjectAnimator.ofInt(switcher, "maxLines", 40);
        switcher.setText(homeDescriptions[mIndex]);
        animation.setDuration(200).start();

        /*
        new AlertDialog.Builder(getActivity())
                .setMessage(homeDescriptions[mIndex])
                .setIcon(R.drawable.ic_yok_angry)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
                */
    }

    private void dispatchNextMessage() {
        Log.e("INDEX", "" + mIndex);
        switcher.setText(homeMessages[mIndex]);
        if (mIndex == homeMessages.length-1) {
            mIndex = 0;
        } else {
            mIndex++;
        }
    }
}
