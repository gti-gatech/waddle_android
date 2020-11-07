package com.appzoro.milton.util_files;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appzoro.milton.R;

public class CustomDialogImage extends Dialog implements View.OnClickListener {

    protected CustomDialogImage(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (getWindow() != null)
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.argb(25, 0, 0, 0)));
        setContentView(R.layout.custom_dialog_image);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
        setCanceledOnTouchOutside(true);

        TextView take_picture = findViewById(R.id.take_picture);
        TextView select_picture = findViewById(R.id.select_picture);
        TextView lin_cancel = findViewById(R.id.lin_cancel);

        lin_cancel.setOnClickListener(this);
        take_picture.setOnClickListener(this);
        select_picture.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_picture:
            case R.id.select_picture:
            default:
                break;
        }
        dismiss();
    }
}

