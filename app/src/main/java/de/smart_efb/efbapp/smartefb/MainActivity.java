package de.smart_efb.efbapp.smartefb;

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


public class MainActivity extends AppCompatActivity {


    // total number of elements (in test-mode please edit variable in class SettingsEfbFragmentD please too!!!!!!!!!!!!!)
    private static int mainMenueNumberOfElements=9;

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
    // background ressource of elemts to show!
    private int[] mainMenueShowElementBackgroundRessources = new int[mainMenueNumberOfElements];


    // show the menue element
    private boolean[] showMainMenueElement = new boolean[mainMenueNumberOfElements];


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
                            //getApplicationContext().startActivity(intent);
                            break;
                        case 4: // grid "faq"
                            //intent = new Intent(getApplicationContext(), ActivityEfbFaq.class);
                            //intent.putExtra("position", position);
                            //intent.putExtra("title", mainMenueElementTitle[position]);
                            //getApplicationContext().startActivity(intent);
                            break;
                        case 5: // grid "termine"
                            //intent = new Intent(getApplicationContext(), ActivityEfbMeeting.class);
                            //intent.putExtra("position", position);
                            //intent.putExtra("title", mainMenueElementTitle[position]);
                            //getApplicationContext().startActivity(intent);
                            break;
                        case 6: // grid "hilfe"
                            //intent = new Intent(getApplicationContext(), ActivityEmergencyHelp.class);
                            //intent.putExtra("position", position);
                            //intent.putExtra("title", mainMenueElementTitle[position]);
                            //getApplicationContext().startActivity(intent);
                            break;
                        case 7: // grid "evaluation"
                            //intent = new Intent(getApplicationContext(), ActivityEmergencyHelp.class);
                            //intent.putExtra("position", position);
                            //intent.putExtra("title", mainMenueElementTitle[position]);
                            //getApplicationContext().startActivity(intent);
                            break;
                        case 8:
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

        Log.d("onStart","Start");


        createMainMenueElementBackgroundRessources ();

        mainMenueGridViewApdapter.notifyDataSetChanged();


    }

    // init the elements arrays (title, color, colorLight, backgroundImage)
    private void initMainMenueElementsArrays() {

        String[] tmpBackgroundRessources, tmpBackgroundRessourcesNewEntry;



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


        for (int i=0; i<mainMenueNumberOfElements; i++) {
            mainMenueElementBackgroundRessources[i] = getResources().getIdentifier(tmpBackgroundRessources[i], "drawable", "de.smart_efb.efbapp.smartefb");
            mainMenueElementBackgroundRessourcesNewEntry[i] = getResources().getIdentifier(tmpBackgroundRessourcesNewEntry[i], "drawable", "de.smart_efb.efbapp.smartefb");
        }

       for (int i=0; i<mainMenueNumberOfElements; i++) {

            String tmpMainMenueElementName ="mainMenueElementId_" + i;

            showMainMenueElement[i] = false;
            if (prefs.getBoolean(tmpMainMenueElementName, false)) {
                showMainMenueElement[i] = true;
            }
        }

    }



    // creates the background ressources for the grid (like new entry or normal image)
    private void createMainMenueElementBackgroundRessources () {



        for (int countElements=0; countElements < mainMenueNumberOfElements; countElements++) {


            switch (countElements) {

                case 0:
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
                case 1:

                    if (myDb.getCountAllNewEntryOurArrangementComment() > 0) {
                        mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesNewEntry[countElements];
                    }
                    else {
                        mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    }


                    break;
                case 2:
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
                case 3:
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
                case 4:
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
                case 5:
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
                case 6:
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
                case 7:
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
                case 8:
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
                default:
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;


            }
        }



    }





    //
    public class mainMenueGridViewApdapter extends BaseAdapter {

        private Context mContext;

        public mainMenueGridViewApdapter(Context c) {

            mContext = c;
        }

        @Override
        public int getCount() {

            Log.d("getCount","C");
            return mainMenueNumberOfElements;
        }

        @Override
        public Object getItem(int item) {

            //return mainMenueElementBackgroundRessources[item];
            Log.d("getItem","I:"+item);
            return mainMenueElementBackgroundRessourcesNewEntry[item];
        }

        @Override
        public long getItemId(int itemId) {

            Log.d("getItemId","I:"+itemId);
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

                Log.d("cV=null","P:"+position);

            }
            else {

                grid = convertView;

                Log.d("cV!=null","P:"+position);
            }

            ImageView imageView = (ImageView) grid.findViewById(R.id.grid_item_image);
            imageView.setImageResource(mainMenueShowElementBackgroundRessources[position]);



            //imageView.setId(position);
            //imageView.setImageResource(mainMenueElementBackgroundRessources[position]);


            TextView txtView = (TextView) grid.findViewById(R.id.grid_item_label);
            txtView.setText(mainMenueElementTitle[position]);

            LinearLayout linearLayoutView = (LinearLayout) grid.findViewById(R.id.grid_linear_layout);
            if (showMainMenueElement[position]) {
                tmpLinearLayoutBackgroundColor = mainMenueElementColor[position];
            }

            linearLayoutView.setBackgroundColor(Color.parseColor(tmpLinearLayoutBackgroundColor));

            return grid;
        }
    }



}








