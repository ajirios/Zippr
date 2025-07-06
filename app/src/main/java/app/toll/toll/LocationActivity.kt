package com.zippr.zippr

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import com.zippr.zippr.Permissions.hasLocationPermission
import com.zippr.zippr.Permissions.requestLocationPermission
import com.zippr.zippr.R
import com.zippr.zippr.databinding.ActivityLocationBinding

class LocationActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    lateinit var locationBinding: ActivityLocationBinding;

    var spinnerOptions = mutableListOf<String>("NG", "UK", "CA", "CL", "CN", "US");

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        locationBinding = ActivityLocationBinding.inflate(layoutInflater);
        setContentView(locationBinding.root);
        if (!hasLocationPermission(this)) {
            requestLocationPermission(this);
        }


        window?.apply {
            // Set status bar background to white
            statusBarColor = android.graphics.Color.WHITE

            // Set light status bar icons (dark text/icons)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        val adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, spinnerOptions);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        val spinner = findViewById<Spinner>(R.id.phoneCodeSpinner);
        if (spinner != null) {
            spinner.adapter = adapter
        };
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
        val intent = Intent(this, LanguageActivity::class.java);
        startActivity(intent);
    }

}