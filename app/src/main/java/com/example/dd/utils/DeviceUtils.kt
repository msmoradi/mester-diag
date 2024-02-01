package com.example.dd.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.example.dd.model.DeviceInfoModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.FileReader
import javax.inject.Inject

class DeviceUtils @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private fun getBatteryHealth(): String {

        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, intentFilter)
        return when (batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            else -> "Unknown"
        }
    }

    private fun getAvailableMemory(): Long {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.availMem
    }

    private fun getTotalMemory(): Long {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)
            return memoryInfo.totalMem
        } else {
            // Fallback method for older devices; requires reading from /proc/meminfo
            val memInfo = "/proc/meminfo"
            val reader = FileReader(memInfo)
            val buffer = BufferedReader(reader, 8192)
            val memoryLine = buffer.readLine()
            val split = memoryLine.split("\\s+".toRegex())
            buffer.close()
            // Total memory in kilobytes
            return split[1].toLong() * 1024
        }
    }

    suspend fun collectDeviceInfo(): DeviceInfoModel = withContext(Dispatchers.IO) {
        DeviceInfoModel(
            batteryHealth = getBatteryHealth(),
            availableMemory = getAvailableMemory(),
            totalMemory = getTotalMemory(),
            deviceModel = Build.MODEL,
            deviceBrand = Build.BRAND,
            deviceBoard = Build.BOARD,
            deviceManufacturer = Build.MANUFACTURER,
            deviceProduct = Build.PRODUCT,
            deviceHardware = Build.HARDWARE,
            osVersion = Build.VERSION.RELEASE,
        )
    }

    suspend fun checkPort(): Boolean = withContext(Dispatchers.IO) {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, intentFilter)
        when (batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0) {
            1, 2 -> true
            else -> false
        }
    }

}
