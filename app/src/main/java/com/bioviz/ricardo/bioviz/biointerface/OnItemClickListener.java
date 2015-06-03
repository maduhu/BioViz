package com.bioviz.ricardo.bioviz.biointerface;

import android.view.View;

/**
 * Created by ricardo on 08-02-2015.
 */
public interface OnItemClickListener {
    void onItemClick(View view , int position);
    void onItemLongClick(View view, int position);
}