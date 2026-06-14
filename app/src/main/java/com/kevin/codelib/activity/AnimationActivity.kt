package com.kevin.codelib.activity

import android.animation.Animator
import android.view.View
import com.kevin.codelib.R
import com.kevin.codelib.base.BaseActivity
import com.kevin.codelib.databinding.ActivityLottieNaimationBinding

/**
 * lottie动画的使用
 */
class AnimationActivity : BaseActivity() {
    private lateinit var binding: ActivityLottieNaimationBinding

    val pingpang: String = "pingpang.json"
    val xigua: String = "xigua.json"

    override fun getLayoutView(): View {
        binding = ActivityLottieNaimationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        addAnimationLisenter()
        binding.lottieAnimationView.setAnimation(pingpang)
        binding.lottieAnimationView.repeatCount = 2
        binding.lottieAnimationView.playAnimation()
        binding.btnStartAnimaitonXiGua.setOnClickListener {
            binding.lottieAnimationView.setAnimation(xigua)
            binding.lottieAnimationView.playAnimation()
            binding.btnStopAnimaiton.setText("暂停")
        }
        binding.btnStartAnimaitonPingPang.setOnClickListener {
            binding.lottieAnimationView.setAnimation(pingpang)
            binding.lottieAnimationView.playAnimation()
            binding.btnStopAnimaiton.setText("暂停")
        }
        binding.btnStopAnimaiton.setOnClickListener {
            if (binding.lottieAnimationView.isAnimating) {
                binding.lottieAnimationView.pauseAnimation()
                binding.btnStopAnimaiton.setText("开始")
            } else {
                binding.btnStopAnimaiton.setText("暂停")
                binding.lottieAnimationView.resumeAnimation()
            }
        }
        binding.btnCancelAnimaiton.setOnClickListener {
            binding.lottieAnimationView.cancelAnimation()
            binding.btnStopAnimaiton.setText("开始")
        }
    }

    /**
     * 动画监听
     */
    private fun addAnimationLisenter() {
        binding.lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                //该回调在play的时候
                binding.tvAnimationState.setText("动画时长:" + animation.duration.toString())
            }

            override fun onAnimationEnd(animation: Animator) {
                binding.tvAnimationState.setText("动画结束")
            }

            override fun onAnimationCancel(animation: Animator) {
                binding.tvAnimationState.setText("动画被取消")
            }

            override fun onAnimationRepeat(animation: Animator) {
                binding.tvAnimationState.setText("动画重播")
            }

        })
    }
}
