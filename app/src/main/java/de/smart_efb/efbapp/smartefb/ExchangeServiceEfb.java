package de.smart_efb.efbapp.smartefb;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ich on 27.07.2017.
 */
public class ExchangeServiceEfb extends Service {

        // Look at:
        // http://www.truiton.com/2014/09/android-service-broadcastreceiver-example/


        // Look at:
        // https://stackoverflow.com/questions/3385106/android-sending-data-from-an-activity-to-a-service


        // Look at
        // https://stackoverflow.com/questions/3293243/pass-data-from-activity-to-service-using-an-intent

        // Look at:
        // https://xjaphx.wordpress.com/2012/07/07/create-a-service-that-does-a-schedule-task/

        private String LOG_TAG = null;
        private ArrayList<String> mList;

        @Override
        public void onCreate() {
            super.onCreate();
            LOG_TAG = this.getClass().getSimpleName();
            Log.i(LOG_TAG, "In onCreate");
            mList = new ArrayList<String>();
            mList.add("Object 1");
            mList.add("Object 2");
            mList.add("Object 3");
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.i(LOG_TAG, "In onStartCommand");
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent broadcastIntent = new Intent();
                    //broadcastIntent.setAction(MainActivity.mBroadcastStringAction);
                    broadcastIntent.putExtra("Data", "Broadcast Data");
                    sendBroadcast(broadcastIntent);

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //broadcastIntent.setAction(MainActivity.mBroadcastIntegerAction);
                    broadcastIntent.putExtra("Data", 10);
                    sendBroadcast(broadcastIntent);

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //broadcastIntent.setAction(MainActivity.mBroadcastArrayListAction);
                    broadcastIntent.putExtra("Data", mList);
                    sendBroadcast(broadcastIntent);
                }
            }).start();
            return START_REDELIVER_INTENT;
        }

        @Override
        public IBinder onBind(Intent intent) {
            // Wont be called as service is not bound
            Log.i(LOG_TAG, "In onBind");
            return null;
        }


        @Override
        public void onTaskRemoved(Intent rootIntent) {
            super.onTaskRemoved(rootIntent);
            Log.i(LOG_TAG, "In onTaskRemoved");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.i(LOG_TAG, "In onDestroy");
        }

















}
