package de.smart_efb.efbapp.smartefb;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by ich on 26.10.2016.
 */
public class ActivityParseDeepLink extends Activity {


    public static final String OUR_ARRANGEMENT = "/ourarrangement";
    public static final String OUR_GOALS = "/ourgoals";

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
        tmpDbId = Integer.parseInt(deepLink.getQueryParameter("db_id"));
        tmpNumberinListView = Integer.parseInt(deepLink.getQueryParameter("arr_num"));
        tmpEvalNext = Boolean.parseBoolean(deepLink.getQueryParameter("eval_next"));




        if (OUR_ARRANGEMENT.equals(path)) {
            // Launch our arrangement
            Log.d("DeepLink","Our Arrangement");

            Intent intent = new Intent(this, ActivityOurArrangement.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("com",tmpCommand);
            intent.putExtra("db_id",tmpDbId);
            intent.putExtra("arr_num",tmpNumberinListView);
            intent.putExtra("eval_next",tmpEvalNext);



            startActivity(intent);
        } else if (OUR_GOALS.equals(path)) {
            // Launch our goals
            Log.d("DeepLink","Our Goals");


            Intent intent = new Intent(this, ActivityOurGoals.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("com",tmpCommand);
            intent.putExtra("db_id",tmpDbId);
            intent.putExtra("arr_num",tmpNumberinListView);
            intent.putExtra("eval_next",tmpEvalNext);


            startActivity(intent);
        } else {
            // Fall back to the main activity
            Log.d("DeepLink","Main Activity");
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
