package com.stonecoders.stockinteligenterfid.fragments

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.rscja.deviceapi.RFIDWithUHFBLE
import com.rscja.deviceapi.interfaces.ConnectionStatus
import com.rscja.deviceapi.interfaces.ConnectionStatusCallback
import com.stonecoders.stockinteligenterfid.R
import com.stonecoders.stockinteligenterfid.databinding.FragmentRegularConnectionsBinding
import com.stonecoders.stockinteligenterfid.entities.SavedDevice
import com.stonecoders.stockinteligenterfid.presentation.BluetoothConnectionAdapter
import com.stonecoders.stockinteligenterfid.room.HelperDatabase
import com.stonecoders.stockinteligenterfid.room.repositories.SavedDeviceRepository
import com.stonecoders.stockinteligenterfid.room.viewmodels.SavedDevicesViewModel
import com.stonecoders.stockinteligenterfid.room.viewmodels.SavedDevicesViewModelFactory
import java.util.*


class RegularConnectionsFragment : Fragment() {

    private val db by lazy { HelperDatabase.getDatabase(requireContext(), lifecycleScope) }
    private val repo by lazy { SavedDeviceRepository(db.savedDeviceDao()) }
    private val savedDevicesViewModel: SavedDevicesViewModel by viewModels {
        SavedDevicesViewModelFactory(repo)
    }
    private lateinit var mAdapter: BluetoothConnectionAdapter

    private var _binding: FragmentRegularConnectionsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegularConnectionsBinding.inflate(inflater, container, false)

        return binding.root
    }

    private val btStatus: ConnectionStatusCallback<Any> = ConnectionStatusCallback<Any> { p0, p1 ->
        Log.d(TAG, "In callback")
        p0?.name?.let {
            val uhf = RFIDWithUHFBLE.getInstance()
            Log.d("Connection Callback", it)
            if (it === "CONNECTED") {
                //TODO: Settear el workmode uhf.setR6Workmode()
                uhf.setEPCMode()
                if (!savedDevicesViewModel.allDevices.value.isNullOrEmpty()) {
                    for (device in savedDevicesViewModel.allDevices.value!!) {
                        if (device.address == savedDevicesViewModel.tempData?.address) {
                            uhf.setRemoteBluetoothName(device.name)
                        }
                    }
                }

                requireActivity().runOnUiThread {
                    binding.configurationButton.isEnabled = true
                    Snackbar.make(requireView(), R.string.connected, Snackbar.LENGTH_SHORT).show()
                }


            }
        }

    }

    private fun connect(device: SavedDevice) {
        val uhf = RFIDWithUHFBLE.getInstance()
        Log.d(ConnectionStatusFragment.TAG, "Connecting to ${device.address}")
        if (uhf.connectStatus == ConnectionStatus.CONNECTING) {

        } else {
            uhf.connect(device.address, btStatus)
            savedDevicesViewModel.tempData = SavedDevice(
                    device.address,
                    "Dispositivo de Lectura ${device.address.substring(11)}"
            )
        }
    }

    private val onTap = fun(savedDevice: SavedDevice) {
        val uhf = RFIDWithUHFBLE.getInstance()
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Snackbar.make(
                    requireView(),
                    getString(R.string.bt_unsupported),
                    Snackbar.LENGTH_SHORT
            ).show()
        }
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, RequestCodes.BLUETOOTH_ENABLE)
        }

        Log.d(ConnectionStatusFragment.TAG, "Started reading")
        TedPermission.with(requireContext())
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        bluetoothAdapter?.startDiscovery()
                        connect(savedDevice)
                    }

                    override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                        Snackbar.make(requireView(), "Permission Denied\n$deniedPermissions", Snackbar.LENGTH_SHORT).show()
                    }

                })
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check()

    }

    override fun onStart() {
        super.onStart()


        lateinit var tempList: MutableList<SavedDevice>
        if (!savedDevicesViewModel.allDevices.value.isNullOrEmpty()) {
            tempList = savedDevicesViewModel.allDevices.value!!.map {
                it
            }.toMutableList()
        } else {
            tempList = mutableListOf()
        }

        mAdapter =
                BluetoothConnectionAdapter(deviceList = tempList, onTap = onTap)

        savedDevicesViewModel.allDevices.observe(viewLifecycleOwner, {
            mAdapter.setData(it.toMutableList())
        })

        binding.configurationButton.isEnabled = false

        binding.configurationButton.setOnClickListener {
            TODO("Configuración del lector y chequeo de conexión")
        }

        binding.floatingActionButton.setOnClickListener {
            val directions =
                RegularConnectionsFragmentDirections.actionRegularConnectionsFragmentToConnectionStatusFragment()
            binding.root.findNavController().navigate(directions)
        }


        binding.recyclerConnect.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = mAdapter


        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "RegularConnections"
        @JvmStatic
        fun newInstance() =
            RegularConnectionsFragment()
    }
}