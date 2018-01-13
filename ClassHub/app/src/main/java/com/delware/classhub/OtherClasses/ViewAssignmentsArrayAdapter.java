package com.delware.classhub.OtherClasses;

import android.content.Context;
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
 * Overview: This class extends the BaseAdapter class so I can make the list view in the
 * ViewAssignmentsActivity behave how it is supposed to. See the getView function below.
 * @author Matt Del Fante
 */
public class ViewAssignmentsArrayAdapter extends BaseAdapter
{
    Context m_ctx = null;
    LayoutInflater m_lInflater = null;

    public ViewAssignmentsArrayAdapter(Context context)
    {
        m_ctx = context;
        m_lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Creates the logic so that if an assignment is completed, it's list view element
     * will have a green background and all non-completed assignments will
     * have list view elements with white backgrounds.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        List<AssignmentModel> assignments = SingletonSelectedClass.getInstance().getSelectedClass().getAssignments();

        if (view == null)
            view = m_lInflater.inflate(R.layout.activity_view_assignments_list_item, parent, false);

        //if the assignment was completed, it needs a green background color
        if (assignments.get(position).getIsCompleted())
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
    public Object getItem(int position)
    {
        List<AssignmentModel> assignments = SingletonSelectedClass.getInstance().getSelectedClass().getAssignments();
        return assignments.get(position).getName();
    }
}
