package de.smart_efb.efbapp.smartefb;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by ich on 08.01.2018.
 */

public class EfbLifecycle implements Application.ActivityLifecycleCallbacks {

    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
    }

    // return application is visible
    public static boolean isApplicationVisible() {
        return started > stopped; // true -> app is visible; false -> app is not visible
    }

    // return application is in foreground!
    public static boolean isApplicationInForeground() {
        return resumed > paused; // true -> app is in foreground; false -> app is not in foreground
    }

}
