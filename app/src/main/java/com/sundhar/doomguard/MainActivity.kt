package com.sundhar.doomguard

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.sundhar.doomguard.databinding.ActivityMainBinding
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var accessibilityEnabled = false

    private lateinit var chipGroup: ChipGroup
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor


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
            editor.putInt("SELECTED_CHIP_ID", -1)
            val state = if (isChecked) "ON" else "OFF"
            editor.apply()
            Toast.makeText(this, "DoomGuard is $state", Toast.LENGTH_SHORT).show()
        }

        val chip5min: Chip = findViewById(R.id.chip5min)
        val chip10min: Chip = findViewById(R.id.chip10min)
        val chip15min: Chip = findViewById(R.id.chip15mins)
        val chip20min: Chip = findViewById(R.id.chip20mins)
        val chipList = listOf(chip5min, chip10min, chip15min, chip20min)

        val durations = mapOf(
            chip5min.id to 5 * 60 * 1000L,   // 5 minutes
            chip10min.id to 10 * 60 * 1000L, // 10 minutes
            chip15min.id to 15 * 60 * 1000L, // 15 minutes
            chip20min.id to 20 * 60 * 1000L  // 20 minutes
        )

        val selectedChipId = sharedPreferences.getInt("SELECTED_CHIP_ID", -1)
        if (selectedChipId != -1) {
            chipList.find { it.id == selectedChipId }?.setChipBackgroundColorResource(R.color.selected_chip_color)
        }

        chipList.forEach { chip ->
            chip.setOnClickListener {
                // Reset all chip colors
                chipList.forEach { it.setChipBackgroundColorResource(R.color.default_chip_color) }

                // Set selected chip color
                chip.setChipBackgroundColorResource(R.color.selected_chip_color)


                // Save selected chip state
                editor.putInt("SELECTED_CHIP_ID", chip.id)
                editor.putBoolean("switchState", false)
                editor.apply()
                binding.switchButton.isChecked = false

                // Get the duration for the clicked chip
                val duration = durations[chip.id] ?: 0L

                // Create input data for WorkManager
                val inputData = Data.Builder()
                    .putLong("DURATION", duration)
                    .build()

                // Create and enqueue the WorkRequest
                val workRequest: WorkRequest = OneTimeWorkRequest.Builder(TimerWorker::class.java)
                    .setInputData(inputData)
                    .build()

                WorkManager.getInstance(this).enqueue(workRequest)
            }
        }

    }
    

    private fun isAccessibilityServiceEnabled(): Boolean {
        val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        val packageName = packageName
        return enabledServices != null && enabledServices.contains(packageName)
    }



}