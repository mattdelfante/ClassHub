package com.delware.classhub.OtherClasses;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;

import com.delware.classhub.R;

public class MyMediaController extends MediaController
{
    //The context of the calling activity
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

        //This will find the id of the Class Hub App Logo so I can use it in the MediaController
        //control panel
        int id = getResources().getIdentifier("ic_launcher", "mipmap", m_context.getPackageName());

        ImageView appIcon = new ImageView(getContext());
        appIcon.setImageResource(id);

        float padding = getResources().getDimension(R.dimen.fab_margin);

        appIcon.setPadding((int) padding, (int) padding / 2, (int) padding, (int) padding / 2);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        //put the app icon on the left side of the control panel
        params.gravity = Gravity.START;

        //add the app icon to the control panel
        addView(appIcon, params);

        //This will find the id of the back arrow so I can use it in the MediaController
        //control panel
        id = getResources().getIdentifier("ic_arrow_back", "drawable", m_context.getPackageName());

        ImageView backArrow = new ImageView(getContext());
        backArrow.setImageResource(id);
        backArrow.setColorFilter(Color.WHITE);
        backArrow.setPadding((int) padding, (int) padding, (int) padding * 2, (int) padding);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        //put the app icon on the right side of the control panel
        params.gravity = Gravity.END;

        //add the back arrow to the control panel
        addView(backArrow, params);

        //makes it so the activity will close when the back arrow is pressed
        backArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) m_context).finish();
            }
        });
    }
}
