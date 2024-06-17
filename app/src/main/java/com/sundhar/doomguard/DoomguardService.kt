package com.sundhar.doomguard

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.core.view.KeyEventDispatcher.dispatchKeyEvent

class DoomguardService : AccessibilityService() {
    private val toastHandler = Handler(Looper.getMainLooper())
    val TAG = "DoomguardService"
    override fun onServiceConnected() {

        super.onServiceConnected()
        Log.d(TAG, "service started")    // Debug

    }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
//            Log.d(TAG, "Received ${event.packageName} event: ${event.eventType}")
            when(event.packageName) {
                "com.google.android.youtube" -> {
                    var YTShortsIdentifier = "com.google.android.youtube:id/reel_scrim_shorts_while_top";
                    if(isElementWithResourceIdPresent(rootInActiveWindow, YTShortsIdentifier)) {
                        Log.d(TAG, "Is YT Shorts")
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                    } else {
//                        Log.d(TAG, "Not YT Shorts")
                    }
                }
                "com.instagram.android" -> {
                    var InstaReelIdentifier = "com.instagram.android:id/reels_ufi_more_button_component"
                    if(isElementWithResourceIdPresent(rootInActiveWindow, InstaReelIdentifier)) {
                        Log.d(TAG, "Is Insta Reel")
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                    } else {
//                        Log.d(TAG, "Not YT Shorts")
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