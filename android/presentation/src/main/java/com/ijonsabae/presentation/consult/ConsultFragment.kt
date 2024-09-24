package com.ijonsabae.presentation.consult

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ijonsabae.domain.model.Consultant
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentConsultBinding
import com.ijonsabae.presentation.main.MainActivity
import com.ijonsabae.presentation.util.convertConsultant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConsultFragment :
    BaseFragment<FragmentConsultBinding>(FragmentConsultBinding::bind, R.layout.fragment_consult) {
    private lateinit var navController: NavController
    private val consultViewModel: ConsultViewModel by viewModels()
    private val consultantListAdapter by lazy {
        ConsultantListAdapter().apply {
            setItemClickListener(
                object : ConsultantListAdapter.OnItemClickListener {
                    override fun onItemClick(item: Consultant) {
                        navController.navigate(ConsultFragmentDirections.actionConsultToConsultDialog(convertConsultant(item)))
                    }
                }
            )
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (fragmentContext as MainActivity).showAppBar("전문가 상담")
        navController = findNavController()
        initRecyclerView()
        initFlow()
    }

    private fun initFlow() {
        lifecycleScope.launch {
            consultViewModel.consultantList.collect {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    consultantListAdapter.submitList(it)
                    binding.tvCount.text = "(All ${it.size})"
                }
            }
        }
    }

    private fun initRecyclerView() {
        consultantListAdapter.submitList(consultViewModel.consultantList.value)
        val mountainRecyclerView = binding.rvConsultant
        mountainRecyclerView.layoutManager = LinearLayoutManager(context)
        mountainRecyclerView.adapter = consultantListAdapter
    }
}