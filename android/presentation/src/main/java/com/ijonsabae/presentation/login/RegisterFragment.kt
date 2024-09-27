package com.ijonsabae.presentation.login

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentRegisterBinding

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::bind, R.layout.fragment_register) {
    private var chkPolicy1 = false
    private var chkPolicy2 = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val underlineString = SpannableString("약관보기").apply { setSpan(UnderlineSpan(),0, "약관보기".length, 0) }
        binding.policy1Detail.text = underlineString
        binding.policy2Detail.text = underlineString

        binding.btnCheckPolicy1.setOnClickListener {
            if(!chkPolicy1){
                chkPolicy1 = true
                it.backgroundTintList = ContextCompat.getColorStateList(fragmentContext, R.color.dark_green)
            }else{
                chkPolicy1 = false
                it.backgroundTintList = ContextCompat.getColorStateList(fragmentContext, android.R.color.darker_gray)
            }
        }

        binding.btnCheckPolicy2.setOnClickListener {
            if(!chkPolicy2){
                chkPolicy2 = true
                it.backgroundTintList = ContextCompat.getColorStateList(fragmentContext, R.color.dark_green)
            }else{
                chkPolicy2 = false
                it.backgroundTintList = ContextCompat.getColorStateList(fragmentContext, android.R.color.darker_gray)
            }
        }

//        binding.policy1Detail.paintFlags = Paint.UNDERLINE_TEXT_FLAG
//        binding.policy2Detail.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        (fragmentContext as LoginActivity).showAppBar("회원가입")

        binding.btnRegister.setOnClickListener {
            navController.navigate(R.id.action_register_to_login)
        }

    }
}