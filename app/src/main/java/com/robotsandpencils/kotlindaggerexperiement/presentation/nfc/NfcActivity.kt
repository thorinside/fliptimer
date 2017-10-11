package com.robotsandpencils.kotlindaggerexperiement.presentation.nfc

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord.createMime
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.gson.Gson
import com.robotsandpencils.kotlindaggerexperiement.R
import com.robotsandpencils.kotlindaggerexperiement.app.model.SharingInfo
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_nfc.*
import javax.inject.Inject


class NfcActivity : AppCompatActivity(), Contract.View, NfcAdapter.CreateNdefMessageCallback {
    @Inject lateinit var presenter: Contract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {

        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        presenter.attach(this)

        connectView()
    }

    private fun connectView() {

        // Check for available NFC Adapter
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        // Register callback
        nfcAdapter.setNdefPushMessageCallback(this, this@NfcActivity)
    }

    override fun createNdefMessage(event: NfcEvent?): NdefMessage {
        val text = presenter.generateShareJson()
        return NdefMessage(
                arrayOf(createMime(
                        "application/vnd.org.nsdev.apps.fliptimer", text.toByteArray())
                )
        )
    }

    public override fun onResume() {
        super.onResume()
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            processIntent(intent)
        }
    }

    public override fun onNewIntent(intent: Intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent)
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    fun processIntent(intent: Intent) {
        val rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES)
        // only one message sent during the beam
        val msg = rawMsgs[0] as NdefMessage
        // record 0 contains the MIME type, record 1 is the AAR, if present
        val json = String(msg.records[0].payload)
        message.text = json
        val info = Gson().fromJson(json, SharingInfo::class.java)
        presenter.pair(info.sharingKey)
    }
}