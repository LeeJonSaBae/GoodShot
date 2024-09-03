package com.ijonsabae.presentation.config

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import javax.inject.Inject
import javax.inject.Named

// 액티비티의 기본을 작성, 뷰 바인딩 활용
abstract class BaseActivity<B : ViewBinding>(private val inflate: (LayoutInflater) -> B) :
    AppCompatActivity() {
    protected lateinit var binding: B
        private set

    @Inject
    @Named("activity")
    lateinit var toastHelper: ToastHelper

    // 뷰 바인딩 객체를 받아서 inflate해서 화면을 만들어줌.
    // 즉 매번 onCreate에서 setContentView를 하지 않아도 됨.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)
        receivedIntent = intent
    }

    // 토스트를 쉽게 띄울 수 있게 해줌.
    fun showToastShort(message: String) {
        toastHelper.showToastShort(message)
    }

    fun showToastLong(message: String) {
        toastHelper.showToastLong(message)
    }

    companion object {
        lateinit var receivedIntent: Intent
    }
}