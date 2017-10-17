package com.example.utils;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.App;
import com.example.imco.model.R;

/**
 * Created by mai on 2016/10/18.
 */

public class DialogUtils {
    // Log
    private final static String TAG = "DialogUtils";
    private final static boolean D = true;
    // object
    private static DialogUtils mInstance;
    private static Context mContext;

    private ProgressDialog mProgressDialog = null;

    private Toast mToast;

    private ProgressBarSupperCallback mProgressBarSupperCallback;

    private HomeWatcherReceiver mHomeWatcherReceiver;

    public static void initial(Context context) {
        if (D) Log.d(TAG, "init()");
        mInstance = new DialogUtils();
        mContext = context;

        mInstance.mHomeWatcherReceiver = new HomeWatcherReceiver();

        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mContext.registerReceiver(mInstance.mHomeWatcherReceiver, filter);
    }

    public void close() {
        mContext.unregisterReceiver(mHomeWatcherReceiver);
    }

    public static DialogUtils getInstance() {
        return mInstance;
    }

    public void showToast(final int message) {
        //if(!JudgeActivityFront.isAppOnForeground(mContext)) {
        if (!App.isInForeground()) {
            if (D) Log.e(TAG, "showToast, Is not in top.");
            return;
        }
        if (mToast == null) {
            mToast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }

    /**
     * 显示Toast通知
     *
     * @param msg 显示的内容
     */
    public void showToast(String msg) {
        //if(!JudgeActivityFront.isAppOnForeground(mContext)) {
        if (!App.isInForeground()) {
            if (D) Log.e(TAG, "showToast, Is not in top.");
            return;
        }
        if (mToast == null) {
            mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    public void showProgressBar(final int message, ProgressBarSupperCallback callback) {
        showProgressBar(mContext.getResources().getString(message), callback);
    }

    public void showProgressBar(final int message) {
        showProgressBar(mContext.getResources().getString(message));
    }

    public void showProgressBar(final String message) {
        showProgressBar(message, null);
    }

    public void showProgressBar(final String message, ProgressBarSupperCallback callback) {
        //if(!JudgeActivityFront.isAppOnForeground(mContext)) {
        if (!App.isInForeground()) {
            if (D) Log.e(TAG, "showProgressBar, Is not in top.");
            return;
        }

        if (mProgressDialog != null) {
            cancelProgressBar();
        }
        mProgressBarSupperCallback = callback;

        mProgressDialog = new ProgressDialog(App.getAppContext().getCurActivity(), R.style.DialogTheme);
        mProgressDialog.setMessage(message);
        mProgressDialog.setTitle(null);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

//        mProgressBarSuperHandler.postDelayed(mProgressBarSuperTask, 30 * 1000);
    }

    public void showHProgressBar(final String message) {
        showHProgressBar(message, null);
    }

    public void showHProgressBar(final String message, ProgressBarSupperCallback callback) {

        //if(!JudgeActivityFront.isAppOnForeground(mContext)) {
        if (!App.isInForeground()) {
            if (D) Log.e(TAG, "showProgressBar, Is not in top.");
            return;
        }

        if (mProgressDialog != null) {
            cancelProgressBar();
        }
        mProgressBarSupperCallback = callback;

        mProgressDialog = new ProgressDialog(App.getAppContext().getCurActivity(), R.style.DialogTheme);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(0);
        mProgressDialog.setMessage(message);
        mProgressDialog.setTitle(null);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

//        mProgressBarSuperHandler.postDelayed(mProgressBarSuperTask, 30 * 1000);
    }


    public void setProgress(int progress) {
        if (mProgressDialog != null) {
            if (mProgressDialog.isIndeterminate()) {
                mProgressDialog.setIndeterminate(false);
            }
            mProgressDialog.setProgress(progress);
            LogUtils.d(TAG, "Progress" + progress);
        }
    }

    public void cancelProgressBar() {
        LogUtils.d(TAG, "cancelProgressBar");

        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.cancel();

                mProgressDialog = null;
            }
        }
        mProgressBarSuperHandler.removeCallbacks(mProgressBarSuperTask);
    }

    // Alarm timer
    Handler mProgressBarSuperHandler = new Handler();
    Runnable mProgressBarSuperTask = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (D) Log.w(TAG, "Wait Progress Timeout");
            // stop timer
            cancelProgressBar();

            if (mProgressBarSupperCallback != null) {
                mProgressBarSupperCallback.onSupperTimeout();
            }
        }
    };

    public boolean isProgressBarShowing() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                return true;
            }
        }
        return false;
    }


    public static class HomeWatcherReceiver extends BroadcastReceiver {
        private static final String LOG_TAG = "HomeReceiver";
        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
        private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(LOG_TAG, "onReceive: action: " + action);
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                // android.intent.action.CLOSE_SYSTEM_DIALOGS
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                Log.i(LOG_TAG, "reason: " + reason);

                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                    DialogUtils.getInstance().cancelProgressBar();

                } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                    // 长按Home键 或者 activity切换键
                    DialogUtils.getInstance().cancelProgressBar();

                } else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
                    // 锁屏
                    DialogUtils.getInstance().cancelProgressBar();
                } else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
                    // samsung 长按Home键
                    DialogUtils.getInstance().cancelProgressBar();
                }

            }
        }
    }

    public static class ProgressBarSupperCallback {
        /**
         * Callback indicating when progressbar super timeout
         */
        public void onSupperTimeout() {
        }
    }
}
