package usbdemo.vr.artifact.com.usbdemo;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Usage:
 *
 * 1. Get the Foreground Singleton, passing a Context or Application object unless you
 * are sure that the Singleton has definitely already been initialised elsewhere.
 *
 * 2.a) Perform a direct, synchronous check: Foreground.isForeground() / .isBackground()
 *
 * or
 *
 * 2.b) Register to be notified (useful in Service or other non-UI components):
 *
 * Foreground.Listener myListener = new Foreground.Listener(){
 * public void onBecameForeground(){
 * // ... whatever you want to do
 * }
 * public void onBecameBackground(){
 * // ... whatever you want to do
 * }
 * }
 *
 * public void onCreate(){
 * super.onCreate();
 * Foreground.get(this).addListener(listener);
 * }
 *
 * public void onDestroy(){
 * super.onCreate();
 * Foreground.get(this).removeListener(listener);
 * }
 */
public class Foreground implements Application.ActivityLifecycleCallbacks {

    public static final long CHECK_DELAY = 500;

    public static final String TAG = Foreground.class.getName();

    public interface Listener {

        void onBecameForeground();

        void onBecameBackground();

    }

    private static Foreground instance;

    private boolean foreground = false, paused = true;

    private Class foregroundActivityClass = null;

    private Handler handler = new Handler();

    private List<Listener> listeners = new CopyOnWriteArrayList<Listener>();

    private Runnable check;

    /**
     * Its not strictly necessary to use this method - _usually_ invoking
     * get with a Context gives us a path to retrieve the Application and
     * initialise, but sometimes (e.g. in test harness) the ApplicationContext
     * is != the Application, and the docs make no guarantees.
     *
     * @return an initialised Foreground instance
     */
    public static Foreground init(Application application) {
        if (instance == null) {
            instance = new Foreground();
            application.registerActivityLifecycleCallbacks(instance);
        }
        return instance;
    }

    public static Foreground get(Application application) {
        if (instance == null) {
            init(application);
        }
        return instance;
    }

    public static Foreground get(Context ctx) {
        if (instance == null) {
            Context appCtx = ctx.getApplicationContext();
            if (appCtx instanceof Application) {
                init((Application) appCtx);
            } else {
                throw new IllegalStateException(
                        "Foreground is not initialised and "
                                + "cannot obtain the Application object");
            }
        }
        return instance;
    }

    public static Foreground get() {
        if (instance == null) {
            throw new IllegalStateException(
                    "Foreground is not initialised - invoke "
                            + "at least once with parameterised init/get");
        }
        return instance;
    }

    public boolean isForeground() {
        return foreground;
    }

    public boolean isBackground() {
        return !foreground;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public boolean isForeground(Class<? extends Activity> activityClass) {
        return foreground
                && foregroundActivityClass != null
                && activityClass != null
                && foregroundActivityClass.equals(activityClass);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;
        foregroundActivityClass = activity.getClass();

        if (check != null) {
            handler.removeCallbacks(check);
        }

        if (wasBackground) {
            Log.i(TAG, "went foreground");
            for (Listener l : listeners) {
                try {
                    l.onBecameForeground();
                } catch (Exception exc) {
                    Log.e(TAG, "Listener threw exception!", exc);
                }
            }
        } else {
            Log.i(TAG, "still foreground");
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        paused = true;
        foregroundActivityClass = null;

        if (check != null) {
            handler.removeCallbacks(check);
        }
        //CHECKSTYLE:OFF
        handler.postDelayed(check = new Runnable() {
            @Override
            public void run() {
                if (foreground && paused) {
                    foreground = false;

                    Log.i(TAG, "went background");
                    for (Listener l : listeners) {
                        try {
                            l.onBecameBackground();
                        } catch (Exception exc) {
                            Log.e(TAG, "Listener threw exception!", exc);
                        }
                    }
                } else {
                    Log.i(TAG, "still foreground");
                }
            }
        }, CHECK_DELAY);
        //CHECKSTYLE:ON
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
