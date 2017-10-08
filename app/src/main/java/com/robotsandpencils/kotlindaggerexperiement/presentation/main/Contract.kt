package com.robotsandpencils.kotlindaggerexperiement.presentation.main

import android.support.annotation.StringRes
import com.robotsandpencils.kotlindaggerexperiement.app.db.Portal
import java.util.*

/**
 * Main Contract
 */
interface Contract {

    /**
     * Presenter Contract
     */
    interface Presenter : com.robotsandpencils.kotlindaggerexperiement.presentation.base.Presenter<View> {
        fun addPortal(portalName: String, faction: Int)
        fun removePortal(portal: Portal) : Boolean
        fun flipPortal(portal: Portal)
        fun editPortalTime(portal: Portal)
        fun setFlipTime(portal: Portal, hourOfDay: Int, minute: Int)
    }

    /**
     * View Contract
     */
    interface View : com.robotsandpencils.kotlindaggerexperiement.presentation.base.View {
        fun getViewModel(): MainViewModel
        fun setTitle(text: String)
        fun setTitle(@StringRes text: Int)
        fun clearFields()
        fun showError(message: String?)
        fun showTimePickerDialog(portal: Portal)
    }
}