package com.ishanknijhawan.notefy.ui

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.ishanknijhawan.notefy.Adapter.NoteAdapter
import com.ishanknijhawan.notefy.Entity.Note
import com.ishanknijhawan.notefy.R
import com.ishanknijhawan.notefy.ViewModel.ViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val add = 1
        const val edit = 2
    }

    //github repo link: https://github.com/ishanknijhawan/Note-Fy.git

    lateinit var viewModel: ViewModel
    lateinit var getAllNotes: LiveData<List<Note>>
    lateinit var allNotes: List<Note>
    lateinit var noteAdapter: NoteAdapter
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottom_app_bar)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val helper by lazy {

            object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val note = allNotes[position]
                    val id = note.id

                        viewModel.delete(allNotes[position])
                        Snackbar.make(fl_main, "Note Deleted", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.parseColor("#FFA500"))
                            .setAction("Undo")
                            { viewModel.insert(note) }
                            .show()

                }
            }
        }

        val helper2 by lazy {

            object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val note = allNotes[position]
                    val id = note.id
                    note.archive = true

                        viewModel.update(allNotes[position])
                        Snackbar.make(fl_main, "Note archived", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.parseColor("#FFA500"))
                            .setAction("Undo")
                            {
                                note.archive = false
                                viewModel.update(note)
                            }
                            .show()

                }
            }
        }

        viewModel = ViewModelProviders.of(this).get(ViewModel::class.java)
        getAllNotes = viewModel.getAllNotes()

        getAllNotes.observe(this, Observer {
            allNotes = getAllNotes.value!!
            rv_main_list.layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)
            rv_main_list.adapter = NoteAdapter(allNotes,this)
            noteAdapter = NoteAdapter(allNotes, this)

            if (prefs.getBoolean("switch_left",false).toString() == "true") {
                val swipe = ItemTouchHelper(helper)
                swipe.attachToRecyclerView(rv_main_list)
            }
            if (prefs.getBoolean("switch_right",false).toString() == "true") {
                val swipe2 = ItemTouchHelper(helper2)
                swipe2.attachToRecyclerView(rv_main_list)
            }

        })

        fabSpeedDial.setOnClickListener {
            val intent = Intent(this@MainActivity,
                TextNoteActivity::class.java)
            startActivityForResult(intent, add)
        }

        toolbar2.setOnMenuItemClickListener { arg0 ->
            if (arg0.itemId == R.id.action_search)
                Toast.makeText(this@MainActivity,"search",Toast.LENGTH_SHORT).show()
            else if (arg0.itemId == R.id.action_settings){
                val intent = Intent(this,SettingsActivity::class.java)
                startActivity(intent)
            }
            else if (arg0.itemId == R.id.action_reminders)
                Toast.makeText(this@MainActivity,"reminders",Toast.LENGTH_SHORT).show()
            else if (arg0.itemId == R.id.action_deleted)
                Toast.makeText(this@MainActivity,"Deleted",Toast.LENGTH_SHORT).show()
            else if (arg0.itemId == R.id.action_labels)
                Toast.makeText(this@MainActivity,"labels",Toast.LENGTH_SHORT).show()
            else if (arg0.itemId == R.id.action_archived)
                Toast.makeText(this@MainActivity,"archived",Toast.LENGTH_SHORT).show()
            false
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.todo -> {
                Toast.makeText(this,"todo",Toast.LENGTH_SHORT).show()
            }
            R.id.attach_image -> {
                Toast.makeText(this,"attach image",Toast.LENGTH_SHORT).show()
            }
            R.id.draw_note -> {
                Toast.makeText(this,"draw note",Toast.LENGTH_SHORT).show()
            }
            R.id.shopping_list -> {
                val gsoo = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                FinalLoginActivity.googleSignInClient = GoogleSignIn.getClient(this,gsoo)

                FinalLoginActivity.auth = FirebaseAuth.getInstance()

                FinalLoginActivity.auth.signOut()
                FinalLoginActivity.googleSignInClient.signOut()
                //FinalLoginActivity.googleSignInClient.signOut()
                val intent = Intent(this,FinalLoginActivity::class.java)
                startActivity(intent)
                finish()
                //Toast.makeText(this,"shopping list",Toast.LENGTH_SHORT).show()
            }



        }
        return super.onOptionsItemSelected(item)
    }

}
