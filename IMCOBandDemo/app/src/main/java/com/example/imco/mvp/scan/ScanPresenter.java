package com.example.imco.mvp.scan;

import android.bluetooth.BluetoothDevice;

import com.example.imco.event.ItemClickEvent;
import com.example.imco.model.BleDevicesBean;
import com.example.utils.LogUtils;
import com.imco.interactivelayer.manager.ConnectCallback;
import com.imco.interactivelayer.manager.ConnectManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashSet;

/**
 * Created by mai on 17-6-23.
 */

public class ScanPresenter extends ScanContract.ScanPresenter {
    private static final java.lang.String TAG = "ScanPresenter";
    private ConnectManager mConnected;
    private HashSet<String> mSet = new HashSet<>();
    private ConnectCallback mCallback = new ConnectCallback() {
        @Override
        public void foundDevices(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (!mSet.contains(device.getAddress())) {
                mSet.add(device.getAddress());
                mView.scanDevices(device, rssi);
            }
        }

        @Override
        public void connectStatus(boolean succeeded, int code) {
            LogUtils.d(TAG, "connectStatus, code: " + code);

            if (succeeded) {
                mView.connecting(code);

            } else {
                mView.connectError(code);

            }
            mSet.clear();
        }
    };


    @Override
    public void onAttached() {
        mConnected = ConnectManager.getInstance();
        EventBus.getDefault().register(this);
    }

    @Override
    void startScan() {
//        ConnectManager.getInstance().forceLeScan();
        mConnected.scanLeDevice(true);
    }

    @Override
    void stopScan() {
        mSet.clear();
        mConnected.scanLeDevice(false);
    }

    @Override
    void connectDevice(BleDevicesBean message) {
        ConnectManager.getInstance().connect(message.getDevice().getAddress());
    }

    @Override
    void registerCallback() {
        mConnected.registerCallback(mCallback);

    }

    @Override
    void unregisterCallback() {
        mConnected.unregisterCallback(mCallback);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClick(ItemClickEvent<BleDevicesBean> event) {
        mView.startConnect();
        connectDevice(event.message);
    }


    @Override
    public void onDetached() {
        super.onDetached();
        EventBus.getDefault().unregister(this);
    }
}
