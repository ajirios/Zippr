package app.toll.toll

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import app.toll.toll.Permissions.hasLocationPermission
import app.toll.toll.Permissions.requestLocationPermission
import app.toll.toll.databinding.ActivityLocationBinding

class LocationActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    lateinit var locationBinding: ActivityLocationBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        locationBinding = ActivityLocationBinding.inflate(layoutInflater);
        setContentView(locationBinding.root);
        if (!hasLocationPermission(this)) {
            requestLocationPermission(this);
        }


        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                // Makes the status bar completely transparent
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                statusBarColor = Color.TRANSPARENT
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(this).build().show();
        }
        else {
            requestLocationPermission(this);
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(this, "Granted!", Toast.LENGTH_SHORT).show();
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    fun onClickLocationProceed(view: View) {
        val intent = Intent(this, NavigationActivity::class.java);
        startActivity(intent);
    }

}