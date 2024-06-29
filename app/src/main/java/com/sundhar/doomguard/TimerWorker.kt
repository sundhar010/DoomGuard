package com.sundhar.doomguard
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.content.SharedPreferences

class TimerWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Retrieve the duration from input data
        val duration = inputData.getLong("DURATION", 0)

        // Sleep for the specified duration
        Thread.sleep(duration)

        // Update SharedPreferences to set switchState to true
        val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("switchState", true)
        editor.putInt("SELECTED_CHIP_ID", -1)
        editor.apply()

        return Result.success()
    }
}
