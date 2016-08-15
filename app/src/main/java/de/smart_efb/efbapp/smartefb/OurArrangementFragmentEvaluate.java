package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ich on 10.08.16.
 */
public class OurArrangementFragmentEvaluate extends Fragment {


    // fragment view
    View viewFragmentEvaluate;

    // fragment context
    Context fragmentEvaluateContext = null;

    // reference to the DB
    DBAdapter myDb;

    // DB-Id of arrangement to comment
    int arrangementDbIdToEvaluate = 0;

    // arrangement number in list view
    int arrangementNumberInListView = 0;

    // cursor for the choosen arrangement
    Cursor cursorChoosenArrangement;



    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentEvaluate = layoutInflater.inflate(R.layout.fragment_our_arrangement_evaluate, null);

        return viewFragmentEvaluate;

    }



    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentEvaluateContext = getActivity().getApplicationContext();

        // init the fragment now
        initFragmentEvaluate();


        // close database-connection
        myDb.close();


    }






    // inits the fragment for use
    private void initFragmentEvaluate() {

        // init the DB
        myDb = new DBAdapter(fragmentEvaluateContext);



        // call getter-methode getArrangementDbIdFromLink() in ActivityOurArrangement to get DB ID for the actuale arrangement
        arrangementDbIdToEvaluate = ((ActivityOurArrangement) getActivity()).getArrangementDbIdFromLink();
        if (arrangementDbIdToEvaluate < 0) arrangementDbIdToEvaluate = 0; // check borders
        // call getter-methode getArrangementNumberInListview() in ActivityOurArrangement to get listView-number for the actuale arrangement
        arrangementNumberInListView = ((ActivityOurArrangement) getActivity()).getArrangementNumberInListview();
        if (arrangementNumberInListView < 1) arrangementNumberInListView = 1; // check borders

        // get choosen arrangement
        cursorChoosenArrangement = myDb.getRowOurArrangement(arrangementDbIdToEvaluate);



        // build the view
        //textview for the comment intro
        TextView textCommentNumberIntro = (TextView) viewFragmentEvaluate.findViewById(R.id.arrangementEvalauteIntroText);
        textCommentNumberIntro.setText(this.getResources().getString(R.string.showEvaluateArrangementIntroText) + " " + arrangementNumberInListView);


        // generate back link "zurueck zu allen Absprachen"
        Uri.Builder commentLinkBuilder = new Uri.Builder();
        commentLinkBuilder.scheme("smart.efb.ilink_comment")
                .authority("www.smart-efb.de")
                .appendQueryParameter("db_id", "0")
                .appendQueryParameter("arr_num", "0")
                .appendQueryParameter("com", "show_arrangement_now");
        TextView linkShowEvaluateBackLink = (TextView) viewFragmentEvaluate.findViewById(R.id.arrangementShowEvaluateBackLink);
        linkShowEvaluateBackLink.setText(Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">" + fragmentEvaluateContext.getResources().getString(fragmentEvaluateContext.getResources().getIdentifier("ourArrangementBackLinkToArrangement", "string", fragmentEvaluateContext.getPackageName())) + "</a>"));
        linkShowEvaluateBackLink.setMovementMethod(LinkMovementMethod.getInstance());


        // textview for the arrangement
        TextView textViewArrangement = (TextView) viewFragmentEvaluate.findViewById(R.id.evaluateArrangement);
        String arrangement = cursorChoosenArrangement.getString(cursorChoosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(arrangement);








        TextView textSaveAndBackButtonIntro = (TextView) viewFragmentEvaluate.findViewById(R.id.evaluateSaveAndBackButtonIntro);
        String tmpSaveAndBackButtonIntroText = String.format(this.getResources().getString(R.string.evaluateSaveAndBackButtonIntroText), arrangementNumberInListView, 100);
        textSaveAndBackButtonIntro.setText(Html.fromHtml(tmpSaveAndBackButtonIntroText));



        // String manipulieren mit Platzhaltern
        // https://developer.android.com/guide/topics/resources/string-resource.html

    }




}
