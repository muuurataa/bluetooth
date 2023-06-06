package com.example.selfbluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {
    // 定数
    private static final int REQUEST_ENABLEBLUETOOTH = 1; // Bluetooth機能の有効化要求時の識別コード
    private static final int REQUEST_CONNECTDEVICE = 2; // デバイス接続要求時の識別コード

    // メンバー変数
    private BluetoothAdapter mBluetoothAdapter;    // BluetoothAdapter : Bluetooth処理で必要
    private String mDeviceAddress = "";    // デバイスアドレス


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bluetoothアダプタの取得
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (null == mBluetoothAdapter) {    // Android端末がBluetoothをサポートしていない
            Toast.makeText(this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT).show();
            finish();    // アプリ終了宣言
            return;
        }
    }

    // 初回表示時、および、ポーズからの復帰時
    @Override
    protected void onResume() {
        super.onResume();

        // Android端末のBluetooth機能の有効化要求
        requestBluetoothFeature();
    }

    // Android端末のBluetooth機能の有効化要求
    private void requestBluetoothFeature() {
        if (mBluetoothAdapter.isEnabled()) {
            return;
        }
        //デバイスのBluetooth機能が有効になっていない時は、有効化要求
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            startActivityForResult(enableBtIntent, REQUEST_ENABLEBLUETOOTH);
            return;
        }

    }

    // 機能の有効化ダイアログの操作結果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLEBLUETOOTH: // Bluetooth有効化要求
                if (Activity.RESULT_CANCELED == resultCode) {    // 有効にされなかった
                    Toast.makeText(this, R.string.bluetooth_is_not_working, Toast.LENGTH_SHORT).show();
                    finish();    // アプリ終了宣言
                    return;
                }
                break;
            case REQUEST_CONNECTDEVICE: // デバイス接続要求
                String strDeviceName;
                if (Activity.RESULT_OK == resultCode) {
                    // デバイスリストアクティビティからの情報の取得
                    strDeviceName = data.getStringExtra(DeviceListActivity.EXTRAS_DEVICE_NAME);
                    mDeviceAddress = data.getStringExtra(DeviceListActivity.EXTRAS_DEVICE_ADDRESS);
                } else {
                    strDeviceName = "";
                    mDeviceAddress = "";
                }
                ((TextView) findViewById(R.id.textview_devicename)).setText(strDeviceName);
                ((TextView) findViewById(R.id.textview_deviceaddress)).setText(mDeviceAddress);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // オプションメニュー作成時の処理
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    // オプションメニューのアイテム選択時の処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_search:
                Intent devicelistactivityIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(devicelistactivityIntent, REQUEST_CONNECTDEVICE);
                return true;
        }
        return false;
    }

}