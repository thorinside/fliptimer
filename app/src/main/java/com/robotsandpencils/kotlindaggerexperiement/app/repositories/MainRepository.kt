package com.robotsandpencils.kotlindaggerexperiement.app.repositories

import android.content.Context
import com.robotsandpencils.kotlindaggerexperiement.App
import com.robotsandpencils.kotlindaggerexperiement.app.db.AppDatabase
import com.robotsandpencils.kotlindaggerexperiement.app.db.FirebasePortalDao
import com.robotsandpencils.kotlindaggerexperiement.app.db.PortalDao
import com.robotsandpencils.kotlindaggerexperiement.app.model.SharingInfo
import java.util.*


/**
 * Simple, empty main repository.
 */

class MainRepository(val app: App, private val database: AppDatabase) {

    val portalDao: PortalDao = FirebasePortalDao(sharingKey)

    fun getSharingInfo(): SharingInfo {
        val key = sharingKey

        return SharingInfo(key)
    }

    private val sharingKey: String
        get() {
            val sharedPref = app.getSharedPreferences("main_preferences", Context.MODE_PRIVATE)
            if (sharedPref.contains("pairedKey")) {
                val key = sharedPref.getString("pairedKey", null)
                if (key != null) {
                    return key
                }
            }
            val key = sharedPref.getString("sharingKey", UUID.randomUUID().toString() + "-"
                    + UUID.randomUUID())

            // Save the sharing key as the default
            sharedPref.edit().putString("sharingKey", key).apply()
            return key
        }

    fun pair(pairingKey: String) {
        val sharedPref = app.getSharedPreferences("main_preferences", Context.MODE_PRIVATE)
        sharedPref.edit().putString("pairedKey", pairingKey).apply()

        (portalDao as FirebasePortalDao).sharedDatabaseKey = sharingKey
    }

    fun unpair() {
        val sharedPref = app.getSharedPreferences("main_preferences", Context.MODE_PRIVATE)
        sharedPref.edit().remove("pairedKey").apply()

        (portalDao as FirebasePortalDao).sharedDatabaseKey = sharingKey
    }
}