package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


public class OurArrangementShowCommentRecylerViewAdapter extends RecyclerView.Adapter<OurArrangementShowCommentViewHolder> {

    final Context contextForActivity;

    // Array List of arrangement comment
    ArrayList<ObjectSmartEFBComment> ourArrangementShowCommentList;

    // DB-Id of arrangement
    int arrangementDbIdToShow = 0;

    // arrangement number in list view of fragment show arrangement now
    int arrangementNumberInListView = 0;

    // true-> comments are limited, false -> comments are not limited
    Boolean commentLimitationBorder = false;

    // the chossen arrangement to show the comments
    Cursor choosenArrangement;

    // shared prefs for the comment arrangement
    SharedPreferences prefs;

    // count headline number for comments
    int countCommentHeadlineNumber = 0;




    // own constructor!!!
    public OurArrangementShowCommentRecylerViewAdapter (Context context, ArrayList<ObjectSmartEFBComment> commentList, int flags, int dbId, int numberInList, Boolean commentsLimitation, Cursor arrangement) {

        //cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Array List of arrangement comment
        ourArrangementShowCommentList = commentList;

        // context of activity OurArrangement
        contextForActivity = context;

        // set db id for arrangement
        arrangementDbIdToShow = dbId;

        // set arrangement number in list view
        arrangementNumberInListView = numberInList;

        // set comments limitation
        commentLimitationBorder = commentsLimitation;

        // set choosen arrangement
        choosenArrangement = arrangement;

        // init the prefs
        prefs = contextForActivity.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, contextForActivity.MODE_PRIVATE);
    }


    @NonNull
    @Override
    public OurArrangementShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_arrangement_show_comment_recylerview, parent, false);
        return new OurArrangementShowCommentViewHolder(view);

    }




    @Override
    public void onBindViewHolder (OurArrangementShowCommentViewHolder holder, int position) {


        final ObjectSmartEFBComment commentElement = ourArrangementShowCommentList.get(position);




        // set author name and date/time
        String tmpAuthorName = commentElement.getAuthorName();
        if (tmpAuthorName.equals(prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt"))) {
            tmpAuthorName = contextForActivity.getResources().getString(R.string.ourArrangementShowCommentPersonalAuthorName);
        }

        String commentDate = EfbHelperClass.timestampToDateFormat(commentElement.getLocalTime(), "dd.MM.yyyy");
        String commentTime = EfbHelperClass.timestampToDateFormat(commentElement.getLocalTime(), "HH:mm");;
        String tmpTextAuthorNameLastActualComment = "";
        tmpTextAuthorNameLastActualComment = String.format(contextForActivity.getResources().getString(R.string.ourArrangementShowCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
        if (commentElement.getStatus() == 4) {tmpTextAuthorNameLastActualComment = String.format(contextForActivity.getResources().getString(R.string.ourArrangementShowCommentAuthorNameWithDateExternal), tmpAuthorName, commentDate, commentTime);} // comment from external-> show not text: locale smartphone time!!!

        holder.tmpTextViewAuthorNameLastActualComment.setText(HtmlCompat.fromHtml(tmpTextAuthorNameLastActualComment, HtmlCompat.FROM_HTML_MODE_LEGACY));




    }


    @Override
    public int getItemCount() {

        return ourArrangementShowCommentList.size();

    }




}
