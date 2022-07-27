package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OurArrangementOldArrangementRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    final Context contextForActivity;

    // actual arrangement date, which is "at work"
    long actualArrangementDate = 0;

    // count old arrangement for view
    int countOldArrangementForView = 1;

    // for prefs
    SharedPreferences prefs;

    // Array list for old arrangements
    ArrayList<ObjectSmartEFBArrangement> oldArrangementListForRecyclerView;

    // own reycler view elements
    final static int HEADER = 0;
    final static int ITEM = 1;
    final static int FOOTER = 2;
    final static int BOTH = 3;
    private int STATE;


    // own constructor!!!
    public OurArrangementOldArrangementRecyclerViewAdapter (Context context, ArrayList<ObjectSmartEFBArrangement> arrangementList, int flags) {

        // context of activity OurArrangement
        contextForActivity = context;

        // open sharedPrefs
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);

        // copy array list of arrangements
        oldArrangementListForRecyclerView = arrangementList;

        // init view has item and footer
        STATE = ITEM;
    }


    // inner class for item element
    private class OurArrangementOldArrangementItemViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewOldArrangementDate, textViewOldArrangementNumberText, textViewOldTextArrangement;

        OurArrangementOldArrangementItemViewHolder(View itemView) {

            super(itemView);

            textViewOldArrangementDate = itemView.findViewById(R.id.listOldArrangementDate);
            textViewOldArrangementNumberText = itemView.findViewById(R.id.listOldArrangementNumberText);
            textViewOldTextArrangement = itemView.findViewById(R.id.listOldTextArrangement);

        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new OurArrangementOldArrangementRecyclerViewAdapter.OurArrangementOldArrangementItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_arrangement_old_arrangement_recyclerview_item, parent, false));
    }


    @Override
    public int getItemViewType(int position) {
        // if there is both footerResource and header
        switch (STATE) {
            case BOTH:
                if (position == 0) {
                    return HEADER;
                }
                else if (position >= (oldArrangementListForRecyclerView.size()+1) ) {
                    return FOOTER;
                }
                return ITEM;
            case HEADER:
                    return position == 0 ? HEADER : ITEM;

            case FOOTER:
                if (position >= (oldArrangementListForRecyclerView.size()) ) {
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

                final ObjectSmartEFBArrangement arrangementElement = oldArrangementListForRecyclerView.get(position);

                // cast holder
                OurArrangementOldArrangementRecyclerViewAdapter.OurArrangementOldArrangementItemViewHolder itemView = (OurArrangementOldArrangementRecyclerViewAdapter.OurArrangementOldArrangementItemViewHolder) holder;

                String txtOldArrangementNumber = "";
                if (position == 0 || actualArrangementDate != arrangementElement.getArrangementWriteTime()) { // listview for first element
                    // item + date
                    actualArrangementDate = arrangementElement.getArrangementWriteTime();

                    countOldArrangementForView = 1;

                    String txtArrangementDate = contextForActivity.getResources().getString(R.string.ourArrangementOldArrangementDateIntro) + " " + EfbHelperClass.timestampToDateFormat(actualArrangementDate, "dd.MM.yyyy");
                    itemView.textViewOldArrangementDate.setText(txtArrangementDate);
                    itemView.textViewOldArrangementDate.setVisibility(View.VISIBLE);

                    txtOldArrangementNumber = contextForActivity.getResources().getString(R.string.showOldArrangementIntroText)+ " " + countOldArrangementForView;

                }
                else { // normal item

                    countOldArrangementForView++;

                    itemView.textViewOldArrangementDate.setVisibility(View.GONE);

                    txtOldArrangementNumber = contextForActivity.getResources().getString(R.string.showOldArrangementIntroText)+ " " + countOldArrangementForView;

                }

                // set item number
                itemView.textViewOldArrangementNumberText.setText(txtOldArrangementNumber);

                // set arrangement text
                itemView.textViewOldTextArrangement.setText(arrangementElement.getArrangementText());

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
            return oldArrangementListForRecyclerView.size() + 2;
            case ITEM:
                // if just items, return just the length
                return oldArrangementListForRecyclerView.size();
            default:
                // if either or, return 1 additional
                return oldArrangementListForRecyclerView.size() + 1;
        }
    }


}



