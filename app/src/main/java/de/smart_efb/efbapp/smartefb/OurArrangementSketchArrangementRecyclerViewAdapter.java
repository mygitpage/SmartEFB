package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.text.Html;
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


public class OurArrangementSketchArrangementRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    final Context contextForActivity;

    // hold layoutInflater
    private LayoutInflater cursorInflater;

    //limitation in count comments true-> yes, there is a border; no, there is no border
    Boolean commentLimitationBorder;

    // for prefs
    SharedPreferences prefs;

    // number for count comments for sketch arrangement (12 numbers!)
    private String[] numberCountForAssessments = new String [12];

    // Array list for now arrangements
    ArrayList<ObjectSmartEFBArrangement> sketchArrangementListForRecyclerView;

    // own reycler view elements
    final static int HEADER = 0;
    final static int ITEM = 1;
    final static int FOOTER = 2;
    final static int BOTH = 3;
    private int STATE;


    // own constructor!!!
    public OurArrangementSketchArrangementRecyclerViewAdapter (Context context, ArrayList<ObjectSmartEFBArrangement> arrangementSketchList, int flags) {

        // context of activity OurArrangement
        contextForActivity = context;

        // get inflater service
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // init array for count comments
        numberCountForAssessments = context.getResources().getStringArray(R.array.ourArrangementCountAssessments);

        //limitation in count comments true-> yes, there is a border; no, there is no border
        commentLimitationBorder = ((ActivityOurArrangement)context).isCommentLimitationBorderSet("current");

        // open sharedPrefs
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);

        // copy array list of arrangements
        sketchArrangementListForRecyclerView = arrangementSketchList;

        // init view has only items
        STATE = ITEM;

    }

    
    // inner class for item element
    private class OurArrangementSketchArrangementItemViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewBorderBetweenItems, textViewNumberOfSketchArrangement, textViewNewSignalForSketchArrangement, textViewAuthorNameForSketchArrangement;
        public TextView textViewSketchArrangementText, textViewLinkForCommentAnSketchArrangement, textViewLinkForShowCommentOfOneSketchArrangement;

        OurArrangementSketchArrangementItemViewHolder(View itemView) {

            super(itemView);

            textViewBorderBetweenItems = itemView.findViewById(R.id.borderBetweenSketchArrangement);
            textViewNumberOfSketchArrangement = itemView.findViewById(R.id.listSketchArrangementNumberText);
            textViewNewSignalForSketchArrangement = itemView.findViewById(R.id.listSketchArrangementNewArrangementText);
            textViewAuthorNameForSketchArrangement = itemView.findViewById(R.id.listTextSketchAuthorName);
            textViewSketchArrangementText = itemView.findViewById(R.id.listTextSketchArrangement);
            textViewLinkForCommentAnSketchArrangement = itemView.findViewById(R.id.linkCommentAnSketchArrangement);
            textViewLinkForShowCommentOfOneSketchArrangement = itemView.findViewById(R.id.linkToShowCommentsOfSketchArrangements);

        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new OurArrangementSketchArrangementRecyclerViewAdapter.OurArrangementSketchArrangementItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_arrangement_sketch_arrangement_recyclerview_item, parent, false));
    }


    @Override
    public int getItemViewType(int position) {
        // if there is both footerResource and header
        switch (STATE) {
            case BOTH:
                if (position == 0) {
                    return HEADER;
                }
                else if (position >= (sketchArrangementListForRecyclerView.size()+1) ) {
                    return FOOTER;
                }
                return ITEM;
            case HEADER:
                return position == 0 ? HEADER : ITEM;

            case FOOTER:
                if (position >= (sketchArrangementListForRecyclerView.size()) ) {
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

                final ObjectSmartEFBArrangement sketchArrangementElement = sketchArrangementListForRecyclerView.get(position);

                // cast holder
                OurArrangementSketchArrangementRecyclerViewAdapter.OurArrangementSketchArrangementItemViewHolder itemView = (OurArrangementSketchArrangementRecyclerViewAdapter.OurArrangementSketchArrangementItemViewHolder) holder;

                // put sketch arrangement number
                int sketchArrangementElementPositionNumber = 0;
                if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementSketchList, "descending").equals("descending")) {
                    sketchArrangementElementPositionNumber = sketchArrangementListForRecyclerView.size() - sketchArrangementElement.getPositionNumber() + 1;
                }
                else {
                    sketchArrangementElementPositionNumber = sketchArrangementElement.getPositionNumber();
                }
                String txtSketchArrangementNumber = contextForActivity.getResources().getString(R.string.showSketchArrangementNumberText)+ " " + sketchArrangementElementPositionNumber;
                itemView.textViewNumberOfSketchArrangement.setText(txtSketchArrangementNumber);

                // put author name
                String tmpTextAuthorNameText = String.format(contextForActivity.getResources().getString(R.string.ourArrangementAuthorNameTextWithDate), sketchArrangementElement.getAuthorName(), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), "dd.MM.yyyy"));
                itemView.textViewAuthorNameForSketchArrangement.setText(HtmlCompat.fromHtml(tmpTextAuthorNameText, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // check if arrangement entry new?
                if (sketchArrangementElement.getNewEntry() == 1) {
                    itemView.textViewNewSignalForSketchArrangement.setVisibility(View.VISIBLE);
                    myDb.deleteStatusNewEntryOurArrangement(sketchArrangementElement.getRowID());
                }

                // put arrangement text
                itemView.textViewSketchArrangementText.setText(sketchArrangementElement.getArrangementText());

                if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowLinkCommentSketchArrangement, false)) {

                    // generate difference text for comment anymore
                    String tmpNumberCommentsPossible;
                    int tmpDifferenceComments = prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0) - prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0);

                    if (commentLimitationBorder) {
                        if (tmpDifferenceComments > 0) {
                            if (tmpDifferenceComments > 1) { //plural comments
                                tmpNumberCommentsPossible = String.format(contextForActivity.getString(R.string.infoTextNumberOfSketchCommentsPossiblePlural), tmpDifferenceComments);
                            } else { // singular comments
                                tmpNumberCommentsPossible = String.format(contextForActivity.getString(R.string.infoTextNumberOfSketchCommentsPossibleSingular), tmpDifferenceComments);
                            }
                        }
                        else {
                            tmpNumberCommentsPossible = contextForActivity.getString(R.string.infoTextNumberOfSketchCommentsPossibleNoMore);
                        }
                    }
                    else {
                        tmpNumberCommentsPossible = contextForActivity.getString(R.string.infoTextNumberOfSketchCommentsPossibleNoBorder);
                    }

                    // get from DB  all comments for choose sketch arrangement (getCount)
                    Cursor cursorSketchArrangementAllComments = myDb.getAllRowsOurArrangementSketchComment(sketchArrangementElement.getServerIdArrangement(), "descending", 0);

                    // generate the number of comments to show
                    String tmpCountAssessments;
                    int tmpIntCountComments = cursorSketchArrangementAllComments.getCount();
                    if (tmpIntCountComments > 10) {
                        tmpCountAssessments = numberCountForAssessments[11];

                    }
                    else {
                        tmpCountAssessments = numberCountForAssessments[cursorSketchArrangementAllComments.getCount()];
                    }

                    // check comments for new entry, the cursor is sorted DESC, so first element is newest!!! new entry is markt by == 1
                    String tmpTextNewEntryComment = "";
                    if (cursorSketchArrangementAllComments.getCount() > 0) {
                        cursorSketchArrangementAllComments.moveToFirst();
                        if (cursorSketchArrangementAllComments.getInt(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY)) == 1) {
                            tmpTextNewEntryComment = "<font color='"+ ContextCompat.getColor(contextForActivity, R.color.text_accent_color) + "'>"+ contextForActivity.getResources().getString(R.string.newEntryText) + "</font>";
                        }
                    }

                    // make link to comment arrangement
                    Uri.Builder commentLinkBuilder = new Uri.Builder();
                    commentLinkBuilder.scheme("smart.efb.deeplink")
                            .authority("linkin")
                            .path("ourarrangement")
                            .appendQueryParameter("db_id", Integer.toString(sketchArrangementElement.getServerIdArrangement()))
                            .appendQueryParameter("arr_num", Integer.toString(sketchArrangementElementPositionNumber))
                            .appendQueryParameter("com", "comment_an_sketch_arrangement");

                    // make link to show comment for arrangement
                    Uri.Builder showCommentLinkBuilder = new Uri.Builder();
                    showCommentLinkBuilder.scheme("smart.efb.deeplink")
                            .authority("linkin")
                            .path("ourarrangement")
                            .appendQueryParameter("db_id", Integer.toString(sketchArrangementElement.getServerIdArrangement()))
                            .appendQueryParameter("arr_num", Integer.toString(sketchArrangementElementPositionNumber))
                            .appendQueryParameter("com", "show_comment_for_sketch_arrangement");;

                    Spanned showCommentSketchArrangementLinkTmp;
                    if (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment,0) < prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment,0) || !commentLimitationBorder) {
                        showCommentSketchArrangementLinkTmp = HtmlCompat.fromHtml(" <a href=\"" + commentLinkBuilder.build().toString() + "\">" + contextForActivity.getResources().getString(contextForActivity.getResources().getIdentifier("ourArrangementAssessmentsString", "string", contextForActivity.getPackageName()))+ " " + tmpNumberCommentsPossible + "</a>", HtmlCompat.FROM_HTML_MODE_LEGACY);
                    }
                    else {
                        showCommentSketchArrangementLinkTmp = HtmlCompat.fromHtml(contextForActivity.getResources().getString(contextForActivity.getResources().getIdentifier("ourArrangementAssessmentsString", "string", contextForActivity.getPackageName()))+ " " + tmpNumberCommentsPossible, HtmlCompat.FROM_HTML_MODE_LEGACY);
                    }
                    itemView.textViewLinkForCommentAnSketchArrangement.setText(showCommentSketchArrangementLinkTmp);
                    itemView.textViewLinkForCommentAnSketchArrangement.setMovementMethod(LinkMovementMethod.getInstance());

                    Spanned showCommentsLinkTmp;
                    if (tmpIntCountComments == 0) {
                        showCommentsLinkTmp = HtmlCompat.fromHtml(tmpCountAssessments, HtmlCompat.FROM_HTML_MODE_LEGACY);
                    }
                    else {
                        showCommentsLinkTmp = HtmlCompat.fromHtml("<a href=\"" + showCommentLinkBuilder.build().toString() + "\">" + tmpCountAssessments + "</a> " + tmpTextNewEntryComment, HtmlCompat.FROM_HTML_MODE_LEGACY);

                    }
                    itemView.textViewLinkForShowCommentOfOneSketchArrangement.setText(showCommentsLinkTmp);
                    itemView.textViewLinkForShowCommentOfOneSketchArrangement.setMovementMethod(LinkMovementMethod.getInstance());
                }

                else { // link comment and show comment are deactivated -> hide them
                    itemView.textViewLinkForShowCommentOfOneSketchArrangement.setVisibility(View.GONE);
                    itemView.textViewLinkForCommentAnSketchArrangement.setVisibility(View.GONE);
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
                return sketchArrangementListForRecyclerView.size() + 2;
            case ITEM:
                // if just items, return just the length
                return sketchArrangementListForRecyclerView.size();
            default:
                // if either or, return 1 additional
                return sketchArrangementListForRecyclerView.size() + 1;
        }
    }

}
