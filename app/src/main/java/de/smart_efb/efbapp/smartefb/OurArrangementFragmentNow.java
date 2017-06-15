package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 26.06.16.
 */
public class OurArrangementFragmentNow extends Fragment {

    // fragment view
    View viewFragmentNow;

    // fragment context
    Context fragmentNowContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;

    // the current date of arrangement -> the other are old (look at tab old)
    long currentDateOfArrangement;

    // block id of cuurrent arrangements
    String currentBlockIdOfArrangement = "";

    // reference cursorAdapter for the listview
    OurArrangementNowCursorAdapter dataAdapterListViewOurArrangement = null;

    //limitation in count comments true-> yes, there is a border; no, there is no border, wirte infitisly comments
    Boolean commentLimitationBorder;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentNow = layoutInflater.inflate(R.layout.fragment_our_arrangement_now, null);

        // register broadcast receiver and intent filter for action ARRANGEMENT_EVALUATE_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ARRANGEMENT_EVALUATE_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourArrangementFragmentNowBrodcastReceiver, filter);

        return viewFragmentNow;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentNowContext = getActivity().getApplicationContext();

        // init the fragment now
        initFragmentNow();

        // show actual arrangement set
        displayActualArrangementSet();

    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourArrangementFragmentNowBrodcastReceiver);

    }


    // Broadcast receiver for action ARRANGEMENT_EVALUATE_STATUS_UPDATE -> comes from alarmmanager ourArrangement
    private BroadcastReceiver ourArrangementFragmentNowBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // notify listView that data has changed (evaluate-link is activ or passiv)
            if (dataAdapterListViewOurArrangement != null) {
                // get new data from db
                Cursor cursor = myDb.getAllRowsCurrentOurArrangement(currentBlockIdOfArrangement, "equalBlockId");
                // and notify listView
                dataAdapterListViewOurArrangement.changeCursor(cursor);
                dataAdapterListViewOurArrangement.notifyDataSetChanged();

            }

        }
    };


    // inits the fragment for use
    private void initFragmentNow() {

        // init the DB
        myDb = new DBAdapter(fragmentNowContext);

        // init the prefs
        prefs = fragmentNowContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentNowContext.MODE_PRIVATE);

        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis());

        //get current block id of arrangements
        currentBlockIdOfArrangement = prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, "0");

        // ask methode isCommentLimitationBorderSet() in ActivityOurArrangement to limitation in comments? true-> yes, linitation; false-> no
        commentLimitationBorder = ((ActivityOurArrangement) getActivity()).isCommentLimitationBorderSet("current");

    }


    // show listView with current arrangements or info: mothing there
    public void displayActualArrangementSet () {

        Cursor cursor = myDb.getAllRowsCurrentOurArrangement(currentBlockIdOfArrangement, "equalBlockId");

        // find the listview
        ListView listView = (ListView) viewFragmentNow.findViewById(R.id.listOurArrangementNow);

        if (cursor.getCount() > 0 && listView != null) {

            // set listView visible and textView hide
            setVisibilityListViewNowArrangements("show");
            setVisibilityTextViewNowNotAvailable("hide");

            // Set correct subtitle in Activity -> "Absprachen vom ..."
            String tmpSubtitle = getResources().getString(getResources().getIdentifier("currentArrangementDateFrom", "string", fragmentNowContext.getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfArrangement, "dd.MM.yyyy");
            ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "now");

            // new dataadapter
            dataAdapterListViewOurArrangement = new OurArrangementNowCursorAdapter(
                    getActivity(),
                    cursor,
                    0);

            // Assign adapter to ListView
            listView.setAdapter(dataAdapterListViewOurArrangement);

        }
        else {

            // set listView hide and textView visible
            setVisibilityListViewNowArrangements("hide");
            setVisibilityTextViewNowNotAvailable("show");

            // Set correct subtitle in Activity -> "Keine Absprachen vorhanden"
            String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleNothingThere", "string", fragmentNowContext.getPackageName()));
            ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "now");

        }

    }


    // set visibility of listViewOurArrangement
    private void setVisibilityListViewNowArrangements (String visibility) {

        ListView tmplistView = (ListView) viewFragmentNow.findViewById(R.id.listOurArrangementNow);

        if (tmplistView != null) {

            switch (visibility) {

                case "show":
                    tmplistView.setVisibility(View.VISIBLE);
                    break;
                case "hide":
                    tmplistView.setVisibility(View.GONE);
                    break;

            }
        }

    }


    // set visibility of textView "nothing there"
    private void setVisibilityTextViewNowNotAvailable (String visibility) {

        TextView tmpNotAvailable = (TextView) viewFragmentNow.findViewById(R.id.textViewArrangementNowNothingThere);

        if (tmpNotAvailable != null) {

            switch (visibility) {

                case "show":
                    tmpNotAvailable.setVisibility(View.VISIBLE);
                    break;
                case "hide":
                    tmpNotAvailable.setVisibility(View.GONE);
                    break;

            }

        }

    }



    // geter for border for comments
    public boolean isCommentLimitationBorderSet () {

        // true-> comments are limited; false-> no limit
        return  commentLimitationBorder;

    }


}
