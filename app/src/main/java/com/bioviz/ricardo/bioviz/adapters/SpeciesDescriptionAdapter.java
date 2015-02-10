package com.bioviz.ricardo.bioviz.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import com.bioviz.ricardo.bioviz.model.GBIFOccurrence;
import com.bioviz.ricardo.bioviz.model.GBIFSpeciesDescription;

import java.util.ArrayList;


public class SpeciesDescriptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder > {

    private Context context;
    private ArrayList<GBIFSpeciesDescription> items;
    private static OnItemClickListener listener;


    public SpeciesDescriptionAdapter(ArrayList<GBIFSpeciesDescription> srcItems,
                                     OnItemClickListener clickListener,
                                     Context context) {

        this.context = context;
        this.items = srcItems;
        listener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        if (viewType == 0) {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_occurence_list, parent, false);

            return new OccurrenceViewHolder(v);
        }

        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.occurence_description_item, parent, false);


        return new DescriptionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder  holder, int position) {
        final GBIFSpeciesDescription item = items.get(position);

        if (getItemViewType(position) == 0) {
            //TODO: load this view type
            ((OccurrenceViewHolder) holder).tvItemCountry.setText("CENAS DO PAIS");

            ((OccurrenceViewHolder) holder).tvItemValue.setText(i.getScientificName());
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

            return;
        }

        String itemType = item.getType();
        if (!item.getLanguage().equals("")) {
            itemType += " (" + item.getLanguage() + ")";
        }

        //Deal with foreign characters
        ((DescriptionViewHolder) holder).tvDescriptionValue.setText(item.getDescription().replaceAll("[^\\x20-\\x7e]", ""));
        ((DescriptionViewHolder) holder).tvDescriptionType.setText(itemType);
        ((DescriptionViewHolder) holder).tvDescriptionLanguage.setText("empty field");
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class DescriptionViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{

        private TextView tvDescriptionValue;
        private TextView tvDescriptionLanguage;
        private TextView tvDescriptionType;


        public DescriptionViewHolder(View rowView) {
            super(rowView);

            tvDescriptionValue = (TextView) rowView.findViewById(R.id.tvDescriptionValue);
            tvDescriptionType = (TextView) rowView.findViewById(R.id.tvDescriptionType);
            tvDescriptionLanguage = (TextView) rowView.findViewById(R.id.tvDescriptionLanguage);

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

    @Override
    public int getItemViewType(int position) {
        //header
        if (position == 0) {
            return 0;
        }
        return 1;
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
}
