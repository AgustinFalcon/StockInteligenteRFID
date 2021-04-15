package com.stonecoders.stockinteligenterfid.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.stonecoders.stockinteligenterfid.databinding.FragmentLogInBinding
import com.stonecoders.stockinteligenterfid.entities.LogInResult
import com.stonecoders.stockinteligenterfid.room.HelperDatabase
import com.stonecoders.stockinteligenterfid.room.repositories.UserRepository
import com.stonecoders.stockinteligenterfid.room.viewmodels.BusinessViewModel
import com.stonecoders.stockinteligenterfid.room.viewmodels.UserViewModel
import com.stonecoders.stockinteligenterfid.room.viewmodels.UserViewModelFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LogInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LogInFragment : Fragment() {
    private val db by lazy { HelperDatabase.getDatabase(requireContext(), lifecycleScope) }
    val businessViewModel: BusinessViewModel by activityViewModels()


    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentLogInBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val repo = UserRepository(db.userDao())
        val userViewModel: UserViewModel by viewModels {
            UserViewModelFactory(repo)
        }

        userViewModel.logInResult.observe(viewLifecycleOwner, Observer { it ->
            when (it) {
                LogInResult.SUCCESS -> finishedSetUp()
                LogInResult.FAILURE -> Snackbar.make(
                    requireView(),
                    "Error al iniciar sesión",
                    Snackbar.LENGTH_SHORT
                ).show()
                LogInResult.WAITING -> {
                }
            }
        })


        binding.button.setOnClickListener {
            val name = binding.usernameInput.text.toString()
            val email = "andykamin3@gmail.com"
            val pwd = binding.passwordInput.text.toString()
            val brand = binding.companyInput.text.toString()
            if (name.isNotBlank() && email.isNotBlank() && pwd.isNotBlank() && brand.isNotBlank()) {
                userViewModel.validateUser(name, pwd)
                businessViewModel.selectBrand(binding.companyInput.text.toString())
            }
        }

    }

    fun finishedSetUp() {
        val directions = LogInFragmentDirections.actionLogInFragment2ToHomeFragment()
        requireView().findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "LogInFrag"

        @JvmStatic
        fun newInstance() =
            LogInFragment()
    }
}