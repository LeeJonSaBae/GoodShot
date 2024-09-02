package com.ijonsabae.goodshot.config

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import javax.inject.Inject
import javax.inject.Named

// Fragment의 기본을 작성, 뷰 바인딩 활용
abstract class BaseFragment<B : ViewBinding>(
  private val bind: (View) -> B,
  @LayoutRes layoutResId: Int
) : Fragment(layoutResId) {
  private var _binding: B? = null
  protected val binding get() = _binding!!
  lateinit var fragmentContext: Context

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
  ): View? {
    _binding = bind(super.onCreateView(inflater, container, savedInstanceState)!!)
    return binding.root
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
}