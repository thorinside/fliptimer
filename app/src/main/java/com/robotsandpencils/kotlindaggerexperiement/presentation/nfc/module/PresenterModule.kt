package com.robotsandpencils.kotlindaggerexperiement.presentation.nfc.module

import com.robotsandpencils.kotlindaggerexperiement.app.repositories.MainRepository
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.LifecycleAwareUiThreadQueue
import com.robotsandpencils.kotlindaggerexperiement.presentation.nfc.Contract
import com.robotsandpencils.kotlindaggerexperiement.presentation.nfc.NfcActivity
import com.robotsandpencils.kotlindaggerexperiement.presentation.nfc.Presenter
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
        @JvmStatic internal fun providesPresenter(activity: NfcActivity, mainRepository: MainRepository): Contract.Presenter {
            return Presenter(mainRepository, LifecycleAwareUiThreadQueue(activity))
        }
    }
}
