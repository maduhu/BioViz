package com.bioviz.ricardo.bioviz.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bioviz.ricardo.bioviz.AppController;
import com.bioviz.ricardo.bioviz.Interface.OnItemClickListener;
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.model.GBIFResponses.GBIFOccurrence;
import com.bioviz.ricardo.bioviz.model.GBIFSpeciesDescription;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class SpeciesDescriptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder > {

    private Context context;
    private ArrayList<GBIFSpeciesDescription> items;
    private GBIFOccurrence occurrenceItem;
    private static OnItemClickListener listener;
    private int textSize;


    public SpeciesDescriptionAdapter(ArrayList<GBIFSpeciesDescription> srcItems,
                                     GBIFOccurrence occurrence,
                                     OnItemClickListener clickListener,
                                     Context context) {

        this.context = context;
        this.occurrenceItem = occurrence;
        this.items = srcItems;
        this.textSize = 14;
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

        //Species details
        if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_species_details, parent, false);

            return new SpeciesExtrasViewHolder(v);
        }

        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.occurence_description_item, parent, false);


        return new DescriptionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder  holder, int position) {
        final GBIFSpeciesDescription item = items.get(position);

        if (getItemViewType(position) == 0) {
            //Occurrence item
            ((OccurrenceViewHolder) holder).tvItemValue.setText(occurrenceItem.getScientificName());
            ((OccurrenceViewHolder) holder).tvItemCountry.setText(occurrenceItem.getCountry() + ", " + occurrenceItem.getYear());
            ((OccurrenceViewHolder) holder).tvItemSpecies.setText(occurrenceItem.getSpecies());

            if (occurrenceItem.getMedia() != null &&
                    occurrenceItem.getMedia().get(0).getIdentifier() != null) {

                Glide.with(context).load(occurrenceItem.getMedia().get(0).getIdentifier())
                        .crossFade()
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher)
                        .into(((OccurrenceViewHolder) holder).ivItemDrawable);
            }
            return;
        }

        if (getItemViewType(position) == 1) {
            //((OccurrenceViewHolder) holder).tvItemValue.setText("Descrição do bicho, alguns extras interessantes");
            return;
        }

        String itemType = item.getType();
        if (!item.getLanguage().equals("")) {
            itemType += " (" + item.getLanguage() + ")";
        }

        //Deal with foreign characters
        ((DescriptionViewHolder) holder).tvDescriptionValue.setText(item.getDescription().replaceAll("[^\\x20-\\x7e]", ""));
        ((DescriptionViewHolder) holder).tvDescriptionType.setText(itemType);
        ((DescriptionViewHolder) holder).tvDescriptionLanguage.setText("");

        ((DescriptionViewHolder) holder).btZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSize -= 1;
                ((DescriptionViewHolder) holder).tvDescriptionValue.setTextSize(textSize);
            }
        });

        ((DescriptionViewHolder) holder).btZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSize += 1;
                ((DescriptionViewHolder) holder).tvDescriptionValue.setTextSize(textSize);
            }
        });

        //Copy text to clipboard
        ((DescriptionViewHolder) holder).btCopy.setOnClickListener(new View.OnClickListener() {
            String text = ((DescriptionViewHolder) holder).tvDescriptionValue.getText().toString();
            @Override
            public void onClick(View v) {
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(text);
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
                    clipboard.setPrimaryClip(clip);
                }

                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.getItemViewType() != 0) {
            textSize = 14;
            ((DescriptionViewHolder) holder).tvDescriptionValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }
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
        private ImageButton btZoomIn;
        private ImageButton btZoomOut;
        private ImageButton btCopy;


        public DescriptionViewHolder(View rowView) {
            super(rowView);

            tvDescriptionValue = (TextView) rowView.findViewById(R.id.tvDescriptionValue);
            tvDescriptionType = (TextView) rowView.findViewById(R.id.tvDescriptionType);
            tvDescriptionLanguage = (TextView) rowView.findViewById(R.id.tvDescriptionLanguage);
            btZoomIn = (ImageButton) rowView.findViewById(R.id.description_item_zoom_in);
            btZoomOut = (ImageButton) rowView.findViewById(R.id.description_item_zoom_out);
            btCopy = (ImageButton) rowView.findViewById(R.id.description_item_copy);

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
        private ImageView ivItemDrawable;


        public OccurrenceViewHolder(View rowView) {
            super(rowView);

            tvItemValue = (TextView) rowView.findViewById(R.id.item_value);
            tvItemCountry = (TextView) rowView.findViewById(R.id.item_country);
            tvItemSpecies = (TextView) rowView.findViewById(R.id.item_species);
            ivItemDrawable = (ImageView) rowView.findViewById(R.id.item_drawable);

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

    static class SpeciesExtrasViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{

        private TextView tvDescriptionValue;


        public SpeciesExtrasViewHolder(View rowView) {
            super(rowView);

            tvDescriptionValue = (TextView) rowView.findViewById(R.id.tvDescriptionValue);

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
