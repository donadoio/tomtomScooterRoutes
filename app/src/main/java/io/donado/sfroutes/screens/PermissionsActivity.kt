package io.donado.sfroutes.screens

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.donado.sfroutes.PermissionsHelper
import io.donado.sfroutes.databinding.ActivityPermissionsBinding

class PermissionsActivity: AppCompatActivity() {
    private lateinit var binding: ActivityPermissionsBinding;
    private lateinit var permissionsHelper: PermissionsHelper;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionsHelper = PermissionsHelper(this)


        binding.imageBack.setOnClickListener { finish() }
        binding.checkPermissionButton.setOnClickListener({ onCheckPermissionClick() })
        //binding.grantPermissionButton.setOnClickListener({ ensureForegroundLocationPermission() })

    }

    private fun onCheckPermissionClick() {
        if (permissionsHelper.areLocationPermissionsGranted()) {
            AlertDialog.Builder(this)
                .setTitle("Permission is OK")
                .setMessage("You have given permission successfully.")
                .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
                .show()
        } else {
            AlertDialog.Builder(this)
                .setTitle("UH OH")
                .setMessage("You have no given the app permission yet.")
                .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
                .show()
        }
    }
}