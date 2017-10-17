package com;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;

import com.example.utils.DialogUtils;
import com.example.utils.LogUtils;
import com.imco.interactivelayer.manager.CommandManager;

import java.util.Stack;

/**
 * Created by mai on 17-6-21.
 */

public class App extends Application {

    private static App mApp;
    public Stack<Activity> store;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        showSMSActivity();
        CommandManager.init(this);

        store = new Stack<>();
        registerActivityLifecycleCallbacks(new SwitchBackgroundCallbacks());
        DialogUtils.initial(this);
    }
    private static boolean isInForeground = false;

    public static boolean isInForeground() {
        return isInForeground;
    }
    public static App getAppContext() {
        return mApp;
    }

    private class SwitchBackgroundCallbacks implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            store.add(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            isInForeground = true;

        }

        @Override
        public void onActivityPaused(Activity activity) {
            isInForeground = false;
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            store.remove(activity);
        }
    }

    /**
     * 获取当前的Activity
     *
     * @return
     */
    public Activity getCurActivity() {
        return store.lastElement();
    }
    public static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private void showSMSActivity() {
        //打开短信界面
        String defaultApplication= Settings.Secure.getString(getContentResolver(),ENABLED_NOTIFICATION_LISTENERS);
        LogUtils.d("mai : "+defaultApplication);
        PackageManager packageManager=getPackageManager();
        Intent SMSIntent=packageManager.getLaunchIntentForPackage(defaultApplication);
        if(SMSIntent!=null){
            startActivity(SMSIntent);
//            finish();
        }
    }
}
