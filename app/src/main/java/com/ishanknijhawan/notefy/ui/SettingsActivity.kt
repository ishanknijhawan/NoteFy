package com.ishanknijhawan.notefy.ui

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.ishanknijhawan.notefy.R
import org.jetbrains.anko.backgroundColor

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

        actionBar?.setDisplayHomeAsUpEnabled(true)

        val prefsx = PreferenceManager.getDefaultSharedPreferences(this)
        prefsx.getString("list", "<unset>")


        if (fragmentManager.findFragmentById(android.R.id.content) == null) {
            fragmentManager.beginTransaction()
                .add(android.R.id.content, SettingsFragment()).commit()

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    class SettingsFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)

            bindPreferenceSummaryToValue(findPreference("list"))
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


