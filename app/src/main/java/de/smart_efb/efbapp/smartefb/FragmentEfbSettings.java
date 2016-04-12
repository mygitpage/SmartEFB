package de.smart_efb.efbapp.smartefb;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by ich on 12.04.16.
 */
public class FragmentEfbSettings extends PreferenceFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference_efbsettings);
    }
}
