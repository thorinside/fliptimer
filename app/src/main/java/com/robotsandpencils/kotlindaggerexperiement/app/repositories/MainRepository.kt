package com.robotsandpencils.kotlindaggerexperiement.app.repositories

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.robotsandpencils.kotlindaggerexperiement.App
import com.robotsandpencils.kotlindaggerexperiement.app.db.AppDatabase
import com.robotsandpencils.kotlindaggerexperiement.app.db.Portal
import com.robotsandpencils.kotlindaggerexperiement.app.db.PortalDao
import com.robotsandpencils.kotlindaggerexperiement.app.model.SharingInfo
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import timber.log.Timber
import java.util.*


/**
 * Simple, empty main repository.
 */

class MainRepository(val app: App, private val database: AppDatabase) {
    fun getPortalDao(): PortalDao {
        return database.portalDao()
    }

    fun getSharingInfo(): SharingInfo {
        val key = getSharingKey()

        return SharingInfo(key)
    }

    private fun getSharingKey(): String {
        val sharedPref = app.getSharedPreferences("main_preferences", Context.MODE_PRIVATE)
        val key = sharedPref.getString("sharingKey", UUID.randomUUID().toString() + "-"
                + UUID.randomUUID())

        // Save the sharing key as the default
        sharedPref.edit().putString("sharingKey", key).apply()
        return key
    }

    fun savePortalsToRemote() {

        async(CommonPool) {

            val key = getSharingKey()
            val portals = database.portalDao().getAllSync()
            val db = FirebaseFirestore.getInstance()

            portals.forEach { portal: Portal ->

                val portalDocument = HashMap<String, Any>()
                portalDocument.put("portalName", portal.portalName)
                portalDocument.put("faction", portal.faction)
                portalDocument.put("flipTime", portal.flipTime.time)

                db.collection("shared")
                        .document(key)
                        .collection("portals")
                        .document(portal.portalName)
                        .set(portalDocument)
                        .addOnFailureListener { exception ->
                            Timber.e(exception, "Didn't work.")
                        }
            }
            /*
        db.collection("shared")
                .add(shareDocument)
                .addOnSuccessListener { doc ->

                    val portals = database.portalDao().getAll().value

                    portals?.forEach {
                        doc.collection("portals")
                                .add(it)
                    }
                }
                */
        }
    }
}