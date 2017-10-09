package com.robotsandpencils.kotlindaggerexperiement.presentation.main.module

import android.app.AlarmManager
import com.robotsandpencils.kotlindaggerexperiement.app.repositories.MainRepository
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.LifecycleAwareUiThreadQueue
import com.robotsandpencils.kotlindaggerexperiement.presentation.main.Contract
import com.robotsandpencils.kotlindaggerexperiement.presentation.main.MainActivity
import com.robotsandpencils.kotlindaggerexperiement.presentation.main.Presenter
import dagger.Module
import dagger.Provides

// This Kotlin file construction seems odd, but follows from this:
// https://stackoverflow.com/questions/44075860/module-must-be-set
@Module
internal abstract class PresenterModule {
    @Module
    companion object {
        @Provides
        @Scope
        @JvmStatic internal fun providesPresenter(activity: MainActivity, mainRepository: MainRepository, alarmManager: AlarmManager): Contract.Presenter {
            return Presenter(mainRepository, LifecycleAwareUiThreadQueue(activity), alarmManager)
        }
    }
}
