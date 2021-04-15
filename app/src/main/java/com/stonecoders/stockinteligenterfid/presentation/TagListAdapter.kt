package com.stonecoders.stockinteligenterfid.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stonecoders.stockinteligenterfid.databinding.TagInfoHolderBinding
import com.stonecoders.stockinteligenterfid.entities.TagInfo


class TagListAdapter(private var tagList: List<TagInfo>) :
    RecyclerView.Adapter<TagListAdapter.TagHolder>() {

    class TagHolder(var binding: TagInfoHolderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagHolder {
        val binding =
            TagInfoHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagHolder(binding)
    }

    override fun onBindViewHolder(holder: TagHolder, position: Int) {
        holder.binding.epcTagTextView.text = tagList[position].epc
    }

    override fun getItemCount(): Int =
            tagList.size

    fun setNewData(newData: MutableList<TagInfo>) {
        tagList = newData
        notifyDataSetChanged()
    }
}