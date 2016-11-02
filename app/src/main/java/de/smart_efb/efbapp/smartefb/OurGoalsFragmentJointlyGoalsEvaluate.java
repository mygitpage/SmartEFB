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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 31.10.2016.
 */
public class OurGoalsFragmentJointlyGoalsEvaluate extends Fragment {


    // fragment view
    View viewFragmentJointlyGoalsEvaluate;

    // fragment context
    Context fragmentEvaluateJointlyGoalsContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the evaluate jointly goals
    SharedPreferences prefs;

    // DB-Id of jointly goal to evaluate
    int jointlyGoalDbIdToEvaluate = 0;

    // jointly goal number in list view
    int jointlyGoalNumberInListView = 0;

    // cursor for the choosen jointly goal
    Cursor cursorChoosenJointlyGoal;
    // cursor for all actual jointly goals to choose the next one to evaluate
    Cursor cursorNextJointlyGoalToEvaluate;

    // values for the next jointly goal to evaluate
    int nextJointlyGoalDbIdToEvaluate = 0;
    int nextJointlyGoalListPositionToEvaluate = 0;

    // evaluate next jointly goal
    Boolean evaluateNextJointlyGoal = false;

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

        viewFragmentJointlyGoalsEvaluate = layoutInflater.inflate(R.layout.fragment_our_goals_jointly_goals_evaluate, null);

        return viewFragmentJointlyGoalsEvaluate;

    }



    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentEvaluateJointlyGoalsContext = getActivity().getApplicationContext();

        // call getter function in ActivityOurArrangment
        callGetterFunctionInSuper();

        // init the fragment evaluate only when an jointly goal is choosen
        if (jointlyGoalDbIdToEvaluate != 0) {

            // init the fragment now
            initFragmentEvaluate();
        }

    }


    // inits the fragment for use
    private void initFragmentEvaluate() {

        // init the DB
        myDb = new DBAdapter(fragmentEvaluateJointlyGoalsContext);

        // init the prefs
        prefs = fragmentEvaluateJointlyGoalsContext.getSharedPreferences("smartEfbSettings", fragmentEvaluateJointlyGoalsContext.MODE_PRIVATE);

        // get choosen jointly goal
        cursorChoosenJointlyGoal = myDb.getJointlyRowOurGoals(jointlyGoalDbIdToEvaluate);

        // get all actual jointly goals
        cursorNextJointlyGoalToEvaluate = myDb.getAllJointlyRowsOurGoals(prefs.getLong("currentDateOfJointlyGoals", System.currentTimeMillis()), "equal");

        // Set correct subtitle in Activity -> "Ziel ... bewerten"
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleEvaluateJointlyGoal", "string", fragmentEvaluateJointlyGoalsContext.getPackageName()));
        tmpSubtitle = String.format(tmpSubtitle, jointlyGoalNumberInListView);
        ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyEvaluate");

        // build the view
        //textview for the comment intro
        TextView textCommentNumberIntro = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.jointlyGoalEvaluateIntroText);
        textCommentNumberIntro.setText(this.getResources().getString(R.string.showEvaluateJointlyGoalIntroText) + " " + jointlyGoalNumberInListView);


        // generate back link "zurueck zu allen Absprachen"
        Uri.Builder commentLinkBuilder = new Uri.Builder();
        commentLinkBuilder.scheme("smart.efb.deeplink")
                .authority("linkin")
                .path("ourgoals")
                .appendQueryParameter("db_id", "0")
                .appendQueryParameter("arr_num", "0")
                .appendQueryParameter("com", "show_jointly_goals_now");
        TextView linkShowEvaluateBackLink = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.jointlyGoalsShowEvaluateBackLink);
        linkShowEvaluateBackLink.setText(Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">" + fragmentEvaluateJointlyGoalsContext.getResources().getString(fragmentEvaluateJointlyGoalsContext.getResources().getIdentifier("ourGoalsBackLinkToJointlyGoals", "string", fragmentEvaluateJointlyGoalsContext.getPackageName())) + "</a>"));
        linkShowEvaluateBackLink.setMovementMethod(LinkMovementMethod.getInstance());


        // textview for the jointly goal
        TextView textViewJointlyGoal = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.evaluateJointlyGoal);
        String jointlyGoal = cursorChoosenJointlyGoal.getString(cursorChoosenJointlyGoal.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL));
        textViewJointlyGoal.setText(jointlyGoal);


        // set textview for the next jointly goal to evaluate
        TextView textViewNextJointlyGoalEvaluateIntro = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.jointlyGoalNextToEvaluateIntroText);
        TextView textViewThankAndEvaluateNext = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.evaluateThankAndNextEvaluation);
        nextJointlyGoalDbIdToEvaluate = 0;
        nextJointlyGoalListPositionToEvaluate = 0;
        if (cursorNextJointlyGoalToEvaluate != null) { // is there another jointly goal to evaluate?

            cursorNextJointlyGoalToEvaluate.moveToFirst();
            do {

                if (cursorNextJointlyGoalToEvaluate.getInt(cursorNextJointlyGoalToEvaluate.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_EVALUATE_POSSIBLE)) == 1 && cursorNextJointlyGoalToEvaluate.getInt(cursorNextJointlyGoalToEvaluate.getColumnIndex(DBAdapter.KEY_ROWID)) != jointlyGoalDbIdToEvaluate) { // evaluation possible for jointly goal?
                    nextJointlyGoalDbIdToEvaluate = cursorNextJointlyGoalToEvaluate.getInt(cursorNextJointlyGoalToEvaluate.getColumnIndex(DBAdapter.KEY_ROWID));
                    nextJointlyGoalListPositionToEvaluate = cursorNextJointlyGoalToEvaluate.getPosition() + 1;
                }

            } while (cursorNextJointlyGoalToEvaluate.moveToNext());

        }

        // set textview textViewNextJointlyGoalEvaluateIntro
        if (nextJointlyGoalDbIdToEvaluate != 0) { // with text: next jointly goal to evaluate

            textViewNextJointlyGoalEvaluateIntro.setText(String.format(this.getResources().getString(R.string.showNextJointlyGoalToEvaluateIntroText), nextJointlyGoalListPositionToEvaluate));

            // Show text "Danke fuer Bewertung und naechste bewerten"
            if (evaluateNextJointlyGoal) {
                textViewThankAndEvaluateNext.setVisibility(View.VISIBLE);
            }

        }
        else { // nothing more to evaluate

            textViewNextJointlyGoalEvaluateIntro.setText(this.getResources().getString(R.string.showNothingNextJointlyGoalToEvaluateText));

            // Show text "Danke fuer Bewertung letzte Vereinbarung"
            if (evaluateNextJointlyGoal) {
                textViewThankAndEvaluateNext.setText(this.getResources().getString(R.string.evaluateThankAndNextEvaluationJointlyGoalLastText));
                textViewThankAndEvaluateNext.setVisibility(View.VISIBLE);
            }

        }

        // view the intro text SaveAndBackButton
        /*
        TextView textSaveAndBackButtonIntro = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.evaluateSaveAndBackButtonIntro);
        String tmpSaveAndBackButtonIntroText = this.getResources().getString(R.string.evaluateSaveAndBackButtonIntroText);
        textSaveAndBackButtonIntro.setText(tmpSaveAndBackButtonIntroText);
        */

        // set onClickListener for radio button in radio group question 1-4
        String tmpRessourceName ="";
        RadioButton tmpRadioButtonQuestion;
        for (int countQuestion = 0; countQuestion < countQuestionNumber; countQuestion++) {

            for (int numberOfButtons=0; numberOfButtons < numberRadioButtonQuestion[countQuestion]; numberOfButtons++) {
                tmpRessourceName ="question" + partRessourceNameQuestion[countQuestion] + (numberOfButtons+1);
                try {
                    int resourceId = this.getResources().getIdentifier(tmpRessourceName, "id", fragmentEvaluateJointlyGoalsContext.getPackageName());

                    tmpRadioButtonQuestion = (RadioButton) viewFragmentJointlyGoalsEvaluate.findViewById(resourceId);
                    tmpRadioButtonQuestion.setOnClickListener(new evaluateRadioButtonListenerQuestion1(numberOfButtons,countQuestion));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        // Button save evaluate result OR abort
        // button send evaluate result
        Button buttonSendEvaluateResult = (Button) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.buttonSendEvaluateResult);
        // onClick listener send evaluate result
        buttonSendEvaluateResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Boolean evaluateNoError = true;

                TextView tmpErrorTextView;

                // check result question 1
                tmpErrorTextView = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.questionOneEvaluateError);
                if ( evaluateResultQuestion1 == 0 && tmpErrorTextView != null) {
                    evaluateNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }

                // check result question 2
                tmpErrorTextView = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.questionTwoEvaluateError);
                if ( evaluateResultQuestion2 == 0 && tmpErrorTextView != null) {
                    evaluateNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }

                // check result question 3
                tmpErrorTextView = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.questionThreeEvaluateError);
                if ( evaluateResultQuestion3 == 0 && tmpErrorTextView != null) {
                    evaluateNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }

                // check result question 4
                tmpErrorTextView = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.questionFourEvaluateError);
                if ( evaluateResultQuestion4 == 0 && tmpErrorTextView != null) {
                    evaluateNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }

                // get evaluate result comment
                EditText tmpInputEvaluateResultComment = (EditText) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.inputEvaluateResultComment);
                String txtInputEvaluateResultComment = "";
                if (tmpInputEvaluateResultComment != null) {
                    txtInputEvaluateResultComment = tmpInputEvaluateResultComment.getText().toString();
                }

                if (evaluateNoError) {

                    // insert comment in DB
                    //myDb.insertRowOurArrangementEvaluate(jointlyGoalDbIdToEvaluate, prefs.getLong("currentDateOfJointlyGoals", System.currentTimeMillis()), evaluateResultQuestion1, evaluateResultQuestion2, evaluateResultQuestion3, evaluateResultQuestion4, txtInputEvaluateResultComment, System.currentTimeMillis(), prefs.getString("userName", "John Doe"));

                    // delete status evaluation possible for jointly goal
                    myDb.changeStatusEvaluationPossibleOurGoals(jointlyGoalDbIdToEvaluate, "delete");


                    // When last evaluation show toast, because textView is not visible -> new fragment
                    if (nextJointlyGoalDbIdToEvaluate == 0 ) {
                        Toast.makeText(fragmentEvaluateJointlyGoalsContext, fragmentEvaluateJointlyGoalsContext.getResources().getString(R.string.evaluateResultJointlyGoalSuccsesfulySend), Toast.LENGTH_SHORT).show();
                    }

                    // reset evaluate results
                    resetEvaluateResult ();

                    // build and send intent to next evaluation jointly goal or back to OurGoalsJointlyGoalsNow
                    if (nextJointlyGoalDbIdToEvaluate != 0) { // is there another jointly goal to evaluate?

                        Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("com","evaluate_an_jointly_goal");
                        intent.putExtra("db_id", (int) nextJointlyGoalDbIdToEvaluate);
                        intent.putExtra("arr_num", (int) nextJointlyGoalListPositionToEvaluate);
                        intent.putExtra("eval_next", true );

                        getActivity().startActivity(intent);

                    } else {
                        // no jointly goal to evaluate anymore! -> go back to OurGoalsFragmentJointlyGoalsNow
                        Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("com","show_jointly_goals_now");
                        intent.putExtra("eval_next", false );
                        getActivity().startActivity(intent);
                    }

                } else {

                    // Hide text "Danke fuer Bewertung..." when is error occurs and text is shown?
                    if (evaluateNextJointlyGoal) {
                        TextView textViewThankAndEvaluateNext = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.evaluateThankAndNextEvaluation);
                        textViewThankAndEvaluateNext.setVisibility(View.GONE);
                    }
                    // Toast "Evaluate not completly"
                    Toast.makeText(fragmentEvaluateJointlyGoalsContext, fragmentEvaluateJointlyGoalsContext.getResources().getString(R.string.evaluateResultNotCompletely), Toast.LENGTH_SHORT).show();
                }

            }
        });

        // button abbort
        Button buttonAbbortEvaluationJointlyGoal = (Button) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.buttonAbortEvaluateJointlyGoal);
        // onClick listener button abbort
        buttonAbbortEvaluationJointlyGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // reset evaluate results
                resetEvaluateResult ();

                Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_jointly_goals_now");
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
        tmpRadioGroupClear = (RadioGroup) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.radioGroupQuestionOne);
        tmpRadioGroupClear.clearCheck();
        tmpRadioGroupClear = (RadioGroup) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.radioGroupQuestionTwo);
        tmpRadioGroupClear.clearCheck();
        tmpRadioGroupClear = (RadioGroup) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.radioGroupQuestionThree);
        tmpRadioGroupClear.clearCheck();
        tmpRadioGroupClear = (RadioGroup) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.radioGroupQuestionFour);
        tmpRadioGroupClear.clearCheck();

        // Clear comment text field for next evaluation
        EditText tmpInputEvaluateResultComment = (EditText) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.inputEvaluateResultComment);
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



    // call getter Functions in ActivityOurGoals for some data
    private void callGetterFunctionInSuper () {

        int tmpJointlyGoalDbIdToComment = 0;

        // call getter-methode getJointlyGoalsDbIdFromLink() in ActivityOurGoals to get DB ID for the actuale jointly goal
        tmpJointlyGoalDbIdToComment = ((ActivityOurGoals)getActivity()).getJointlyGoalDbIdFromLink();

        if (tmpJointlyGoalDbIdToComment > 0) {
            jointlyGoalDbIdToEvaluate = tmpJointlyGoalDbIdToComment;

            // call getter-methode getJointlyGoalNumberInListview() in ActivityOurGoals to get listView-number for the actuale jointly goal
            jointlyGoalNumberInListView = ((ActivityOurGoals) getActivity()).getJointlyGoalNumberInListview();
            if (jointlyGoalNumberInListView < 1) jointlyGoalNumberInListView = 1; // check borders

            // call getter-methode getevaluateNextJointlyGoal() in ActivityOurGoals for evaluation next jointly goal?
            evaluateNextJointlyGoal = ((ActivityOurGoals) getActivity()).getEvaluateNextJointlyGoal();
        }

    }

}
