package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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



    // Number of radio button for question 1,2,3,4,5
    final int numberRadioButtonQuestion1 = 5;


    // Evaluate question result
    int evaluateResultQuestion1 = 0;
    int evaluateResultQuestion2 = 0;
    int evaluateResultQuestion3 = 0;
    int evaluateResultQuestion4 = 0;
    int evaluateResultQuestion5 = 0;




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


        // view the intro text SaveAndBackButton, calculate the percent and set it in the text
        TextView textSaveAndBackButtonIntro = (TextView) viewFragmentEvaluate.findViewById(R.id.evaluateSaveAndBackButtonIntro);
        String tmpSaveAndBackButtonIntroText = String.format(this.getResources().getString(R.string.evaluateSaveAndBackButtonIntroText), arrangementNumberInListView, 100);
        textSaveAndBackButtonIntro.setText(Html.fromHtml(tmpSaveAndBackButtonIntroText));



        // String manipulieren mit Platzhaltern
        // https://developer.android.com/guide/topics/resources/string-resource.html



        // Button save evaluate result OR abort




        // get input fields from evaluate fragment!



        String tmpRessourceName ="";
        RadioButton tmpRadioButtonQuestion;
        // set onClickListener for radio button in radio group question 1
        for (int numberOfButtons=0; numberOfButtons < numberRadioButtonQuestion1; numberOfButtons++) {
            tmpRessourceName ="questionOne_" + (numberOfButtons+1);
            try {
                int resourceId = this.getResources().getIdentifier(tmpRessourceName, "id", fragmentEvaluateContext.getPackageName());

                tmpRadioButtonQuestion = (RadioButton) viewFragmentEvaluate.findViewById(resourceId);
                tmpRadioButtonQuestion.setOnClickListener(new evaluateRadioButtonListenerQuestion1(numberOfButtons,0));


            } catch (Exception e) {
                e.printStackTrace();
            }
        }









        //final EditText txtInputEvaluateResultComment = (EditText) viewFragmentEvaluate.findViewById(R.id.inputEvaluateResultComment);



        // button send evaluate result
        Button buttonSendEvaluateResult = (Button) viewFragmentEvaluate.findViewById(R.id.buttonSendEvaluateResult);

        // onClick listener send arrangement comment
        buttonSendEvaluateResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Boolean evaluateNoError = true;



                if (evaluateNoError) {

                    // insert comment in DB
                    //long newID = myDb.insertRowOurArrangementComment(txtInputArrangementComment.getText().toString(), prefs.getString("userName", "John Doe"), System.currentTimeMillis() , arrangementDbIdToComment, true, prefs.getLong("currentDateOfArrangement", System.currentTimeMillis()));

                    // Toast "Comment sucsessfull send"
                    Toast.makeText(fragmentEvaluateContext, fragmentEvaluateContext.getResources().getString(R.string.evaluateResultSuccsesfulySend) + " -> " + evaluateResultQuestion1, Toast.LENGTH_SHORT).show();

                    // reset evaluate results
                    resetEvaluateResult ();

                    // build intent to get back to OurArrangementFragmentNow
                    Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","show_arrangement_now");
                    getActivity().startActivity(intent);

                } else {
                    // Toast "Evaluate not completly"
                    Toast.makeText(fragmentEvaluateContext, fragmentEvaluateContext.getResources().getString(R.string.evaluateResultNotCompletely), Toast.LENGTH_SHORT).show();
                }

            }
        });

        // button abbort
        Button buttonAbbortArrangementComment = (Button) viewFragmentEvaluate.findViewById(R.id.buttonAbortEvaluate);
        // onClick listener button abbort
        buttonAbbortArrangementComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // reset evaluate results
                resetEvaluateResult ();

                Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_arrangement_now");
                getActivity().startActivity(intent);

            }
        });


    }


    private void resetEvaluateResult () {

        // reset results
        evaluateResultQuestion1 = 0;
        evaluateResultQuestion2 = 0;
        evaluateResultQuestion3 = 0;
        evaluateResultQuestion4 = 0;
        evaluateResultQuestion5 = 0;

    }




    //
    // onClickListener for radioButtons in fragment layout evaluate
    //
    public class evaluateRadioButtonListenerQuestion1 implements View.OnClickListener {

        int radioButtonNumber;
        int questionNumber;

        public evaluateRadioButtonListenerQuestion1 (int number, int questionNr) {

            this.radioButtonNumber = number;
            this.questionNumber = questionNr;

        }

        @Override
        public void onClick(View v) {

            int tmpResultQuestion;

            // check button number and get result
            switch (radioButtonNumber) {

                case 0: // ever
                    tmpResultQuestion = 1;
                    break;
                case 1:
                    tmpResultQuestion = 2;
                    break;
                case 2:
                    tmpResultQuestion = 3;
                    break;
                case 3:
                    tmpResultQuestion = 4;
                    break;
                case 4: // radioButton never
                    tmpResultQuestion = 5;
                    break;
                case 5: // radioButton not affected
                    tmpResultQuestion = 10;
                    break;
                default:
                    tmpResultQuestion = 0;
                    break;
            }

            // put result into questionVar
            switch (questionNumber) {

                case 0: // question 1
                    evaluateResultQuestion1 = tmpResultQuestion;
                    break;
                case 1: // question 2
                    evaluateResultQuestion2 = tmpResultQuestion;
                    break;
                case 2: // question 3
                    evaluateResultQuestion3 = tmpResultQuestion;
                    break;
                case 3: // question 4
                    evaluateResultQuestion4 = tmpResultQuestion;
                    break;
                case 4:
                    evaluateResultQuestion5 = tmpResultQuestion;
                    break;
                default:
                    evaluateResultQuestion1 = 0;
                    evaluateResultQuestion2 = 0;
                    evaluateResultQuestion3 = 0;
                    evaluateResultQuestion4 = 0;
                    evaluateResultQuestion5 = 0;
                    break;
            }

        }

    }






}
