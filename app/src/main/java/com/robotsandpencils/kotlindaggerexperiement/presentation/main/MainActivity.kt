package com.robotsandpencils.kotlindaggerexperiement.presentation.main

import android.app.TimePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.widget.TimePicker
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.github.ajalt.timberkt.Timber
import com.robotsandpencils.kotlindaggerexperiement.R
import com.robotsandpencils.kotlindaggerexperiement.app.db.Portal
import com.robotsandpencils.kotlindaggerexperiement.presentation.nfc.NfcActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.UpdatingGroup
import com.xwray.groupie.ViewHolder
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), Contract.View {

    @Inject lateinit var presenter: Contract.Presenter

    private val groupAdapter = GroupAdapter<ViewHolder>()
    private val updatingGroup = UpdatingGroup()

    override fun onCreate(savedInstanceState: Bundle?) {

        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.attach(this)

        connectView()
    }

    private fun connectView() {
        connectButton()
        connectRecyclerView()
    }

    private fun connectButton() {
        button.setOnClickListener { _ ->
            // Tell the presenter to perform the database insert
            presenter.addPortal(portalName.text.toString(), Portal.FACTION_RESISTANCE)
        }
    }

    override fun showTimePickerDialog(portal: Portal) {
        val c = Calendar.getInstance()
        c.time = portal.flipTime

        TimePickerDialog(this,
                { timePicker : TimePicker, hourOfDay: Int, minute: Int ->
                    presenter.setFlipTime(portal, hourOfDay, minute)
                },
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)
                .show()
    }

    private fun connectRecyclerView() {
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = groupAdapter

        groupAdapter.add(updatingGroup)

        getViewModel().portals.observe(this, Observer { portals ->
            Timber.w { "Portals Changed: ${Thread.currentThread().name}" }
            updatingGroup.update(getPortalItems(portals))
            portals?.let { presenter.scheduleExpiryTimers(it) }
        })

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean =
                    false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = groupAdapter.getItem(position)
                val portal = (item as PortalItem).portal

                MaterialDialog.Builder(this@MainActivity)
                        .title(getString(R.string.delete_portal_title))
                        .content("Do you want to delete the portal named ${portal.portalName}?")
                        .positiveText(getString(R.string.delete_portal_delete))
                        .negativeText(getString(R.string.delete_portal_cancel))
                        .onPositive { _, _ ->
                            presenter.removePortal(portal)
                        }
                        .onNegative { _, _ ->
                            groupAdapter.notifyItemRangeChanged(position, groupAdapter.itemCount)
                        }
                        .show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(list)
    }

    override fun refreshPortalList() {
        Timber.w { "Refresh Portal List: ${Thread.currentThread().name}" }
        groupAdapter.notifyDataSetChanged()
    }

    override fun showError(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun getPortalItems(portals: List<Portal>?): List<Item<ViewHolder>> {
        val items = ArrayList<PortalItem>()

        portals?.forEach { portal ->
            items.add(PortalItem(portal, presenter))
        }

        return items
    }

    override fun clearFields() {
        portalName.requestFocus()
        arrayOf(portalName.text)
                .forEach { it.clear() }
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }

    override fun setTitle(text: String) {
        message.text = text
    }

    override fun setTitle(text: Int) {
        message.text = getString(text)
    }

    override fun getViewModel(): MainViewModel {
        return ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.action_nfc -> {
                startActivity(Intent(this@MainActivity, NfcActivity::class.java))
            }
            R.id.action_unpair -> {
                presenter.unpair()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
