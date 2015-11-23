package mantvydas.volumr;

import android.app.Application;
import mantvydas.volumr.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by baranauskasm on 13/11/2015.
 */
public class AnalyticsLogger extends Application {
    private Tracker tracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(R.xml.global_tracker);
        }
        return tracker;
    }

    public static void logActivity(String activityName, Application application) {
        Tracker tracker = getSharedGoogleAnalyticsTracker(application);
        tracker.setScreenName(activityName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private static Tracker getSharedGoogleAnalyticsTracker(Application app) {
        AnalyticsLogger application = (AnalyticsLogger) app;
        return application.getDefaultTracker();
    }

}
