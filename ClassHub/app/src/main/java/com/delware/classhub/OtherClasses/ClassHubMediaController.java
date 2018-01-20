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

/**
 * Overview: This class extends the MediaController class to change the UI of the
 * MediaController control panel to include a picture of the ClassHub logo and a back button.
 * @author Matt Del Fante
 */
public class ClassHubMediaController extends MediaController
{
    //The context of the calling activity
    private Context m_context = null;

    public ClassHubMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_context = context;
    }

    public ClassHubMediaController(Context context, boolean useFastForward) {
        super(context, useFastForward);
        m_context = context;
    }

    public ClassHubMediaController(Context context) {
        super(context);
        m_context = context;
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);

        //This will find the id of the Class Hub Logo so I can use it in the MediaController
        //control panel
        int id = getResources().getIdentifier("ic_launcher", "mipmap", m_context.getPackageName());

        ImageView appIcon = new ImageView(getContext());
        appIcon.setImageResource(id);

        float padding = getResources().getDimension(R.dimen.fab_margin);

        appIcon.setPadding((int) padding, (int) padding / 2, (int) padding, (int) padding / 2);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        //this puts the app icon on the left side of the control panel
        params.gravity = Gravity.START;

        //add the app icon to the control panel
        addView(appIcon, params);

        //This will find the id of the back arrow so I can use it in the MediaController control panel
        id = getResources().getIdentifier("ic_arrow_back", "drawable", m_context.getPackageName());

        ImageView backArrow = new ImageView(getContext());
        backArrow.setImageResource(id);
        backArrow.setColorFilter(Color.WHITE);
        backArrow.setPadding((int) padding, (int) padding, (int) padding * 2, (int) padding);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        //puts the back arrow on the right side of the control panel
        params.gravity = Gravity.END;

        //add the back arrow to the control panel
        addView(backArrow, params);

        //make it so the activity will close when the back arrow is pressed
        backArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) m_context).finish();
            }
        });
    }
}
