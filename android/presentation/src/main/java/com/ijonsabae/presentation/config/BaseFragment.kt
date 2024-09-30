package com.ijonsabae.presentation.config

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.ijonsabae.domain.model.RetrofitException
import com.ijonsabae.presentation.login.LoginViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Named

// Fragment의 기본을 작성, 뷰 바인딩 활용
abstract class BaseFragment<B : ViewBinding>(
  private val bind: (View) -> B,
  @LayoutRes layoutResId: Int
) : Fragment(layoutResId) {
  private var _binding: B? = null
  protected val binding get() = _binding!!
  protected lateinit var navController: NavController
  lateinit var fragmentContext: Context

  protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    // error handling
    if(throwable is RetrofitException)
    throwable.apply {
      printStackTrace()
      showToastShort("$code : $message")
    }
    else if(throwable is RuntimeException){
      throwable.apply {
        printStackTrace()
        showToastShort("통신 에러 : $message")
      }
    }
    else{
      throwable.apply {
        printStackTrace()
        showToastShort("$message")
      }
    }
  }

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
}