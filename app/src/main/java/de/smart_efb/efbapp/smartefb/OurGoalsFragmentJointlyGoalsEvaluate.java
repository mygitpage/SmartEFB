package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Created by ich on 31.10.2016.
 */
public class OurGoalsFragmentJointlyGoalsEvaluate extends Fragment {

    // fragment view
    View viewFragmentJointlyGoalsEvaluate;

    // fragment context
    Context fragmentEvaluateJointlyGoalsContext = null;

    // the fragment
    Fragment fragmentEvaluateThisFragmentContext;

    // reference to the DB
    DBAdapter myDb;

    // the fab
    FloatingActionButton fabFragmentJointlyGoal;

    // shared prefs for the evaluate jointly goals
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // DB-Id of jointly goal to evaluate
    int jointlyGoalServerDbIdToEvaluate = 0;

    // jointly goal number in list view
    int jointlyGoalNumberInListView = 0;

    // block id of current jointly goals
    String currentBlockIdOfJointlyGoals = "";

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
    String[] partRessourceNameQuestion =  { "OneJointlyGoal_", "TwoJointlyGoal_", "ThreeJointlyGoal_", "FourJointlyGoal_" };

    // Evaluate question result
    int evaluateResultQuestion1 = 0;
    int evaluateResultQuestion2 = 0;
    int evaluateResultQuestion3 = 0;
    int evaluateResultQuestion4 = 0;

    // set max letters for evaluation result comment
    final int maxLengthForEvaluationResultComment = 600;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentJointlyGoalsEvaluate = layoutInflater.inflate(R.layout.fragment_our_goals_jointly_goals_evaluate, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourGoalsFragmentEvaluateJointlyGoalsBrodcastReceiver, filter);

        return viewFragmentJointlyGoalsEvaluate;
    }



    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentEvaluateJointlyGoalsContext = getActivity().getApplicationContext();

        fragmentEvaluateThisFragmentContext = this;

        // call getter function in ActivityOurGoals
        callGetterFunctionInSuper();

        // init the fragment evaluate only when an jointly goal is choosen
        if (jointlyGoalServerDbIdToEvaluate != 0) {

            // init the fragment now
            initFragmentEvaluate();
        }

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentEvaluateJointlyGoalsContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentEvaluateJointlyGoalsContext, startServiceIntent);
        }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourGoalsFragmentEvaluateJointlyGoalsBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private final BroadcastReceiver ourGoalsFragmentEvaluateJointlyGoalsBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {

                Boolean refreshView = false;

                // check intent order
                String tmpExtraOurGoals = intentExtras.getString("OurGoals","0");
                String tmpExtraOurGoalsNow = intentExtras.getString("OurGoalsJointlyNow","0");
                String tmpExtraOurGoalsNowComment = intentExtras.getString("OurGoalsJointlyComment","0");
                String tmpExtraOurGoalsSettings = intentExtras.getString("OurGoalsSettings","0");
                String tmpExtraOurGoalsCommentShareEnable = intentExtras.getString("OurGoalsSettingsCommentShareEnable","0");
                String tmpExtraOurGoalsCommentShareDisable = intentExtras.getString("OurGoalsSettingsCommentShareDisable","0");
                String tmpExtraOurGoalsResetCommentCountComment = intentExtras.getString("OurGoalsSettingsCommentCountComment","0");
                String tmpUpdateEvaluationLink = intentExtras.getString("UpdateJointlyEvaluationLink");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentEvaluateThisFragmentContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsNowComment != null && tmpExtraOurGoalsNowComment.equals("1")) {
                    // new comments -> update now view -> show toast
                    String updateMessageCommentNow = fragmentEvaluateThisFragmentContext.getString(R.string.toastMessageCommentJointlyGoalsNewComments);
                    Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG).show();
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsNow != null && tmpExtraOurGoalsNow.equals("1")) {
                    // new jointly goals on smartphone -> update now view

                    //update current block id of jointly goals
                    currentBlockIdOfJointlyGoals = prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, "0");

                    // check jointly and debetable goals update and show dialog jointly and debetable goals change
                    ((ActivityOurGoals) getActivity()).checkUpdateForShowDialog ("jointly");

                    // go back to fragment jointly goals -> this is my mother!
                    Intent backIntent = new Intent(getActivity(), ActivityOurGoals.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    backIntent.putExtra("com","show_jointly_goals_now");
                    getActivity().startActivity(backIntent);
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsResetCommentCountComment != null && tmpExtraOurGoalsResetCommentCountComment.equals("1")) {
                    // reset now comment counter -> show toast and update view
                    String updateMessageCommentNow = fragmentEvaluateThisFragmentContext.getString(R.string.toastMessageJointlyGoalsResetCommentCountComment);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareDisable  != null && tmpExtraOurGoalsCommentShareDisable.equals("1")) {
                    // sharing is disable -> show toast and update view
                    String updateMessageCommentNow = fragmentEvaluateThisFragmentContext.getString(R.string.toastMessageJointlyGoalsCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareEnable  != null && tmpExtraOurGoalsCommentShareEnable.equals("1")) {
                    // sharing is enable -> show toast and update view
                    String updateMessageCommentNow = fragmentEvaluateThisFragmentContext.getString(R.string.toastMessageJointlyGoalsCommentShareEnable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
                else if (tmpUpdateEvaluationLink != null && tmpUpdateEvaluationLink.equals("1")) {
                    // set new start point for evaluation timer in view
                    if (System.currentTimeMillis() >= prefs.getLong(ConstansClassOurGoals.namePrefsStartPointJointlyGoalsEvaluationPeriodInMills, 0)) {
                        prefsEditor.putLong(ConstansClassOurGoals.namePrefsStartPointJointlyGoalsEvaluationPeriodInMills, System.currentTimeMillis());
                        prefsEditor.apply();
                    }
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1")) {

                    // goal settings change
                    refreshView = true;
                }

                if (refreshView) {
                    // refresh fragments view
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(fragmentEvaluateThisFragmentContext).attach(fragmentEvaluateThisFragmentContext).commit();
                }
            }
        }
    };


    // inits the fragment for use
    private void initFragmentEvaluate() {

        // init the DB
        myDb = new DBAdapter(fragmentEvaluateJointlyGoalsContext);

        // show fab and set on click listener
        if (fabFragmentJointlyGoal != null) {
            fabFragmentJointlyGoal.hide();
        }

        // init the prefs
        prefs = fragmentEvaluateJointlyGoalsContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentEvaluateJointlyGoalsContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        //get current block id of jointly goals
        currentBlockIdOfJointlyGoals = prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, "0");

        // get choosen jointly goal
        cursorChoosenJointlyGoal = myDb.getJointlyRowOurGoals(jointlyGoalServerDbIdToEvaluate);

        // get all actual jointly goals
        cursorNextJointlyGoalToEvaluate = myDb.getAllJointlyRowsOurGoals(currentBlockIdOfJointlyGoals, "equalBlockId", "ascending");

        // Set correct subtitle in Activity -> "Ziel ... bewerten"
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleEvaluateJointlyGoal", "string", fragmentEvaluateJointlyGoalsContext.getPackageName()));
        tmpSubtitle = String.format(tmpSubtitle, jointlyGoalNumberInListView);
        ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyEvaluate");

        // set visibility of FAB for this fragment
        ((ActivityOurGoals) getActivity()).setOurGoalFABVisibility ("hide", "jointlyEvaluate");

        // build the view
        //textview for the evaluation intro
        TextView textCommentNumberIntro = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.jointlyGoalEvaluateIntroText);
        textCommentNumberIntro.setText(this.getResources().getString(R.string.showEvaluateJointlyGoalIntroText) + " " + jointlyGoalNumberInListView);

        // onClick listener back button
        Button tmpBackToArrangement = viewFragmentJointlyGoalsEvaluate.findViewById(R.id.buttonHeaderBackToGoal);

        tmpBackToArrangement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_jointly_goals_now");
                getActivity().startActivity(intent);
            }
        });

        // put author name of goal
        TextView tmpTextViewAuthorNameText = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.textAuthorNameGoal);
        String tmpTextAuthorNameText = String.format(fragmentEvaluateJointlyGoalsContext.getResources().getString(R.string.ourGoalsEvaluationAuthorNameWithDateForGoal), cursorChoosenJointlyGoal.getString(cursorChoosenJointlyGoal.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis()), "dd.MM.yyyy"));
        tmpTextViewAuthorNameText.setText(HtmlCompat.fromHtml(tmpTextAuthorNameText, HtmlCompat.FROM_HTML_MODE_LEGACY));

        // textview for the jointly goal
        TextView textViewJointlyGoal = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.evaluateJointlyGoal);
        String jointlyGoal = cursorChoosenJointlyGoal.getString(cursorChoosenJointlyGoal.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL));
        textViewJointlyGoal.setText(jointlyGoal);

        // set info text evaluation period
        TextView textViewEvaluationPeriod = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.infoEvaluationTimePeriod);
        // make time and date variables
        String tmpBeginEvaluationDate = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsStartDateJointlyGoalsEvaluationInMills, System.currentTimeMillis()), "dd.MM.yyyy");
        String tmpBeginEvaluatioTime = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsStartDateJointlyGoalsEvaluationInMills, System.currentTimeMillis()), "HH:mm");
        String tmpEndEvaluationDate = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsEndDateJointlyGoalsEvaluationInMills, System.currentTimeMillis()), "dd.MM.yyyy");
        String tmpEndEvaluatioTime = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsEndDateJointlyGoalsEvaluationInMills, System.currentTimeMillis()), "HH:mm");
        int tmpEvaluationPeriodActive = prefs.getInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsActiveTimeInSeconds, 3600) / 3600; // make hours from seconds
        int tmpEvaluationPeriodPassiv = prefs.getInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsPauseTimeInSeconds, 3600) / 3600; // make hours from seconds

        String textEvaluationPeriod = "";
        if (tmpEvaluationPeriodActive < 2 && tmpEvaluationPeriodPassiv < 2) {
            // 0 or 1 hour for active and passiv time
            textEvaluationPeriod = String.format(fragmentEvaluateJointlyGoalsContext.getResources().getString(R.string.evaluateJointlyGoalInfoEvaluationPeriodSingularSingular), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive);
        }
        else if (tmpEvaluationPeriodActive < 2 && tmpEvaluationPeriodPassiv >= 2) {
            // 0 or 1 hour for active and more than one hour for passiv time
            textEvaluationPeriod = String.format(fragmentEvaluateJointlyGoalsContext.getResources().getString(R.string.evaluateJointlyGoalInfoEvaluationPeriodSingularPlural), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive, tmpEvaluationPeriodPassiv);
        }
        else if (tmpEvaluationPeriodActive >= 2 && tmpEvaluationPeriodPassiv < 2) {
            // more than one hour for active  and 0 or 1 hour for passiv time
            textEvaluationPeriod = String.format(fragmentEvaluateJointlyGoalsContext.getResources().getString(R.string.evaluateJointlyGoalInfoEvaluationPeriodPluralSingular), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive, tmpEvaluationPeriodPassiv);
        }
        else {
            // more than one hour for active and passiv time
            textEvaluationPeriod = String.format(fragmentEvaluateJointlyGoalsContext.getResources().getString(R.string.evaluateJointlyGoalInfoEvaluationPeriodPluralPlural), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive, tmpEvaluationPeriodPassiv);
        }

        String textEvaluationNoEvaluationPossibleWhenEvaluate = fragmentEvaluateJointlyGoalsContext.getResources().getString(R.string.ourGoalsEvaluationNoEvaluationPossibleWhenEvaluate);
        textViewEvaluationPeriod.setText(textEvaluationPeriod + " " + textEvaluationNoEvaluationPossibleWhenEvaluate);
        
        // set textview for the next jointly goal to evaluate
        TextView textViewNextJointlyGoalEvaluateIntro = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.jointlyGoalNextToEvaluateIntroText);
        TextView textViewThankAndEvaluateNext = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.evaluateThankAndNextEvaluation);
        TextView textViewBorderBetweenThankAndEvaluateNext = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.borderBetweenEvaluation1); // Border between Text and evaluation
        nextJointlyGoalDbIdToEvaluate = 0;
        nextJointlyGoalListPositionToEvaluate = 0;
        if (cursorNextJointlyGoalToEvaluate != null) { // is there another jointly goal to evaluate?

            cursorNextJointlyGoalToEvaluate.moveToFirst();
            do {
                if (cursorNextJointlyGoalToEvaluate.getInt(cursorNextJointlyGoalToEvaluate.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_EVALUATE_POSSIBLE)) == 1 && cursorNextJointlyGoalToEvaluate.getInt(cursorNextJointlyGoalToEvaluate.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID)) != jointlyGoalServerDbIdToEvaluate) { // evaluation possible for jointly goal?
                    nextJointlyGoalDbIdToEvaluate = cursorNextJointlyGoalToEvaluate.getInt(cursorNextJointlyGoalToEvaluate.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID));
                    nextJointlyGoalListPositionToEvaluate = cursorNextJointlyGoalToEvaluate.getPosition() + 1;
                    cursorNextJointlyGoalToEvaluate.moveToLast();
                }

            } while (cursorNextJointlyGoalToEvaluate.moveToNext());
        }

      // set textview textViewNextJointlyGoalEvaluateIntro
        if (nextJointlyGoalDbIdToEvaluate != 0) { // with text: next jointly goal to evaluate

            textViewNextJointlyGoalEvaluateIntro.setText(String.format(this.getResources().getString(R.string.showNextJointlyGoalToEvaluateIntroText), nextJointlyGoalListPositionToEvaluate));

            // Show text "Danke fuer Bewertung und naechste bewerten"
            if (evaluateNextJointlyGoal) {
                textViewThankAndEvaluateNext.setVisibility(View.VISIBLE);
                textViewBorderBetweenThankAndEvaluateNext.setVisibility(View.VISIBLE);
            }
        }
        else { // nothing more to evaluate

            textViewNextJointlyGoalEvaluateIntro.setText(this.getResources().getString(R.string.showNothingNextJointlyGoalToEvaluateText));

            // Show text "Danke fuer Bewertung letztes Ziel"
            if (evaluateNextJointlyGoal) {
                textViewThankAndEvaluateNext.setText(this.getResources().getString(R.string.evaluateThankAndNextEvaluationJointlyGoalLastText));
                textViewThankAndEvaluateNext.setVisibility(View.VISIBLE);
                textViewBorderBetweenThankAndEvaluateNext.setVisibility(View.VISIBLE);
            }
        }

        // set onClickListener for radio button in radio group question 1-4
        String tmpRessourceName ="";
        RadioButton tmpRadioButtonQuestion;
        for (int countQuestion = 0; countQuestion < countQuestionNumber; countQuestion++) {

            for (int numberOfButtons=0; numberOfButtons < numberRadioButtonQuestion[countQuestion]; numberOfButtons++) {
                tmpRessourceName ="question" + partRessourceNameQuestion[countQuestion] + (numberOfButtons+1);
                try {
                    int resourceId = this.getResources().getIdentifier(tmpRessourceName, "id", fragmentEvaluateJointlyGoalsContext.getPackageName());

                    tmpRadioButtonQuestion = viewFragmentJointlyGoalsEvaluate.findViewById(resourceId);
                    tmpRadioButtonQuestion.setOnClickListener(new evaluateRadioButtonListenerQuestion1(numberOfButtons,countQuestion));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // get textView to count input letters and init it
        final TextView textViewCountLettersCommentEditText = viewFragmentJointlyGoalsEvaluate.findViewById(R.id.countLettersEvaluationCommentResultText);
        String tmpInfoTextCountLetters =  getResources().getString(R.string.infoTextCountLettersForComment);
        tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, "0", maxLengthForEvaluationResultComment);
        textViewCountLettersCommentEditText.setText(tmpInfoTextCountLetters);

        // comment result textfield
        final EditText inputEvaluateResultComment = viewFragmentJointlyGoalsEvaluate.findViewById(R.id.inputJointlyGoalEvaluateResultComment);

        // set text watcher to count letters in comment result field
        final TextWatcher inputEvaluateResultCommentTextWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //
                String tmpInfoTextCountLetters =  getResources().getString(R.string.infoTextCountLettersForComment);
                tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, String.valueOf(s.length()), maxLengthForEvaluationResultComment);
                textViewCountLettersCommentEditText.setText(tmpInfoTextCountLetters);
            }
            public void afterTextChanged(Editable s) {
            }
        };

        // set text watcher to count input letters
        inputEvaluateResultComment.addTextChangedListener(inputEvaluateResultCommentTextWatcher);

        // set input filter max length for comment field
        inputEvaluateResultComment.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthForEvaluationResultComment)});

        // Button save evaluate result OR abort
        // button send evaluate result
        Button buttonSendEvaluateResult = viewFragmentJointlyGoalsEvaluate.findViewById(R.id.buttonSendEvaluateJointlyGoalResult);
        // onClick listener send evaluate result
        buttonSendEvaluateResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean evaluateNoError = true;

                TextView tmpErrorTextView;

                // check result question 1
                tmpErrorTextView = viewFragmentJointlyGoalsEvaluate.findViewById(R.id.questionOneJointlyGoalEvaluateError);
                if ( evaluateResultQuestion1 == 0 && tmpErrorTextView != null) {
                    evaluateNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }

                // check result question 2
                tmpErrorTextView = viewFragmentJointlyGoalsEvaluate.findViewById(R.id.questionTwoJointlyGoalEvaluateError);
                if ( evaluateResultQuestion2 == 0 && tmpErrorTextView != null) {
                    evaluateNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }

                // check result question 3
                tmpErrorTextView = viewFragmentJointlyGoalsEvaluate.findViewById(R.id.questionThreeJointlyGoalEvaluateError);
                if ( evaluateResultQuestion3 == 0 && tmpErrorTextView != null) {
                    evaluateNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }

                // check result question 4
                tmpErrorTextView = viewFragmentJointlyGoalsEvaluate.findViewById(R.id.questionFourJointlyGoalEvaluateError);
                if ( evaluateResultQuestion4 == 0 && tmpErrorTextView != null) {
                    evaluateNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }

                // get evaluate result comment
                String txtInputEvaluateResultComment = "";
                if (inputEvaluateResultComment != null) {
                    txtInputEvaluateResultComment = inputEvaluateResultComment.getText().toString();
                }

                // get start time and end time for evaluation
                Long startEvaluationDate = prefs.getLong(ConstansClassOurGoals.namePrefsStartDateJointlyGoalsEvaluationInMills, System.currentTimeMillis());
                Long endEvaluationDate = prefs.getLong(ConstansClassOurGoals.namePrefsEndDateJointlyGoalsEvaluationInMills, System.currentTimeMillis());

                // check evaluation on, system time between start- and end date and system time >= last start point
                if (prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkEvaluateJointlyGoals, false) && System.currentTimeMillis() < endEvaluationDate && System.currentTimeMillis() > startEvaluationDate && System.currentTimeMillis() >= prefs.getLong(ConstansClassOurGoals.namePrefsStartPointJointlyGoalsEvaluationPeriodInMills, 0)) { // evaluation on/off?

                    if (evaluateNoError) {

                        Long tmpCurrentDateofJointlyGoals = prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis());
                        Long localeTime = System.currentTimeMillis();
                        Long resultTime = System.currentTimeMillis(); // first insert with local system time; will be replace with server time!
                        if (prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L) > 0) {
                            resultTime = prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L); // this is server time, but not actual!
                        }
                        String userName = prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt");
                        int resultStatus = 0; // 0= not send to sever; 1= send to server; 4= external comment
                        Long tmpStartEvaluationDate = prefs.getLong(ConstansClassOurGoals.namePrefsStartDateJointlyGoalsEvaluationInMills, 0);
                        Long tmpEndEvaluationDate =  prefs.getLong(ConstansClassOurGoals.namePrefsEndDateJointlyGoalsEvaluationInMills, 0);
                        String blockId = cursorChoosenJointlyGoal.getString(cursorChoosenJointlyGoal.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_BLOCK_ID));

                        // insert evaluation result in DB
                        Long tmpDbId = myDb.insertRowOurGoalsJointlyGoalEvaluate(jointlyGoalServerDbIdToEvaluate, tmpCurrentDateofJointlyGoals, evaluateResultQuestion1, evaluateResultQuestion2, evaluateResultQuestion3, evaluateResultQuestion4, txtInputEvaluateResultComment, localeTime, resultTime, userName, resultStatus, tmpStartEvaluationDate, tmpEndEvaluationDate, blockId);

                        // delete status evaluation possible for jointly goal
                        myDb.changeStatusEvaluationPossibleOurGoals(jointlyGoalServerDbIdToEvaluate, "delete");

                        // change last evaluation time point for choosen goal
                        myDb.setEvaluationTimePointForGoal(jointlyGoalServerDbIdToEvaluate);

                        // When last evaluation show toast, because textView is not visible -> new fragment
                        if (nextJointlyGoalDbIdToEvaluate == 0) {
                            String tmpEvaluationResultsSendSuccsessfull = fragmentEvaluateJointlyGoalsContext.getString(R.string.evaluateResultJointlyGoalSuccsesfulySend);
                            Toast toast = Toast.makeText(fragmentEvaluateJointlyGoalsContext, tmpEvaluationResultsSendSuccsessfull, Toast.LENGTH_LONG);
                            TextView viewToast = toast.getView().findViewById(android.R.id.message);
                            if (v != null) viewToast.setGravity(Gravity.CENTER);
                            toast.show();
                        }

                        // reset evaluate results
                        resetEvaluateResult();

                        // send intent to service to start the service and send evaluation result to server!
                        Intent startServiceIntent = new Intent(fragmentEvaluateJointlyGoalsContext, ExchangeJobIntentServiceEfb.class);
                        // set command = "ask new data" on server
                        startServiceIntent.putExtra("com", "send_evaluation_result_goal");
                        startServiceIntent.putExtra("dbid", tmpDbId);
                        startServiceIntent.putExtra("receiverBroadcast", "");
                        // start service
                        ExchangeJobIntentServiceEfb.enqueueWork(fragmentEvaluateJointlyGoalsContext, startServiceIntent);

                        // build and send intent to next evaluation jointly goal or back to OurGoalsJointlyGoalsNow
                        if (nextJointlyGoalDbIdToEvaluate != 0) { // is there another jointly goal to evaluate?

                            Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("com", "evaluate_an_jointly_goal");
                            intent.putExtra("db_id", (int) nextJointlyGoalDbIdToEvaluate);
                            intent.putExtra("arr_num", (int) nextJointlyGoalListPositionToEvaluate);
                            intent.putExtra("eval_next", true);

                            getActivity().startActivity(intent);

                        } else {
                            // no jointly goal to evaluate anymore! -> go back to OurGoalsFragmentJointlyGoalsNow
                            Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("com", "show_jointly_goals_now");
                            intent.putExtra("db_id", 0);
                            intent.putExtra("arr_num", 0);
                            intent.putExtra("eval_next", false);
                            getActivity().startActivity(intent);
                        }

                    } else {

                        // Hide text "Danke fuer Bewertung..." when is error occurs and text is shown?
                        if (evaluateNextJointlyGoal) {
                            TextView textViewThankAndEvaluateNext = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.evaluateThankAndNextEvaluation);
                            textViewThankAndEvaluateNext.setVisibility(View.GONE);
                            TextView textViewBorderBetweenThankAndEvaluateNext = (TextView) viewFragmentJointlyGoalsEvaluate.findViewById(R.id.borderBetweenEvaluation1);
                            textViewBorderBetweenThankAndEvaluateNext.setVisibility(View.GONE);
                        }
                        // Toast "Evaluate not completly"
                        Toast.makeText(fragmentEvaluateJointlyGoalsContext, fragmentEvaluateJointlyGoalsContext.getResources().getString(R.string.evaluateJointlyGoalResultNotCompletely), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    // error system time not in evaluation period -> go back to OurGoalsJointlyGoalsNowFragment
                    Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com", "show_jointly_goals_now");
                    intent.putExtra("db_id", 0);
                    intent.putExtra("arr_num", 0);
                    intent.putExtra("eval_next", false);
                    getActivity().startActivity(intent);
                }
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
        tmpRadioGroupClear = viewFragmentJointlyGoalsEvaluate.findViewById(R.id.radioGroupQuestionOneJointlyGoal);
        tmpRadioGroupClear.clearCheck();
        tmpRadioGroupClear = viewFragmentJointlyGoalsEvaluate.findViewById(R.id.radioGroupQuestionTwoJointlyGoal);
        tmpRadioGroupClear.clearCheck();
        tmpRadioGroupClear = viewFragmentJointlyGoalsEvaluate.findViewById(R.id.radioGroupQuestionThreeJointlyGoal);
        tmpRadioGroupClear.clearCheck();
        tmpRadioGroupClear = viewFragmentJointlyGoalsEvaluate.findViewById(R.id.radioGroupQuestionFourJointlyGoal);
        tmpRadioGroupClear.clearCheck();

        // Clear comment text field for next evaluation
        EditText tmpInputEvaluateResultComment = viewFragmentJointlyGoalsEvaluate.findViewById(R.id.inputJointlyGoalEvaluateResultComment);
        tmpInputEvaluateResultComment.setText("");
    }

    // onClickListener for radioButtons in fragment layout evaluate
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

        // call getter-methode getFabViewOurGoals() in ActivityOurGoals to get view for fab
        fabFragmentJointlyGoal = ((ActivityOurGoals)getActivity()).getFabViewOurGoals();

        if (tmpJointlyGoalDbIdToComment > 0) {
            jointlyGoalServerDbIdToEvaluate = tmpJointlyGoalDbIdToComment;

            // call getter-methode getJointlyGoalNumberInListview() in ActivityOurGoals to get listView-number for the actuale jointly goal
            jointlyGoalNumberInListView = ((ActivityOurGoals) getActivity()).getJointlyGoalNumberInListview();
            if (jointlyGoalNumberInListView < 1) jointlyGoalNumberInListView = 1; // check borders

            // call getter-methode getevaluateNextJointlyGoal() in ActivityOurGoals for evaluation next jointly goal?
            evaluateNextJointlyGoal = ((ActivityOurGoals) getActivity()).getEvaluateNextJointlyGoal();
        }
    }


}
