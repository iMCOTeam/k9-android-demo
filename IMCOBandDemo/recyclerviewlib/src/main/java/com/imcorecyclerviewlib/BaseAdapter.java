package com.imcorecyclerviewlib;

import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by mai on 17-1-6.
 */

public class BaseAdapter<T> extends SelectableAdapter<BaseViewHolder> {

    private Class<?> vHClass;

    private static HashMap<String, BaseViewHolder.ViewHolderCreator> creatorHashMap = new HashMap<>();

    public BaseAdapter(Class<?> vHClass) {
        this.vHClass = vHClass;
    }

    protected List<T> dataList = new ArrayList<>();


    /**
     * 获取该 Adapter 中存的数据
     *
     * @return
     */
    public List<T> getDataList() {
        return dataList;
    }

    /**
     * 设置数据，会清空以前数据
     *
     * @param dataList
     */
    public void setDataList(List<T> dataList) {
        this.dataList.clear();
        if (null != dataList) {
            this.dataList.addAll(dataList);
        }
    }

    /**
     * 添加数据，默认在最后插入，以前数据保留,用于加载更多数据
     *
     * @param dataList
     */
    public void addDataList(List<T> dataList) {
        this.dataList.addAll(dataList);

    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (null == vHClass) {
            try {
                throw new IllegalArgumentException("Please use BaseAdapter(Class<?> vHClass)");
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
        }

        BaseViewHolder.ViewHolderCreator<?> creator = null;
        if (creatorHashMap.containsKey(vHClass.getName())) {
            creator = creatorHashMap.get(vHClass.getName());
        } else {
            try {
                creator = (BaseViewHolder.ViewHolderCreator<?>) vHClass.getField("HOLDER_CREATOR").get(null);
                creatorHashMap.put(vHClass.getName(), creator);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        if (null != creator) {
            return creator.createByViewGroupAndType(parent, viewType);
        } else {
            throw new IllegalArgumentException(vHClass.getName() + " HOLDER_CREATOR should be instantiated");
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (position >= 0 && position < dataList.size()) {
            holder.bindData(dataList.get(position), position, isSelected(position));
        }
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
