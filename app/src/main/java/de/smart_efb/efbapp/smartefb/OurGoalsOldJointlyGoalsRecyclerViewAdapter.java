package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OurGoalsOldJointlyGoalsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final Context contextForActivity;

    // actual jointly goals date, which is "at work"
    long actualJointlyGoalsDate = 0;

    // count old jointly goal for view
    int countOldJointlyGoalsForView = 1;

    // for prefs
    SharedPreferences prefs;

    // Array list for old jointly goals
    ArrayList<ObjectSmartEFBGoals> oldGoalListForRecyclerView;

    // own reycler view elements
    final static int HEADER = 0;
    final static int ITEM = 1;
    final static int FOOTER = 2;
    final static int BOTH = 3;
    private int STATE;


    // own constructor!!!
    public OurGoalsOldJointlyGoalsRecyclerViewAdapter (Context context, ArrayList<ObjectSmartEFBGoals> jointlyOldGoalList, int flags) {

        // context of activity OurGoals
        contextForActivity = context;

        // open sharedPrefs
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);

        // copy array list of jointly goals
        oldGoalListForRecyclerView = jointlyOldGoalList;

        // init view has item and footer
        STATE = ITEM;
    }


    // inner class for item element
    private class OurGoalsOldJointlyGoalsItemViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewOldJointlyGoalDate, textViewOldJointlyGoalNumberText, textViewOldTextJointlyGoal;

        OurGoalsOldJointlyGoalsItemViewHolder(View itemView) {

            super(itemView);

            textViewOldJointlyGoalDate = itemView.findViewById(R.id.listOldJointlyGoalDate);
            textViewOldJointlyGoalNumberText = itemView.findViewById(R.id.listOldJointlyGoalNumberText);
            textViewOldTextJointlyGoal = itemView.findViewById(R.id.listOldTextJointlyGoal);

        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new OurGoalsOldJointlyGoalsRecyclerViewAdapter.OurGoalsOldJointlyGoalsItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_goals_old_jointly_goal_recycler_item, parent, false));
    }


    @Override
    public int getItemViewType(int position) {
        // if there is both footerResource and header
        switch (STATE) {
            case BOTH:
                if (position == 0) {
                    return HEADER;
                }
                else if (position >= (oldGoalListForRecyclerView.size()+1) ) {
                    return FOOTER;
                }
                return ITEM;
            case HEADER:
                return position == 0 ? HEADER : ITEM;

            case FOOTER:
                if (position >= (oldGoalListForRecyclerView.size()) ) {
                    return FOOTER;
                }
                return ITEM;
            default:
                return ITEM;
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = holder.getItemViewType();

        switch (viewType) {
            case ITEM:

                final ObjectSmartEFBGoals oldJointlyGoalElement = oldGoalListForRecyclerView.get(position);

                // cast holder
                OurGoalsOldJointlyGoalsRecyclerViewAdapter.OurGoalsOldJointlyGoalsItemViewHolder itemView = (OurGoalsOldJointlyGoalsRecyclerViewAdapter.OurGoalsOldJointlyGoalsItemViewHolder) holder;

                String txtOldJointlyGoalNumber = "";
                if (position == 0 || actualJointlyGoalsDate != oldJointlyGoalElement.getJointlyGoalWriteTime()) { // recycler view for first element
                    // item + date
                    actualJointlyGoalsDate = oldJointlyGoalElement.getJointlyGoalWriteTime();

                    countOldJointlyGoalsForView = 1;

                    String txtOldJointlyGoalDate = contextForActivity.getResources().getString(R.string.ourGoalsOldJointlyGoalDateIntro) + " " + EfbHelperClass.timestampToDateFormat(actualJointlyGoalsDate, "dd.MM.yyyy");
                    itemView.textViewOldJointlyGoalDate.setText(txtOldJointlyGoalDate);
                    itemView.textViewOldJointlyGoalDate.setVisibility(View.VISIBLE);

                    txtOldJointlyGoalNumber = contextForActivity.getResources().getString(R.string.showOurGoalsOldJointlyGoalIntroText)+ " " + countOldJointlyGoalsForView;

                }
                else { // normal item

                    countOldJointlyGoalsForView++;

                    itemView.textViewOldJointlyGoalDate.setVisibility(View.GONE);

                    txtOldJointlyGoalNumber = contextForActivity.getResources().getString(R.string.showOurGoalsOldJointlyGoalIntroText)+ " " + countOldJointlyGoalsForView;

                }

                // set item number
                itemView.textViewOldJointlyGoalNumberText.setText(txtOldJointlyGoalNumber);

                // set old jointly goal text
                itemView.textViewOldTextJointlyGoal.setText(oldJointlyGoalElement.getGoalText());

                break;

            case HEADER:

                break;

            case FOOTER:

                break;

            default:
                break;
        }

    }


    @Override
    public int getItemCount() {
        switch (STATE) { case BOTH:
            // if both, return 2 additional for header & footer Resource
            return oldGoalListForRecyclerView.size() + 2;
            case ITEM:
                // if just items, return just the length
                return oldGoalListForRecyclerView.size();
            default:
                // if either or, return 1 additional
                return oldGoalListForRecyclerView.size() + 1;
        }
    }
    

}
