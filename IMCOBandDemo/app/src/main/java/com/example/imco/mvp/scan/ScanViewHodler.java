package com.example.imco.mvp.scan;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.imco.event.ItemClickEvent;
import com.example.imco.model.BleDevicesBean;
import com.example.imco.model.R;
import com.imcorecyclerviewlib.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by mai on 17-6-13.
 */

public class ScanViewHodler extends BaseViewHolder<BleDevicesBean> implements View.OnClickListener {
    TextView tvDeviceName;
    TextView tvMac;
    TextView tvRssi;
    BleDevicesBean mBleDevicesBean;
    public ScanViewHodler(Context context, ViewGroup root, int layoutRes) {

        super(context, root, layoutRes);
        tvDeviceName = (TextView) itemView.findViewById(R.id.tv_device_name);
        tvMac = (TextView) itemView.findViewById(R.id.tv_mac);
        tvRssi = (TextView) itemView.findViewById(R.id.tv_rssi);
    }

    @Override
    public void bindData(BleDevicesBean bleDevicesBean, int position, boolean isSelected) {
        mBleDevicesBean = bleDevicesBean;
        itemView.setOnClickListener(this);
        tvDeviceName.setText(bleDevicesBean.getDevice().getName());
        tvMac.setText(bleDevicesBean.getDevice().getAddress());
        tvRssi.setText("" + bleDevicesBean.getRssi());
    }

    public static ViewHolderCreator HOLDER_CREATOR = new ViewHolderCreator<ScanViewHodler>() {
        @Override
        public ScanViewHodler createByViewGroupAndType(ViewGroup parent, int viewType) {
            return new ScanViewHodler(parent.getContext(), parent, R.layout.scan_devices_item);
        }
    };

    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(new ItemClickEvent<>(getAdapterPosition(), mBleDevicesBean));
    }
}
