package com.imcorecyclerviewlib;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mai on 17-1-6.
 * 注意： 每个子类都要声明并实例化一个HOLDER_CREATOR 字段.
 * 为了完全解耦，Adapter中直接通过类名.getField 获取字段HOLDER_CREATOR 来获取 变量实例。
 *
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    public static final int HEAD = 0;
    public static final int NORMAL = 1;
    public static final int TITLE = 2;
    public static final int FLOOR = 3;
    public BaseViewHolder(Context context, ViewGroup root, int layoutRes) {
        super(LayoutInflater.from(context).inflate(layoutRes, root, false));
    }

    public Context getContext() {
        return itemView.getContext();
    }

    public abstract void bindData(T t, int position, boolean isSelected);

    protected boolean equals(String title, @StringRes int sr){
        return title.equals(getContext().getString(sr));
    }

    protected View getView(@IdRes int ir) {
        return itemView.findViewById(ir);
    }
    public interface ViewHolderCreator<VH extends BaseViewHolder> {
        VH createByViewGroupAndType(ViewGroup parent, int viewType);
    }

}
