package com.robotsandpencils.kotlindaggerexperiement.app.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

/**
 * Created by nealsanche on 2017-09-08.
 */

@Database(entities = arrayOf(Portal::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun portalDao() : PortalDao
}