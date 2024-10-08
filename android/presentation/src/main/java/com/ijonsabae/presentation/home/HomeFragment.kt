package com.ijonsabae.presentation.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ijonsabae.domain.usecase.login.GetLocalAccessTokenUseCase
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentHomeBinding
import com.ijonsabae.presentation.main.MainActivity
import com.ijonsabae.presentation.replay.HorizontalMarginItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs


private const val TAG = "굿샷_HomeFragment"

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home) {
    @Inject
    lateinit var getLocalAccessTokenUseCase: GetLocalAccessTokenUseCase

    private val homeViewModel: HomeViewModel by viewModels()
    private var newsList = mutableListOf<NewsDTO>()
    private val NEWS_MARGIN_PX by lazy { resources.getDimension(R.dimen.home_news_margin_dp_between_items) }
    private lateinit var newsViewPagAdapter: NewsViewPagerAdapter
    private lateinit var youtubeRecyclerViewAdapter: YoutubeRecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initClickListener()
        initAppBarMotionLayout()
    }

    private fun initView() {
        (fragmentContext as MainActivity).hideAppBar()

//        initAnimation()

        lifecycleScope.launch {
            if (getLocalAccessTokenUseCase() == null) {
                binding.layoutContentNotLogin.visibility = View.VISIBLE
                binding.layoutContentLogin.visibility = View.GONE
            } else {
                binding.layoutContentNotLogin.visibility = View.GONE
                binding.layoutContentLogin.visibility = View.VISIBLE
//                initFlow()
                initNewsViewPager(binding.vpNews)
                initYoutubeRecyclerView(binding.rvYoutube)
                sendLoadingCompleteMessage()
            }
        }

    }

//    private fun initFlow() {
//        lifecycleScope.launch(coroutineExceptionHandler) {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                homeViewModel.youtubeList.collectLatest {
//                    val list = it.getOrThrow().items.map { videoItem ->
//                        convertVideoItemToYoutubeDTO(videoItem)
//                    }
//                    youtubeRecyclerViewAdapter.submitList(list)
//                }
//            }
//        }
////        Log.d(TAG, "onViewCreated: margin = $NEWS_MARGIN_PX")
//    }

    private fun sendLoadingCompleteMessage() {
        LocalBroadcastManager.getInstance(fragmentContext).sendBroadcast(Intent().apply {
            action = "loading"
            putExtra("complete", false)
        })
    }

    private fun initClickListener() {
        binding.btnConsult.setOnClickListener {
            navController.navigate(R.id.action_home_to_consult)
        }
    }

    private fun initAppBarMotionLayout() {
        val motionLayout = binding.motionLayout
        binding.appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val seekPosition = -verticalOffset / appBarLayout.totalScrollRange.toFloat()
            motionLayout.progress = seekPosition
        }
    }

    private fun initNewsViewPager(viewPager: ViewPager2) {
        newsList = getNewsData()
        newsViewPagAdapter = NewsViewPagerAdapter(requireContext()).apply { submitList(newsList) }
        viewPager.adapter = newsViewPagAdapter

        // 초기 위치를 중간으로 설정
        val initialPosition =
            Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2 % newsViewPagAdapter.currentList.size)
        viewPager.setCurrentItem(initialPosition, false)

        val screenWidth = resources.displayMetrics.widthPixels
        val marginPx =
            (screenWidth * 0.05).toInt() + (NEWS_MARGIN_PX * 2).toInt()

        viewPager.offscreenPageLimit = 1
        viewPager.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val itemDecoration = HorizontalMarginItemDecoration(horizontalMarginInPx = marginPx)
        viewPager.addItemDecoration(itemDecoration)
        viewPager.setPageTransformer { page, position ->
            val offsetX = position * -(2.5 * marginPx).toInt() // offset 값으로 간격 조정
            page.translationX = offsetX

            val scale = 1 - abs(position) // scale 값으로 양쪽 애들 높이 조정
            page.scaleY = 0.85f + 0.15f * scale
        }

        autoScroll(viewPager, 5000)
    }

    private fun initYoutubeRecyclerView(recyclerView: RecyclerView) {
        youtubeRecyclerViewAdapter =
            YoutubeRecyclerViewAdapter(fragmentContext).apply {
                setOnYoutubeClickListener(object :
                    YoutubeRecyclerViewAdapter.OnYoutubeItemClickListener {
                    override fun onYoutubeItemClick(item: YoutubeDTO) {
                        toggleYoutubeItems(item)
                    }
                })
                submitList(homeViewModel.youtubeList)
            }
//        youtubeRecyclerViewAdapter.submitList(homeViewModel.youtubeList.v)
        recyclerView.adapter = youtubeRecyclerViewAdapter
        recyclerView.layoutManager =
            LinearLayoutManager(fragmentContext, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun toggleYoutubeItems(item: YoutubeDTO) {
        item.isVisible = !item.isVisible
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

    private fun getNewsData(): MutableList<NewsDTO> {
        return mutableListOf(
            NewsDTO(
                title = "골프 구질도 알고 골프 실력도 쌓고",
                "https://www.golfjournal.co.kr/news/articleView.html?idxno=4455",
                description = "골프를 처음 시작하는 당신을 위한 실속 정보! 이번에는 골프 구질에 대해 알아보자.",
                thumbnail = ContextCompat.getDrawable(fragmentContext, R.drawable.article1)!!
            ),
            NewsDTO(
                title = "골프 스윙에 도움되는 균형을 찾아가는 호흡법",
                "https://www.golfmagazinekorea.com/news/articleView.html?idxno=271",
                description = "완벽한 골프 스윙은 셋업에서 시작된다.",
                thumbnail = ContextCompat.getDrawable(fragmentContext, R.drawable.article2)!!
            ),
            NewsDTO(
                title = "골프 임팩트 다섯 선수로 살펴본 그 순간의 찰라",
                "https://blog.naver.com/PostView.naver?blogId=beheaded&logNo=223454216551&categoryNo=9&parentCategoryNo=0&viewDate=&currentPage=5&postListTopCurrentPage=1&from=thumbnailList&userTopListOpen=true&userTopListCount=5&userTopListManageOpen=false&userTopListCurrentPage=5",
                description = "골프 스윙 어떤 것이 정답인지는 모르지만 PGA 투어 선수들을 살펴보면 다들 개성 있는 임팩트 동작을 하는 것을 알 수 있습니다.",
                thumbnail = ContextCompat.getDrawable(fragmentContext, R.drawable.article3)!!
            ),
            NewsDTO(
                title = "골프 자세를 교정하는 방법",
                "https://www.golfmagazinekorea.com/news/articleView.html?idxno=8766",
                description = "골프에서 자세가 좋을수록 좋은 스윙을 할 수 있기 때문이다.",
                thumbnail = ContextCompat.getDrawable(fragmentContext, R.drawable.article4)!!
            ),
            NewsDTO(
                title = "골퍼님들의 오버스윙 교정을 위한 모든 것",
                "https://kimcaddie.com/post/2024-golf-over-swing",
                description = "오버스윙은 샷의 정확성을 떨어뜨리기 때문에 교정이 필요합니다.",
                thumbnail = ContextCompat.getDrawable(fragmentContext, R.drawable.article5)!!
            ),
            NewsDTO(
                title = "중급자를 위한 골프레슨",
                "https://www.golfjournal.co.kr/news/articleView.html?idxno=5012",
                description = "그립의 재발견 #JNGK",
                thumbnail = ContextCompat.getDrawable(fragmentContext, R.drawable.article7)!!
            ),
            NewsDTO(
                title = "골프 백스윙 고민 많은 분을 위한 해결책",
                "https://blog.naver.com/PostView.naver?blogId=beheaded&logNo=223343746913&categoryNo=9&parentCategoryNo=0&viewDate=&currentPage=10&postListTopCurrentPage=1&from=thumbnailList&userTopListOpen=true&userTopListCount=5&userTopListManageOpen=false&userTopListCurrentPage=10",
                description = "일관된 백스윙 하는 것이 골프 스윙에서 가장 중요한 포인트",
                thumbnail = ContextCompat.getDrawable(fragmentContext, R.drawable.article8)!!
            ),
            NewsDTO(
                title = "팔만 쓰는 스윙이 훅을 낳는다",
                "http://jtbcgolf.joins.com/academy/column/column_view.asp?page=3&column_type=10&ac1=165",
                description = "힘에 의존하는 스윙 습관은 잘못… 자연스런 스윙 익혀야",
                thumbnail = ContextCompat.getDrawable(fragmentContext, R.drawable.article9)!!
            ),
            NewsDTO(
                title = "골프 구질 알아보기",
                "https://kimcaddie.com/post/golf_shot_pitch_%EA%B5%AC%EC%A7%88_%EA%B5%90%EC%A0%95_1",
                description = "훅, 페이드, 슬라이스 등 뜻과 이유, 교정 꿀팁 1편",
                thumbnail = ContextCompat.getDrawable(fragmentContext, R.drawable.article10)!!
            ),
        )
    }
}