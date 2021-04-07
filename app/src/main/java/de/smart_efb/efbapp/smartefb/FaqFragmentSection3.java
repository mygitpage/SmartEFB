package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 12.08.16.
 */
public class FaqFragmentSection3 extends Fragment {

    View viewFragmentSection3;

    // fragment context
    Context fragmentFaqSectionThreeContext = null;

    // shared prefs
    SharedPreferences prefs;

    // expand text list
    String expandTextList = "";

    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentSection3 = layoutInflater.inflate(R.layout.fragment_faq_section_3, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(faqFragmentSection3BrodcastReceiver, filter);

        return viewFragmentSection3;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentFaqSectionThreeContext = getActivity().getApplicationContext();

        // open sharedPrefs
        prefs =  fragmentFaqSectionThreeContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentFaqSectionThreeContext.MODE_PRIVATE);

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentFaqSectionThreeContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentFaqSectionThreeContext, startServiceIntent);
         }

        // display the view
        displayFaqSectionThreeView ("");
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(faqFragmentSection3BrodcastReceiver);
    }


    private BroadcastReceiver faqFragmentSection3BrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {

                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");
                // expand faq strings
                String tmpLessOrMoreText = intentExtras.getString("less_or_more_text", "0");
                String tmpExpandTextList = intentExtras.getString("expand_text_list", "");
                String tmpLinkTextHash = intentExtras.getString("link_text_hash", "");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentFaqSectionThreeContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                }
                else if (tmpLessOrMoreText != null && tmpLessOrMoreText.equals("1") && tmpExpandTextList != null && tmpLinkTextHash != null) {

                    if (tmpExpandTextList.contains(tmpLinkTextHash+";")) {
                        tmpExpandTextList = tmpExpandTextList.replace(tmpLinkTextHash+";", "");
                    }
                    else {
                        tmpExpandTextList = tmpExpandTextList.concat(tmpLinkTextHash+";");
                    }

                    expandTextList = tmpExpandTextList;

                    // display view of expand text
                    displayFaqSectionThreeView (tmpExpandTextList);
                }
            }
        }
    };


    void displayFaqSectionThreeView (String tmpExpandTextList) {

        String tmpText;

        TextView textViewAnswer;

        Bundle returnBundle;

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // get question/ answer set 1 section 3
        tmpText = fragmentFaqSectionThreeContext.getString(R.string.faq_section3_answer1);
        textViewAnswer = (TextView) viewFragmentSection3.findViewById(R.id.faq_section3_answer1);
        returnBundle = ((ActivityFaq) getActivity()).checkAndGenerateMoreOrLessStringLink(tmpText, tmpExpandTextList);
        if (returnBundle.getBoolean("generate")) {
            Spanned tmpLinkText = ((ActivityFaq) getActivity()).makeLinkForMoreOrLessText(returnBundle.getString("substring"), expandTextList, returnBundle.getString("hash_value"));
            textViewAnswer.setText(tmpLinkText);
            textViewAnswer.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            textViewAnswer.setText(tmpText);
        }

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // get question/ answer set 2 section 3
        tmpText = fragmentFaqSectionThreeContext.getString(R.string.faq_section3_answer2);
        textViewAnswer = (TextView) viewFragmentSection3.findViewById(R.id.faq_section3_answer2);
        returnBundle = ((ActivityFaq) getActivity()).checkAndGenerateMoreOrLessStringLink(tmpText, tmpExpandTextList);
        if (returnBundle.getBoolean("generate")) {
            Spanned tmpLinkText = ((ActivityFaq) getActivity()).makeLinkForMoreOrLessText(returnBundle.getString("substring"), expandTextList, returnBundle.getString("hash_value"));
            textViewAnswer.setText(tmpLinkText);
            textViewAnswer.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            textViewAnswer.setText(tmpText);
        }

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // get question/ answer set 3 section 3
        tmpText = fragmentFaqSectionThreeContext.getString(R.string.faq_section3_answer3);
        textViewAnswer = (TextView) viewFragmentSection3.findViewById(R.id.faq_section3_answer3);
        returnBundle = ((ActivityFaq) getActivity()).checkAndGenerateMoreOrLessStringLink(tmpText, tmpExpandTextList);
        if (returnBundle.getBoolean("generate")) {
            Spanned tmpLinkText = ((ActivityFaq) getActivity()).makeLinkForMoreOrLessText(returnBundle.getString("substring"), expandTextList, returnBundle.getString("hash_value"));
            textViewAnswer.setText(tmpLinkText);
            textViewAnswer.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            textViewAnswer.setText(tmpText);
        }

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // get question/ answer set 4 section 3
        tmpText = fragmentFaqSectionThreeContext.getString(R.string.faq_section3_answer4);
        textViewAnswer = (TextView) viewFragmentSection3.findViewById(R.id.faq_section3_answer4);
        returnBundle = ((ActivityFaq) getActivity()).checkAndGenerateMoreOrLessStringLink(tmpText, tmpExpandTextList);
        if (returnBundle.getBoolean("generate")) {
            Spanned tmpLinkText = ((ActivityFaq) getActivity()).makeLinkForMoreOrLessText(returnBundle.getString("substring"), expandTextList, returnBundle.getString("hash_value"));
            textViewAnswer.setText(tmpLinkText);
            textViewAnswer.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            textViewAnswer.setText(tmpText);
        }

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // get question/ answer set 5 section 3
        tmpText = fragmentFaqSectionThreeContext.getString(R.string.faq_section3_answer5);
        textViewAnswer = (TextView) viewFragmentSection3.findViewById(R.id.faq_section3_answer5);
        returnBundle = ((ActivityFaq) getActivity()).checkAndGenerateMoreOrLessStringLink(tmpText, tmpExpandTextList);
        if (returnBundle.getBoolean("generate")) {
            Spanned tmpLinkText = ((ActivityFaq) getActivity()).makeLinkForMoreOrLessText(returnBundle.getString("substring"), expandTextList, returnBundle.getString("hash_value"));
            textViewAnswer.setText(tmpLinkText);
            textViewAnswer.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            textViewAnswer.setText(tmpText);
        }

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // get question/ answer set 6 section 3
        tmpText = fragmentFaqSectionThreeContext.getString(R.string.faq_section3_answer6);
        textViewAnswer = (TextView) viewFragmentSection3.findViewById(R.id.faq_section3_answer6);
        returnBundle = ((ActivityFaq) getActivity()).checkAndGenerateMoreOrLessStringLink(tmpText, tmpExpandTextList);
        if (returnBundle.getBoolean("generate")) {
            Spanned tmpLinkText = ((ActivityFaq) getActivity()).makeLinkForMoreOrLessText(returnBundle.getString("substring"), expandTextList, returnBundle.getString("hash_value"));
            textViewAnswer.setText(tmpLinkText);
            textViewAnswer.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            textViewAnswer.setText(tmpText);
        }
    }

}
