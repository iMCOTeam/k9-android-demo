package com.imcorecyclerviewlib;


/**
 * Created by mai on 17-1-6.
 */

public class MultiItemAdapter extends BaseAdapter {

    public MultiItemAdapter(Class<?> vHClass) {
        super(vHClass);
    }

    @Override
    public int getItemViewType(int position) {
        int curType = BaseViewHolder.NORMAL;
        if (dataList.get(0) instanceof String) {
            if (null == dataList.get(position)) {
                curType = BaseViewHolder.NORMAL;
            }else if (dataList.get(position).equals("head")) {
                curType = BaseViewHolder.HEAD;
            } else if (dataList.get(position).equals("title")) {
                curType = BaseViewHolder.TITLE;
            } else if (dataList.get(position).equals("floor")) {
                curType = BaseViewHolder.FLOOR;
            } else {
                curType = BaseViewHolder.NORMAL;
            }
        } else if (dataList.get(0) instanceof BaseTitleBean) {
            if (null == ((BaseTitleBean) dataList.get(position)).title) {
                curType = BaseViewHolder.NORMAL;
            } else if (((BaseTitleBean) dataList.get(position)).title.equals("head")) {
                curType = BaseViewHolder.HEAD;
            } else if (((BaseTitleBean) dataList.get(position)).title.equals("title")) {
                curType = BaseViewHolder.TITLE;
            } else if (((BaseTitleBean) dataList.get(position)).title.equals("floor")) {
                curType = BaseViewHolder.FLOOR;
            } else {
                curType = BaseViewHolder.NORMAL;
            }
        }

        return curType;
    }
}
