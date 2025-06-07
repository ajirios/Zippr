package app.toll.toll

import android.app.Activity
import android.content.Context
import com.vmadalin.easypermissions.EasyPermissions
import app.toll.toll.Constants.PERMISSION_LOCATION_REQUEST_CODE

object Permissions {
    fun hasLocationPermission(context: Context) =
        EasyPermissions.hasPermissions(context, android.Manifest.permission.ACCESS_FINE_LOCATION);


    fun requestLocationPermission(activity: Activity) {
        EasyPermissions.requestPermissions(activity, "This application cannot work without location permission.", PERMISSION_LOCATION_REQUEST_CODE, android.Manifest.permission.ACCESS_FINE_LOCATION);
    }
}