package com.robotsandpencils.kotlindaggerexperiement.app.db

import java.util.*

data class Portal(
        val portalName: String,
        var flipTime: Date,
        var faction: Int) {
    companion object {
        val FACTION_ENLIGHTENED = 0
        val FACTION_RESISTANCE = 1
    }
}
