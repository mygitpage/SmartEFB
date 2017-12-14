package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;




/**
 * Created by ich on 16.05.2017.
 */
public class EfbHelperConnectionClass {

    Context context;

    public EfbHelperConnectionClass (Context tmp_context) {

        context = tmp_context;

    }

    public boolean internetAvailable(){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }

}
