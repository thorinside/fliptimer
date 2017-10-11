package com.robotsandpencils.kotlindaggerexperiement.presentation.nfc

/**
 * Created by neal on 10/10/17.
 */
interface Contract {
    /**
     * Presenter Contract
     */
    interface Presenter : com.robotsandpencils.kotlindaggerexperiement.presentation.base.Presenter<View> {
        fun generateShareJson(): String
        fun pair(pairingKey: String)
    }

    /**
     * View Contract
     */
    interface View : com.robotsandpencils.kotlindaggerexperiement.presentation.base.View

}