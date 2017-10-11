package com.robotsandpencils.kotlindaggerexperiement.app.modules

import com.robotsandpencils.kotlindaggerexperiement.App
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Main App Component
 */
@Singleton
@Component(modules = arrayOf(AndroidSupportInjectionModule::class, AppModule::class,
        com.robotsandpencils.kotlindaggerexperiement.presentation.main.module.Module::class,
        com.robotsandpencils.kotlindaggerexperiement.presentation.nfc.module.Module::class))
interface AppComponent {
    fun inject(app: App)
}