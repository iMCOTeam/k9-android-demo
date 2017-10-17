package com.example.imco.mvp.scan;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imco.model.BleDevicesBean;
import com.example.imco.model.R;
import com.example.imco.mvp.BaseActivity;
import com.example.imco.mvp.main.MainActivity;
import com.example.utils.DialogUtils;
import com.example.utils.LogUtils;
import com.imco.interactivelayer.manager.CommandManager;
import com.imcorecyclerviewlib.BaseAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ScanActivity extends BaseActivity<ScanPresenter> implements ScanContract.ScanView {

    private static final int REQUEST_ENABLE_BT = 0x0011;
    private static final String TAG = "ScanActivity";
    @BindView(R.id.rvScanDevices)
    RecyclerView rvScanDevices;

    @BindView(R.id.et_scan_filter)
    EditText etScanFilter;

    @BindView(R.id.tv_rssi_filter)
    TextView tvRssiFilter;

    @BindView(R.id.srlRefresh)
    SwipeRefreshLayout srlRefresh;

    private boolean mScanning;
    private ArrayList<BleDevicesBean> mDeviceList;
    private BaseAdapter mAdapter;
    private Unbinder mBind;

    private final static String MENU_SELECTED = "mRssiSelected";
    private int mRssiSelected = -1;
    private int mRssiFilter = -1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        LogUtils.d(TAG, ">>>onCreateOptionsMenu");

        if (!mScanning) {
            LogUtils.d(TAG, "show shop menu");
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_stop).setVisible(false);
            if (srlRefresh.isRefreshing())
                srlRefresh.setRefreshing(false);
        } else {
            LogUtils.d(TAG, "show scanning menu");
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            if (!srlRefresh.isRefreshing())
                srlRefresh.setRefreshing(true);
        }

        MenuItem item;
        switch (mRssiSelected) {
            case R.id.rssi_30:
                item = menu.findItem(R.id.rssi_30);
                item.setChecked(true);
                break;
            case R.id.rssi_40:
                item = menu.findItem(R.id.rssi_40);
                item.setChecked(true);
                break;
            case R.id.rssi_50:
                item = menu.findItem(R.id.rssi_50);
                item.setChecked(true);
                break;
            case R.id.rssi_60:
                item = menu.findItem(R.id.rssi_60);
                item.setChecked(true);
                break;
            case R.id.rssi_70:
                item = menu.findItem(R.id.rssi_70);
                item.setChecked(true);
                break;
            case R.id.rssi_80:
                item = menu.findItem(R.id.rssi_80);
                item.setChecked(true);
                break;
            case R.id.rssi_all:
                item = menu.findItem(R.id.rssi_all);
                item.setChecked(true);
                break;
            case -1:
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mScanning = true;
                mDeviceList.clear();
                mAdapter.setDataList(mDeviceList);
                mAdapter.notifyDataSetChanged();
                mPresenter.startScan();
                invalidateOptionsMenu();
                break;
            case R.id.menu_stop:
                mScanning = false;
                mPresenter.stopScan();
                invalidateOptionsMenu();
                break;

            case R.id.rssi_30:
                mRssiFilter = 30;
                mRssiSelected = R.id.rssi_30;
                item.setChecked(true);
                break;
            case R.id.rssi_40:
                mRssiFilter = 40;
                mRssiSelected = R.id.rssi_40;
                item.setChecked(true);
                break;
            case R.id.rssi_50:
                mRssiFilter = 50;
                mRssiSelected = R.id.rssi_50;
                item.setChecked(true);
                break;
            case R.id.rssi_60:
                mRssiFilter = 60;
                mRssiSelected = R.id.rssi_60;
                item.setChecked(true);
                break;
            case R.id.rssi_70:
                mRssiFilter = 70;
                mRssiSelected = R.id.rssi_70;
                item.setChecked(true);
                break;
            case R.id.rssi_80:
                mRssiFilter = 80;
                mRssiSelected = R.id.rssi_80;
                item.setChecked(true);
                break;
            case R.id.rssi_all:
                mRssiFilter = -1;
                mRssiSelected = R.id.rssi_all;
                item.setChecked(true);
                break;
        }
        tvRssiFilter.setText((mRssiFilter == -1 ? getString(R.string.scan_all) : "" + mRssiFilter));
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBind = ButterKnife.bind(this);
        srlRefresh.setColorSchemeColors(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.BLACK);
        isSupportBLE();
        rvScanDevices.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BaseAdapter(ScanViewHodler.class);
        rvScanDevices.setAdapter(mAdapter);
        mDeviceList = new ArrayList<>();
        mAdapter.setDataList(mDeviceList);

        srlRefresh.setOnRefreshListener(() -> {
//            if (srlRefresh.isRefreshing()) {
//                srlRefresh.setRefreshing(false);
//            }
            mDeviceList.clear();
            mAdapter.setDataList(mDeviceList);
            mAdapter.notifyDataSetChanged();
            mPresenter.startScan();
            invalidateOptionsMenu();
            mScanning = true;
        });
    }

    private void isSupportBLE() {
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                //do nothing
                Toast.makeText(this, "Bt is enabled!", Toast.LENGTH_LONG).show();
            } else {
                // User did not enable Bluetooth or an error occured
                Toast.makeText(this, "Bt is not enabled!", Toast.LENGTH_LONG).show();
                finish();
            }
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mBind.unbind();
        mPresenter.unregisterCallback();
    }


    @Override
    public void scanDevices(BluetoothDevice device, int rssi) {
        LogUtils.d(TAG, "RssiFilter : " + mRssiFilter);
        if ((mRssiFilter != -1) && (mRssiFilter <= Math.abs(rssi))) return;
        String sFilter = etScanFilter.getText().toString();
        if (!sFilter.trim().isEmpty()) {
            LogUtils.d(TAG, "Filter : " + sFilter);
            if (!device.getAddress().contains(sFilter) && !device.getName().contains(sFilter))
                return;
        }
//        mDeviceList.clear();
//        BleDevicesBean bean = new BleDevicesBean(device, rssi);
//        mDeviceList.add(bean);
//        mAdapter.addDataList(mDeviceList);
//        mAdapter.notifyDataSetChanged();
        ArrayList<BleDevicesBean> dataList = (ArrayList<BleDevicesBean>) mAdapter.getDataList();
        dataList.add(new BleDevicesBean(device, rssi));
        Collections.sort(dataList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void startConnect() {
        DialogUtils.getInstance().showProgressBar(R.string.connect_band);
    }


    @Override
    public void connecting(int statusCode) {
        LogUtils.d(TAG, "connectStatus, code: " + statusCode);

        switch (statusCode) {
            case -1:
                break;
            case CommandManager.LoginState.STATE_WRIST_LOGGING:
                DialogUtils.getInstance().cancelProgressBar();
                DialogUtils.getInstance().showProgressBar(R.string.login);
                break;
            case CommandManager.LoginState.STATE_WRIST_BONDING:
                DialogUtils.getInstance().cancelProgressBar();
                DialogUtils.getInstance().showProgressBar(R.string.bonding);
                break;
            case CommandManager.LoginState.STATE_WRIST_LOGIN:
                mPresenter.stopScan();
                DialogUtils.getInstance().cancelProgressBar();
                startActivity(new Intent(this, MainActivity.class));
                mPresenter.stopScan();
                break;
        }
    }

    @Override
    public void connectError(int errorCode) {
        DialogUtils.getInstance().cancelProgressBar();

        switch (errorCode) {
            case CommandManager.ErrorCode.ERROR_CODE_NO_LOGIN_RESPONSE_COME:
                showToast(R.string.error_code_no_login_response_come);
                break;
            case CommandManager.ErrorCode.ERROR_CODE_BOND_ERROR:
                showToast(R.string.connect_band_already_bond);
                break;
            case CommandManager.ErrorCode.ERROR_CODE_COMMAND_SEND_ERROR:
                showToast(R.string.error_code_command_send_error);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>onResume");
        mPresenter.registerCallback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>onPause");
        mPresenter.unregisterCallback();
    }
}
