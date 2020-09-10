package com.kevin.codelib.interfaces;

import android.view.View;

/**
 * auther：lkt
 * 时间：2020/9/9 16:05
 * 功能：
 */
public interface ItemClickLisenter {

    /**
     * @param position 被点击条目的位置
     * @param view     被点击的view
     */
    void onItemClick(int position, View view);
}
