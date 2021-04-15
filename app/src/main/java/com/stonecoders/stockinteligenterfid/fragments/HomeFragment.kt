package com.stonecoders.stockinteligenterfid.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.stonecoders.stockinteligenterfid.databinding.FragmentHomeBinding
import com.stonecoders.stockinteligenterfid.entities.Deposito
import com.stonecoders.stockinteligenterfid.room.HelperDatabase
import com.stonecoders.stockinteligenterfid.room.repositories.UserRepository
import com.stonecoders.stockinteligenterfid.room.viewmodels.BusinessViewModel
import com.stonecoders.stockinteligenterfid.room.viewmodels.DepositoViewModel
import com.stonecoders.stockinteligenterfid.room.viewmodels.UserViewModel
import com.stonecoders.stockinteligenterfid.room.viewmodels.UserViewModelFactory

class HomeFragment : Fragment() {
    private val db by lazy { HelperDatabase.getDatabase(requireContext(), lifecycleScope) }
    private lateinit var repo: UserRepository
    val userViewModel: UserViewModel by activityViewModels {
        UserViewModelFactory(repo)
    }
    val depositoViewModel: DepositoViewModel by activityViewModels()
    val businessViewModel: BusinessViewModel by activityViewModels()
    var _binding: FragmentHomeBinding? = null
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setUpObservers() {

    }

    override fun onStart() {
        super.onStart()

        val mAdapter =
                ArrayAdapter<Deposito>(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        mAdapter.clear()




        binding.spinner.adapter = mAdapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                depositoViewModel.depositos.value?.get(pos)?.let {
                    depositoViewModel.setCurrentDeposito(it)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        repo = UserRepository(db.userDao())
        Log.d(TAG, "Default value ${businessViewModel.business.value}")

        depositoViewModel.retrieveDepositos("https://wanama.stockinteligente.com/")

        depositoViewModel.depositos.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                for (item in it) {
                    Log.d(TAG, item.toString())
                    mAdapter.add(item)
                }
            }
        })





        binding.configButton.setOnClickListener {
            val directions = HomeFragmentDirections.actionHomeFragmentToRegularConnectionsFragment()
            requireView().findNavController().navigate(directions)
        }

        binding.searchTagButton.setOnClickListener {
            val directions = HomeFragmentDirections.actionHomeFragmentToReadElements()
            requireView().findNavController().navigate(directions)
        }

        binding.searchTagButton.setOnClickListener {
            val directions = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
            requireView().findNavController().navigate(directions)
        }

        binding.readInventoryButton.setOnClickListener {
            val directions = HomeFragmentDirections.actionHomeFragmentToInventoryFragment()
            requireView().findNavController().navigate(directions)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "HomeFrag"

        @JvmStatic
        fun newInstance() =
                HomeFragment()
    }

}

