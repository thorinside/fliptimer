package com.robotsandpencils.kotlindaggerexperiement.presentation.main

import android.app.TimePickerDialog
import android.util.Log
import com.robotsandpencils.kotlindaggerexperiement.app.db.Portal
import com.robotsandpencils.kotlindaggerexperiement.app.repositories.MainRepository
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.BasePresenter
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.UiThreadQueue
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.util.*

/**
 * A super simple presenter
 */

class Presenter(private val mainRepository: MainRepository, uiThreadQueue: UiThreadQueue) :
        BasePresenter<Contract.View>(uiThreadQueue), Contract.Presenter {

    override fun attach(view: Contract.View) {
        super.attach(view)

        view.setTitle("")

        val viewModel = view.getViewModel()
        viewModel.portals = mainRepository.getPortalDao().getAll()
    }

    override fun addPortal(portalName: String, faction: Int) {
        // Use Coroutines to rn this in the background and then do something on the UI
        // thread if successful.
        val deferred = async(CommonPool) {
            mainRepository.getPortalDao().insert(Portal(portalName, Date(), faction))
            uiThreadQueue.run(Runnable {
                view?.clearFields()
            })
        }

        // This will be called back when done, and if there is an error, throwable will be set
        deferred.invokeOnCompletion { throwable ->
            if (throwable != null) {
                Log.e("DB", "Unable to save: ${Thread.currentThread().name}", throwable)

                uiThreadQueue.run(Runnable {
                    view?.showError(throwable.message)
                })
            }
        }
    }

    override fun removePortal(portal: Portal) : Boolean {
        async(CommonPool) {
            mainRepository.getPortalDao().delete(portal)
        }
        return true
    }

    override fun flipPortal(portal: Portal) {
        async(CommonPool) {
            portal.flipTime = Date()
            if (portal.faction == Portal.FACTION_ENLIGHTENED) {
                portal.faction = Portal.FACTION_RESISTANCE
            } else {
                portal.faction = Portal.FACTION_ENLIGHTENED
            }
            mainRepository.getPortalDao().update(portal)
        }
    }

    override fun editPortalTime(portal: Portal) {
        view?.showTimePickerDialog(portal)
    }

    override fun setFlipTime(portal: Portal, hourOfDay: Int, minute: Int) {
        val c = Calendar.getInstance()
        c.time = Date()
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)
        async(CommonPool) {
            portal.flipTime = c.time
            mainRepository.getPortalDao().update(portal)
        }
    }
}
