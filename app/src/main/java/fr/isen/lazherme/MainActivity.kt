package fr.isen.lazherme

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.BluetoothGattCharacteristic.PERMISSION_WRITE
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.common.util.Hex
import fr.isen.lazherme.R
import fr.isen.lazherme.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var userKey : String
    private lateinit var userEmail : String
    lateinit var swipeContainer: SwipeRefreshLayout
    private var isScanning = false
    private val listeBle = ArrayList<ScanResult>()
    private var bluetoothGatt : BluetoothGatt? = null
    private lateinit var bleCar : BluetoothGattCharacteristic

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        userKey = intent.getStringExtra("uid").toString()
        userEmail = intent.getStringExtra("email").toString()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bleScanList.layoutManager = LinearLayoutManager(this)
        binding.bleScanList.adapter = BleAdapter(listeBle){
            Log.w("ScanResultAdapter", "Connecting to ${it.address}")
            //it.connectGatt(this, false, gattCallback)
            connectToDevice(this,it)
        }
        supportActionBar?.hide()
        swipeContainer = findViewById(R.id.swipeContainer)
        when{
            bluetoothAdapter?.isEnabled == true ->{
                swipeContainer.setOnRefreshListener {
                    onRefresh()
                }
            }
            bluetoothAdapter != null ->
                askBluetoothPermission()
            else -> {
                displayBLEUnAvailable()
            }
        }
        binding.bleScanImg.setOnClickListener {
            startLeScanBLEWithPermission(!isScanning)
        }
        binding.bleScanText.setOnClickListener {
            startLeScanBLEWithPermission(!isScanning)
        }
    }

    private fun connectToDevice(context: Context,device:BluetoothDevice) {
        val intent = Intent(context,BluetoothService::class.java)
        intent.putExtra("idServ","0")
        intent.putExtra(ITEM_KEY, device)
        intent.putExtra("address",userEmail)
        intent.putExtra("uid",userKey)
        startService(intent)
    }

    fun onRefresh(){
        binding.texteSwipe.isVisible = false
        startLeScanBLEWithPermission(true)
        val handler = Handler()
        handler.postDelayed(Runnable {
            if (swipeContainer.isRefreshing()) {
                swipeContainer.setRefreshing(false)
                startLeScanBLEWithPermission(false)
            }
        }, 3000)
        binding.texteSwipe.isVisible = listeBle.isEmpty()
    }
    override fun onStop() {
        super.onStop()
        startLeScanBLEWithPermission(false)
    }
    private fun startLeScanBLEWithPermission(enable : Boolean){
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )==PackageManager.PERMISSION_GRANTED
        ){
            startLeScanBLE(enable)
        }else{
            ActivityCompat.requestPermissions(this, getAllPermissions(), ALL_PERMISSION_REQUEST_CODE)
        }
    }
    @SuppressLint("MissingPermission")
    private fun startLeScanBLE(enable : Boolean) {
        bluetoothAdapter?.bluetoothLeScanner?.apply {
            if (enable){
                isScanning = true
                startScan(scanCallback)
            }else{
                isScanning = false
                stopScan(scanCallback)
            }
            handlePlayPause()
        }
    }
    private fun checkAllPermissionGranted():Boolean{
        return getAllPermissions().all{ permission ->
            ActivityCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    private fun getAllPermissions(): Array<String> {
        return if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT
            )
        }else{
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }
    private fun displayBLEUnAvailable() {
        binding.bleScanImg.isVisible = false
        binding.bleScanText.text=getString(R.string.ble_scan_error)
    }
    private fun askBluetoothPermission(){
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ){
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }
    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d("BLEScanActivity","result : ${result.device.address}, rssi : ${result.rssi},nom : ${result.device.name}")
            (binding.bleScanList.adapter as BleAdapter).apply {
                addToList(result)
                notifyDataSetChanged()
            }
        }
    }
    private fun handlePlayPause(){
        if (isScanning){
            binding.bleScanImg.setImageResource(R.drawable.ic_baseline_pause_24)
            binding.bleScanText.text=getString(R.string.ble_scan_pause)
        }else{
            binding.bleScanImg.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            binding.bleScanText.text=getString(R.string.ble_scan_play)
        }
    }
    companion object {
        val DEVICE_KEY ="device_key"
        val ITEM_KEY ="item_key"
        private const val ALL_PERMISSION_REQUEST_CODE = 1
        private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1

    }
}