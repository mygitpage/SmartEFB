package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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


    // title of main Menue Elements
    private String[] mainMenueElementTitle = new String [mainMenueNumberOfElements];
    // color of active grid element
    private String[] mainMenueElementColor = new String [mainMenueNumberOfElements];
    // color of inactive element
    private String[] mainMenueElementColorLight = new String [mainMenueNumberOfElements];
    // background ressource of element
    private int[] mainMenueElementBackgroundRessources = new int[mainMenueNumberOfElements];
    // show the menue element
    private boolean[] showMainMenueElement = new boolean[mainMenueNumberOfElements];


    Context mainContext;

    // point to shared preferences
    SharedPreferences prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_main);


        GridView gridview = (GridView) findViewById(R.id.mainMenueGridView);
        gridview.setAdapter(new mainMenueGridViewApdapter(this));
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

        // init the elements arrays (title, color, colorLight, backgroundImage)
        initMainMenueElementsArrays();


    }




    // init the elements arrays (title, color, colorLight, backgroundImage)
    private void initMainMenueElementsArrays() {

        String[] tmpBackgroundRessources;

        // init the context
        mainContext = this;

        // get the shared preferences
        prefs = this.getSharedPreferences("smartEfbSettings", MODE_PRIVATE);

        mainMenueElementTitle = getResources().getStringArray(R.array.mainMenueElementTitle);

        mainMenueElementColor = getResources().getStringArray(R.array.mainMenueElementColor);

        mainMenueElementColorLight = getResources().getStringArray(R.array.mainMenueElementColorLight);

        tmpBackgroundRessources = getResources().getStringArray(R.array.mainMenueElementImage);


        for (int i=0; i<mainMenueNumberOfElements; i++) {
            mainMenueElementBackgroundRessources[i] = getResources().getIdentifier(tmpBackgroundRessources[i], "drawable", "de.smart_efb.efbapp.smartefb");
        }


        for (int i=0; i<mainMenueNumberOfElements; i++) {

            String tmpMainMenueElementName ="mainMenueElementId_" + i;

            showMainMenueElement[i] = false;
            if (prefs.getBoolean(tmpMainMenueElementName, false)) {
                showMainMenueElement[i] = true;
            }
        }

    }


    // Return the number of Buttons
    public int getNumberOfButtons () {
        return mainMenueNumberOfElements;
    }

    //Return original title of menue button
    public String menueButtonTitle (int position) {
        return mainMenueElementTitle[position];
    }



    //
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
        public Object getItem(int arg0) {
            return mainMenueElementBackgroundRessources[arg0];
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View grid;

            // init the layout color with light color
            String tmpLinearLayoutBackgroundColor = mainMenueElementColorLight[position];

            if(convertView==null){
                //grid = new View(mContext);
                LayoutInflater inflater=getLayoutInflater();
                grid=inflater.inflate(R.layout.gridview_main_layout, parent, false);
            }else{
                grid = (View)convertView;
            }

            ImageView imageView = (ImageView) grid.findViewById(R.id.grid_item_image);
            imageView.setImageResource(mainMenueElementBackgroundRessources[position]);

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








