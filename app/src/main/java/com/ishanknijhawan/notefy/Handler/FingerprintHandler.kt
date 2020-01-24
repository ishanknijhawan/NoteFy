package com.ishanknijhawan.notefy.Handler

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.ishanknijhawan.notefy.R
import com.ishanknijhawan.notefy.ui.MainActivity


@TargetApi(Build.VERSION_CODES.M)
class FingerprintHandler(context: Context) :
    FingerprintManager.AuthenticationCallback() {
    private val context: Context

    fun startAuth(
        fingerprintManager: FingerprintManager,
        cryptoObject: FingerprintManager.CryptoObject?
    ) {
        val cancellationSignal = CancellationSignal()
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    override fun onAuthenticationError(
        errorCode: Int,
        errString: CharSequence
    ) {
        update("There was an Auth Error. $errString", false)
    }

    override fun onAuthenticationFailed() {
        update("Fingerprint not recognized", false)
    }

    override fun onAuthenticationHelp(
        helpCode: Int,
        helpString: CharSequence
    ) {
        update("Error: $helpString", false)
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
        update("", true)
    }

    private fun update(s: String, b: Boolean) {
        val paraLabel =
            (context as Activity).findViewById<View>(R.id.tv_main_text) as TextView
        val imageView: ImageView =
            context.findViewById<View>(R.id.ivFingerPrint) as ImageView
        paraLabel.text = s
        if (!b) {
            paraLabel.setTextColor(ContextCompat.getColor(context,
                R.color.colorRed
            ))
            imageView.setImageResource(R.drawable.ic_exclamation)
        }
        else {
            paraLabel.setTextColor(ContextCompat.getColor(context,
                R.color.colorGreen
            ))
            imageView.setImageResource(R.drawable.ic_check_green)
            val intent = Intent(context,MainActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }

    init {
        this.context = context
    }
}