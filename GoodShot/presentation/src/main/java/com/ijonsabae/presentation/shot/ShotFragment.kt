package com.ijonsabae.presentation.shot

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentShotBinding

class ShotFragment: BaseFragment<FragmentShotBinding>(FragmentShotBinding::bind, R.layout.fragment_shot) {
    private lateinit var navController: NavController
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(binding.root)
        binding.btnCamera.setOnClickListener {
            navController.navigate(R.id.action_shot_to_camera)
        }
    }
}