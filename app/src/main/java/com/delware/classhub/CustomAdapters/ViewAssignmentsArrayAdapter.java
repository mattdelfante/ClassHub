package com.delware.classhub.CustomAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.delware.classhub.DatabaseObjs.AssignmentModel;
import com.delware.classhub.R;
import com.delware.classhub.Singletons.SingletonSelectedClass;

import java.util.List;

/**
 * Created by Matt on 12/12/2017.
 */

//This class extends the BaseAdapter class so I can control the ViewAssignmentsActivity
//to behave how it is supposed to. See the getView function below
public class ViewAssignmentsArrayAdapter extends BaseAdapter
{
    Context m_ctx = null;
    LayoutInflater m_lInflater = null;

    public ViewAssignmentsArrayAdapter(Context context) {
        m_ctx = context;
        m_lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        List<AssignmentModel> assignments = SingletonSelectedClass.getInstance().getSelectedClass().getAssignments();

        if (view == null)
            view = m_lInflater.inflate(R.layout.activity_view_assignments_list_item, parent, false);

        //if the assignment was completed, it needs a green background color
        if (assignments.get(position).getIsCompleted() == true)
            view.setBackgroundResource(R.drawable.list_background_color_green);
        else
            //the background color is white
            view.setBackgroundResource(R.drawable.list_background_color_white);

        //set the name of field to the assignment name
        ((TextView)view.findViewById(R.id.viewAssignmentsActivityListItemHeading)).setText(assignments.get(position).getName());

        return view;
    }

    @Override
    public int getCount() {
        List<AssignmentModel> assignments = SingletonSelectedClass.getInstance().getSelectedClass().getAssignments();
        return assignments.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        List<AssignmentModel> assignments = SingletonSelectedClass.getInstance().getSelectedClass().getAssignments();
        return assignments.get(position).getName();
    }
}
