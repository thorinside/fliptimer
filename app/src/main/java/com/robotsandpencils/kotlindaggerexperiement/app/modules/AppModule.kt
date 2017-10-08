package com.robotsandpencils.kotlindaggerexperiement.app.modules

import android.arch.persistence.room.Room
import com.robotsandpencils.kotlindaggerexperiement.App
import com.robotsandpencils.kotlindaggerexperiement.app.db.AppDatabase
import com.robotsandpencils.kotlindaggerexperiement.app.repositories.MainRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * App Module
 */

@Module
class AppModule(val app: App) {
    @Provides
    @Singleton
    fun provideApp() = app

    @Provides
    @Singleton
    fun provideAppDatabase(app: App): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "database-name").build()
    }

    @Provides
    @Singleton
    fun provideMainRepository(database: AppDatabase) = MainRepository(app, database)
}