package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

    // shared prefs for the evaluate arrangement
    SharedPreferences prefs;

    // DB-Id of arrangement to comment
    int arrangementDbIdToEvaluate = 0;

    // arrangement number in list view
    int arrangementNumberInListView = 0;

    // cursor for the choosen arrangement
    Cursor cursorChoosenArrangement;
    // cursor for all actual arrangement to choos the next one to evaluate
    Cursor cursorNextArrangementToEvaluate;

    // values for the next arrangement to evaluate
    int nextArrangementDbIdToEvaluate = 0;
    int nextArrangementListPositionToEvaluate = 0;

    // evaluate next arrangement
    Boolean evaluateNextArrangement = false;

    // number of questions
    final int countQuestionNumber = 4;

    // number of items in every question
    int[] numberRadioButtonQuestion = {6,6,6,6};
    // part strin in ressource radiobutton question
    String[] partRessourceNameQuestion =  { "One_", "Two_", "Three_", "Four_" };

    // Evaluate question result
    int evaluateResultQuestion1 = 0;
    int evaluateResultQuestion2 = 0;
    int evaluateResultQuestion3 = 0;
    int evaluateResultQuestion4 = 0;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentEvaluate = layoutInflater.inflate(R.layout.fragment_our_arrangement_evaluate, null);

        return viewFragmentEvaluate;

    }



    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentEvaluateContext = getActivity().getApplicationContext();

        // call getter function in ActivityOurArrangment
        callGetterFunctionInSuper();

        // init the fragment evaluate only when an arrangement is choosen
        if (arrangementDbIdToEvaluate != 0) {

            // init the fragment now
            initFragmentEvaluate();
        }

    }


    // inits the fragment for use
    private void initFragmentEvaluate() {

        // init the DB
        myDb = new DBAdapter(fragmentEvaluateContext);

        // init the prefs
        prefs = fragmentEvaluateContext.getSharedPreferences("smartEfbSettings", fragmentEvaluateContext.MODE_PRIVATE);

        // get choosen arrangement
        cursorChoosenArrangement = myDb.getRowOurArrangement(arrangementDbIdToEvaluate);

        // get all actual arrangements
        cursorNextArrangementToEvaluate = myDb.getAllRowsCurrentOurArrangement(prefs.getLong("currentDateOfArrangement", System.currentTimeMillis()), "equal");

        // Set correct subtitle in Activity -> "Bewerten Absprache ..."
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleFragmentEvaluateArrangementText", "string", fragmentEvaluateContext.getPackageName())) + " " + arrangementNumberInListView;
        ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "evaluate");

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


        // set textview for the next arrangement to evaluate
        TextView textViewNextArrangementEvaluateIntro = (TextView) viewFragmentEvaluate.findViewById(R.id.arrangementNextToEvaluateIntroText);
        TextView textViewThankAndEvaluateNext = (TextView) viewFragmentEvaluate.findViewById(R.id.evaluateThankAndNextEvaluation);
        nextArrangementDbIdToEvaluate = 0;
        nextArrangementListPositionToEvaluate = 0;
        if (cursorNextArrangementToEvaluate != null) { // is there another arrangement to evaluate?

            cursorNextArrangementToEvaluate.moveToFirst();
            do {

                if (cursorNextArrangementToEvaluate.getInt(cursorNextArrangementToEvaluate.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE)) == 1 && cursorNextArrangementToEvaluate.getInt(cursorNextArrangementToEvaluate.getColumnIndex(DBAdapter.KEY_ROWID)) != arrangementDbIdToEvaluate) { // evaluation possible for arrangement?
                    nextArrangementDbIdToEvaluate = cursorNextArrangementToEvaluate.getInt(cursorNextArrangementToEvaluate.getColumnIndex(DBAdapter.KEY_ROWID));
                    nextArrangementListPositionToEvaluate = cursorNextArrangementToEvaluate.getPosition() + 1;
                }

            } while (cursorNextArrangementToEvaluate.moveToNext());

        }

        // set textview textViewNextArrangementEvaluateIntro
        if (nextArrangementDbIdToEvaluate != 0) { // with text: next arrangement to evaluate

            //textViewNextArrangementEvaluateIntro.setText(this.getResources().getString(R.string.showNextArrangementToEvaluateIntroText) + " " + nextArrangementListPositionToEvaluate);
            textViewNextArrangementEvaluateIntro.setText(String.format(this.getResources().getString(R.string.showNextArrangementToEvaluateIntroText), nextArrangementListPositionToEvaluate));

            // Show text "Danke fuer Bewertung und naechste bewerten"
            if (evaluateNextArrangement) {
                textViewThankAndEvaluateNext.setVisibility(View.VISIBLE);
            }

        }
        else { // nothing more to evaluate

            textViewNextArrangementEvaluateIntro.setText(this.getResources().getString(R.string.showNothingNextArrangementToEvaluateText));

            // Show text "Danke fuer Bewertung letzte Vereinbarung"
            if (evaluateNextArrangement) {
                textViewThankAndEvaluateNext.setText(this.getResources().getString(R.string.evaluateThankAndNextEvaluationLastText));
                textViewThankAndEvaluateNext.setVisibility(View.VISIBLE);
            }

        }

        // view the intro text SaveAndBackButton, calculate the percent and set it in the text
        TextView textSaveAndBackButtonIntro = (TextView) viewFragmentEvaluate.findViewById(R.id.evaluateSaveAndBackButtonIntro);
        String tmpSaveAndBackButtonIntroText = String.format(this.getResources().getString(R.string.evaluateSaveAndBackButtonIntroText), arrangementNumberInListView, 100);
        textSaveAndBackButtonIntro.setText(Html.fromHtml(tmpSaveAndBackButtonIntroText));

        // set onClickListener for radio button in radio group question 1-4
        String tmpRessourceName ="";
        RadioButton tmpRadioButtonQuestion;
        for (int countQuestion = 0; countQuestion < countQuestionNumber; countQuestion++) {

            for (int numberOfButtons=0; numberOfButtons < numberRadioButtonQuestion[countQuestion]; numberOfButtons++) {
                tmpRessourceName ="question" + partRessourceNameQuestion[countQuestion] + (numberOfButtons+1);
                try {
                    int resourceId = this.getResources().getIdentifier(tmpRessourceName, "id", fragmentEvaluateContext.getPackageName());

                    tmpRadioButtonQuestion = (RadioButton) viewFragmentEvaluate.findViewById(resourceId);
                    tmpRadioButtonQuestion.setOnClickListener(new evaluateRadioButtonListenerQuestion1(numberOfButtons,countQuestion));
                    //tmpRadioButtonQuestion.setChecked(false);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        // Button save evaluate result OR abort
        // button send evaluate result
        Button buttonSendEvaluateResult = (Button) viewFragmentEvaluate.findViewById(R.id.buttonSendEvaluateResult);
        // onClick listener send evaluate result
        buttonSendEvaluateResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Boolean evaluateNoError = true;

                TextView tmpErrorTextView;

                // check result question 1
                tmpErrorTextView = (TextView) viewFragmentEvaluate.findViewById(R.id.questionOneEvaluateError);
                if ( evaluateResultQuestion1 == 0 && tmpErrorTextView != null) {
                    evaluateNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }

                // check result question 2
                tmpErrorTextView = (TextView) viewFragmentEvaluate.findViewById(R.id.questionTwoEvaluateError);
                if ( evaluateResultQuestion2 == 0 && tmpErrorTextView != null) {
                    evaluateNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }

                // check result question 3
                tmpErrorTextView = (TextView) viewFragmentEvaluate.findViewById(R.id.questionThreeEvaluateError);
                if ( evaluateResultQuestion3 == 0 && tmpErrorTextView != null) {
                    evaluateNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }

                // check result question 4
                tmpErrorTextView = (TextView) viewFragmentEvaluate.findViewById(R.id.questionFourEvaluateError);
                if ( evaluateResultQuestion4 == 0 && tmpErrorTextView != null) {
                    evaluateNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }

                // get evaluate result comment
                EditText tmpInputEvaluateResultComment = (EditText) viewFragmentEvaluate.findViewById(R.id.inputEvaluateResultComment);
                String txtInputEvaluateResultComment = "";
                if (tmpInputEvaluateResultComment != null) {
                    txtInputEvaluateResultComment = tmpInputEvaluateResultComment.getText().toString();
                }

                if (evaluateNoError) {

                    // insert comment in DB
                    myDb.insertRowOurArrangementEvaluate(arrangementDbIdToEvaluate, prefs.getLong("currentDateOfArrangement", System.currentTimeMillis()), evaluateResultQuestion1, evaluateResultQuestion2, evaluateResultQuestion3, evaluateResultQuestion4, txtInputEvaluateResultComment, System.currentTimeMillis(), prefs.getString("userName", "John Doe"));

                    // delete status evaluation possible for arrangement
                    myDb.changeStatusEvaluationPossibleOurArrangement(arrangementDbIdToEvaluate, "delete");


                    // When last evaluation show toast, because textView is not visible -> new fragment
                    if (nextArrangementDbIdToEvaluate == 0 ) {
                        Toast.makeText(fragmentEvaluateContext, fragmentEvaluateContext.getResources().getString(R.string.evaluateResultSuccsesfulySend), Toast.LENGTH_SHORT).show();
                    }

                    // reset evaluate results
                    resetEvaluateResult ();

                    // build and send intent to next evaluation arrangement or back to OurArrangementNow
                    if (nextArrangementDbIdToEvaluate != 0) { // is there another arrangement to evaluate?

                        Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("com","evaluate_an_arrangement");
                        intent.putExtra("db_id", (int) nextArrangementDbIdToEvaluate);
                        intent.putExtra("arr_num", (int) nextArrangementListPositionToEvaluate);
                        intent.putExtra("eval_next", true );

                        getActivity().startActivity(intent);

                    } else {
                        // no arrangement to evaluate anymore! -> go back to OurArrangementNowFragment
                        Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("com","show_arrangement_now");
                        intent.putExtra("eval_next", false );
                        getActivity().startActivity(intent);
                    }

                } else {

                    // Hide text "Danke fuer Bewertung..." when is error occurs and text is shown?
                    if (evaluateNextArrangement) {
                        TextView textViewThankAndEvaluateNext = (TextView) viewFragmentEvaluate.findViewById(R.id.evaluateThankAndNextEvaluation);
                        textViewThankAndEvaluateNext.setVisibility(View.GONE);
                    }
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

    // reset evaluation results
    private void resetEvaluateResult () {

        // reset results
        evaluateResultQuestion1 = 0;
        evaluateResultQuestion2 = 0;
        evaluateResultQuestion3 = 0;
        evaluateResultQuestion4 = 0;

        // Clear radio groups for next evaluation
        RadioGroup tmpRadioGroupClear;
        tmpRadioGroupClear = (RadioGroup) viewFragmentEvaluate.findViewById(R.id.radioGroupQuestionOne);
        tmpRadioGroupClear.clearCheck();
        tmpRadioGroupClear = (RadioGroup) viewFragmentEvaluate.findViewById(R.id.radioGroupQuestionTwo);
        tmpRadioGroupClear.clearCheck();
        tmpRadioGroupClear = (RadioGroup) viewFragmentEvaluate.findViewById(R.id.radioGroupQuestionThree);
        tmpRadioGroupClear.clearCheck();
        tmpRadioGroupClear = (RadioGroup) viewFragmentEvaluate.findViewById(R.id.radioGroupQuestionFour);
        tmpRadioGroupClear.clearCheck();

        // Clear comment text field for next evaluation
        EditText tmpInputEvaluateResultComment = (EditText) viewFragmentEvaluate.findViewById(R.id.inputEvaluateResultComment);
        tmpInputEvaluateResultComment.setText("");


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
                default:
                    evaluateResultQuestion1 = 0;
                    evaluateResultQuestion2 = 0;
                    evaluateResultQuestion3 = 0;
                    evaluateResultQuestion4 = 0;
                    break;
            }

        }

    }



    // call getter Functions in ActivityOurArrangement for some data
    private void callGetterFunctionInSuper () {

        int tmpArrangementDbIdToComment = 0;

        // call getter-methode getArrangementDbIdFromLink() in ActivityOurArrangement to get DB ID for the actuale arrangement
        tmpArrangementDbIdToComment = ((ActivityOurArrangement)getActivity()).getArrangementDbIdFromLink();

        if (tmpArrangementDbIdToComment > 0) {
            arrangementDbIdToEvaluate = tmpArrangementDbIdToComment;

            // call getter-methode getArrangementNumberInListview() in ActivityOurArrangement to get listView-number for the actuale arrangement
            arrangementNumberInListView = ((ActivityOurArrangement) getActivity()).getArrangementNumberInListview();
            if (arrangementNumberInListView < 1) arrangementNumberInListView = 1; // check borders

            // call getter-methode getEvaluateNextArrangement() in ActivityOurArrangement for evaluation next arrangement?
            evaluateNextArrangement = ((ActivityOurArrangement) getActivity()).getEvaluateNextArrangement();
        }

    }

}
