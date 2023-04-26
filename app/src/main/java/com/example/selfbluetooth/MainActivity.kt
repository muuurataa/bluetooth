package com.example.selfbluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.selfbluetooth.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var btPermission = false
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun scanBt(view: View) {
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "BT接続が許可されていません", Toast.LENGTH_LONG).show()

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                blueToothPermissionLauncher.launch(android.Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                blueToothPermissionLauncher.launch(android.Manifest.permission.BLUETOOTH)
            }
        }
    }

    private val blueToothPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                btPermission = true
                checkBluetoothState()
            } else {
                btPermission = false
            }
        }

    @SuppressLint("MissingPermission")
    private fun checkBluetoothState() {
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            btActivityResultLauncher.launch(enableBtIntent)
        } else {
            scanBluetoothDevice()
        }
    }

    private val btActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                checkBluetoothState()
            }
        }

    @SuppressLint("MissingPermission")
    private fun scanBluetoothDevice() {
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager.adapter

        val builder = AlertDialog.Builder(this@MainActivity)
        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.scan_bt, null)

        builder.setCancelable(false)
        builder.setView(dialogView)
        val listViewBluetooth = dialogView.findViewById<ListView>(R.id.bt_lst)
        val dialog = builder.create()

        val pairedBluetoothDevices = bluetoothAdapter?.bondedDevices

        val deviceData = mutableListOf<Map<String, String>>()

        if (pairedBluetoothDevices.isNullOrEmpty()) {
            val value = "デバイスが見つかりません"
            Toast.makeText(this, value, Toast.LENGTH_LONG).show()
            return
        }

        deviceData.add(mapOf("device_name1" to "", "device_name2" to ""))
        1
        pairedBluetoothDevices.forEach { device ->
            deviceData.add(mapOf("device_name1" to device.name.orEmpty(), "device_name2" to device.address.orEmpty()))
        }

        val from = arrayOf("device_name1")
        val to = intArrayOf(R.id.item_name)
        val simpleAdapter = SimpleAdapter(this@MainActivity, deviceData, R.layout.item_list, from, to)

        listViewBluetooth.adapter = simpleAdapter
        simpleAdapter.notifyDataSetInvalidated()

        listViewBluetooth.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val string = simpleAdapter.getItem(position) as HashMap<String, String>
            binding.deviceName.text = string["device_name1"]
            dialog.dismiss()
        }

        dialog.show()
    }
}

