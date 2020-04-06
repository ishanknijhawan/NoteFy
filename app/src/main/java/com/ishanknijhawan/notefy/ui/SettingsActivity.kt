package com.ishanknijhawan.notefy.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.ishanknijhawan.notefy.R
import kotlinx.android.synthetic.main.signout_layout.*
import org.jetbrains.anko.backgroundColor
import java.util.zip.Inflater

class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        const val KEY_APP_OPEN = "app open"
        var fpBoolean: Int = 0

        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->

            val stringValue = value.toString()

            if (preference is ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                val listPreference = preference
                val index = listPreference.findIndexOfValue(stringValue)

                // Set the summary to reflect the new value.
                preference.setSummary(
                    if (index >= 0)
                        listPreference.entries[index]
                    else
                        null)

            }
            else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.summary = stringValue
            }
            true
        }

        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                    .getDefaultSharedPreferences(preference.context)
                    .getString(preference.key, ""))
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar?.title = "  Settings"
        actionBar?.elevation = 0F
        actionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FAFAFA")))
        val settingsView = layoutInflater.inflate(R.layout.signout_layout, null)

        val tvSignout = settingsView.findViewById<TextView>(R.id.btnSignOut)
        val tvPrivacy = settingsView.findViewById<TextView>(R.id.privacy_policy)

        window.navigationBarColor = Color.parseColor("#FFFFFF")
        window.statusBarColor = Color.parseColor("#FFFFFF")

        actionBar?.setDisplayHomeAsUpEnabled(true)

        val prefsx = PreferenceManager.getDefaultSharedPreferences(this)
        prefsx.getString("list", "<unset>")


        if (fragmentManager.findFragmentById(android.R.id.content) == null) {
            fragmentManager.beginTransaction()
                .add(android.R.id.content, SettingsFragment()).commit()

        }

        tvSignout.setOnClickListener {
            Toast.makeText(this,"clicked",Toast.LENGTH_SHORT).show()
            val gsoo = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            FinalLoginActivity.googleSignInClient =
                GoogleSignIn.getClient(this, gsoo)

            FinalLoginActivity.auth = FirebaseAuth.getInstance()

            FinalLoginActivity.auth.signOut()
            FinalLoginActivity.googleSignInClient.signOut()
            //FinalLoginActivity.googleSignInClient.signOut()
            val intent = Intent(this, FinalLoginActivity::class.java)
            startActivity(intent)
            //finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }

    class SettingsFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
        }
    }

    override fun onResume() {
        super.onResume()
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        val prefs2 = PreferenceManager.getDefaultSharedPreferences(this)
        val prefs = getPreferences(Context.MODE_PRIVATE)

        fpBoolean = prefs2.getInt(KEY_APP_OPEN,0)

        if (p1 == "switch4"){
            if (prefs2.getBoolean("switch4",false).toString() == "true"){
                fpBoolean = 1
                prefs.edit{
                    putInt(KEY_APP_OPEN, fpBoolean)
                }
            }
            else{
                fpBoolean = 0
                prefs.edit{
                    putInt(KEY_APP_OPEN, fpBoolean)
                }
            }
        }
    }
}


