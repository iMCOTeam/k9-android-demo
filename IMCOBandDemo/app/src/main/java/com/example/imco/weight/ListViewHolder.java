package com.example.imco.weight;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.imco.event.FwItemClickEvent;
import com.example.imco.model.R;
import com.example.utils.LogUtils;
import com.imco.dfu.network.bean.AllFwResultBean;
import com.imcorecyclerviewlib.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by mai on 17-7-29.
 */

public class ListViewHolder extends BaseViewHolder<AllFwResultBean.PayloadBean> implements View.OnClickListener{
    private static final String TAG = "ListViewHolder";
    private final TextView tvTitle;
    private AllFwResultBean.PayloadBean payloadBean;
    public ListViewHolder(Context context, ViewGroup root, int layoutRes) {
        super(context, root, layoutRes);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        itemView.setOnClickListener(this);
    }

    @Override
    public void bindData(AllFwResultBean.PayloadBean baseBean, int position, boolean isSelected) {
        LogUtils.d(""+baseBean.version);
        tvTitle.setText(baseBean.version);
        payloadBean = baseBean;
    }

    public static ViewHolderCreator HOLDER_CREATOR=new ViewHolderCreator<ListViewHolder>() {
        @Override
        public ListViewHolder createByViewGroupAndType(ViewGroup parent, int viewType) {
            int layoutRes = R.layout.settings_item;
            return new ListViewHolder(parent.getContext(), parent, layoutRes);
        }
    };

    @Override
    public void onClick(View v) {
        LogUtils.d(TAG, ">>>>>>> OnClick");
        EventBus.getDefault().post(new FwItemClickEvent(getAdapterPosition(), payloadBean));
    }
}
