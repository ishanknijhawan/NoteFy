package com.ishanknijhawan.notefy.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs2 = PreferenceManager.getDefaultSharedPreferences(this)

        //Toast.makeText(this,"value is ${prefs2.getBoolean("switch4",false)}",Toast.LENGTH_SHORT).show()
        if (prefs2.getBoolean("switch4",false).toString() == "true"){
            //startActivity(intentFor<fingerPrintActivity>().newTask().clearTask())
            val intent = Intent(this, fingerPrintActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            //startActivity(intentFor<MainActivity>().newTask().clearTask())
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
