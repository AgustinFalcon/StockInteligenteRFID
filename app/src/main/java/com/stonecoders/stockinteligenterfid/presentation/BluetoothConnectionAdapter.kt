package com.stonecoders.stockinteligenterfid.presentation

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stonecoders.stockinteligenterfid.databinding.ConnectionHolderBinding
import com.stonecoders.stockinteligenterfid.entities.SavedDevice

class BluetoothConnectionAdapter(
    private var deviceList: MutableList<SavedDevice>,
    private var onTap: (device: SavedDevice) -> Unit
) : RecyclerView.Adapter<BluetoothConnectionAdapter.ConnectionHolder>() {
    class ConnectionHolder(val binding: ConnectionHolderBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    fun setData(newData: MutableList<SavedDevice>) {
        deviceList = newData
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionHolder {
        var binding = ConnectionHolderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConnectionHolder(binding)
    }

    override fun onBindViewHolder(holder: ConnectionHolder, position: Int) {
        Log.d("RECYCLER VIEW", deviceList[position].address)
        holder.binding.addressDevice.text = deviceList[position].address
        holder.binding.root.setOnClickListener {
            onTap(deviceList[position])
        }
    }

    override fun getItemCount(): Int =
        deviceList.size

}