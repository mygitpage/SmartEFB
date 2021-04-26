package de.smart_efb.efbapp.smartefb;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class OurArrangementShowCommentViewHolder extends RecyclerView.ViewHolder {


    public TextView textViewShowActualCommentHeadline, newEntryOfArrangement, tmpTextViewAuthorNameLastActualComment;
    public TextView tmpTextViewSendInfoLastActualComment, textViewShowActualComment, linkToCommentAnArrangement;


    public OurArrangementShowCommentViewHolder (View ItemView) {

        super (ItemView);

        textViewShowActualCommentHeadline = (TextView) ItemView.findViewById(R.id.actualCommentInfoText);
        newEntryOfArrangement = (TextView) ItemView.findViewById(R.id.actualCommentNewInfoText);
        tmpTextViewAuthorNameLastActualComment = (TextView) ItemView.findViewById(R.id.textAuthorNameActualComment);
        tmpTextViewSendInfoLastActualComment = (TextView) ItemView.findViewById(R.id.textSendInfoActualComment);
        textViewShowActualComment = (TextView) ItemView.findViewById(R.id.listActualTextComment);
        linkToCommentAnArrangement = (TextView) ItemView.findViewById(R.id.linkToCommentAnArrangement);
    }


}
