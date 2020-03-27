package com.ishanknijhawan.notefy.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.ishanknijhawan.notefy.Fragments.ArchiveFragment
import com.ishanknijhawan.notefy.Fragments.MainFragment
import com.ishanknijhawan.notefy.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    lateinit var drawer: DrawerLayout
    lateinit var navigationview : NavigationView

    companion object {
        const val add = 1
        const val edit = 2
    }

    //github repo link: https://github.com/ishanknijhawan/Note-Fy.git

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //prefs = PreferenceManager.getDefaultSharedPreferences(this)
        drawer = findViewById(R.id.drawer_layout)
        navigationview = findViewById(R.id.nav_view)
        navigationview.setNavigationItemSelectedListener(this)
        navigationview.setItemIconTintList(null)

        window.navigationBarColor = Color.parseColor("#FFFFFF")
        window.statusBarColor = Color.parseColor("#FFFFFF")
        val goodMorning2 = navigationview.getHeaderView(0)
        val goodMorning = goodMorning2.findViewById<TextView>(R.id.tv_goodMorning)

        val tvEmail = navigationview.getHeaderView(0)
        val tvEmailFinal = tvEmail.findViewById<TextView>(R.id.tv_email)

        val rightNow: Calendar = Calendar.getInstance()
        var username = FirebaseAuth.getInstance().currentUser?.displayName
        val splitName = username?.split(" ")
        val finalName = splitName?.get(0)

        val time24: Int =
            rightNow.get(Calendar.HOUR_OF_DAY) // return the hour in 24 hrs format (ranging from 0-23)

        val currentHourIn12Format: Int =
            rightNow.get(Calendar.HOUR) // return the hour in 12 hrs format (ranging from 0-11)

        when (time24) {
            in 5..11 -> goodMorning.text = "Good Morning, $finalName"
            in 12..16 -> goodMorning.text = "Good Afternoon, $finalName"
            in 17..22 -> goodMorning.text = "Good Evening, $finalName"
            else -> goodMorning.text = "Welcome back, $finalName"
        }

        tvEmailFinal.text = FirebaseAuth.getInstance().currentUser?.email

        ivHamburger.setOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                MainFragment()
            ).commit()
            navigationview.setCheckedItem(R.id.nav_notes) }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.nav_notes -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    MainFragment()
                ).commit()
            }
            R.id.nav_archive -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    ArchiveFragment()
                ).commit()
                navigationview.setCheckedItem(R.id.nav_archive)
            }
            R.id.nav_delete -> {
                val intent = Intent(this, DeletedActivity::class.java)
                startActivity(intent)
            }
            R.id.action_labels -> {

            }
            R.id.nav_reminders -> {

            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.help_and_feedback -> {

            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {

        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }
        else super.onBackPressed()
    }

}
