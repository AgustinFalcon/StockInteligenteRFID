package com.stonecoders.stockinteligenterfid.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.stonecoders.stockinteligenterfid.databinding.FragmentInventoryBinding
import com.stonecoders.stockinteligenterfid.entities.Articulo
import com.stonecoders.stockinteligenterfid.entities.Stock
import com.stonecoders.stockinteligenterfid.presentation.ArticleListAdapter
import com.stonecoders.stockinteligenterfid.room.viewmodels.ArticleViewModel
import com.stonecoders.stockinteligenterfid.room.viewmodels.BusinessViewModel
import com.stonecoders.stockinteligenterfid.room.viewmodels.DepositoViewModel
import com.stonecoders.stockinteligenterfid.room.viewmodels.TagViewModel


class InventoryFragment : Fragment() {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private val businessViewModel by activityViewModels<BusinessViewModel>()
    private val depositoViewModel by activityViewModels<DepositoViewModel>()
    private val articleViewModel by activityViewModels<ArticleViewModel>()
    private val mAdapter: ArticleListAdapter = ArticleListAdapter(emptyList<Articulo>())
    private val tagViewModel by activityViewModels<TagViewModel>()
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInventoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    fun setUpObservers() {
        if (!articleViewModel.allInventory.value.isNullOrEmpty() && !articleViewModel.allParents.value.isNullOrEmpty()) {
            Log.d(TAG, "All loaded")

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()

        tagViewModel.tagList.observe(viewLifecycleOwner, {
            //Cruzar info de stock con los articulos que aparecen
            if (!depositoViewModel.metadata.value.isNullOrEmpty() && !articleViewModel.allInventory.value.isNullOrEmpty() && !articleViewModel.allParents.value.isNullOrEmpty()) {
                val builder = Articulo.Companion.Builder()
                        .metadata(depositoViewModel.metadata.value!!)
                        .parents(articleViewModel.allParents.value!!)
                val articleList = it.mapNotNull { tagInfo ->
                    builder.epc(tagInfo).build()
                }
                Log.d(TAG, articleList.toString())
                mAdapter.setNewData(articleList)

                val stockMap : MutableMap<Stock, Articulo> = mutableMapOf()
                articleViewModel.allInventory.value!!.forEach { stock->
                    articleList.find { it.codigo == stock.articulo }?.let { item -> stockMap.putIfAbsent(stock, item) }
                }

            }
        })



        binding.recyclerViewArticles.apply {
            adapter = mAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.readTagButton.setOnClickListener {
            if (!articleViewModel.allInventory.value.isNullOrEmpty() && !articleViewModel.allParents.value.isNullOrEmpty()) {
                tagViewModel.scanSingleTag()
            } else {
                Snackbar.make(requireView(), "Espera que termine de cargar", Snackbar.LENGTH_SHORT).show()
            }

        }




        if (businessViewModel.business.value == null || depositoViewModel.currentDeposito.value == null) {
            Snackbar.make(
                    requireView(),
                    "Volvé a la pantalla de selección de depósitos",
                    Snackbar.LENGTH_SHORT
            ).show()
        } else {
            articleViewModel.retrieveParents(
                    businessViewModel.business.value!!.rutaws,
                    depositoViewModel.currentDeposito.value!!.sucursal
            )
            articleViewModel.retrieveInventory(
                    businessViewModel.business.value!!.rutaws,
                    depositoViewModel.currentDeposito.value!!.codigo
            )
            binding.progressBarInventory.isIndeterminate = true

        }

        articleViewModel.allInventory.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                Log.d(TAG, it[0].toString())
                Snackbar.make(
                        requireView(),
                        "Carga de stock finalizada || ${it.size}",
                        Snackbar.LENGTH_SHORT
                ).show()
            }

        })

        articleViewModel.allParents.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "List size ${it.size}")
            if (it.isNotEmpty()) {
                Snackbar.make(
                        requireView(),
                        "Carga de artículos finalizada || ${it.size}",
                        Snackbar.LENGTH_SHORT
                ).show()
                binding.progressBarInventory.isIndeterminate = false
                binding.progressBarInventory.visibility = View.INVISIBLE
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InventoryFragment()

        private const val TAG = "InventoryFragment"
    }
}