package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OurArrangementNowArrangementRecylerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    final Context contextForActivity;

    // hold layoutInflater
    private LayoutInflater cursorInflater;

    //limitation in count comments true-> yes, there is a border; no, there is no border
    Boolean commentLimitationBorder;

    // for prefs
    SharedPreferences prefs;

    // number for count comments for arrangement (12 numbers!)
    private String[] numberCountForComments = new String [12];

    // Array list for now arrangements
    ArrayList<ObjectSmartEFBArrangement> nowArrangementListForRecyclerView;

    // own reycler view elements
    final static int HEADER = 0;
    final static int ITEM = 1;
    final static int FOOTER = 2;
    final static int BOTH = 3;
    private int STATE;






    // own constructor!!!
    public OurArrangementNowArrangementRecylerViewAdapter (Context context, ArrayList<ObjectSmartEFBArrangement> arrangementList, int flags) {

        // context of activity OurArrangement
        contextForActivity = context;

        // get inflater service
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // init array for count comments
        numberCountForComments = context.getResources().getStringArray(R.array.ourArrangementCountComments);

        //limitation in count comments true-> yes, there is a border; no, there is no border
        commentLimitationBorder = ((ActivityOurArrangement)context).isCommentLimitationBorderSet("current");

        // open sharedPrefs
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);

        // copy array list of arrangements
        nowArrangementListForRecyclerView = arrangementList;

        // init view has header, items and footer
        STATE = HEADER;

    }




    // inner class for header element
    private class OurArrangementNowArrangementHeaderViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewNumberOfArrangement, textViewNewSignalForArrangement, textViewAuthorNameForArrangement;
        public TextView textViewArrangementText, textViewLinkForCommentAnArrangement, textViewLinkForShowCommentOfOneArrangement, textViewLinkForEvaluationOfAnArrangement;

        
        OurArrangementNowArrangementHeaderViewHolder(View headerView) {

            super(headerView);
            
            textViewNumberOfArrangement = headerView.findViewById(R.id.listArrangementNumberText);
            textViewNewSignalForArrangement = headerView.findViewById(R.id.listArrangementNewArrangementText);
            textViewAuthorNameForArrangement = headerView.findViewById(R.id.listTextAuthorName);
            textViewArrangementText = headerView.findViewById(R.id.listTextArrangement);
            textViewLinkForCommentAnArrangement = headerView.findViewById(R.id.linkCommentAnArrangement);
            textViewLinkForShowCommentOfOneArrangement = headerView.findViewById(R.id.linkToShowCommentsOfArrangements);
            textViewLinkForEvaluationOfAnArrangement = headerView.findViewById(R.id.linkToEvaluateAnArrangement);
        }

    }


    // inner class for item element
    private class OurArrangementNowArrangementFooterViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewBorderBetweenElementAndFooterNothingElse, textViewBorderBetweenElementAndFooter, textViewInfoTextForEvaluationTimePeriod;
        

        OurArrangementNowArrangementFooterViewHolder(View footerView) {

            super (footerView);
            
            textViewBorderBetweenElementAndFooterNothingElse = footerView.findViewById(R.id.borderToBottomOfDisplayWhenNeeded);
            textViewBorderBetweenElementAndFooter = footerView.findViewById(R.id.borderBetweenLastElementAndEvaluationInfo);
            textViewInfoTextForEvaluationTimePeriod = footerView.findViewById(R.id.infoEvaluationTimePeriod);
         }
    }
    
    
    // inner class for item element
    private class OurArrangementNowArrangementItemViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewNumberOfArrangement, textViewNewSignalForArrangement, textViewAuthorNameForArrangement;
        public TextView textViewArrangementText, textViewLinkForCommentAnArrangement, textViewLinkForShowCommentOfOneArrangement, textViewLinkForEvaluationOfAnArrangement;


        OurArrangementNowArrangementItemViewHolder(View itemView) {

            super(itemView);

            textViewNumberOfArrangement = itemView.findViewById(R.id.listArrangementNumberText);
            textViewNewSignalForArrangement = itemView.findViewById(R.id.listArrangementNewArrangementText);
            textViewAuthorNameForArrangement = itemView.findViewById(R.id.listTextAuthorName);
            textViewArrangementText = itemView.findViewById(R.id.listTextArrangement);
            textViewLinkForCommentAnArrangement = itemView.findViewById(R.id.linkCommentAnArrangement);
            textViewLinkForShowCommentOfOneArrangement = itemView.findViewById(R.id.linkToShowCommentsOfArrangements);
            textViewLinkForEvaluationOfAnArrangement = itemView.findViewById(R.id.linkToEvaluateAnArrangement);
        }
    }



















    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == HEADER) {
            return new OurArrangementNowArrangementRecylerViewAdapter.OurArrangementNowArrangementHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_arrangement_now_arrangement_recyclerview_header, parent, false));
        }
        
        if (viewType == FOOTER) {
            return new OurArrangementNowArrangementRecylerViewAdapter.OurArrangementNowArrangementFooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_arrangement_now_arrangement_recyclerview_footer, parent, false));
        }
        
        return new OurArrangementNowArrangementRecylerViewAdapter.OurArrangementNowArrangementItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_arrangement_now_arrangement_recyclerview_item, parent, false));
    }




    @Override
    public int getItemViewType(int position) {
        // if there is both footerResource and header
        switch (STATE) {
            case BOTH:
                if (position == 0) {
                    return HEADER;
                }
                else if (position >= (nowArrangementListForRecyclerView.size()+1) ) {
                    return FOOTER;
                }
                return ITEM;
            case HEADER:
                return position == 0 ? HEADER : ITEM;

            case FOOTER:
                return position == nowArrangementListForRecyclerView.size() ? FOOTER : ITEM;

            default:
                return ITEM;
        }
    }






    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int actualPosition = position;
        int viewType = holder.getItemViewType();

        // init the DB
        final DBAdapter myDb = new DBAdapter(contextForActivity);

        switch (viewType) {
            case ITEM:

                final ObjectSmartEFBArrangement arrangementElement = nowArrangementListForRecyclerView.get(position-1);

                // cast holder
                OurArrangementNowArrangementRecylerViewAdapter.OurArrangementNowArrangementItemViewHolder itemView = (OurArrangementNowArrangementRecylerViewAdapter.OurArrangementNowArrangementItemViewHolder) holder;


                break;
            case HEADER:

                // cast holder
                OurArrangementNowArrangementRecylerViewAdapter.OurArrangementNowArrangementHeaderViewHolder headerView = (OurArrangementNowArrangementRecylerViewAdapter.OurArrangementNowArrangementHeaderViewHolder) holder;


                break;
            case FOOTER:

                // cast holder
                OurArrangementNowArrangementRecylerViewAdapter.OurArrangementNowArrangementFooterViewHolder footerView = (OurArrangementNowArrangementRecylerViewAdapter.OurArrangementNowArrangementFooterViewHolder) holder;



                break;

            default:
                break;
        }

        // close DB connection
        myDb.close();

    }







    @Override
    public int getItemCount() {
        switch (STATE) {
            case BOTH:
                // if both, return 2 additional for header & footer Resource
                return nowArrangementListForRecyclerView.size() + 2;
            case ITEM:
                // if just items, return just the length
                return nowArrangementListForRecyclerView.size();
            default:
                // if either or, return 1 additional
                return nowArrangementListForRecyclerView.size() + 1;
        }
    }












}
