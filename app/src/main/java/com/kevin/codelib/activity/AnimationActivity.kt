package com.kevin.codelib.activity

import android.animation.Animator
import com.kevin.codelib.R
import com.kevin.codelib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_lottie_naimation.*

/**
 * lottie动画的使用
 */
class AnimationActivity : BaseActivity() {

    val pingpang: String = "pingpang.json"
    val xigua: String = "xigua.json"

    override fun getLayoutResID(): Int {
        return R.layout.activity_lottie_naimation
    }

    override fun initView() {
        addAnimationLisenter()
        lottieAnimationView.setAnimation(pingpang)
        lottieAnimationView.repeatCount = 2
        lottieAnimationView.playAnimation()
        btnStartAnimaitonXiGua.setOnClickListener {
            lottieAnimationView.setAnimation(xigua)
            lottieAnimationView.playAnimation()
            btnStopAnimaiton.setText("暂停")
        }
        btnStartAnimaitonPingPang.setOnClickListener {
            lottieAnimationView.setAnimation(pingpang)
            lottieAnimationView.playAnimation()
            btnStopAnimaiton.setText("暂停")
        }
        btnStopAnimaiton.setOnClickListener {
            if (lottieAnimationView.isAnimating) {
                lottieAnimationView.pauseAnimation()
                btnStopAnimaiton.setText("开始")
            } else {
                btnStopAnimaiton.setText("暂停")
                lottieAnimationView.resumeAnimation()
            }
        }
        btnCancelAnimaiton.setOnClickListener {
            lottieAnimationView.cancelAnimation()
            btnStopAnimaiton.setText("开始")
        }
    }

    /**
     * 动画监听
     */
    private fun addAnimationLisenter() {
        lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                //该回调在play的时候
                tvAnimationState.setText("动画时长:" + animation?.duration.toString())
            }

            override fun onAnimationEnd(animation: Animator?) {
                tvAnimationState.setText("动画结束")
            }

            override fun onAnimationCancel(animation: Animator?) {
                tvAnimationState.setText("动画被取消")
            }

            override fun onAnimationRepeat(animation: Animator?) {
                tvAnimationState.setText("动画重播")
            }

        })
    }
}