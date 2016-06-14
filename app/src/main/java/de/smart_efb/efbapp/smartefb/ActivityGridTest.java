package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by ich on 14.06.16.
 */
public class ActivityGridTest extends AppCompatActivity {



    private Integer[] mThumbIds = {
            R.drawable.ic_sentiment_satisfied_black_24dp,
            R.drawable.ic_sentiment_satisfied_black_24dp,
            R.drawable.ic_sentiment_satisfied_black_24dp,
            R.drawable.ic_sentiment_satisfied_black_24dp,
            R.drawable.ic_sentiment_satisfied_black_24dp,
            R.drawable.ic_sentiment_satisfied_black_24dp,
            R.drawable.ic_sentiment_satisfied_black_24dp,
            R.drawable.ic_sentiment_satisfied_black_24dp,
            R.drawable.ic_sentiment_satisfied_black_24dp,
            R.drawable.ic_sentiment_satisfied_black_24dp,
            R.drawable.ic_sentiment_satisfied_black_24dp,
            R.drawable.ic_sentiment_satisfied_black_24dp
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_test);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new MyAdapter(this));
        gridview.setNumColumns(2);


    }








    public class MyAdapter extends BaseAdapter {

        private Context mContext;

        public MyAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return mThumbIds.length;
        }

        @Override
        public Object getItem(int arg0) {
            return mThumbIds[arg0];
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View grid;

            if(convertView==null){
                grid = new View(mContext);
                LayoutInflater inflater=getLayoutInflater();
                grid=inflater.inflate(R.layout.gridview_main_layout, parent, false);
            }else{
                grid = (View)convertView;
            }

            ImageView imageView = (ImageView)grid.findViewById(R.id.image);
            imageView.setImageResource(mThumbIds[position]);

            return grid;
        }
    }





}









