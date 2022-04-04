package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OurGoalsDebetableGoalsNowRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final Context contextForActivity;

    // hold layoutInflater
    private LayoutInflater cursorInflater;

    //limitation in count comments true-> yes, there is a border; no, there is no border
    Boolean commentLimitationBorder;

    // for prefs
    SharedPreferences prefs;

    // number for count comments for debetable goals (12 numbers!)
    private String[] numberCountForAssessments = new String [12];

    // Array list for debetable goals
    ArrayList<ObjectSmartEFBGoals> debetableGoalsListForRecyclerView;

    // own reycler view elements
    final static int HEADER = 0;
    final static int ITEM = 1;
    final static int FOOTER = 2;
    final static int BOTH = 3;
    private int STATE;


    // own constructor!!!
    public OurGoalsDebetableGoalsNowRecyclerViewAdapter (Context context, ArrayList<ObjectSmartEFBGoals> goalsDebetableList, int flags) {

        // context of activity OurArrangement
        contextForActivity = context;

        // get inflater service
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // init array for count comments
        numberCountForAssessments = context.getResources().getStringArray(R.array.ourGoalsDebetableGoalsCountAssessments);

        //limitation in count comments true-> yes, there is a border; no, there is no border
        commentLimitationBorder = ((ActivityOurGoals)context).isCommentLimitationBorderSet("debetableGoals");

        // open sharedPrefs
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);

        // copy array list of debetable goals
        debetableGoalsListForRecyclerView = goalsDebetableList;

        // init view has only items
        STATE = ITEM;

    }


    // inner class for item element
    private class OurGoalsDebetableGoalsItemViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewBorderBetweenItems, textViewNumberOfDebetableGoal, textViewNewSignalForDebetableGoal, textViewAuthorNameForDebetableGoal;
        public TextView textViewDebetableGoalText, textViewLinkForCommentAnDebetableGoal, textViewLinkForShowCommentOfOneDebetableGoal;

        OurGoalsDebetableGoalsItemViewHolder(View itemView) {

            super(itemView);

            textViewBorderBetweenItems = itemView.findViewById(R.id.borderBetweenDebetableGoals);
            textViewNumberOfDebetableGoal = itemView.findViewById(R.id.listDebetableGoalsNumberText);
            textViewNewSignalForDebetableGoal = itemView.findViewById(R.id.listDebetableGoalsNewGoalText);
            textViewAuthorNameForDebetableGoal = itemView.findViewById(R.id.listTextDebetableAuthorName);
            textViewDebetableGoalText = itemView.findViewById(R.id.listTextDebetableGoal);
            textViewLinkForCommentAnDebetableGoal = itemView.findViewById(R.id.linkCommentAnDebetableGoal);
            textViewLinkForShowCommentOfOneDebetableGoal = itemView.findViewById(R.id.linkToShowCommentsOfDebetableGoal);

        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new OurGoalsDebetableGoalsNowRecyclerViewAdapter.OurGoalsDebetableGoalsItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_goals_debetable_goals_recyclerview_item, parent, false));
    }


    @Override
    public int getItemViewType(int position) {
        // if there is both footerResource and header
        switch (STATE) {
            case BOTH:
                if (position == 0) {
                    return HEADER;
                }
                else if (position >= (debetableGoalsListForRecyclerView.size()+1) ) {
                    return FOOTER;
                }
                return ITEM;
            case HEADER:
                return position == 0 ? HEADER : ITEM;

            case FOOTER:
                if (position >= (debetableGoalsListForRecyclerView.size()) ) {
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

        // init the DB
        final DBAdapter myDb = new DBAdapter(contextForActivity);

        switch (viewType) {
            case ITEM:

                final ObjectSmartEFBGoals debetableGoalElement = debetableGoalsListForRecyclerView.get(position);

                // cast holder
                OurGoalsDebetableGoalsNowRecyclerViewAdapter.OurGoalsDebetableGoalsItemViewHolder itemView = (OurGoalsDebetableGoalsNowRecyclerViewAdapter.OurGoalsDebetableGoalsItemViewHolder) holder;

                // put debetable goal number
                int debetableGoalElementPositionNumber = 0;
                if (prefs.getString(ConstansClassOurGoals.namePrefsSortSequenceOfDebetableGoalsList, "descending").equals("descending")) {
                    debetableGoalElementPositionNumber = debetableGoalsListForRecyclerView.size() - debetableGoalElement.getPositionNumber() + 1;
                }
                else {
                    debetableGoalElementPositionNumber = debetableGoalElement.getPositionNumber();
                }
                String txtDebetableGoalNumber = contextForActivity.getResources().getString(R.string.showDebetableGoalNumberText)+ " " + debetableGoalElementPositionNumber;
                itemView.textViewNumberOfDebetableGoal.setText(txtDebetableGoalNumber);

                // put author name
                String tmpTextAuthorNameText = String.format(contextForActivity.getResources().getString(R.string.ourGoalsDebetableGoalsAuthorNameTextWithDate), debetableGoalElement.getAuthorName(), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfDebetableGoals, System.currentTimeMillis()), "dd.MM.yyyy"));
                itemView.textViewAuthorNameForDebetableGoal.setText(HtmlCompat.fromHtml(tmpTextAuthorNameText, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // check if debetable goal entry new?
                if (debetableGoalElement.getNewEntry() == 1) {
                    itemView.textViewNewSignalForDebetableGoal.setVisibility(View.VISIBLE);
                    myDb.deleteStatusNewEntryOurGoals(debetableGoalElement.getRowID());
                }

                // put debetable goal text
                itemView.textViewDebetableGoalText.setText(debetableGoalElement.getGoalText());

                if (prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkCommentDebetableGoals, false)) {

                    // generate difference text for comment anymore
                    String tmpNumberCommentsPossible;
                    int tmpDifferenceComments = prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0) - prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, 0);

                    if (commentLimitationBorder) {
                        if (tmpDifferenceComments > 0) {
                            if (tmpDifferenceComments > 1) { //plural comments
                                tmpNumberCommentsPossible = String.format(contextForActivity.getString(R.string.infoTextNumberOfDebetableCommentsPossiblePlural), tmpDifferenceComments);
                            } else { // singular comments
                                tmpNumberCommentsPossible = String.format(contextForActivity.getString(R.string.infoTextNumberOfDebetableCommentsPossibleSingular), tmpDifferenceComments);
                            }
                        }
                        else {
                            tmpNumberCommentsPossible = contextForActivity.getString(R.string.infoTextNumberOfDebetableCommentsPossibleNoMore);
                        }
                    }
                    else {
                        tmpNumberCommentsPossible = contextForActivity.getString(R.string.infoTextNumberOfDebetableCommentsPossibleNoBorder);
                    }

                    // get from DB  all comments for chose debetable goal (getCount)
                    Cursor cursorDebetableGoalsAllComments = myDb.getAllRowsOurGoalsDebetableGoalsComment (debetableGoalElement.getServerIdGoal(), "descending");

                    // generate the number of comments to show
                    String tmpCountAssessments;
                    int tmpIntCountComments = cursorDebetableGoalsAllComments.getCount();
                    if (tmpIntCountComments > 10) {
                        tmpCountAssessments = numberCountForAssessments[11];

                    }
                    else {
                        tmpCountAssessments = numberCountForAssessments[cursorDebetableGoalsAllComments.getCount()];
                    }

                    // check comments for new entry, the cursor is sorted DESC, so first element is newest!!! new entry is markt by == 1
                    String tmpTextNewEntryComment = "";
                    if (cursorDebetableGoalsAllComments.getCount() > 0) {
                        cursorDebetableGoalsAllComments.moveToFirst();
                        if (cursorDebetableGoalsAllComments.getInt(cursorDebetableGoalsAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY)) == 1) {
                            tmpTextNewEntryComment = "<font color='"+ ContextCompat.getColor(contextForActivity, R.color.text_accent_color) + "'>"+ contextForActivity.getResources().getString(R.string.newEntryText) + "</font>";
                        }
                    }

                    // make link to comment debetable goal
                    Uri.Builder commentLinkBuilder = new Uri.Builder();
                    commentLinkBuilder.scheme("smart.efb.deeplink")
                            .authority("linkin")
                            .path("ourgoals")
                            .appendQueryParameter("db_id", Integer.toString(debetableGoalElement.getServerIdGoal()))
                            .appendQueryParameter("arr_num", Integer.toString(debetableGoalElementPositionNumber))
                            .appendQueryParameter("com", "comment_an_debetable_goal");

                    // make link to show comment for debetable goal
                    Uri.Builder showCommentLinkBuilder = new Uri.Builder();
                    showCommentLinkBuilder.scheme("smart.efb.deeplink")
                            .authority("linkin")
                            .path("ourgoals")
                            .appendQueryParameter("db_id", Integer.toString(debetableGoalElement.getServerIdGoal()))
                            .appendQueryParameter("arr_num", Integer.toString(debetableGoalElementPositionNumber))
                            .appendQueryParameter("com", "show_comment_for_debetable_goal");

                    Spanned showCommentDebetableGoalLinkTmp;
                    if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment,0) < prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment,0) || !commentLimitationBorder) {
                        showCommentDebetableGoalLinkTmp = HtmlCompat.fromHtml(" <a href=\"" + commentLinkBuilder.build().toString() + "\">" + contextForActivity.getResources().getString(contextForActivity.getResources().getIdentifier("ourGoalsDebetableGoalsAssessmentsString", "string", contextForActivity.getPackageName()))+ " " + tmpNumberCommentsPossible + "</a>", HtmlCompat.FROM_HTML_MODE_LEGACY);
                    }
                    else {
                        showCommentDebetableGoalLinkTmp = HtmlCompat.fromHtml(contextForActivity.getResources().getString(contextForActivity.getResources().getIdentifier("ourGoalsDebetableGoalsAssessmentsString", "string", contextForActivity.getPackageName()))+ " " + tmpNumberCommentsPossible, HtmlCompat.FROM_HTML_MODE_LEGACY);
                    }
                    itemView.textViewLinkForCommentAnDebetableGoal.setText(showCommentDebetableGoalLinkTmp);
                    itemView.textViewLinkForCommentAnDebetableGoal.setMovementMethod(LinkMovementMethod.getInstance());

                    Spanned showCommentsLinkTmp;
                    if (tmpIntCountComments == 0) {
                        showCommentsLinkTmp = HtmlCompat.fromHtml(tmpCountAssessments, HtmlCompat.FROM_HTML_MODE_LEGACY);
                    }
                    else {
                        showCommentsLinkTmp = HtmlCompat.fromHtml("<a href=\"" + showCommentLinkBuilder.build().toString() + "\">" + tmpCountAssessments + "</a> " + tmpTextNewEntryComment, HtmlCompat.FROM_HTML_MODE_LEGACY);

                    }
                    itemView.textViewLinkForShowCommentOfOneDebetableGoal.setText(showCommentsLinkTmp);
                    itemView.textViewLinkForShowCommentOfOneDebetableGoal.setMovementMethod(LinkMovementMethod.getInstance());
                }

                else { // link comment and show comment are deactivated -> hide them
                    itemView.textViewLinkForShowCommentOfOneDebetableGoal.setVisibility(View.GONE);
                    itemView.textViewLinkForCommentAnDebetableGoal.setVisibility(View.GONE);
                }

                break;
            case HEADER:
                break;
            case FOOTER:
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
                return debetableGoalsListForRecyclerView.size() + 2;
            case ITEM:
                // if just items, return just the length
                return debetableGoalsListForRecyclerView.size();
            default:
                // if either or, return 1 additional
                return debetableGoalsListForRecyclerView.size() + 1;
        }
    }






}
