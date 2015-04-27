package com.bioviz.ricardo.bioviz.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bioviz.ricardo.bioviz.AppController;
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.activity.MainActivity;
import com.bioviz.ricardo.bioviz.model.Trivia.Clue;
import com.bioviz.ricardo.bioviz.model.Trivia.TriviaResponse;
import com.bioviz.ricardo.bioviz.utils.Values;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Initial screen
 */
public class Home extends Fragment implements ViewSwitcher.ViewFactory, View.OnClickListener {

    private TextView switcher;
    private TextView triviaSwitcher;
    private int mIndex = 0;
    private int triviaIndex = 0;
    private String[] homeMessages;
    private String[] homeDescriptions;

    private ArrayList<Clue> clues;

    private JsonObjectRequest getRequest;

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
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        switcher = (TextView) rootView.findViewById(R.id.home_switcher);
        triviaSwitcher = (TextView) rootView.findViewById(R.id.trivia_switcher);

        switcher.setOnClickListener(this);

        homeMessages = getResources().getStringArray(R.array.home_messages);
        homeDescriptions = getResources().getStringArray(R.array.home_expanded_messages);

        switcher.setText(homeMessages[mIndex]);

        rootView.findViewById(R.id.switcher_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchNextMessage();
            }
        });

        rootView.findViewById(R.id.trivia_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clues == null || clues.isEmpty()) {
                    AppController.getInstance().addToRequestQueue(getRequest, "trivia_request");
                } else {
                    dispatchNextClue();
                }
            }
        });

        triviaSwitcher.setText(getString(R.string.loading));
        ((ImageView) rootView.findViewById(R.id.iv_trivia)).setImageDrawable(
                getResources().getDrawable(R.drawable.ic_yok_loading)
        );



        getRequest = new JsonObjectRequest(Request.Method.GET,
                "http://jservice.io/api/category?id=21", null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded()) {
                            return;
                        }
                        ArrayList<Clue> inClues = new Gson().fromJson(response.toString(), TriviaResponse.class).getClues();
                        clues = new ArrayList<>();

                        for (Clue c : inClues) {
                            if (!c.getQuestion().equals("")) {
                                clues.add(c);
                            }
                        }

                        long seed = System.nanoTime();
                        Collections.shuffle(clues, new Random(seed));

                        triviaIndex = 0;
                        rootView.findViewById(R.id.trivia_next).setEnabled(true);
                        dispatchNextClue();

                        ((ImageView) rootView.findViewById(R.id.iv_trivia)).setImageDrawable(
                                getResources().getDrawable(R.drawable.ic_yok)
                        );
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ((ImageView) rootView.findViewById(R.id.iv_trivia)).setImageDrawable(
                                getResources().getDrawable(R.drawable.ic_yok_tired)
                        );

                        triviaSwitcher.setText(getString(R.string.trivia_error));
                    }
                }
        );

        // add it to the RequestQueue
        AppController.getInstance().addToRequestQueue(getRequest, "trivia_request");


        return rootView;
    }

    private void dispatchNextClue() {
        triviaIndex++;

        if (triviaIndex == clues.size() - 1) {
            //get new ones
            AppController.getInstance().addToRequestQueue(getRequest, "trivia_request");
            return;
        }

        triviaSwitcher.setText(clues.get(triviaIndex).getQuestion() + "\n\n"
                + "   - " + clues.get(triviaIndex).getAnswer());
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
