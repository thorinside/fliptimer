package com.robotsandpencils.kotlindaggerexperiement.presentation.main

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.robotsandpencils.kotlindaggerexperiement.app.db.Portal
import com.robotsandpencils.kotlindaggerexperiement.app.repositories.MainRepository
import com.robotsandpencils.kotlindaggerexperiement.app.services.NotificationPublisher
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.BasePresenter
import com.robotsandpencils.kotlindaggerexperiement.presentation.base.UiThreadQueue
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A super simple presenter
 */

class Presenter(private val mainRepository: MainRepository, uiThreadQueue: UiThreadQueue, private val alarmManager: AlarmManager) :
        BasePresenter<Contract.View>(uiThreadQueue), Contract.Presenter {

    private var disposables: CompositeDisposable = CompositeDisposable()

    override fun attach(view: Contract.View) {
        super.attach(view)

        view.setTitle("")

        val viewModel = view.getViewModel()
        viewModel.portals = mainRepository.getPortalDao().getAll()
    }

    override fun detach() {
        super.detach()
        disposables.clear()
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

    override fun removePortal(portal: Portal): Boolean {
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
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)

        async(CommonPool) {
            portal.flipTime = c.time
            mainRepository.getPortalDao().update(portal)
        }
    }

    override fun scheduleExpiryTimers(portals: List<Portal>) {

        // Schedule expiry broadcasts with the AlarmManager
        Observable.just(portals)
                .flatMapIterable { p -> p }
                .filter { portal -> expiryTime(portal).after(Date()) }
                .subscribe({ portal ->
                    val intent = Intent(mainRepository.app, NotificationPublisher::class.java)
                    intent.putExtra("PORTAL_NAME", portal.portalName)
                    val pendingIntent = PendingIntent.getBroadcast(mainRepository.app, portal.portalName.hashCode(), intent, PendingIntent.FLAG_ONE_SHOT)
                    alarmManager.setExact(AlarmManager.RTC,
                            System.currentTimeMillis() + millisecondsUntilExpiry(portal, Date()),
                            pendingIntent)
                })

        disposables.clear()

        // Peel the portals into a stream of portals filtered by expiry time. Then
        // for each of those, delay until the expiry. Effectively, this calls back the
        // subscriber when each of the unexpired portals actually expires.
        disposables.add(Observable.just(portals)
                .flatMapIterable { p -> p }
                .filter { portal -> expiryTime(portal).after(Date()) }
                .concatMap { portal -> Observable.just(portal).delay(millisecondsUntilExpiry(portal, Date()), TimeUnit.MILLISECONDS) }
                .subscribe(
                        { portal -> expire(portal) },
                        { error -> Timber.e(error) },
                        { allExpired() }))
    }

    @Suppress("UNUSED_PARAMETER")
    private fun expire(portal: Portal) {
        // Force update the view
        uiThreadQueue.run(Runnable {
            view?.refreshPortalList()
        })
    }

    private fun allExpired() {
    }

    private fun expiryTime(portal: Portal): Date {
        val c = Calendar.getInstance()
        c.time = portal.flipTime
        c.add(Calendar.HOUR_OF_DAY, 1)
        return c.time
    }

    private fun millisecondsUntilExpiry(portal: Portal, now: Date): Long {
        val millisecondsUntilExpiry = (expiryTime(portal).time - now.time)
        // Round up a bit to make sure expiry has happened
        return millisecondsUntilExpiry + 1000
    }
}
