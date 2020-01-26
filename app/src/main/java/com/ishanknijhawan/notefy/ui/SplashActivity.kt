package com.ishanknijhawan.notefy.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Toast.makeText(this,"value is ${prefs2.getBoolean("switch4",false)}",Toast.LENGTH_SHORT).show()
        askforFingerPrint()
    }

//    override fun onStart() {
//        super.onStart()
//        askforFingerPrint()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        askforFingerPrint()
//    }

    private fun askforFingerPrint() {

        val prefs2 = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs2.getBoolean("switch4",false).toString() == "true"){
            //startActivity(intentFor<fingerPrintActivity>().newTask().clearTask())
            val intent = Intent(this, fingerPrintActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            if (FirebaseAuth.getInstance().currentUser == null){
                val intent = Intent(this, FinalLoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
