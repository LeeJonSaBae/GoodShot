package com.ijonsabae.presentation.shot

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.window.layout.WindowInfoTracker
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentSwingExampleBinding
import com.ijonsabae.presentation.main.MainActivity
import com.ijonsabae.presentation.shot.flex.FoldingStateActor
import kotlinx.coroutines.launch

private const val TAG = "SwingExampleFragment 싸피"
class SwingExampleFragment :
    BaseFragment<FragmentSwingExampleBinding>(FragmentSwingExampleBinding::bind, R.layout.fragment_swing_example) {
    private lateinit var navController: NavController
    private lateinit var foldingStateActor: FoldingStateActor

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(binding.root)

        binding.layoutSwing.setOnClickListener{
            navController.navigate(R.id.action_swing_example_to_camera)
        }
        foldingStateActor = FoldingStateActor(WindowInfoTracker.getOrCreate(fragmentContext))
    }

    override fun onResume() {
        super.onResume()
        (fragmentContext as MainActivity).hideBottomNavBar()

        lifecycleScope.launch {
            foldingStateActor.checkFoldingStateForSwingExample(
                fragmentContext as AppCompatActivity,
                binding.layoutExample,
                binding.ivSwingExample,
                binding.layoutMenu,
                binding.cvReplay,
                binding.layoutReplay,
                binding.cvSwing,
                binding.layoutSwing,
                binding.tvReplayTitle,
                binding.tvSwingTitle,
                binding.tvReplayDescription,
                binding.tvSwingDescription,
                binding.ivReplay,
                binding.ivSwing
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (fragmentContext as MainActivity).showBottomNavBar()
    }
}