package com.intelligentz.sehesara.view;

import android.content.Context;
import android.util.AttributeSet;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

/**
 * Created by Lakshan on 2017-03-31.
 */

public class SelectiveSpiner extends SearchableSpinner {
    OnItemSelectedListener listener;
    public SelectiveSpiner(Context context) {
        super(context);
    }

    public SelectiveSpiner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectiveSpiner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        if (listener != null)
            listener.onItemSelected(null, null, position, 0);
    }

    public void setOnItemSelectedEvenIfUnchangedListener(
            OnItemSelectedListener listener) {
        this.listener = listener;
    }
}
