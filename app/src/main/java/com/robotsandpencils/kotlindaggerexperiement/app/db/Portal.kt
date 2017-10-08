package com.robotsandpencils.kotlindaggerexperiement.app.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity data class Portal(
        @PrimaryKey val portalName: String,
        @ColumnInfo(name = "flip_time") var flipTime: Date,
        @ColumnInfo(name = "faction") var faction: Int) {
    companion object {
        val FACTION_ENLIGHTENED = 0
        val FACTION_RESISTANCE = 1
    }
}
