package de.smart_efb.efbapp.smartefb;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ActivityFirstPage extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_first_page);


    }


    /*protected void onStart() {

        super.onStart();

        ActionBar actionBar = getActionBar();

        actionBar.setSubtitle("First Page");
        actionBar.setTitle("Smart EFB");
    }
    */

}
