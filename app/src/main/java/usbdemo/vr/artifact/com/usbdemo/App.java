package usbdemo.vr.artifact.com.usbdemo;

import android.app.Application;

/**
 * Created by guyacong on 2017/10/1.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Foreground.init(this);
    }
}
