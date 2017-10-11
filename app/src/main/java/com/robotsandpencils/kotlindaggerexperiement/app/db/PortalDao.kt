package com.robotsandpencils.kotlindaggerexperiement.app.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface PortalDao {

    @Query("SELECT count(*) FROM portal")
    fun getCount(): LiveData<Int>

    @Query("SELECT * FROM portal order by flip_time asc")
    fun getAll(): LiveData<List<Portal>>

    @Query("SELECT * FROM portal order by flip_time asc")
    fun getAllSync(): List<Portal>

    @Insert
    fun insert(portal: Portal)

    @Update
    fun update(portal: Portal)

    @Delete
    fun delete(portal: Portal)
}