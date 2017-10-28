package com.robotsandpencils.kotlindaggerexperiement.app.modules

import android.app.AlarmManager
import android.content.Context
import com.robotsandpencils.kotlindaggerexperiement.App
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
    fun provideMainRepository() = MainRepository(app)

    @Provides
    @Singleton
    fun provideAlarmManager(app: App) = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}