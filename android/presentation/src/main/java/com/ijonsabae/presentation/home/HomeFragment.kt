package com.ijonsabae.presentation.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentHomeBinding
import com.ijonsabae.presentation.main.MainActivity
import com.ijonsabae.presentation.replay.HorizontalMarginItemDecoration
import kotlin.math.abs

class HomeFragment :
    BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home) {

    private var newsList = mutableListOf<News>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (fragmentContext as MainActivity).hideAppBar()

        initAppBarMotionLayout()
        initNewsViewPager(binding.vpNews)

    }

    private fun initAppBarMotionLayout() {
        val motionLayout = binding.motionLayout
        binding.appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val seekPosition = -verticalOffset / appBarLayout.totalScrollRange.toFloat()
            motionLayout.progress = seekPosition
        }
    }

    private fun initNewsViewPager(viewPager: ViewPager2) {
        val adapter = NewsViewPagerAdapter(requireContext()).apply { submitList(newsList) }
        viewPager.adapter = adapter

        viewPager.offscreenPageLimit = 1
        viewPager.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val itemDecoration = HorizontalMarginItemDecoration(horizontalMarginInDp = 20)
        viewPager.addItemDecoration(itemDecoration)
        viewPager.setPageTransformer { page, position ->
            val offsetX = position * -(2 * 120) // offset 값으로 간격 조정
            page.translationX = offsetX

            val scale = 1 - abs(position) // scale 값으로 양쪽 애들 높이 조정
            page.scaleY = 0.85f + 0.15f * scale
        }
        autoScroll(viewPager, 5000)
    }

    private fun autoScroll(viewPager: ViewPager2, delay: Long) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                viewPager.currentItem += 1
                handler.postDelayed(this, delay) // 스크롤 간격
            }
        }
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, delay)
            }
        })
        handler.post(runnable)
    }

    private fun setNewsData() {
//        return listOf(
//            News(
//                title = "훅 vS 슬라이스", description = "다르지만 비슷한 스윙 교정 방법에\n대해 알아볼까요?!"
//            )
//        )
    }

}