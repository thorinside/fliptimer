package com.robotsandpencils.kotlindaggerexperiement.presentation.main

import android.content.res.ColorStateList
import com.robotsandpencils.kotlindaggerexperiement.R
import com.robotsandpencils.kotlindaggerexperiement.app.db.Portal
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.item_portal.view.*
import java.text.SimpleDateFormat
import java.util.*

class PortalItem(internal val portal: Portal, internal val presenter: Contract.Presenter) : Item<ViewHolder>(portal.portalName.hashCode().toLong()) {

    override fun getLayout(): Int {
        return R.layout.item_portal
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            name.text = portal.portalName
            resetTime.text = formatResetTime(portal.flipTime, context.getString(R.string.ready))
            flipButton.setOnClickListener { presenter.flipPortal(portal) }

            resetTime.setOnClickListener { presenter.editPortalTime(portal) }

            if (portal.faction == Portal.FACTION_RESISTANCE) {
                val color = context.getColor(R.color.resistance_blue)
                backgroundLayer.setBackgroundColor(color)
                flipButton.backgroundTintList = ColorStateList.valueOf(color)
            } else {
                val color = context.getColor(R.color.enlightened_green)
                backgroundLayer.setBackgroundColor(color)
                flipButton.backgroundTintList = ColorStateList.valueOf(color)
            }
        }
    }

    private fun formatResetTime(virusTime: Date, readyText: String): String {
        val c : Calendar = Calendar.getInstance()
        c.time = virusTime
        c.add(Calendar.HOUR, 1)

        return if (c.time.after(Date())) {
            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.CANADA)
            dateFormat.format(c.time)
        } else {
            readyText
        }
    }
}