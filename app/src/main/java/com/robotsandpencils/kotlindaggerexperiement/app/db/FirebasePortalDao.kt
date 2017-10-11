package com.robotsandpencils.kotlindaggerexperiement.app.db

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import timber.log.Timber
import java.util.*

/**
 * A Dao implementation that uses Firebase to store the Portal records.
 */

class FirebasePortalDao(sharedDatabaseKey: String) : PortalDao {

    var sharedDatabaseKey = sharedDatabaseKey
        set(value) {
            field = value
            listener.remove()
            attachQuery(sharedDatabaseKey)
        }

    private val db = FirebaseFirestore.getInstance()
    private val all = MutableLiveData<List<Portal>>()
    private lateinit var listener: ListenerRegistration

    init {
        attachQuery(sharedDatabaseKey)
    }

    private fun attachQuery(sharedDatabaseKey: String) {
        listener = db.collection("shared")
                .document(sharedDatabaseKey)
                .collection("portals")
                .orderBy("flipTime")
                .addSnapshotListener { querySnapshot, exception ->
                    if (exception != null) {
                        Timber.e(exception)
                        return@addSnapshotListener
                    }
                    val portals = ArrayList<Portal>()
                    querySnapshot.forEach {
                        val portal = Portal(it.get("portalName") as String,
                                Date(it.get("flipTime") as Long),
                                (it.get("faction") as Long).toInt())
                        portals.add(portal)
                    }

                    all.value = portals
                }
    }

    override fun getAll(): LiveData<List<Portal>> = all

    override fun insert(portal: Portal) {

        val portalDocument = HashMap<String, Any>()
        portalDocument.put("portalName", portal.portalName)
        portalDocument.put("faction", portal.faction)
        portalDocument.put("flipTime", portal.flipTime.time)

        db.collection("shared")
                .document(sharedDatabaseKey)
                .collection("portals")
                .document(portal.portalName)
                .set(portalDocument)
                .addOnFailureListener { exception ->
                    Timber.e(exception, "Didn't work.")
                }

    }

    override fun update(portal: Portal) {
        insert(portal)
    }

    override fun delete(portal: Portal) {
        db.collection("shared")
                .document(sharedDatabaseKey)
                .collection("portals")
                .document(portal.portalName)
                .delete()
                .addOnFailureListener { exception ->
                    Timber.e(exception, "Didn't work.")
                }
    }

}