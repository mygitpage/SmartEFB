package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by ich on 30.11.2016.
 */
public class OurGoalsJointlyOldCursorAdapter extends CursorAdapter {


    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // actual jointly goal date, which is "at work"
    long actualJointlyGoalsDate = 0;

    // old jointly goal change true -> change, false -> no change, same Date!
    Boolean oldJointlyGoalsDateChange = false;

    // count old jointly goals for view
    int countOldJointlyGoalsForView = 1;


    // Default constructor
    public OurGoalsJointlyOldCursorAdapter (Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if (oldJointlyGoalsDateChange) { // listview for first element or date change
            TextView tmpOldJointlyGoalDate = (TextView) view.findViewById(R.id.listOldJointlyGoalDate);
            String txtJointlyGoalNumber = context.getResources().getString(R.string.ourGoalsOldJointlyGoalDateIntro) + " " + EfbHelperClass.timestampToDateFormat(actualJointlyGoalsDate, "dd.MM.yyyy");
            tmpOldJointlyGoalDate.setText(txtJointlyGoalNumber);
            oldJointlyGoalsDateChange = false;
        }

        // set old jointly goal text
        TextView textViewJointlyGoal = (TextView) view.findViewById(R.id.listOldTextJointlyGoal);
        String title = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL));
        textViewJointlyGoal.setText(title);

    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        if (cursor.isFirst() || actualJointlyGoalsDate != cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_WRITE_TIME))) { // listview for first element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_old_jointly_goal_start, parent, false);
            actualJointlyGoalsDate = cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_WRITE_TIME));
            oldJointlyGoalsDateChange = true;

            countOldJointlyGoalsForView = 1;

            TextView numberOfOldJointlyGoal = (TextView) inflatedView.findViewById(R.id.listOldJointlyGoalNumberText);
            String txtOldGoalNumber = context.getResources().getString(R.string.showOurGoalsOldJointlyGoalIntroText)+ " " + countOldJointlyGoalsForView;
            numberOfOldJointlyGoal.setText(txtOldGoalNumber);
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_old_jointly_goal, parent, false);
            oldJointlyGoalsDateChange = false;

            countOldJointlyGoalsForView++;

            TextView numberOfOldJointlyGoal = (TextView) inflatedView.findViewById(R.id.listOldJointlyGoalNumberText);
            String txtOldGoalNumber = context.getResources().getString(R.string.showOurGoalsOldJointlyGoalIntroText)+ " " + countOldJointlyGoalsForView;
            numberOfOldJointlyGoal.setText(txtOldGoalNumber);
        }
        
        return inflatedView;

    }



    // Turn off view recycling in listview, because there are different views (first, normal, last)
    // getViewTypeCount(), getItemViewType
    @Override
    public int getViewTypeCount () {

        return getCount();
    }

    @Override
    public int getItemViewType (int position) {

        return position;
    }






}
