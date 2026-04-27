package com.tatva.app.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import kotlin.math.pow

data class VictimDevice(val address: String, val rssi: Int, val distance: Double, val timestamp: Long = System.currentTimeMillis())

class TatvaBluetoothManager(private val context: Context) {
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        manager.adapter
    }

    private val scanner: BluetoothLeScanner? by lazy { bluetoothAdapter?.bluetoothLeScanner }
    private val advertiser: BluetoothLeAdvertiser? by lazy { bluetoothAdapter?.bluetoothLeAdvertiser }

    private val SERVICE_UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb") // Using Heart Rate UUID for simplicity or custom
    private val VICTIM_NAME = "TATVA_VICTIM"

    private val _detectedVictims = MutableStateFlow<Map<String, VictimDevice>>(emptyMap())
    val detectedVictims = _detectedVictims.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    private val _isAdvertising = MutableStateFlow(false)
    val isAdvertising = _isAdvertising.asStateFlow()

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val deviceName = result.device.name ?: result.scanRecord?.deviceName
            if (deviceName == VICTIM_NAME) {
                val rssi = result.rssi
                val distance = 10.0.pow((-59.0 - rssi) / 20.0)
                val victim = VictimDevice(result.device.address, rssi, distance)
                
                val currentMap = _detectedVictims.value.toMutableMap()
                currentMap[result.device.address] = victim
                _detectedVictims.value = currentMap
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startScanning() {
        if (_isScanning.value) return
        
        // Inject mock devices for demonstration
        val mockMap = mutableMapOf<String, VictimDevice>()
        mockMap["MOCK_1"] = VictimDevice("Alpha-Node", -65, 4.2)
        mockMap["MOCK_2"] = VictimDevice("Beta-Node", -78, 12.5)
        _detectedVictims.value = mockMap

        val filter = ScanFilter.Builder().setDeviceName(VICTIM_NAME).build()
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        scanner?.startScan(listOf(filter), settings, scanCallback)
        _isScanning.value = true
    }

    @SuppressLint("MissingPermission")
    fun stopScanning() {
        scanner?.stopScan(scanCallback)
        _isScanning.value = false
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            _isAdvertising.value = true
        }
        override fun onStartFailure(errorCode: Int) {
            _isAdvertising.value = false
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isWhisperModeActive = false

    @SuppressLint("MissingPermission")
    fun startAdvertising() {
        if (isWhisperModeActive) return
        isWhisperModeActive = true
        whisperCycle()
    }

    @SuppressLint("MissingPermission")
    private fun whisperCycle() {
        if (!isWhisperModeActive) return

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setConnectable(false)
            .setTimeout(0)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .build()

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .addServiceUuid(ParcelUuid(SERVICE_UUID))
            .build()

        advertiser?.startAdvertising(settings, data, advertiseCallback)
        
        // Active for 2s, sleep for 58s
        handler.postDelayed({
            stopAdvertisingInternal()
            handler.postDelayed({
                whisperCycle()
            }, 58000)
        }, 2000)
    }

    @SuppressLint("MissingPermission")
    private fun stopAdvertisingInternal() {
        advertiser?.stopAdvertising(advertiseCallback)
        _isAdvertising.value = false
    }

    @SuppressLint("MissingPermission")
    fun stopAdvertising() {
        isWhisperModeActive = false
        handler.removeCallbacksAndMessages(null)
        stopAdvertisingInternal()
    }
}
