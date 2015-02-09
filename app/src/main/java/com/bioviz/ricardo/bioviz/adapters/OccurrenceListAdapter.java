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

import java.util.ArrayList;


public class OccurrenceListAdapter  extends RecyclerView.Adapter<OccurrenceListAdapter.OccurrenceViewHolder> {

    private Context context;
    private ArrayList<GBIFOccurrence> items;

    private static OnItemClickListener listener;


    public OccurrenceListAdapter(ArrayList<GBIFOccurrence> srcItems,
                                 OnItemClickListener clickListener,
                                 Context context) {

        this.context = context;
        this.items = srcItems;
        listener = clickListener;
    }

    @Override
    public OccurrenceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_occurence_list, parent, false);


        return new OccurrenceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final OccurrenceViewHolder holder, int position) {
        final GBIFOccurrence item = items.get(position);

        holder.tvItemValue.setText(item.getScientificName());
        holder.tvItemCountry.setText(item.getCountry() + ", " + item.getYear());
        holder.tvItemSpecies.setText(item.getSpecies());

        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        holder.ivItemDrawable.setErrorImageResId(R.drawable.ic_drawer);
        if (item.getMedia() != null &&
                item.getMedia().get(0).getIdentifier() != null) {
            holder.ivItemDrawable.setImageUrl(item.getMedia().get(0).getIdentifier(), imageLoader);
            Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            holder.ivItemDrawable.setAnimation(anim);
            anim.start();
        }
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
}
