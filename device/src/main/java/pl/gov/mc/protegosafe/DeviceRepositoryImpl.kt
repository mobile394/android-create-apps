package pl.gov.mc.protegosafe

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import pl.gov.mc.protegosafe.domain.repository.DeviceRepository
import pl.gov.mc.protegosafe.domain.repository.OpenTraceRepository
import pl.gov.mc.protegosafe.model.ServicesStatus
import pl.gov.mc.protegosafe.model.ServicesStatusRoot
import pub.devrel.easypermissions.EasyPermissions

class DeviceRepositoryImpl(
    private val context: Context,
    private val openTraceRepository: OpenTraceRepository
) : DeviceRepository {

    //TODO: prepare broadcast receiver to track service status changes
    private  val traceServiceEnabledSubject: BehaviorSubject<Boolean> =
        BehaviorSubject.createDefault(false)

    override val traceServiceEnabled: Observable<Boolean> = traceServiceEnabledSubject.hide()

    override fun isBtSupported(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }

    override fun isLocationEnabled(): Boolean {
        return EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun isBtOn(): Boolean {
        return (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)
            ?.adapter
            ?.isEnabled == true
    }

    override fun isBatteryOptimizationOn(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (context.getSystemService(AppCompatActivity.POWER_SERVICE) as? PowerManager)
                ?.isIgnoringBatteryOptimizations(context.packageName) == true
        } else {
            true
        }
    }

    override fun isNotificationEnabled(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    override fun isBtServiceOn(): Boolean {
        return openTraceRepository.getBLEServiceStatus()
    }

    override fun getServicesStatusJson(): String {
        val servicesStatus = ServicesStatusRoot(
            ServicesStatus(
                isBtSupported = isBtSupported(),
                isLocationEnabled = isLocationEnabled(),
                isBtOn = isBtOn(),
                isBatteryOptimizationOn = isBatteryOptimizationOn(),
                isNotificationEnabled = isNotificationEnabled(),
                isBtServiceOn = isBtServiceOn()
            )
        )
        return Gson().toJson(servicesStatus)
    }
}