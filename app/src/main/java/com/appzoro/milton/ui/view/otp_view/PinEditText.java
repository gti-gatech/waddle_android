package com.appzoro.milton.ui.view.otp_view;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;

public class PinEditText extends androidx.appcompat.widget.AppCompatEditText {

    ArrayList<PinEditTextListener> listeners;

    public PinEditText(Context context)
    {
        super(context);
        listeners = new ArrayList<>();
    }

    public PinEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        listeners = new ArrayList<>();
    }

    public PinEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        listeners = new ArrayList<>();
    }

    public void addListener(PinEditTextListener listener) {
        try {
            listeners.add(listener);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Here you can catch paste, copy and cut events
     */
    @Override
    public boolean onTextContextMenuItem(int id) {
        boolean consumed = super.onTextContextMenuItem(id);
        switch (id){
            case android.R.id.cut:
                onTextCut();
                break;
            case android.R.id.paste:
                onTextPaste();
                break;
            case android.R.id.copy:
                onTextCopy();
        }
        return consumed;
    }

    public void onTextCut(){
    }

    public void onTextCopy(){
    }

    /**
     * adding listener for Paste for example
     */
    public void onTextPaste(){
        for (PinEditTextListener listener : listeners) {
            listener.onUpdate();
        }
    }
}
