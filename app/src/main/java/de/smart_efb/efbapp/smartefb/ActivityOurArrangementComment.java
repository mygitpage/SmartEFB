package de.smart_efb.efbapp.smartefb;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by ich on 02.06.16.
 */
public class ActivityOurArrangementComment extends AppCompatActivity {



    // reference to the DB
    DBAdapter myDb;

    // reference cursorAdapter for the listview
    OurArrangementCursorAdapter dataAdapter;

    // shared prefs for the settings
    SharedPreferences prefs;

    // reference for the toolbar
    Toolbar toolbar;
    ActionBar actionBar;

    // uri to handle the data from the link
    Uri commentLinkData;


    // id of the given arrangement
    int arrangementId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_our_arrangement_comment);

        // init our arragement
        initOurArrangementComment();


        // commentLinkData correct?
        if (commentLinkData.equals(null)) {
            Toast.makeText(this, "Keine Daten vorhanden", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {


            arrangementId = Integer.parseInt(commentLinkData.getQueryParameter("id"));



            Toast.makeText(this, "Die ID lautet: " + arrangementId, Toast.LENGTH_SHORT).show();

            displayArrangementSet(arrangementId);
        }










    }


    private void initOurArrangementComment() {

        // init the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbarOurArrangementComment);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // init the DB
        myDb = new DBAdapter(getApplicationContext());

        // init the prefs
        prefs = this.getSharedPreferences("smartEfbSettings", MODE_PRIVATE);


        // get the link data
        commentLinkData = getIntent().getData();



    }




    public void displayArrangementSet (int tmpArrangementId) {

        // get the choosen arrangement
        Cursor cursorArrangement = myDb.getRowOurArrangement(tmpArrangementId);

        // get all comments to the arrangement
        Cursor cursorArrangementComment = myDb.getAllRowsOurArrangementComment(tmpArrangementId);




        //textview for the comment intro
        TextView textCommentIntro = (TextView) findViewById(R.id.arrangementCommentIntro);
        textCommentIntro.setText(this.getResources().getString(R.string.arrangementCommentIntro));


        // textview for the arrangement
        TextView textViewArrangement = (TextView) findViewById(R.id.choosenArrangement);
        String title = cursorArrangement.getString(cursorArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(title);





        // textview intro for the hostory of comments
        TextView textCommentHistoryIntro = (TextView) findViewById(R.id.commentHistoryIntro);
        if (cursorArrangementComment.getColumnCount() > 0) {


            textCommentHistoryIntro.setText(this.getResources().getString(R.string.commentHistoryIntroText));
            Toast.makeText(this, "Anzahl Kommentare: " + cursorArrangementComment.getColumnCount(), Toast.LENGTH_SHORT).show();



        } else {
            textCommentHistoryIntro.setVisibility(View.INVISIBLE);
        }




    }




        @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }




}

