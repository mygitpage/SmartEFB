package de.smart_efb.efbapp.smartefb;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {


    // total number of elements (in test-mode please edit variable in class SettingsEfbFragmentD please too!!!!!!!!!!!!!)
    private static int mainMenueNumberOfElements=8;

    // number of grid columns
    final private int numberOfGridColumns = 2;

    // grid view adapter
    mainMenueGridViewApdapter mainMenueGridViewApdapter;


    // title of main Menue Elements
    private String[] mainMenueElementTitle = new String [mainMenueNumberOfElements];
    // color of active grid element
    private String[] mainMenueElementColor = new String [mainMenueNumberOfElements];
    // color of inactive element
    private String[] mainMenueElementColorLight = new String [mainMenueNumberOfElements];

    // background ressource of normal elements (image icon)
    private int[] mainMenueElementBackgroundRessources = new int[mainMenueNumberOfElements];
    // background ressource of new entry elements (image icon)
    private int[] mainMenueElementBackgroundRessourcesNewEntry = new int[mainMenueNumberOfElements];
    // background ressource of inactiv elements (image icon)
    private int[] mainMenueElementBackgroundRessourcesInactiv = new int[mainMenueNumberOfElements];
    // background ressource of elemts to show!
    private int[] mainMenueShowElementBackgroundRessources = new int[mainMenueNumberOfElements];


    // show the menue element
    private boolean[] showMainMenueElement = new boolean[mainMenueNumberOfElements];

    // prefs name for info new meeting date and time (in ActivityMeeting also!!!!!!!!!)
    static final String namePrefsNewMeetingDateAndTime = "meetingNewDateAndTime";
    // prefs praefix for  (in mainActivity also!!!!!!!!!)
    String [] prefsPraefixMeetings = {"_A","_B"};

    Context mainContext;

    // point to shared preferences
    SharedPreferences prefs;

    // reference to the DB
    DBAdapter myDb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_main);

        // init the elements arrays (title, color, colorLight, backgroundImage)
        initMainMenueElementsArrays();

        // create background ressources to show in grid
        createMainMenueElementBackgroundRessources();

        GridView gridview = (GridView) findViewById(R.id.mainMenueGridView);

        mainMenueGridViewApdapter = new mainMenueGridViewApdapter(this);

        gridview.setAdapter(mainMenueGridViewApdapter);
        gridview.setNumColumns(numberOfGridColumns);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if (showMainMenueElement[position]) {

                    Intent intent;

                    switch (position) {

                        case 0: // grid "uebergabe"
                            intent = new Intent(mainContext, ActivityConnectBook.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 1: // grid "absprachen"
                            intent = new Intent(mainContext, ActivityOurArrangement.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 2: // grid "ziele"
                            intent = new Intent(mainContext, ActivityOurGoals.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 3: // grid "praevention"
                            //intent = new Intent(getApplicationContext(), ActivityPrevention.class);
                            //intent.putExtra("position", position);
                            //intent.putExtra("title", mainMenueElementTitle[position]);
                            //mainContext.startActivity(intent);
                            break;
                        case 4: // grid "faq"
                            intent = new Intent(getApplicationContext(), ActivityFaq.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 5: // grid "termine"
                            intent = new Intent(getApplicationContext(), ActivityMeeting.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 6: // grid "hilfe"
                            intent = new Intent(getApplicationContext(), ActivityEmergencyHelp.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 7:
                            // grid "einstellungen"
                            intent = new Intent(getApplicationContext(), ActivitySettingsEfb.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        default:
                            break;
                    }

                }

            }
        });

    }


    @Override
    public void onStart() {

        super.onStart();

        // init array show elements
        initShowElementArray();

        // create background ressources to show in grid
        if (createMainMenueElementBackgroundRessources()) { // new things in grid?

            mainMenueGridViewApdapter.notifyDataSetChanged();

        }

    }

    // init the elements arrays (title, color, colorLight, backgroundImage)
    private void initMainMenueElementsArrays() {

        String[] tmpBackgroundRessources, tmpBackgroundRessourcesNewEntry, tmpBackgroundRessourcesInactiv;

        // init the context
        mainContext = this;

        // get the shared preferences
        prefs = this.getSharedPreferences("smartEfbSettings", MODE_PRIVATE);

        // init the DB
        myDb = new DBAdapter(this);


        mainMenueElementTitle = getResources().getStringArray(R.array.mainMenueElementTitle);

        mainMenueElementColor = getResources().getStringArray(R.array.mainMenueElementColor);

        mainMenueElementColorLight = getResources().getStringArray(R.array.mainMenueElementColorLight);

        tmpBackgroundRessources = getResources().getStringArray(R.array.mainMenueElementImage);
        tmpBackgroundRessourcesNewEntry = getResources().getStringArray(R.array.mainMenueElementImageNewEntry);
        tmpBackgroundRessourcesInactiv =  getResources().getStringArray(R.array.mainMenueElementImageInactiv);

        for (int i=0; i<mainMenueNumberOfElements; i++) {
            mainMenueElementBackgroundRessources[i] = getResources().getIdentifier(tmpBackgroundRessources[i], "drawable", "de.smart_efb.efbapp.smartefb");
            mainMenueElementBackgroundRessourcesNewEntry[i] = getResources().getIdentifier(tmpBackgroundRessourcesNewEntry[i], "drawable", "de.smart_efb.efbapp.smartefb");
            mainMenueElementBackgroundRessourcesInactiv[i] = getResources().getIdentifier(tmpBackgroundRessourcesInactiv[i], "drawable", "de.smart_efb.efbapp.smartefb");
        }

        // init array show elements
        initShowElementArray();

    }

    // init array show elements
    private void initShowElementArray () {

        for (int i=0; i<mainMenueNumberOfElements; i++) {

            String tmpMainMenueElementName ="mainMenueElementId_" + i;

            showMainMenueElement[i] = false;
            if (prefs.getBoolean(tmpMainMenueElementName, false)) {
                showMainMenueElement[i] = true;
            }
        }

    }


    // creates the background ressources for the grid (like new entry or normal image)
    private boolean createMainMenueElementBackgroundRessources () {

        boolean tmpNew = false;

        for (int countElements=0; countElements < mainMenueNumberOfElements; countElements++) {

            switch (countElements) {

                case 0: // menue item "Uebergabe"
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
                case 1: // menue item "Absprachen"
                    if (showMainMenueElement[countElements]) { // is element aktiv?

                        if (myDb.getCountAllNewEntryOurArrangementSketchComment(prefs.getLong("currentDateOfSketchArrangement", System.currentTimeMillis())) > 0 || myDb.getCountAllNewEntryOurArrangementComment(prefs.getLong("currentDateOfArrangement", System.currentTimeMillis())) > 0 || myDb.getCountNewEntryOurArrangement(prefs.getLong("currentDateOfArrangement", System.currentTimeMillis()), "current") > 0 || myDb.getCountNewEntryOurArrangement(prefs.getLong("currentDateOfSketchArrangement", System.currentTimeMillis()), "sketch") > 0) {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesNewEntry[countElements];
                        } else {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                        }
                        tmpNew = true;
                    }
                    else { // element is inaktiv
                        mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesInactiv[countElements];
                        tmpNew = true;
                    }
                    break;
                case 2: // menue item "Ziele"

                    if (showMainMenueElement[countElements]) { // is element aktiv?

                        if (myDb.getCountNewEntryOurGoals(prefs.getLong("currentDateOfJointlyGoals", System.currentTimeMillis())) > 0 || myDb.getCountAllNewEntryOurGoalsJointlyGoalsComment(prefs.getLong("currentDateOfJointlyGoals", System.currentTimeMillis())) > 0 || myDb.getCountNewEntryOurGoals(prefs.getLong("currentDateOfDebetableGoals", System.currentTimeMillis())) > 0 || myDb.getCountAllNewEntryOurGoalsDebetableGoalsComment(prefs.getLong("currentDateOfDebetableGoals", System.currentTimeMillis())) > 0) {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesNewEntry[countElements];
                        } else {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                        }
                        tmpNew = true;
                    }
                    else { // element is inaktiv
                        mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesInactiv[countElements];
                        tmpNew = true;
                    }

                    break;
                case 3: // menue item "Praevention"
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
                case 4: // menue item "FAQ"
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
                case 5: // menue item "Termine"


                    if ( prefs.getBoolean(namePrefsNewMeetingDateAndTime  + prefsPraefixMeetings[0], false) || prefs.getBoolean(namePrefsNewMeetingDateAndTime  + prefsPraefixMeetings[1], false)) {
                        // meeting A or meeting B new!
                        mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesNewEntry[countElements];
                    } else {
                        mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    }
                    tmpNew = true;






                    //mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
                case 6: // menue item "Notfallhilfe"
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
                case 7: // menue item "Evaluation"
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
                case 8: // menue item "Einstellungen"
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
                default:
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;

            }
        }

        return tmpNew;

    }


    // inner class grid view adapter
    public class mainMenueGridViewApdapter extends BaseAdapter {

        private Context mContext;

        public mainMenueGridViewApdapter(Context c) {

            mContext = c;
        }

        @Override
        public int getCount() {

            return mainMenueNumberOfElements;
        }

        @Override
        public Object getItem(int item) {

            return mainMenueElementBackgroundRessources[item];

        }

        @Override
        public long getItemId(int itemId) {

            return itemId;
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent) {

            View grid;

            // init the layout color with light color
            String tmpLinearLayoutBackgroundColor = mainMenueElementColorLight[position];

            if(convertView==null){
                LayoutInflater inflater = getLayoutInflater();
                grid = inflater.inflate (R.layout.gridview_main_layout, parent, false);

            }
            else {

                grid = convertView;

            }

            ImageView imageView = (ImageView) grid.findViewById(R.id.grid_item_image);
            LinearLayout linearLayoutView = (LinearLayout) grid.findViewById(R.id.grid_linear_layout);
            TextView txtView = (TextView) grid.findViewById(R.id.grid_item_label);

            // Element aktiv?
            if (showMainMenueElement[position]) {

                if (imageView != null) {
                    imageView.setImageResource(mainMenueShowElementBackgroundRessources[position]);
                }
                else {
                    imageView = new ImageView(mContext);
                    imageView.setId(R.id.grid_item_image);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    imageView.setImageResource(mainMenueShowElementBackgroundRessources[position]);
                    linearLayoutView.addView(imageView,0);

                }


                txtView.setText(mainMenueElementTitle[position]);
                txtView.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_white));
                tmpLinearLayoutBackgroundColor = mainMenueElementColor[position];
            }
            else { //Element is inaktiv
                txtView.setText(getResources().getString(getResources().getIdentifier("main_menue_text_inactiv", "string", getPackageName())));
                txtView.setTextColor(ContextCompat.getColor(mContext, R.color.main_menue_text_inactiv_color));
                linearLayoutView.removeView(imageView);
            }

            linearLayoutView.setBackgroundColor(Color.parseColor(tmpLinearLayoutBackgroundColor));

            return grid;
        }
    }



}








