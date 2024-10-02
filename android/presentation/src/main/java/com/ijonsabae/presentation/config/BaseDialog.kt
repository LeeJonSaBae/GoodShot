package com.ijonsabae.presentation.config

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import javax.inject.Inject
import javax.inject.Named

// Fragment의 기본을 작성, 뷰 바인딩 활용
abstract class BaseDialog<B : ViewBinding>(
  private val bind: (View) -> B,
  @LayoutRes layoutResId: Int
) : DialogFragment(layoutResId) {
  private var _binding: B? = null
  protected val binding get() = _binding!!
  lateinit var fragmentContext: Context
  protected lateinit var navController: NavController

  @Inject
  @Named("fragment")
  lateinit var toastHelper: ToastHelper

  override fun onAttach(context: Context) {
    super.onAttach(context)
    fragmentContext = context
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = bind(super.onCreateView(inflater, container, savedInstanceState)!!)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    navController = findNavController()
  }

  override fun onDestroyView() {
    _binding = null
    super.onDestroyView()
  }

  // 토스트를 쉽게 띄울 수 있게 해줌.
  fun showToastShort(message: String) {
    toastHelper.showToastShort(message)
  }

  fun showToastLong(message: String) {
    toastHelper.showToastLong(message)
  }

  protected fun safeCall(action: () -> Unit) {
    if (_binding != null) {
      action()
    }
  }

  private fun getScreenWidth(context: Context): Int {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val windowMetrics = wm.currentWindowMetrics
      val insets = windowMetrics.windowInsets
        .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
      windowMetrics.bounds.width() - insets.left - insets.right
    } else {
      val displayMetrics = DisplayMetrics()
      wm.defaultDisplay.getMetrics(displayMetrics)
      displayMetrics.widthPixels
    }
  }

  private fun getScreenHeight(context: Context): Int {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val windowMetrics = wm.currentWindowMetrics
      val insets = windowMetrics.windowInsets
        .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
      windowMetrics.bounds.height() - insets.bottom - insets.top
    } else {
      val displayMetrics = DisplayMetrics()
      wm.defaultDisplay.getMetrics(displayMetrics)
      displayMetrics.heightPixels
    }
  }

  protected fun setScreenWidthPercentage(percentage: Float){
    val layoutParams = requireView().layoutParams
    layoutParams.width = (getScreenWidth(fragmentContext)*percentage).toInt()
  }

  protected fun setScreenHeightPercentage(percentage: Float){
    val layoutParams = requireView().layoutParams
    layoutParams.height = (getScreenHeight(fragmentContext)*percentage).toInt()
  }
}