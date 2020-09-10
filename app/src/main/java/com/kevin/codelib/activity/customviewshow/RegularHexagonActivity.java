package com.kevin.codelib.activity.customviewshow;

import android.widget.SeekBar;

import com.kevin.codelib.R;
import com.kevin.codelib.base.BaseActivity;
import com.kevin.codelib.customview.RegularHexagon;

/**
 * auther：lkt
 * 时间：2020/9/9 16:21
 * 功能：展示RegularHexagon的使用
 */
public class RegularHexagonActivity extends BaseActivity {

    private RegularHexagon viewRegularHexagon;

    @Override
    public int getLayoutResID() {
        return R.layout.activity_regularhexagon;
    }

    @Override
    public void initView() {
        viewRegularHexagon = findViewById(R.id.viewRegularHexagon);
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewRegularHexagon.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
