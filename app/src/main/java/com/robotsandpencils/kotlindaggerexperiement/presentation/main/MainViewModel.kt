package com.robotsandpencils.kotlindaggerexperiement.presentation.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.robotsandpencils.kotlindaggerexperiement.app.db.Portal


class MainViewModel : ViewModel() {
    lateinit var portals: LiveData<List<Portal>>
}