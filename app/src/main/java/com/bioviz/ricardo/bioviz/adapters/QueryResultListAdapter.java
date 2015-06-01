package com.bioviz.ricardo.bioviz.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bioviz.ricardo.bioviz.Interface.OnItemClickListener;
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.model.GBIF.GBIFOccurrence;
import com.bioviz.ricardo.bioviz.model.iNat.iNatObservation;
import com.bioviz.ricardo.bioviz.utils.Values;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;


/**
 * Adapter to hold all the defined object types.
 * For now, it is configured to hold both observations (GBIF) and occurrences (iNat)
 */
public class QueryResultListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Object> items;
    private int lastPosition = -1;

    private static OnItemClickListener listener;


    public QueryResultListAdapter(ArrayList<Object> srcItems,
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
            bindOccurrenceHolder((OccurrenceViewHolder)holder, item, position);

        } else if (getItemViewType(position) == Values.view_observation) {

            final iNatObservation item = (iNatObservation) items.get(position);
            bindObservationViewHolder((ObservationViewHolder)holder, item, position);
        }
    }

    /**
     * Binds the viewholder for GBIF observations
     * @param holder holder to manipulate (casted)
     * @param item occurrence item
     * @param position position in the view
     */
    private void bindObservationViewHolder(final ObservationViewHolder holder, iNatObservation item, int position) {

        holder.tvItemValue.setText(item.getSpecies_guess());
        holder.tvItemCountry.setText(item.getPlace_guess());
        holder.rlItemLegend
                .setBackgroundColor(context.getResources().getColor(R.color.text_blue_grey_darker));

        if (item.getPhotos().size() > 0) {
            String thumbUrl;
            if (item.getPhotos().get(0).getMedium_url() != null ) {
                thumbUrl = item.getPhotos().get(0).getMedium_url();
            } else {
                thumbUrl = item.getPhotos().get(0).getLarge_url();
            }

            Glide.with(context).load(thumbUrl)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.ic_yok_loading)
                    .into(new BitmapImageViewTarget(holder.ivItemDrawable) {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            super.onResourceReady(bitmap, anim);
                            Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    // Here's your generated palette

                                    holder.rlItemLegend.setBackgroundColor(
                                            palette.getLightVibrantColor(R.color.text_blue_grey_darker));

                                    holder.rlItemLegend.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });

            setAnimation(holder.itemContainer, position);
        } else {
            holder.ivItemDrawable.setVisibility(View.GONE);
        }
    }

    /**
     * Binds the viewholder for iNatObservation
     * @param holder holder to manipulate (casted)
     * @param item observation item
     * @param position position in the view
     */
    private void bindOccurrenceHolder(final OccurrenceViewHolder holder, GBIFOccurrence item, int position) {
        String location = "";
        if (item.getVerbatimLocality() != null) {
            location += item.getVerbatimLocality();
        }

        String coordinates = "";
        if (item.getDecimalLatitude() != null &&
                item.getDecimalLongitude() != null) {
            coordinates += item.getDecimalLatitude() + ", " + item.getDecimalLongitude();
        }

        holder.tvItemValue.setText(item.getScientificName());
        holder.tvItemCountry.setText(location + item.getCountry() + ", " + item.getYear());
        holder.tvItemSpecies.setText(item.getSpecies());
        holder.tvItemCoordinates.setText(coordinates);
        holder.rlItemLegend
                .setBackgroundColor(context.getResources().getColor(R.color.text_blue_grey_darker));

        if (item.getMedia() != null &&
                item.getMedia().get(0).getIdentifier() != null) {

            Glide.with(context).load(item.getMedia().get(0).getIdentifier())
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.ic_yok_loading)
                    .into(new BitmapImageViewTarget(holder.ivItemDrawable) {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            super.onResourceReady(bitmap, anim);
                            Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    holder.rlItemLegend.setBackgroundColor(
                                            palette.getVibrantColor(R.color.text_blue_grey_darker));

                                    holder.rlItemLegend.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
        } else {
            holder.ivItemDrawable.setVisibility(View.GONE);
        }
        setAnimation(holder.itemContainer, position);
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
            implements View.OnClickListener, View.OnLongClickListener {

        private TextView tvItemValue;
        private TextView tvItemCoordinates;
        private TextView tvItemCountry;
        private TextView tvItemSpecies;
        private ImageView ivItemDrawable;
        private CardView itemContainer;
        private RelativeLayout rlItemLegend;


        public OccurrenceViewHolder(View rowView) {
            super(rowView);

            tvItemValue = (TextView) rowView.findViewById(R.id.item_value);
            tvItemCountry = (TextView) rowView.findViewById(R.id.item_country);
            tvItemSpecies = (TextView) rowView.findViewById(R.id.item_species);
            tvItemCoordinates = (TextView) rowView.findViewById(R.id.item_coordinates);
            ivItemDrawable = (ImageView) rowView.findViewById(R.id.item_drawable);
            itemContainer = (CardView) rowView.findViewById(R.id.item_card_container);
            rlItemLegend = (RelativeLayout) rowView.findViewById(R.id.rl_item_legend);

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
        private ImageView ivItemDrawable;
        private CardView itemContainer;
        private RelativeLayout rlItemLegend;


        public ObservationViewHolder(View rowView) {
            super(rowView);

            tvItemValue = (TextView) rowView.findViewById(R.id.item_value);
            tvItemCountry = (TextView) rowView.findViewById(R.id.item_country);
            ivItemDrawable = (ImageView) rowView.findViewById(R.id.item_drawable);
            itemContainer = (CardView) rowView.findViewById(R.id.item_card_container);
            rlItemLegend = (RelativeLayout) rowView.findViewById(R.id.rl_item_legend);

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

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
