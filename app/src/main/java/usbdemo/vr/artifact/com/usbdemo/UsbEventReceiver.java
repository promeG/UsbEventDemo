package usbdemo.vr.artifact.com.usbdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by guyacong on 2017/10/1.
 */

public class UsbEventReceiver extends BroadcastReceiver {

    private static final String ACTION_USB_KEY_EVENT = "artifact.vr.intent.action.USB_KEY_EVENT";
    private static final int KEYCODE_UP = 0;
    private static final int KEYCODE_DOWN = 1;
    private static final int KEYCODE_LEFT = 2;
    private static final int KEYCODE_RIGHT = 3;
    private static final int KEYCODE_OK = 4;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Foreground.get().isForeground()) {
            Log.d("UsbEventReceiver", "app不在前台，忽略 usb event...");
            return;
        }
        if(ACTION_USB_KEY_EVENT.equals(intent.getAction())){
            int event = intent.getIntExtra("event", -1);
            switch (event) {
                case KEYCODE_UP:
                    Log.d("UsbEventReceiver", "UP");
                    break;
                case KEYCODE_DOWN:
                    Log.d("UsbEventReceiver", "DOWN");
                    break;
                case KEYCODE_LEFT:
                    Log.d("UsbEventReceiver", "LEFT");
                    break;
                case KEYCODE_RIGHT:
                    Log.d("UsbEventReceiver", "RIGHT");
                    break;
                case KEYCODE_OK:
                    Log.d("UsbEventReceiver", "OK");
                    break;

            }
        }
    }
}
