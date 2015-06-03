package com.bioviz.ricardo.bioviz.adapters.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bioviz.ricardo.bioviz.biointerface.OnItemClickListener;
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.model.GBIF.GBIFMediaElement;
import com.bioviz.ricardo.bioviz.model.GBIF.GBIFOccurrence;
import com.bioviz.ricardo.bioviz.utils.Values;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;

/**
 * Created by riku on 16-04-2015.
 */
public class SpeciesMediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder > {

    private static Context mContext;
    private ArrayList<GBIFMediaElement> items;
    private static OnItemClickListener listener;


    public SpeciesMediaAdapter(ArrayList<GBIFMediaElement> srcItems,
                               GBIFOccurrence occurrence,
                               OnItemClickListener clickListener,
                               Context context) {

        mContext = context;
        GBIFOccurrence occurrenceItem = occurrence;
        this.items = srcItems;
        int textSize = 14;
        listener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;

        if (viewType == Values.ITEM_TYPE_MEDIA) {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_occurence_media, parent, false);
            return new MediaViewHolder(v);
        }

        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.view_end, parent, false);

        return new LastItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder  holder, int position) {
        final GBIFMediaElement item = items.get(position);
        int viewType = getItemViewType(position);

        switch (viewType) {
            case Values.ITEM_TYPE_MEDIA:

                ((MediaViewHolder) holder).creator.setText(item.getCreator());
                ((MediaViewHolder) holder).title.setText(item.getTitle());

                if (item.getIdentifier() != null &&
                        !item.getIdentifier().equals("")) {


                    Glide.with(mContext)
                            .load(item.getIdentifier())
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>(200,200) {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                    (((MediaViewHolder) holder).mediaView).setImageBitmap(resource); // Possibly runOnUiThread()
                                }
                            });
/*
                    Glide.with(context).load(item.getIdentifier())
                            .crossFade()
                            .placeholder(R.drawable.ic_yok_loading)
                            .into(((MediaViewHolder) holder).mediaView);
                            */
                }

            case Values.ITEM_TYPE_END:
                break;
        }
    }



    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class MediaViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{

        private ImageView mediaView;
        private TextView creator;
        private TextView title;

        public MediaViewHolder(View rowView) {
            super(rowView);

            mediaView = (ImageView) rowView.findViewById(R.id.iv_media);
            creator = (TextView) rowView.findViewById(R.id.tv_creator);
            title = (TextView) rowView.findViewById(R.id.tv_title);

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
        switch (position) {
            default:
                if (position == items.size()-1) {
                    return Values.ITEM_TYPE_END;
                }
                return Values.ITEM_TYPE_MEDIA;
        }
    }


    static class LastItemViewHolder extends RecyclerView.ViewHolder {
        public LastItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
