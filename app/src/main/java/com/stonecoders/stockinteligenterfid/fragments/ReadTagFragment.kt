package com.stonecoders.stockinteligenterfid.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rscja.deviceapi.RFIDWithUHFBLE
import com.stonecoders.stockinteligenterfid.R
import com.stonecoders.stockinteligenterfid.databinding.FragmentReadElementsBinding
import com.stonecoders.stockinteligenterfid.entities.TagInfo
import com.stonecoders.stockinteligenterfid.presentation.TagListAdapter
import com.stonecoders.stockinteligenterfid.room.viewmodels.ArticleViewModel
import com.stonecoders.stockinteligenterfid.room.viewmodels.BusinessViewModel
import com.stonecoders.stockinteligenterfid.room.viewmodels.DepositoViewModel
import com.stonecoders.stockinteligenterfid.room.viewmodels.TagViewModel
import kotlinx.coroutines.launch

class FragmentReadElements : Fragment() {
    private var _binding: FragmentReadElementsBinding? = null
    private val binding get() = _binding!!
    private var tagList = mutableListOf<TagInfo>()
    private lateinit var mAdapter: TagListAdapter
    private val tagViewModel by activityViewModels<TagViewModel>()
    private val articleViewModel by activityViewModels<ArticleViewModel>()
    private val depositoViewModel by activityViewModels<DepositoViewModel>()
    private val businessViewModel by activityViewModels<BusinessViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReadElementsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        val uhf = RFIDWithUHFBLE.getInstance()
        uhf.init(requireContext())




        mAdapter = TagListAdapter(tagList)

        binding.tagRecyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        tagViewModel.tagList.observe(viewLifecycleOwner, {
            mAdapter.setNewData(it.toMutableList())
        })


        binding.clearTextView.setOnClickListener {
            tagViewModel.clearData()
        }

        binding.buttonScanSingle.setOnClickListener {
            tagViewModel.scanSingleTag()
        }

        binding.buttonScanMultiple.setOnClickListener {
            toggleMultipleScanning()
        }


        uhf.setKeyEventCallback {
            toggleMultipleScanning()
        }
    }

    private fun toggleMultipleScanning() {
        if (tagViewModel.isScanning) {
            tagViewModel.stopScan()
            Log.d(TAG, "Scanning stopped")
            binding.buttonScanMultiple.text = getString(R.string.scan_multiple)
        } else {
            tagViewModel.viewModelScope.launch {
                tagViewModel.startScan()
            }
            binding.buttonScanMultiple.text = getString(R.string.stop)
            Log.d(TAG, "Scanning started")
        }
    }

    companion object {

        const val TAG = "ReadTagFragment"

        @JvmStatic
        fun newInstance() =
                FragmentReadElements()
    }
}

enum class ReadingFlags(val flag: Int) {
    FLAG_START(0), //开始,
    FLAG_STOP(1),//停止

    FLAG_UPDATE_TIME(2), // 更新时间

    FLAG_UHFINFO(3),
    FLAG_UHFINFO_LIST(5),
    FLAG_SUCCESS(10),//成功

    FLAG_FAIL(11)//失败
}