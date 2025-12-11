package com.sundhar.doomguard

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class DoomguardService : AccessibilityService() {
    private val toastHandler = Handler(Looper.getMainLooper())
    val TAG = "DoomguardService"
    override fun onServiceConnected() {

        super.onServiceConnected()
        Log.d(TAG, "service started")    // Debug

    }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val isSwitchOn = sharedPreferences.getBoolean("switchState", true)
            if(!isSwitchOn) {
                return
            }
            val rootNode = rootInActiveWindow ?: return;
            when(event.packageName) {
                "com.google.android.youtube" -> {
                    val ytShortsIdentifier = "com.google.android.youtube:id/reel_scrim_shorts_while_top";
                    if(isElementWithResourceIdPresent(rootNode, ytShortsIdentifier)) {
                        Log.d(TAG, "Is YT Shorts")
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                    } else {
                        Log.d(TAG, "Not YT Shorts")
                    }
                }
                "com.instagram.android" -> {
                    val igReelsIdentifier = "com.instagram.android:id/clips_ufi_more_button_component"
                    if(isElementWithResourceIdPresent(rootNode, igReelsIdentifier)) {
                        Log.d(TAG, "Is IG Reel")
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                    } else {
                        Log.d(TAG, "Not IG reel")
                    }
                }

                else -> {
                    Log.d(TAG, "Not known package")
                }
            }
        }
    }

    private fun isElementWithResourceIdPresent(rootNode: AccessibilityNodeInfo, resourceId: String): Boolean {
        // Traverse the accessibility hierarchy to find the element with the specified record ID
        return findNodeById(rootNode, resourceId ) != null
    }

    private fun findNodeById(parentNode: AccessibilityNodeInfo, resourceId: String): AccessibilityNodeInfo? {
        val nodeInfoList = parentNode.findAccessibilityNodeInfosByViewId(resourceId)
        return if (nodeInfoList.isNotEmpty()) nodeInfoList[0] else null
    }




    override fun onInterrupt() {
        TODO("Not yet implemented")
    }
}