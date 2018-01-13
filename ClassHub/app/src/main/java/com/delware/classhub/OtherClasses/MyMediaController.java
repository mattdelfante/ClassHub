package com.delware.classhub.OtherClasses;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;

import com.delware.classhub.R;

/**
 * Created by Matt on 1/11/2018.
 */

public class MyMediaController extends MediaController {

    private Context m_context = null;

    public MyMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_context = context;
    }

    public MyMediaController(Context context, boolean useFastForward) {
        super(context, useFastForward);
        m_context = context;
    }

    public MyMediaController(Context context) {
        super(context);
        m_context = context;
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);

//        Button searchButton = new Button(m_context);
//        searchButton.setText(" ");
//        searchButton.setBackgroundColor(Color);
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        params.gravity = Gravity.RIGHT;
//        addView(searchButton, params);

        ImageView appIcon = new ImageView(getContext());
        appIcon.setImageResource(android.R.mipmap.sym_def_app_icon);
        float padding = getResources().getDimension(R.dimen.fab_margin);

        appIcon.setPadding((int) padding, (int) padding / 2, (int) padding, (int) padding / 2);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.START;
        addView(appIcon, params);

        ImageView closeButton = new ImageView(getContext());
        closeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        closeButton.setColorFilter(Color.RED);
        padding = getResources().getDimension(R.dimen.fab_margin);
        closeButton.setPadding((int) padding, (int) padding, (int) padding, (int) padding);
        params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        addView(closeButton, params);
        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                player.finish();
            }
        });
    }

    //    @Override
//    public void show(int timeout) {
//        super.show(0);
//    }
//
//    @Override
//    public void hide() {
//        super.hide();
//    }
}
