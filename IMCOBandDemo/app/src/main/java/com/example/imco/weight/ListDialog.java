package com.example.imco.weight;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.App;
import com.example.imco.model.R;
import com.imco.dfu.network.bean.AllFwResultBean;
import com.imcorecyclerviewlib.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by mai on 17-7-29.
 */

public class ListDialog extends DialogFragment {

    private RecyclerView rvList;
    private BaseAdapter mBaseAdapter;
    private ArrayList<AllFwResultBean.PayloadBean> mDatas;
    private static Context mContext;


    public static ListDialog getInstance(Context context) {
        final ListDialog fragment = new ListDialog();
        mContext = context;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.list_dialog_fragment, null);
        final AlertDialog dialog = builder.setView(dialogView).create();
        rvList = (RecyclerView) dialogView.findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
        mBaseAdapter = new BaseAdapter(ListViewHolder.class);
        mBaseAdapter.setDataList(mDatas);
        rvList.setAdapter(mBaseAdapter);

        return dialog;
    }
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.list_dialog_fragment, container, false);
//        rvList = (RecyclerView) rootView.findViewById(R.id.rv_list);
//        rvList.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
//        mBaseAdapter = new BaseAdapter(ListViewHolder.class);
//        mBaseAdapter.setDataList(mDatas);
//        rvList.setAdapter(mBaseAdapter);
//        return rootView;
//    }


    public void setData(ArrayList datas) {
        this.mDatas = datas;
    }

}
