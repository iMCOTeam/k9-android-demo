package com.example.imco.mvp.main;

import android.content.Context;
import android.util.Log;

import com.App;
import com.imco.dfu.OtaProxy;
import com.imco.interactivelayer.bean.AlarmClockBean;
import com.imco.interactivelayer.bean.FunctionsBean;
import com.imco.interactivelayer.bean.SleepItemPacket;
import com.imco.interactivelayer.bean.SleepPacket;
import com.imco.interactivelayer.bean.SportItemPacket;
import com.imco.interactivelayer.bean.SportPacket;
import com.imco.interactivelayer.manager.CommandCallback;
import com.imco.interactivelayer.manager.ConnectCallback;
//import com.imco.interactivelayer.manager.ControlManager;
import com.app.annotation.apt.InstanceFactory;
import com.example.imco.event.FwItemClickEvent;
import com.example.imco.model.SleepItem;
import com.example.utils.LogUtils;
import com.imco.dfu.OtaCallback;
import com.imco.dfu.network.bean.AllFwResultBean;
import com.imco.interactivelayer.manager.CommandManager;
import com.imco.interactivelayer.manager.ConnectManager;
import com.imco.interactivelayer.manager.SendCommandCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mai on 17-6-22.
 */

@InstanceFactory
public class MainPresenter extends MainContract.MainPresenter {
    private final static String TAG = "MainPresenter";

    private ConnectManager mConnectManager;
    private CommandCallback mCommandCallback;
    private ArrayList<SleepItem> mSleepItems = new ArrayList<>();

    private SendCommandCallback mNormalCallback = new SendCommandCallback() {
        @Override
        public void onCommandSend(boolean status) {
            Log.d("TEST_COMMAND", "onCommandSend() : " + status);

            mView.cmdResult(status);
        }

        @Override
        public void onDisconnected() {
            mView.deviceNotConnect();
        }

        @Override
        public void onError(Throwable error) {
            mView.cmdError(error);
        }
    };

    @Override
    public void onAttached() {
        LogUtils.d(TAG, "onConnectionStateChange, status: ");
        EventBus.getDefault().register(this);

        mConnectManager = ConnectManager.getInstance();
        mConnectManager.registerCallback(new ConnectCallback() {
            @Override
            public void connectStatus(boolean succeeded, int code) {
                super.connectStatus(succeeded, code);
                LogUtils.d(TAG, "connectStatus, code: " + code);
                if (succeeded) {
                    mView.connecting(code);
                    switch (code) {
                        case CommandManager.LoginState.STATE_WRIST_LOGGING:
                            // When the bracelet is disconnected, all callbacks are cleared
                            // So need to re-register the callback when connect the bracelet again
//                            CommandManager.registerCallback(mCommandCallback);
                            break;
                        case CommandManager.LoginState.STATE_WRIST_BONDING:

                            break;
                        case CommandManager.LoginState.STATE_WRIST_LOGIN:
                            mView.getContentStatus(mConnectManager.isConnected());
//                            CommandManager.readBatteryLevel(null);
//                            CommandManager.setTimeSync(null);
//                            CommandManager.sendDataRequest(null);
                            break;
                    }
                } else {
                    mView.connectError(code);
                }
            }
        });


        mCommandCallback = new CommandCallback() {
            @Override
            public void onConnectionStateChange(final boolean status) {
                mView.getContentStatus(status);
                LogUtils.d(TAG, "onConnectionStateChange, status: " + status);
            }

            @Override
            public void onTodaySumSportDataReceived(long totalStep, long totalCalories, long totalDistance) {
                LogUtils.d(TAG, "onTodaySumSportDataReceived>>>>>>>>>>>1");

                mView.getCurrentStep(totalStep);
                mView.getCalorie(totalCalories);
                mView.getDistance(totalDistance);
            }

            @Override
            public void onSportDataCmdHistorySyncEnd(SportPacket sportPacket) {
                mView.getContentStatus(mConnectManager.isConnected());
                LogUtils.d(TAG, "onTodaySumSportDataReceived >>>>>>>>>>> end ");

                if (sportPacket.getSportItems().size() > 0) {
                    SportItemPacket itemPacket = sportPacket.getSportItems().get(0);
                    LogUtils.d(TAG, "onTodaySumSportDataReceived >>>>>>>>>>>" + itemPacket.getStepCount());
                }
            }


            @Override
            public void onTurnOverWristSettingReceived(boolean mode) {

            }

            @Override
            public void onSportDataReceivedIndication(SportPacket sportPacket) {
                SportItemPacket itemPacket = sportPacket.getSportItems().get(0);
                LogUtils.d(TAG, "onSportDataReceivedIndication, step: " + itemPacket.getStepCount());
                mView.getCurrentStep(itemPacket.getStepCount());
                mView.getCalorie(itemPacket.getCalory());
                mView.getDistance(itemPacket.getDistance());
            }

            @Override
            public void onSleepDataReceivedIndication(SleepPacket packet) {
                if (packet.getSleepItems().size() > 0) {
                    for (int i = 0; i < packet.getSleepItems().size(); i++) {
                        SleepItemPacket itemPacket = packet.getSleepItems().get(i);
                        LogUtils.d(TAG, "onSleepDataReceivedIndication >>>>>>>>>>>" + itemPacket.getMinutes());
                        mSleepItems.add(new SleepItem(itemPacket.getMinutes(), itemPacket.getMode()));
                    }
                    mView.sleepData(mSleepItems);
                }
            }

            @Override
            public void onHrpDataReceivedIndication(int minutes, int hrpValue) {
                LogUtils.d(TAG, "timestamp : " + minutes);
                mView.showHeartRate(hrpValue);
            }


            @Override
            public void onAlarmsDataReceived(ArrayList<AlarmClockBean> data) {
                mView.alarmList(data);
            }

            @Override
            public void onVersionRead(int appVersion, int patchVersion) {
                mView.firmwareVersion("app version : " + appVersion + "patch version : " + patchVersion);

                LogUtils.d(TAG, "app version : " + appVersion + "patch version : " + patchVersion);
            }

            @Override
            public void onNoNewVersion(int code, String message) {
                LogUtils.d(TAG, "code : " + code + " message: " + message);
                mView.firmwareVersion(" message : " + message + "app code : " + code);
            }

            @Override
            public void onHasNewVersion(String description, String version) {
                LogUtils.d(TAG, "description : " + description + " version : " + version);
                mView.firmwareVersion("description " + description + " version : " + version);
                CommandManager.downloadFirmware();
            }

            @Override
            public void downloadProgress(int progressRate) {
                LogUtils.d(TAG, "progressRate : " + progressRate);
                mView.downloadProgress(progressRate);
            }

            @Override
            public void getNewFirmwareError(Throwable e,
                                            @CommandManager.CheckFirmwareUpdatesError int errorCode) {
                LogUtils.d(TAG, "netWorkError ");
                mView.downError(e.getMessage());
            }

            @Override
            public void downloadComplete(String fwPath) {
                LogUtils.d(TAG, "fwPath : " + fwPath);
                mView.downloadComplete(fwPath);
            }

            @Override
            public void allFWVersion(ArrayList<AllFwResultBean.PayloadBean> firmwareList) {
                mView.showAllFWVersion(firmwareList);
                for (int i = 0; i < firmwareList.size(); i++) {
                    LogUtils.d(TAG, "" + firmwareList.get(i).resourceUrl);
                }
            }

            @Override
            public void onHistoryDataSyncEnd() {
                LogUtils.d(TAG, " >>>>> onHistoryDataSyncEnd");
            }

            @Override
            public void onLinkLossValueReceive(boolean value) {
                mView.showLinkLossValue(value);
            }

            @Override
            public void onDisplaySwitchReturn(@CommandManager.ScreenState int status) {
                LogUtils.d(TAG, " >>>>> onDisplaySwitchReturn : " + status);

                mView.displaySwitchStatus(status);
            }

            @Override
            public void onFunctions(FunctionsBean functions) {
                mView.onFunctions(functions);
            }
        };
        CommandManager.registerCallback(mCommandCallback);

    }


    @Override
    public void startSync() {
        CommandManager.setTimeSync(mNormalCallback);

        CommandManager.syncHistoryDataRequest(mNormalCallback);
    }

    @Override
    public void unBind() {
        mConnectManager.unbind(mNormalCallback);
    }

    @Override
    void startAutoConnect() {
        mConnectManager.startAutoConnect();
    }

    @Override
    void stopAutoConnect() {
        mConnectManager.stopAutoConnect();
    }

    @Override
    void readHeartRate() {
        CommandManager.readHrpValue(mNormalCallback);
    }

    @Override
    void stopReadHeartRate() {
        CommandManager.stopReadHrpValue(mNormalCallback);
    }

    @Override
    void cycleMeasureHeartRate(boolean enable, int interval) {
        CommandManager.setContinueHrp(enable, interval, mNormalCallback);
    }

    @Override
    void isConnected() {
        mView.getContentStatus(mConnectManager.isConnected());
    }

    @Override
    void connect() {
        sendConnectCommand((e) -> e.onNext(ConnectManager.getInstance().connect(CommandManager.getBondDeviceMac(App.getAppContext()))));
    }

    @Override
    void initOtaProxy(Context context, OtaCallback listener) {
        CommandManager.initOtaProxy(context, listener);
    }

    @Override
    boolean ota(OtaProxy dfu, String firmwarePath) {
        return CommandManager.startOTA(dfu, firmwarePath);
    }

    @Override
    void readBatteryLevel() {
        CommandManager.readBatteryLevel(new SendCommandCallback() {
            @Override
            public void onCommandSend(boolean status) {
                Log.d(TAG, ">>>>>>>>>>>>>>" + status);
                mView.showBattery(CommandManager.getBatteryLevel());
            }

            @Override
            public void onDisconnected() {
                Log.d(TAG, ">>>>>>>>>>>>>>onDisconnected");
            }

            @Override
            public void onError(Throwable error) {
                Log.d(TAG, ">>>>>>>>>>>>>>" + error.getMessage());
            }
        });
    }


    @Override
    int getBatteryLevel() {
        return CommandManager.getBatteryLevel();
    }

    @Override
    void checkNewVersion(String userId, String vendor) {
        int currentFWVersion = CommandManager.getCurrentFWVersion();
        if (currentFWVersion == -1) {
            mView.getFwVersionTip();
        } else {
            CommandManager.checkNewFirmwareVersion("4.0.1", userId, vendor, "k9");
        }
    }

    @Override
    void loadOtaFileInfo(String path) {
        mView.newOtaFileVersion(CommandManager.getOtaFileVersion(path));
    }

    @Override
    void getFwVersion() {
        CommandManager.readDfuVersion(mNormalCallback);
    }

    @Override
    void selectMultiFileFromNet(String userId, String vendor) {
        CommandManager.checkAllFwVersion(userId, vendor);
//        ControlManager.getInstance().checkAllFWVersion(userId, vendor);
    }

    @Override
    void settingAlarm(int hour, int minute, byte daysFlag, int alarmId) {
        CommandManager.setClockAlarm(hour, minute, daysFlag, alarmId, mNormalCallback);
    }

    @Override
    void closeAlarm() {
//        CommandManager.setClock(null, mNormalCallback);
    }

    @Override
    void syncAlarmList() {
        CommandManager.sendClocksSyncRequest(mNormalCallback);
    }

    @Override
    void saveUserProfile(boolean gender, int age, int height, int weight) {


        CommandManager.setUserProfile(gender, age, height, weight, mNormalCallback);
    }

    @Override
    void saveStepTarget(int stepTarget) {
        CommandManager.setTargetStep(stepTarget, mNormalCallback);
    }

    @Override
    void findBand() {
        CommandManager.enableImmediateAlert(true, mNormalCallback);
    }

    @Override
    void setSedentaryReminder(boolean enable, int alarmCycle) {
        CommandManager.setLongSit(enable, alarmCycle, 0, 23, (byte) 255, mNormalCallback);
    }

    @Override
    void setTurnOverWrist(boolean enable) {
        CommandManager.setTurnOverWrist(enable, mNormalCallback);
    }

    @Override
    void setLeftOrRightHand(byte mode) {
        CommandManager.settingCmdLeftRightSetting(mode, mNormalCallback);
    }

    @Override
    void enableNotifyInfo(byte mode) {
        CommandManager.setNotifyMode(mode, mNormalCallback);
    }

    @Override
    void sendCallNotifyInfo(String notifyInfo) {
        CommandManager.sendCallNotifyInfo(notifyInfo, mNormalCallback);
    }

    @Override
    void setCallAcceptNotify() {
        CommandManager.sendCallAcceptNotifyInfo(mNormalCallback);
    }

    @Override
    void setCallRejectNotify() {
        CommandManager.sendCallRejectNotifyInfo(mNormalCallback);
    }

    @Override
    void sendOtherNotifyInfo(byte mode, String notifyInfo) {
        CommandManager.sendOtherNotifyInfo(CommandManager.NotifyInfo.OTHER_NOTIFY_INFO_QQ, notifyInfo, mNormalCallback);
    }

    @Override
    void sendUniversalMessageInfo(String notifyInfo) {
        CommandManager.sendOtherNotifyInfoWithVibrateCount(1, 3, notifyInfo, mNormalCallback);
    }


    @Override
    void setDeviceName(String deviceName) {
        CommandManager.setDeviceName(deviceName, mNormalCallback);
    }

    @Override
    void setDataSync(boolean enable) {
        CommandManager.setDataSync(enable, mNormalCallback);
    }

    @Override
    void getSleepData() {

    }

    @Override
    void queryTurnOverWristStatus() {
        CommandManager.sendTurnOverWristRequest(mNormalCallback);
    }

    @Override
    void sendSyncTodayNearlyOffsetStepCommand() {
        CommandManager.sendSyncTodayStepCommand(1000, 1000, 1000, mNormalCallback);
    }

    @Override
    void setAntLost(boolean ant_lost) {
        CommandManager.enableLinkLossAlert(ant_lost, mNormalCallback);
    }

    @Override
    void setDisplaySwitchSetting(@CommandManager.ScreenState int status) {
        CommandManager.sendDisplaySwitchSetting(status, mNormalCallback);
    }

    @Override
    void setDisplaySwitchRequest() {
        CommandManager.sendDisplaySwitchRequest(mNormalCallback);
    }

    @Override
    void queryFunctions() {
        CommandManager.setFunctionsRequest(mNormalCallback);
    }

    @Override
    void disconnect() {
        mConnectManager.disconnect();
    }


    private void sendConnectCommand(ObservableOnSubscribe<Boolean> cmdObservable) {
        Observable.create(cmdObservable)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> mView.cmdResult(aBoolean), throwable -> mView.cmdError(throwable));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClick(FwItemClickEvent<AllFwResultBean.PayloadBean> event) {
        mView.fwItemOnClick();
        mView.startDownload();
        CommandManager.download(event.message.resourceUrl);
//        ControlManager.getInstance().download(event.message.resourceUrl);
    }

    @Override
    public void onDetached() {
        super.onDetached();
        EventBus.getDefault().unregister(this);
    }
}
