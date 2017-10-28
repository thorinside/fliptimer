package com.robotsandpencils.kotlindaggerexperiement.app.db

import android.arch.lifecycle.LiveData

interface PortalDao {
    fun getAll(): LiveData<List<Portal>>
    fun insert(portal: Portal)
    fun update(portal: Portal)
    fun delete(portal: Portal)
}