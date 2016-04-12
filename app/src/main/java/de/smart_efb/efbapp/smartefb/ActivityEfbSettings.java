package de.smart_efb.efbapp.smartefb;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

/**
 * Created by ich on 09.04.16.
 */
public class ActivityEfbSettings extends PreferenceActivity {



    @Override
    public void onBuildHeaders(List<Header> target)
    {
        loadHeadersFromResource(R.xml.preference_efbsettings, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        return FragmentEfbSettings.class.getName().equals(fragmentName);
    }

    /*
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_settings);






    }
    */
}
