package de.smart_efb.efbapp.smartefb;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by ich on 26.10.2016.

 Dispatcher for deep link in the app
 */
public class ActivityParseDeepLink extends Activity {

    public static final String OUR_ARRANGEMENT = "/ourarrangement";
    public static final String OUR_GOALS = "/ourgoals";
    public static final String MEETING = "/meeting";
    public static final String SETTINGS = "/settings";
    public static final String FAQ = "/faq";
    public static final String PREVENTION = "/prevention";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check link for border
        Intent intent = getIntent();
        if (intent == null || intent.getData() == null) {
            finish();
        }

        // handle the link
        openDeepLink(intent.getData());

        // Finish this activity
        finish();
    }

    private void openDeepLink (Uri deepLink) {

        String path = deepLink.getPath();

        String tmpCommand = "";
        int tmpDbId = 0;
        int tmpNumberinListView = 0;
        Boolean tmpEvalNext = false;

        // get data that comes with intent-link from URI
        tmpCommand = (deepLink.getQueryParameter("com"));

        if (OUR_ARRANGEMENT.equals(path)) {

            tmpDbId = Integer.parseInt(deepLink.getQueryParameter("db_id"));
            tmpNumberinListView = Integer.parseInt(deepLink.getQueryParameter("arr_num"));
            tmpEvalNext = Boolean.parseBoolean(deepLink.getQueryParameter("eval_next"));

            // send broadcast or intent?
            if (tmpCommand != null && tmpCommand.equals("change_sort_sequence_arrangement_comment")) {
                Intent tmpIntent = new Intent();
                tmpIntent.putExtra("changeSortSequenceOfListView", "1"); // refresh list view in activity/fragement
                tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                this.sendBroadcast(tmpIntent);
            }
            else if (tmpCommand != null && tmpCommand.equals("change_sort_sequence_arrangement_sketch_comment")) {
                Intent tmpIntent = new Intent();
                tmpIntent.putExtra("changeSortSequenceOfListViewSketchComment", "1"); // refresh list view in activity/fragement
                tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                this.sendBroadcast(tmpIntent);
            }
            else {
                // Launch our arrangement
                Intent intent = new Intent(this, ActivityOurArrangement.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com", tmpCommand);
                intent.putExtra("db_id", tmpDbId);
                intent.putExtra("arr_num", tmpNumberinListView);
                intent.putExtra("eval_next", tmpEvalNext);
                startActivity(intent);
            }

        } else if (OUR_GOALS.equals(path)) {

            tmpDbId = Integer.parseInt(deepLink.getQueryParameter("db_id"));
            tmpNumberinListView = Integer.parseInt(deepLink.getQueryParameter("arr_num"));
            tmpEvalNext = Boolean.parseBoolean(deepLink.getQueryParameter("eval_next"));

            // send broadcast or intent?
            if (tmpCommand != null && tmpCommand.equals("change_sort_sequence_jointly_goal_comment")) {
                Intent tmpIntent = new Intent();
                tmpIntent.putExtra("changeSortSequenceOfListViewJointlyComment", "1"); // refresh list view in activity/fragement
                tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                this.sendBroadcast(tmpIntent);
            }
            else if (tmpCommand != null && tmpCommand.equals("change_sort_sequence_debetable_goal_comment")) {
                Intent tmpIntent = new Intent();
                tmpIntent.putExtra("changeSortSequenceOfListViewDebetableComment", "1"); // refresh list view in activity/fragement
                tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                this.sendBroadcast(tmpIntent);
            }
            else {
                // Launch our goals
                Intent intent = new Intent(this, ActivityOurGoals.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com", tmpCommand);
                intent.putExtra("db_id", tmpDbId);
                intent.putExtra("arr_num", tmpNumberinListView);
                intent.putExtra("eval_next", tmpEvalNext);
                startActivity(intent);
            }

        } else if (MEETING.equals(path)) {

            Long tmpMeetingId = Long.parseLong(deepLink.getQueryParameter("meeting_id"));

            // Launch settings
            Intent intent = new Intent(this, ActivityMeeting.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("meeting_id",tmpMeetingId);
            intent.putExtra("com",tmpCommand);
            startActivity(intent);

        } else if (SETTINGS.equals(path)) {

            // Launch settings
            Intent intent = new Intent(this, ActivitySettingsEfb.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("com",tmpCommand);
            startActivity(intent);

        } else if (FAQ.equals(path)) {
            // Launch settings
            Intent intent = new Intent(this, ActivityFaq.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("com",tmpCommand);
            startActivity(intent);

        } else if (PREVENTION.equals(path)) {

            String expandTextList = "";
            String linkTextHash = "";
            if (tmpCommand.equals("less_or_more_text")) {
                expandTextList = deepLink.getQueryParameter("expand_text_list");
                linkTextHash = deepLink.getQueryParameter("link_text_hash");
            }

            // Launch prevention
            Intent intent = new Intent(this, ActivityPrevention.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("com",tmpCommand);
            intent.putExtra("expand_text_list",expandTextList);
            intent.putExtra("link_text_hash",linkTextHash);
            startActivity(intent);

        } else {
            // Fall back to the main activity
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
