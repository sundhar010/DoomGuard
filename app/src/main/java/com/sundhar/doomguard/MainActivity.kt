package com.sundhar.doomguard

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import com.sundhar.doomguard.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var accessibilityEnabled = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        accessibilityEnabled = isAccessibilityServiceEnabled()
        if(!accessibilityEnabled) {
            startActivity(Intent(this, InstructionsActivity::class.java))
            finish()
        }


        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Load and set the default switch button state
        val isSwitchOn = sharedPreferences.getBoolean("switchState", true)
        binding.switchButton.isChecked = isSwitchOn
        binding.switchButton.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("switchState", isChecked)
            val state = if (isChecked) "ON" else "OFF"
            editor.apply()
            Toast.makeText(this, "DoomGuard is $state", Toast.LENGTH_SHORT).show()
        }

    }
    

    private fun isAccessibilityServiceEnabled(): Boolean {
        val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        val packageName = packageName
        return enabledServices != null && enabledServices.contains(packageName)
    }



}