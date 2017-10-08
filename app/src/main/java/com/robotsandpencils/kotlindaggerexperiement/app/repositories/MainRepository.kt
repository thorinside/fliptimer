package com.robotsandpencils.kotlindaggerexperiement.app.repositories

import com.robotsandpencils.kotlindaggerexperiement.App
import com.robotsandpencils.kotlindaggerexperiement.app.db.AppDatabase
import com.robotsandpencils.kotlindaggerexperiement.app.db.PortalDao

/**
 * Simple, empty main repository.
 */

class MainRepository(val app: App, private val database: AppDatabase) {
    fun getPortalDao() : PortalDao {
        return database.portalDao()
    }
}