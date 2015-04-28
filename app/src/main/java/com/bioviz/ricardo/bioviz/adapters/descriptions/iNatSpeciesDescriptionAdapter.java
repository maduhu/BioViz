package com.bioviz.ricardo.bioviz.adapters.descriptions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bioviz.ricardo.bioviz.Interface.OnItemClickListener;
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.model.iNatResponses.iNatObservation;
import com.bioviz.ricardo.bioviz.utils.Values;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by ricardo on 17-03-2015.
 */
public class iNatSpeciesDescriptionAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder > {

    private static Context context;
    private ArrayList<String> items;
    private iNatObservation observationItem;
    private static OnItemClickListener listener;

    public iNatSpeciesDescriptionAdapter(ArrayList<String> srcItems,
                                         iNatObservation observation,
                                         OnItemClickListener clickListener,
                                         Context context) {

        this.context = context;
        this.observationItem = observation;
        this.items = srcItems;
        listener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        if (viewType == Values.INAT_ITEM_TYPE_HEADER) {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_occurence_list, parent, false);

            return new OccurrenceViewHolder(v);
        }

        //Species details
        if (viewType == Values.INAT_ITEM_TYPE_EXTRAS) {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_species_details, parent, false);

            return new SpeciesExtrasViewHolder(v);
        }

        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.view_end, parent, false);

        return new LastItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder  holder, int position) {

        int viewType = getItemViewType(position);

        switch (viewType) {
            case Values.INAT_ITEM_TYPE_HEADER:
                ((OccurrenceViewHolder) holder).tvItemValue.setText(observationItem.getSpecies_guess());
                ((OccurrenceViewHolder) holder).tvItemCountry.setText(observationItem.getPlace_guess());
                ((OccurrenceViewHolder) holder).tvItemSpecies.setText("ITEM SPECIES HERE");

                if (!observationItem.getUri().equals("")) {

                    Glide.with(context).load(observationItem.getUri())
                            .crossFade()
                            .centerCrop()
                            .placeholder(R.drawable.ic_yok_loading)
                            .into(((OccurrenceViewHolder) holder).ivItemDrawable);
                }
                break;

            case Values.INAT_ITEM_TYPE_EXTRAS:

            default:
                break;
        }
        /*
        int viewType = getItemViewType(position);

        switch (viewType) {
            case Values.ITEM_TYPE_HEADER:
                ((OccurrenceViewHolder) holder).tvItemValue.setText(occurrenceItem.getScientificName());
                ((OccurrenceViewHolder) holder).tvItemCountry.setText(occurrenceItem.getCountry() + ", " + occurrenceItem.getYear());
                ((OccurrenceViewHolder) holder).tvItemSpecies.setText(occurrenceItem.getSpecies());

                if (occurrenceItem.getMedia() != null &&
                        occurrenceItem.getMedia().get(0).getIdentifier() != null) {

                    Glide.with(context).load(occurrenceItem.getMedia().get(0).getIdentifier())
                            .crossFade()
                            .centerCrop()
                            .placeholder(R.drawable.ic_yok_loading)
                            .into(((OccurrenceViewHolder) holder).ivItemDrawable);
                }
                break;

            case Values.ITEM_TYPE_EXTRAS:
                ((SpeciesExtrasViewHolder) holder).tvExtrasKingdom.setText(occurrenceItem.getKingdom());
                ((SpeciesExtrasViewHolder) holder).tvExtrasPhylum.setText(occurrenceItem.getPhylum());
                ((SpeciesExtrasViewHolder) holder).tvExtrasClass.setText(occurrenceItem.getSpeciesClass());
                ((SpeciesExtrasViewHolder) holder).tvExtrasOrder.setText(occurrenceItem.getOrder());
                ((SpeciesExtrasViewHolder) holder).tvExtrasFamily.setText(occurrenceItem.getFamily());
                ((SpeciesExtrasViewHolder) holder).tvExtrasGenus.setText(occurrenceItem.getGenus());
                ((SpeciesExtrasViewHolder) holder).tvExtrasSpecies.setText(occurrenceItem.getSpecies());
                break;

            case Values.ITEM_TYPE_DESCRIPTION:
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
                break;
            case Values.ITEM_TYPE_END:

                break;
        }*/
    }


    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return Values.ITEM_TYPE_HEADER;
            case 1:
                return Values.ITEM_TYPE_EXTRAS;
            default:
                if (position == items.size()-1) {
                    return Values.ITEM_TYPE_END;
                } else {
                    return Values.ITEM_TYPE_DESCRIPTION;
                }
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    /**
     * Species identification and basic picture
     */
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

    /**
     * ViewHolder for basic string resources (must be formatted when passed to the adapter)
     */
    static class SpeciesExtrasViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{

        private TextView tvExtrasHeader;

        public SpeciesExtrasViewHolder(View rowView) {
            super(rowView);

            tvExtrasHeader = (TextView) rowView.findViewById(R.id.tvExtrasHeader);
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

    static class LastItemViewHolder extends RecyclerView.ViewHolder {

        public LastItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
