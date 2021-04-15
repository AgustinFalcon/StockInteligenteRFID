package com.stonecoders.stockinteligenterfid.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.rscja.deviceapi.RFIDWithUHFBLE
import com.rscja.deviceapi.interfaces.ConnectionStatus
import com.rscja.deviceapi.interfaces.ConnectionStatusCallback
import com.stonecoders.stockinteligenterfid.R
import com.stonecoders.stockinteligenterfid.databinding.DialogNewConnectionsBinding
import com.stonecoders.stockinteligenterfid.databinding.FragmentConnectionStatusBinding
import com.stonecoders.stockinteligenterfid.entities.SavedDevice
import com.stonecoders.stockinteligenterfid.presentation.BluetoothConnectionAdapter
import com.stonecoders.stockinteligenterfid.room.HelperDatabase
import com.stonecoders.stockinteligenterfid.room.repositories.SavedDeviceRepository
import com.stonecoders.stockinteligenterfid.room.viewmodels.SavedDevicesViewModel
import com.stonecoders.stockinteligenterfid.room.viewmodels.SavedDevicesViewModelFactory
import java.util.*


class ConnectionStatusFragment : Fragment() {

    private val db by lazy { HelperDatabase.getDatabase(requireContext(), lifecycleScope) }
    private val repo by lazy { SavedDeviceRepository(db.savedDeviceDao()) }
    private val savedDevicesViewModel: SavedDevicesViewModel by viewModels {
        SavedDevicesViewModelFactory(repo)
    }

    private var _binding: FragmentConnectionStatusBinding? = null
    private val binding get() = _binding!!
    private val btStatus: ConnectionStatusCallback<Any> = ConnectionStatusCallback<Any> { p0, p1 ->
        p0?.name?.let {
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
                    binding.textViewConnectionStatus.text = getString(R.string.connected)
                    Snackbar.make(requireView(), R.string.connected, Snackbar.LENGTH_SHORT).show()
                }

                savedDevicesViewModel.tempData?.let { device ->
                    savedDevicesViewModel.insert(
                            device
                    )
                }
            }
        }


    }
    private var uhf = RFIDWithUHFBLE.getInstance()
    private val REQUEST_ENABLE_BT = 2
    private val REQUEST_SELECT_DEVICE = 1

    private val deviceList = mutableListOf<SavedDevice>()
    private lateinit var newConnectionAdapter: BluetoothConnectionAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        uhf.init(requireContext())

    }


    fun connect(deviceAddress: String?) {
        Log.d(TAG, "Connecting to $deviceAddress")
        if (uhf.connectStatus == ConnectionStatus.CONNECTING) {

        } else {
            uhf.connect(deviceAddress, btStatus)
            savedDevicesViewModel.tempData = deviceAddress?.let {
                SavedDevice(
                        it,
                        "Dispositivo de Lectura ${deviceAddress.substring(11)}"
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_SELECT_DEVICE ->                 //When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    if (uhf.connectStatus == ConnectionStatus.CONNECTED) {
                        uhf.disconnect()
                    }
                    //val deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE)
                    //val mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress)
                    //Snackbar.make(requireView(), String.format("%s(%s)\nconnecting", mDevice.getName(), deviceAddress), Snackbar.LENGTH_SHORT)
                }
            REQUEST_ENABLE_BT -> if (resultCode == Activity.RESULT_OK) {
                Snackbar.make(requireView(), "Bluetooth has turned on ", Snackbar.LENGTH_SHORT)
            } else {
                Snackbar.make(requireView(), "Problem in BT Turning ON ", Snackbar.LENGTH_SHORT)
            }
            else -> {
            }
        }
    }

    private val onTap = fun(mDevice: SavedDevice) {
        connect(mDevice.address)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentConnectionStatusBinding.inflate(inflater, container, false)
        newConnectionAdapter = BluetoothConnectionAdapter(deviceList, onTap = onTap)
        return binding.root
    }


    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {

                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address
                    deviceHardwareAddress?.let { Log.d(TAG, it) }
                    device?.let {
                        deviceList.add(SavedDevice.fromBluetoothDevice(it))
                        newConnectionAdapter.setData(deviceList)
                    }
                }
            }
        }
    }

    val permissionlistener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            Snackbar.make(requireView(), "Permission Granted", Snackbar.LENGTH_SHORT).show()
            val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
            bluetoothAdapter!!.startDiscovery()
        }

        override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
            Snackbar.make(requireView(), "Permission Denied\n$deniedPermissions", Snackbar.LENGTH_SHORT).show()
        }

    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        requireActivity().registerReceiver(receiver, filter)
        binding.textViewConnectionStatus.text = uhf.connectStatus.name
        binding.textViewConnectionStatus.setOnClickListener {
            binding.textViewConnectionStatus.text = uhf.connectStatus.name
        }


        val seekBarChangeListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // updated continuously as the user slides the thumb
                binding.textViewPower.text = "Potencia: $progress dB"

                //uhf.setPower()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // called when the user first touches the SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // called after the user finishes moving the SeekBar
            }
        }

        binding.seekBar2.setOnSeekBarChangeListener(seekBarChangeListener)
        binding.seekBar2.max = 30

        binding.buttonSearch.setOnClickListener {
            deviceList.clear()
            checkBluetooth()
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

            binding.recyclerViewElements.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = newConnectionAdapter
                setHasFixedSize(true)
            }

            Log.d(TAG, "Started reading")
            //bluetoothAdapter?.startDiscovery()+
            TedPermission.with(requireContext())
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                    .check()


        }



    }





    class NewConnectionDialogues(
            val rvAdapter: BluetoothConnectionAdapter,
    ) : DialogFragment() {
        private var _binding: DialogNewConnectionsBinding? = null
        private val binding get() = _binding!!


        override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
        ): View? {
            _binding = DialogNewConnectionsBinding.inflate(inflater, container, false)
            binding.recViewIds.apply {
                adapter = rvAdapter
                layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                )
                setHasFixedSize(true)
            }


            return binding.root
        }


        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                // Use the Builder class for convenient dialog construction
                val builder = AlertDialog.Builder(it)
                builder.setMessage(R.string.new_readers_prompt)
                        .setPositiveButton("",
                                DialogInterface.OnClickListener { dialog, id ->
                                    // FIRE ZE MISSILES!
                                })
                        .setNegativeButton(R.string.cancel,
                                DialogInterface.OnClickListener { dialog, id ->
                                    // User cancelled the dialog
                                })


                // Create the AlertDialog object and return it
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")

        }
    }


    private fun checkBluetooth() {
        val REQUEST_CODE = 123
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        REQUEST_CODE
                )
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.BLUETOOTH
                    )
                    != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.BLUETOOTH),
                        REQUEST_CODE
                )
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.BLUETOOTH_ADMIN
                    )
                    != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                        REQUEST_CODE
                )
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_CODE
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter?.cancelDiscovery()
        uhf.stopScanBTDevices()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter?.cancelDiscovery()
        uhf.stopScanBTDevices()
        requireActivity().unregisterReceiver(receiver)

    }

    companion object {
        const val TAG = "ConnStatusFragment"

        @JvmStatic
        fun newInstance() = ConnectionStatusFragment()
    }
}

object RequestCodes {
    const val BLUETOOTH_ENABLE = 1221
    const val BT_DISCOVERY_STRINGS = "BT_DISCOVERY"
}



