package com.stonecoders.stockinteligenterfid.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stonecoders.stockinteligenterfid.databinding.TagInfoHolderBinding
import com.stonecoders.stockinteligenterfid.entities.Articulo

class ArticleListAdapter(private var articleList: List<Articulo>) :
    RecyclerView.Adapter<ArticleListAdapter.ArticleHolder>() {

    class ArticleHolder(var binding: TagInfoHolderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleHolder {
        val binding =
            TagInfoHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleHolder, position: Int) {
        holder.binding.epcTagTextView.text = articleList[position].nombre
    }

    override fun getItemCount(): Int =
        articleList.size

    fun setNewData(newData: List<Articulo>) {
        articleList = newData
        notifyDataSetChanged()
    }


}