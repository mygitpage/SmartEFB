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
 * Created by ich on 10.08.16.
 */
public class OurArrangementFragmentEvaluate extends Fragment {

    // fragment view
    View viewFragmentEvaluate;

    // fragment context
    Context fragmentEvaluateContext = null;

    // the fragment
    Fragment fragmentEvaluateThisFragmentContext;

    // reference to the DB
    DBAdapter myDb;

    // the fab
    FloatingActionButton fabFragmentNowArrangement;

    // shared prefs for the evaluate arrangement
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // DB-Id of arrangement to evaluate
    int arrangementServerDbIdToEvaluate = 0;

    // arrangement number in list view
    int arrangementNumberInListView = 0;

    // cursor for the choosen arrangement
    Cursor cursorChoosenArrangement;
    // cursor for all actual arrangement to choos the next one to evaluate
    Cursor cursorNextArrangementToEvaluate;

    // values for the next arrangement to evaluate
    int nextArrangementServerDbIdToEvaluate = 0;
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

    // set max letters for evaluation result comment
    final int maxLengthForEvaluationResultComment = 600;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentEvaluate = layoutInflater.inflate(R.layout.fragment_our_arrangement_evaluate, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourArrangementFragmentEvaluateBrodcastReceiver, filter);

        return viewFragmentEvaluate;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentEvaluateContext = getActivity().getApplicationContext();

        fragmentEvaluateThisFragmentContext = this;

        // call getter function in ActivityOurArrangement
        callGetterFunctionInSuper();

        // init the fragment evaluate only when an arrangement is choosen
        if (arrangementServerDbIdToEvaluate != 0) {
            // init the fragment now
            initFragmentEvaluate();
        }

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentEvaluateContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentEvaluateContext, startServiceIntent);
        }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourArrangementFragmentEvaluateBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private final BroadcastReceiver ourArrangementFragmentEvaluateBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {
                // check intent order

                Boolean refreshView = false;

                String tmpExtraOurArrangement = intentExtras.getString("OurArrangement","0");
                String tmpExtraOurArrangementNow = intentExtras.getString("OurArrangementNow","0");
                String tmpExtraOurArrangementNowComment = intentExtras.getString("OurArrangementNowComment","0");
                String tmpExtraOurArrangementSettings = intentExtras.getString("OurArrangementSettings","0");
                String tmpExtraOurArrangementCommentShareEnable = intentExtras.getString("OurArrangementSettingsCommentShareEnable","0");
                String tmpExtraOurArrangementCommentShareDisable = intentExtras.getString("OurArrangementSettingsCommentShareDisable","0");
                String tmpExtraOurArrangementResetCommentCountComment = intentExtras.getString("OurArrangementSettingsCommentCountComment","0");
                String tmpUpdateEvaluationLink = intentExtras.getString("UpdateEvaluationLink");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentEvaluateContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    toast.show();

                } else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementNowComment != null && tmpExtraOurArrangementNowComment.equals("1")) {
                    // show toast new comments
                    String updateMessageCommentNow = fragmentEvaluateContext.getString(R.string.toastMessageCommentNowNewComments);
                    Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG).show();
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementNow != null && tmpExtraOurArrangementNow.equals("1")) {
                    // update now arrangement! -> go back to fragment now arrangement and show dialog

                    // check arrangement and now arrangement update and show dialog arrangement and now arrangement change
                    ((ActivityOurArrangement) getActivity()).checkUpdateForShowDialog ("now");

                    // go back to fragment now arrangement -> this is my mother!
                    Intent backIntent = new Intent(getActivity(), ActivityOurArrangement.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    backIntent.putExtra("com","show_arrangement_now");
                    getActivity().startActivity(backIntent);
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementResetCommentCountComment != null && tmpExtraOurArrangementResetCommentCountComment.equals("1")) {
                    // reset now comment counter -> show toast
                    String updateMessageCommentNow = fragmentEvaluateContext.getString(R.string.toastMessageArrangementResetCommentCountComment);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    toast.show();
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementCommentShareDisable  != null && tmpExtraOurArrangementCommentShareDisable .equals("1")) {
                    // sharing is disable -> show toast
                    String updateMessageCommentNow = fragmentEvaluateContext.getString(R.string.toastMessageArrangementCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    toast.show();
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementCommentShareEnable  != null && tmpExtraOurArrangementCommentShareEnable .equals("1")) {
                    // sharing is enable -> show toast
                    String updateMessageCommentNow = fragmentEvaluateContext.getString(R.string.toastMessageArrangementCommentShareEnable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    toast.show();
                }
                else if (tmpUpdateEvaluationLink != null && tmpUpdateEvaluationLink.equals("1")) {
                    // set new start point for evaluation timer in view, when current time is bigger then last start point
                    if (System.currentTimeMillis() >= prefs.getLong(ConstansClassOurArrangement.namePrefsStartPointEvaluationPeriodInMills, 0)) {
                        prefsEditor.putLong(ConstansClassOurArrangement.namePrefsStartPointEvaluationPeriodInMills, System.currentTimeMillis());
                        prefsEditor.apply();
                    }
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1")) {
                    // arrangement settings have change -> refresh view
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
        myDb = new DBAdapter(fragmentEvaluateContext);

        // show fab and set on click listener
        if (fabFragmentNowArrangement != null) {
            fabFragmentNowArrangement.hide();
        }

        // init the prefs
        prefs = fragmentEvaluateContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentEvaluateContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // get choosen arrangement
        cursorChoosenArrangement = myDb.getRowOurArrangement(arrangementServerDbIdToEvaluate);

        // get all actual arrangements
        cursorNextArrangementToEvaluate = myDb.getAllRowsCurrentOurArrangement(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, "equalBlockId"),  "equalBlockId", "ascending");

        // Set correct subtitle in Activity -> "Bewerten Absprache ..."
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleFragmentEvaluateArrangementText", "string", fragmentEvaluateContext.getPackageName())) + " " + arrangementNumberInListView;
        ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "evaluate");

        // set visibility of FAB for this fragment
        ((ActivityOurArrangement) getActivity()).setOurArrangementFABVisibility ("hide", "evaluate");

        // build the view
        //textview for the evaluation intro
        TextView textCommentNumberIntro = (TextView) viewFragmentEvaluate.findViewById(R.id.arrangementEvaluateIntroText);
        textCommentNumberIntro.setText(this.getResources().getString(R.string.showEvaluateArrangementIntroText) + " " + arrangementNumberInListView);

        // onClick listener back button
        Button tmpBackToArrangement = viewFragmentEvaluate.findViewById(R.id.buttonHeaderBackToArrangement);

        tmpBackToArrangement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_arrangement_now");
                getActivity().startActivity(intent);
            }
        });

        // put author name of arrangement
        TextView tmpTextViewAuthorNameText = viewFragmentEvaluate.findViewById(R.id.textAuthorNameArrangement);
        String tmpTextAuthorNameText = String.format(fragmentEvaluateContext.getResources().getString(R.string.ourArrangementEvaluationAuthorNameWithDateForArrangement), cursorChoosenArrangement.getString(cursorChoosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), "dd.MM.yyyy"));
        tmpTextViewAuthorNameText.setText(HtmlCompat.fromHtml(tmpTextAuthorNameText, HtmlCompat.FROM_HTML_MODE_LEGACY));

        // textview for the arrangement
        TextView textViewArrangement = viewFragmentEvaluate.findViewById(R.id.evaluateArrangement);
        String arrangement = cursorChoosenArrangement.getString(cursorChoosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(arrangement);

        // set info text evaluation period
        TextView textViewEvaluationPeriod = viewFragmentEvaluate.findViewById(R.id.infoEvaluationTimePeriod);
        // make time and date variables
        String tmpBeginEvaluationDate = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, System.currentTimeMillis()), "dd.MM.yyyy");
        String tmpBeginEvaluatioTime = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, System.currentTimeMillis()), "HH:mm");
        String tmpEndEvaluationDate = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, System.currentTimeMillis()), "dd.MM.yyyy");
        String tmpEndEvaluatioTime = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, System.currentTimeMillis()), "HH:mm");
        int tmpEvaluationPeriodActive = prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluateActiveTimeInSeconds, 3600) / 3600; // make hours from seconds
        int tmpEvaluationPeriodPassiv = prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluatePauseTimeInSeconds, 3600) / 3600; // make hours from seconds

        String textEvaluationPeriod = "";
        if (tmpEvaluationPeriodActive < 2 && tmpEvaluationPeriodPassiv < 2) {
            // 0 or 1 hour for active and passiv time
            textEvaluationPeriod = String.format(fragmentEvaluateContext.getResources().getString(R.string.ourArrangementEvaluationInfoEvaluationPeriodSingularSingular), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive);
        }
        else if (tmpEvaluationPeriodActive < 2 && tmpEvaluationPeriodPassiv >= 2) {
            // 0 or 1 hour for active and more than one hour for passiv time
            textEvaluationPeriod = String.format(fragmentEvaluateContext.getResources().getString(R.string.ourArrangementEvaluationInfoEvaluationPeriodSingularPlural), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive, tmpEvaluationPeriodPassiv);
        }
        else if (tmpEvaluationPeriodActive >= 2 && tmpEvaluationPeriodPassiv < 2) {
            // more than one hour for active  and 0 or 1 hour for passiv time
            textEvaluationPeriod = String.format(fragmentEvaluateContext.getResources().getString(R.string.ourArrangementEvaluationInfoEvaluationPeriodPluralSingular), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive, tmpEvaluationPeriodPassiv);
        }
        else {
            // more than one hour for active and passiv time
            textEvaluationPeriod = String.format(fragmentEvaluateContext.getResources().getString(R.string.ourArrangementEvaluationInfoEvaluationPeriodPluralPlural), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive, tmpEvaluationPeriodPassiv);
        }

        String textEvaluationNoEvaluationPossibleWhenEvaluate = fragmentEvaluateContext.getResources().getString(R.string.ourArrangementEvaluationNoEvaluationPossibleWhenEvaluate);
        textViewEvaluationPeriod.setText(textEvaluationPeriod + " " + textEvaluationNoEvaluationPossibleWhenEvaluate);

        // set textview for the next arrangement to evaluate
        TextView textViewNextArrangementEvaluateIntro = (TextView) viewFragmentEvaluate.findViewById(R.id.arrangementNextToEvaluateIntroText);
        TextView textViewThankAndEvaluateNext = (TextView) viewFragmentEvaluate.findViewById(R.id.evaluateThankAndNextEvaluation);
        TextView textViewBorderBetweenThankAndEvaluateNext = (TextView) viewFragmentEvaluate.findViewById(R.id.borderBetweenEvaluation1); // Border between Text and evaluation
        nextArrangementServerDbIdToEvaluate = 0;
        nextArrangementListPositionToEvaluate = 0;
        if (cursorNextArrangementToEvaluate != null) { // is there another arrangement to evaluate?

            cursorNextArrangementToEvaluate.moveToFirst();
            do {

                if (cursorNextArrangementToEvaluate.getInt(cursorNextArrangementToEvaluate.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE)) == 1 && cursorNextArrangementToEvaluate.getInt(cursorNextArrangementToEvaluate.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)) != arrangementServerDbIdToEvaluate) { // evaluation possible for arrangement?
                    nextArrangementServerDbIdToEvaluate = cursorNextArrangementToEvaluate.getInt(cursorNextArrangementToEvaluate.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID));
                    nextArrangementListPositionToEvaluate = cursorNextArrangementToEvaluate.getPosition() + 1;
                    cursorNextArrangementToEvaluate.moveToLast();
                }

            } while (cursorNextArrangementToEvaluate.moveToNext());
        }

        // set textview textViewNextArrangementEvaluateIntro
        if (nextArrangementServerDbIdToEvaluate != 0) { // with text: next arrangement to evaluate

            //textViewNextArrangementEvaluateIntro.setText(this.getResources().getString(R.string.showNextArrangementToEvaluateIntroText) + " " + nextArrangementListPositionToEvaluate);
            textViewNextArrangementEvaluateIntro.setText(String.format(this.getResources().getString(R.string.showNextArrangementToEvaluateIntroText), nextArrangementListPositionToEvaluate));

            // Show text "Danke fuer Bewertung und naechste bewerten"
            if (evaluateNextArrangement) {
                textViewThankAndEvaluateNext.setVisibility(View.VISIBLE);
                textViewBorderBetweenThankAndEvaluateNext.setVisibility(View.VISIBLE);
            }
        }
        else { // nothing more to evaluate

            textViewNextArrangementEvaluateIntro.setText(this.getResources().getString(R.string.showNothingNextArrangementToEvaluateText));

            // Show text "Danke fuer Bewertung letzte Vereinbarung"
            if (evaluateNextArrangement) {
                textViewThankAndEvaluateNext.setText(this.getResources().getString(R.string.evaluateThankAndNextEvaluationLastText));
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
                    int resourceId = this.getResources().getIdentifier(tmpRessourceName, "id", fragmentEvaluateContext.getPackageName());

                    tmpRadioButtonQuestion = viewFragmentEvaluate.findViewById(resourceId);
                    tmpRadioButtonQuestion.setOnClickListener(new evaluateRadioButtonListenerQuestion1(numberOfButtons,countQuestion));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // get textView to count input letters and init it
        final TextView textViewCountLettersCommentEditText = viewFragmentEvaluate.findViewById(R.id.countLettersEvaluationCommentResultText);
        String tmpInfoTextCountLetters =  getResources().getString(R.string.infoTextCountLettersForComment);
        tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, "0", maxLengthForEvaluationResultComment);
        textViewCountLettersCommentEditText.setText(tmpInfoTextCountLetters);

        // comment result textfield
        final EditText inputEvaluateResultComment = viewFragmentEvaluate.findViewById(R.id.inputEvaluateResultComment);

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
        Button buttonSendEvaluateResult = viewFragmentEvaluate.findViewById(R.id.buttonSendEvaluateResult);
        // onClick listener send evaluate result
        buttonSendEvaluateResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean evaluateNoError = true;

                TextView tmpErrorTextView;

                // check result question 1
                tmpErrorTextView = viewFragmentEvaluate.findViewById(R.id.questionOneEvaluateError);
                if ( evaluateResultQuestion1 == 0 && tmpErrorTextView != null) {
                    evaluateNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }

                // check result question 2
                tmpErrorTextView = viewFragmentEvaluate.findViewById(R.id.questionTwoEvaluateError);
                if ( evaluateResultQuestion2 == 0 && tmpErrorTextView != null) {
                    evaluateNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }

                // check result question 3
                tmpErrorTextView = viewFragmentEvaluate.findViewById(R.id.questionThreeEvaluateError);
                if ( evaluateResultQuestion3 == 0 && tmpErrorTextView != null) {
                    evaluateNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }

                // check result question 4
                tmpErrorTextView = viewFragmentEvaluate.findViewById(R.id.questionFourEvaluateError);
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
                Long startEvaluationDate = prefs.getLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, System.currentTimeMillis());
                Long endEvaluationDate = prefs.getLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, System.currentTimeMillis());

                // check evaluation on, system time between start- and end date and system time >= last start point
                if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false) && System.currentTimeMillis() < endEvaluationDate && System.currentTimeMillis() > startEvaluationDate && System.currentTimeMillis() >= prefs.getLong(ConstansClassOurArrangement.namePrefsStartPointEvaluationPeriodInMills, 0)) { // evaluation on/off?

                    if (evaluateNoError) {

                        Long tmpCurrentDateofArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis());
                        Long localeTime = System.currentTimeMillis();
                        Long resultTime = System.currentTimeMillis(); // first insert with local system time; will be replace with server time!
                        if (prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L) > 0) {
                            resultTime = prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L); // this is server time, but not actual!
                        }
                        String userName = prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt");
                        int resultStatus = 0; // 0= not send to sever; 1= send to server; 4= external comment
                        Long tmpStartEvaluationDate = prefs.getLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, 0);
                        Long tmpEndEvaluationDate = prefs.getLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, 0);
                        String blockId = cursorChoosenArrangement.getString(cursorChoosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_BLOCK_ID));

                        // insert evaluation result in DB
                        Long tmpDbId = myDb.insertRowOurArrangementEvaluate(arrangementServerDbIdToEvaluate, tmpCurrentDateofArrangement, evaluateResultQuestion1, evaluateResultQuestion2, evaluateResultQuestion3, evaluateResultQuestion4, txtInputEvaluateResultComment, localeTime, resultTime, userName, resultStatus, tmpStartEvaluationDate, tmpEndEvaluationDate, blockId);

                        // delete status evaluation possible for arrangement
                        myDb.changeStatusEvaluationPossibleOurArrangement(arrangementServerDbIdToEvaluate, "delete");

                        // change last evaluation time point for choosen goal
                        myDb.setEvaluationTimePointForArrangement(arrangementServerDbIdToEvaluate);

                        // When last evaluation show toast, because textView is not visible -> new fragment
                        if (nextArrangementServerDbIdToEvaluate == 0) {
                            String messageThankYouForEvaluation = fragmentEvaluateContext.getResources().getString(R.string.evaluateResultSuccsesfulySend);
                            Toast toast = Toast.makeText(fragmentEvaluateContext, messageThankYouForEvaluation, Toast.LENGTH_LONG);
                            toast.show();
                        }

                        // reset evaluate results
                        resetEvaluateResult();

                        // send intent to service to start the service
                        Intent startServiceIntent = new Intent(fragmentEvaluateContext, ExchangeJobIntentServiceEfb.class);
                        startServiceIntent.putExtra("com", "send_evaluation_result_arrangement");
                        startServiceIntent.putExtra("dbid", tmpDbId);
                        startServiceIntent.putExtra("receiverBroadcast", "");
                        // start service
                        ExchangeJobIntentServiceEfb.enqueueWork(fragmentEvaluateContext, startServiceIntent);

                        // build and send intent to next evaluation arrangement or back to OurArrangementNow
                        if (nextArrangementServerDbIdToEvaluate != 0) { // is there another arrangement to evaluate?

                            Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("com", "evaluate_an_arrangement");
                            intent.putExtra("db_id", nextArrangementServerDbIdToEvaluate);
                            intent.putExtra("arr_num", nextArrangementListPositionToEvaluate);
                            intent.putExtra("eval_next", true);

                            getActivity().startActivity(intent);

                        } else {
                            // no arrangement to evaluate anymore! -> go back to OurArrangementNowFragment
                            Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("com", "show_arrangement_now");
                            intent.putExtra("db_id", 0);
                            intent.putExtra("arr_num", 0);
                            intent.putExtra("eval_next", false);
                            getActivity().startActivity(intent);
                        }

                    } else {

                        // Hide text "Danke fuer Bewertung..." when error occurs and text is shown?
                        if (evaluateNextArrangement) {
                            TextView textViewThankAndEvaluateNext = (TextView) viewFragmentEvaluate.findViewById(R.id.evaluateThankAndNextEvaluation);
                            textViewThankAndEvaluateNext.setVisibility(View.GONE);
                            TextView textViewBorderBetweenThankAndEvaluateNext = (TextView) viewFragmentEvaluate.findViewById(R.id.borderBetweenEvaluation1); // Border between Text and evaluation
                            textViewBorderBetweenThankAndEvaluateNext.setVisibility(View.GONE);
                        }
                        // Toast "Evaluate not completly"
                        Toast.makeText(fragmentEvaluateContext, fragmentEvaluateContext.getResources().getString(R.string.evaluateResultNotCompletely), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    // error system time not in evaluation period -> go back to OurArrangementNowFragment
                    Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com", "show_arrangement_now");
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
        tmpRadioGroupClear = viewFragmentEvaluate.findViewById(R.id.radioGroupQuestionOne);
        tmpRadioGroupClear.clearCheck();
        tmpRadioGroupClear = viewFragmentEvaluate.findViewById(R.id.radioGroupQuestionTwo);
        tmpRadioGroupClear.clearCheck();
        tmpRadioGroupClear = viewFragmentEvaluate.findViewById(R.id.radioGroupQuestionThree);
        tmpRadioGroupClear.clearCheck();
        tmpRadioGroupClear = viewFragmentEvaluate.findViewById(R.id.radioGroupQuestionFour);
        tmpRadioGroupClear.clearCheck();

        // Clear comment text field for next evaluation
        EditText tmpInputEvaluateResultComment = viewFragmentEvaluate.findViewById(R.id.inputEvaluateResultComment);
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


    // call getter Functions in ActivityOurArrangement for some data
    private void callGetterFunctionInSuper () {

        int tmpArrangementDbIdToComment = 0;

        // call getter-methode getArrangementDbIdFromLink() in ActivityOurArrangement to get DB ID for the actuale arrangement
        tmpArrangementDbIdToComment = ((ActivityOurArrangement)getActivity()).getArrangementDbIdFromLink();

        // call getter-methode getFabViewOurArrangement() in ActivityOurArrangement to get view for fab
        fabFragmentNowArrangement = ((ActivityOurArrangement)getActivity()).getFabViewOurArrangement();

        if (tmpArrangementDbIdToComment > 0) {
            arrangementServerDbIdToEvaluate = tmpArrangementDbIdToComment;

            // call getter-methode getArrangementNumberInListview() in ActivityOurArrangement to get listView-number for the actuale arrangement
            arrangementNumberInListView = ((ActivityOurArrangement) getActivity()).getArrangementNumberInListview();
            if (arrangementNumberInListView < 1) arrangementNumberInListView = 1; // check borders

            // call getter-methode getEvaluateNextArrangement() in ActivityOurArrangement for evaluation next arrangement?
            evaluateNextArrangement = ((ActivityOurArrangement) getActivity()).getEvaluateNextArrangement();
        }
    }

}
