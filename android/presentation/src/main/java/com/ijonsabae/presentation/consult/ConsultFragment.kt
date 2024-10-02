package com.ijonsabae.presentation.consult

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ijonsabae.domain.model.Expert
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.databinding.FragmentConsultBinding
import com.ijonsabae.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConsultFragment :
    BaseFragment<FragmentConsultBinding>(FragmentConsultBinding::bind, R.layout.fragment_consult) {
    private val consultViewModel: ConsultViewModel by viewModels()
    private val consultantListAdapter by lazy {
        ConsultantListAdapter().apply {
            setItemClickListener(
                object : ConsultantListAdapter.OnItemClickListener {
                    override fun onItemClick(item: Expert) {
                        navController.navigate(
                            ConsultFragmentDirections.actionConsultToConsultDialog(
                                item.id
                            )
                        )
                    }
                }
            )
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (fragmentContext as MainActivity).showAppBar("전문가 상담")
        initRecyclerView()
        initFlow()
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                consultViewModel.consultantList.collect {
                    consultantListAdapter.submitData(it)
                    binding.tvCount.text = "(All )"
                }
            }
        }
    }

    private fun initRecyclerView() {
        lifecycleScope.launch {
            consultantListAdapter.submitData(consultViewModel.consultantList.value)
        }
        val consultantRecyclerView = binding.rvConsultant
        consultantRecyclerView.layoutManager = LinearLayoutManager(fragmentContext)
        consultantRecyclerView.adapter = consultantListAdapter
    }
}