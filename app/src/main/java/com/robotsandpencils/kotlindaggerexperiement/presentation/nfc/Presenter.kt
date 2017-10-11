package com.robotsandpencils.kotlindaggerexperiement.presentation.nfc

import com.google.gson.Gson
import com.robotsandpencils.kotlindaggerexperiement.app.repositories.MainRepository
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.BasePresenter
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.UiThreadQueue

class Presenter(private val mainRepository: MainRepository, uiThreadQueue: UiThreadQueue) :
        BasePresenter<Contract.View>(uiThreadQueue), Contract.Presenter {

    override fun generateShareJson(): String {
        return Gson().toJson(mainRepository.getSharingInfo())
    }
}