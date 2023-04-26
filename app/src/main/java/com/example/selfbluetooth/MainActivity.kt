package com.example.selfbluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.selfbluetooth.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun btScan() {
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager.adapter

        if(bluetoothAdapter == null) {
            Toast.makeText(this, "BT接続が許可されていません", Toast.LENGTH_LONG).show()
            return
        }

        // Bluetooth接続が許可されていたら
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // アプリがすでにペア設定されている Bluetooth デバイスと通信する場合は、アプリのマニフェストに BLUETOOTH_CONNECT 権限を追加


        } else {

        }

    }

    /** Bluetoothアクセス要求許可をするためのオブジェクト launchでよばれたときにコールバックを返す・・・ **/
    val blueToothPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if(isGranted) {
            val bluetoothManager = getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter = bluetoothManager.adapter

            // もしBluetoothが有効になっていなかったら
            if (bluetoothAdapter?.isEnabled == false) {
                //ACTION_REQUEST_ENABLE → アクティビティ アクション: ユーザーが Bluetooth をオンにできるようにするシステム アクティビティを表示する
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                btActivityResultLauncher.launch(enableBtIntent)


            }


        }

    }

    private val btActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

            // Bluetooth有効化が成功した場合
            if (it.resultCode==RESULT_OK) {
                checkBluetoothState()
            }
        }

    @SuppressLint("MissingPermission")
    private fun checkBluetoothState() {
        // BluetoothManagerオブジェクトを取得
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        // BluetoothAdapterオブジェクトを取得
        val bluetoothAdapter = bluetoothManager.adapter

        // Bluetoothが有効になっていない場合は有効化を要求するダイアログを表示する
        if (bluetoothAdapter?.isEnabled == false) {
            // Bluetoothを有効にするダイアログを表示するためのIntentを作成
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            // Bluetoothを有効にするダイアログを表示するためのアクティビティを起動する
            btActivityResultLauncher.launch(enableBtIntent)
        } else {
            // Bluetoothが有効になっている場合はBluetoothデバイスのスキャンを開始する
            scanBluetoothDevice()
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanBluetoothDevice() {
        // BluetoothManagerを取得
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        // BluetoothAdapterを取得
        val bluetoothAdapter = bluetoothManager.adapter

        // Bluetoothデバイスの一覧を表示するためのダイアログを作成
        val builder = AlertDialog.Builder(this@MainActivity)
        // ダイアログ用のレイアウトを設定
        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.scan_bt, null)
        // ダイアログのキャンセルを禁止
        builder.setCancelable(false)
        // ダイアログにレイアウトを設定
        builder.setView(dialogView)
        // Bluetoothデバイス一覧表示用のListViewを取得
        val bluetoothList = dialogView.findViewById<ListView>(R.id.bt_lst)
        // ダイアログを作成
        val dialog = builder.create()

        // ペアリング済みのBluetoothデバイス一覧を取得
        val pairedDevices = bluetoothAdapter?.bondedDevices

        // Bluetoothデバイス一覧を格納するための空のListを作成
        val data = mutableListOf<Map<String, String>>()

        // ペアリング済みのデバイスが存在しない場合、メッセージを表示して処理を終了
        if (pairedDevices.isNullOrEmpty()) {
            val value = "デバイスが見つかりません"
            Toast.makeText(this, value, Toast.LENGTH_LONG).show()
            return
        }
    }


}
