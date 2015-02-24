package com.bioviz.ricardo.bioviz.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bioviz.ricardo.bioviz.AppController;
import com.bioviz.ricardo.bioviz.Interface.OnItemClickListener;
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.model.GBIFResponses.GBIFOccurrence;
import com.bioviz.ricardo.bioviz.model.iNatResponses.iNatObservation;
import com.bioviz.ricardo.bioviz.utils.Values;

import java.util.ArrayList;


public class OccurrenceListAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Object> items;

    private static OnItemClickListener listener;


    public OccurrenceListAdapter(ArrayList<Object> srcItems,
                                 OnItemClickListener clickListener,
                                 Context context) {

        this.context = context;
        this.items = srcItems;
        listener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        if (viewType == Values.view_observation) {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_observation_list, parent, false);

            return new ObservationViewHolder(v);
        }

        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_occurence_list, parent, false);
        return new OccurrenceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == Values.view_occurrence) {
            final GBIFOccurrence item = (GBIFOccurrence) items.get(position);

            ((OccurrenceViewHolder) holder).tvItemValue.setText(item.getScientificName());
            ((OccurrenceViewHolder) holder).tvItemCountry.setText(item.getCountry() + ", " + item.getYear());
            ((OccurrenceViewHolder) holder).tvItemSpecies.setText(item.getSpecies());

            ImageLoader imageLoader = AppController.getInstance().getImageLoader();
            ((OccurrenceViewHolder) holder).ivItemDrawable.setErrorImageResId(R.drawable.ic_drawer);
            if (item.getMedia() != null &&
                    item.getMedia().get(0).getIdentifier() != null) {
                ((OccurrenceViewHolder) holder).ivItemDrawable.setImageUrl(item.getMedia().get(0).getIdentifier(), imageLoader);
                Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                ((OccurrenceViewHolder) holder).ivItemDrawable.setAnimation(anim);
                anim.start();
            }

        } else if (getItemViewType(position) == Values.view_observation) {
            final iNatObservation item = (iNatObservation) items.get(position);

            ((ObservationViewHolder) holder).tvItemValue.setText(item.getSpecies_guess());
            ((ObservationViewHolder) holder).tvItemCountry.setText(item.getPlace_guess());
            ((ObservationViewHolder) holder).ivItemDrawable.setErrorImageResId(R.drawable.ic_drawer);
            if (item.getPhotos().size() > 0) {
                String thumbUrl;
                if (item.getPhotos().get(0).getMedium_url() != null ) {
                    thumbUrl = item.getPhotos().get(0).getMedium_url();
                } else {
                    thumbUrl = item.getPhotos().get(0).getLarge_url();
                }

                ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                ((ObservationViewHolder) holder).ivItemDrawable.setImageUrl(thumbUrl, imageLoader);
                Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                ((ObservationViewHolder) holder).ivItemDrawable.setAnimation(anim);
                anim.start();
            } else {
                ((ObservationViewHolder) holder).ivItemDrawable.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof GBIFOccurrence) {
            return Values.view_occurrence;
        } else if (items.get(position) instanceof iNatObservation) {
            return Values.view_observation;
        }

        return -1;
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

static class OccurrenceViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener{

    private TextView tvItemValue;
    private TextView tvItemCountry;
    private TextView tvItemSpecies;
    private NetworkImageView ivItemDrawable;


    public OccurrenceViewHolder(View rowView) {
        super(rowView);

        tvItemValue = (TextView) rowView.findViewById(R.id.item_value);
        tvItemCountry = (TextView) rowView.findViewById(R.id.item_country);
        tvItemSpecies = (TextView) rowView.findViewById(R.id.item_species);
        ivItemDrawable = (NetworkImageView) rowView.findViewById(R.id.item_drawable);

        rowView.setOnClickListener(this);
        rowView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View view) {
        listener.onItemClick(view, getPosition());
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}


static class ObservationViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener{

    private TextView tvItemValue;
    private TextView tvItemCountry;
    private TextView tvItemSpecies;
    private NetworkImageView ivItemDrawable;


    public ObservationViewHolder(View rowView) {
        super(rowView);

        tvItemValue = (TextView) rowView.findViewById(R.id.item_value);
        tvItemCountry = (TextView) rowView.findViewById(R.id.item_country);
        tvItemSpecies = (TextView) rowView.findViewById(R.id.item_species);
        ivItemDrawable = (NetworkImageView) rowView.findViewById(R.id.item_drawable);

        rowView.setOnClickListener(this);
        rowView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View view) {
        listener.onItemClick(view, getPosition());
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
}
