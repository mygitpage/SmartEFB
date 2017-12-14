package de.smart_efb.efbapp.smartefb;

/**
 * Created by ich on 08.02.2017.
 */
class ConstansClassMain {

    // prefs name for the app version
    static final String namePrefsMainNameAppVersion = "smartEfbAppVersion";

    // actual app version
    static final int actualVersion = 1;

    // actual app sub version
    static final int actualSubVersion = 0;

    // prefs name for the app sub version
    static final String namePrefsMainNameAppSubVersion = "smartEfbAppSubVersion";

    // prefs name for the prefs main name
    static final String namePrefsMainNamePrefs = "smartEfbSettings";

    // prefs name for substring of boolean to show/hide main menue element (concat with a number)
    static final String namePrefsSubstringMainMenueElementId = "mainMenueElementId_";

        // prefs name for substring of boolean to show/hide main menue element connect book
        static final String namePrefsMainMenueElementId_ConnectBook = "mainMenueElementId_0";

        // prefs name for substring of boolean to show/hide main menue element our arrangement
        static final String namePrefsMainMenueElementId_OurArrangement = "mainMenueElementId_1";

        // prefs name for substring of boolean to show/hide main menue element our goals
        static final String namePrefsMainMenueElementId_OurGoals = "mainMenueElementId_2";

        // prefs name for substring of boolean to show/hide main menue element timetable
        static final String namePrefsMainMenueElementId_TimeTable = "mainMenueElementId_3";

        // prefs name for substring of boolean to show/hide main menue element prevention
        static final String namePrefsMainMenueElementId_Prevention = "mainMenueElementId_4";

        // prefs name for substring of boolean to show/hide main menue element faq
        static final String namePrefsMainMenueElementId_Faq = "mainMenueElementId_5";

        // prefs name for substring of boolean to show/hide main menue element meeting
        static final String namePrefsMainMenueElementId_Meeting = "mainMenueElementId_6";

        // prefs name for substring of boolean to show/hide main menue element emergency help
        static final String namePrefsMainMenueElementId_EmergencyHelp = "mainMenueElementId_7";

        // prefs name for substring of boolean to show/hide main menue element settings
        static final String namePrefsMainMenueElementId_Settings = "mainMenueElementId_8";

    // total number of elements in main menue (in test-mode please edit variable in class SettingsEfbFragmentD please too!!!!!!!!!!!!!)
    static final int mainMenueNumberOfElements = 9;

    // number of grid columns in main menue
    static final int numberOfGridColumns = 2;

    // wake up time in secondes for exchange service
    static final int wakeUpTimeExchangeService = 50; // secondes

}
