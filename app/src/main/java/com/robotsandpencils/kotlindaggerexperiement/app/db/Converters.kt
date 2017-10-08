package com.robotsandpencils.kotlindaggerexperiement.app.db

import android.arch.persistence.room.TypeConverter
import java.util.*

class Converters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromTimestamp(value: Long): Date {
            return Date(value)
        }

        @TypeConverter
        @JvmStatic
        fun dateToTimestamp(date: Date): Long {
            return date.time
        }
    }
}