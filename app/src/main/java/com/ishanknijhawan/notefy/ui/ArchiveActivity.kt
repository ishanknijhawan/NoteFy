package com.ishanknijhawan.notefy.ui

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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
import com.google.firebase.database.DatabaseReference
import com.ishanknijhawan.notefy.Adapter.NoteAdapter
import com.ishanknijhawan.notefy.Entity.Note
import com.ishanknijhawan.notefy.FirebaseDatabase.FireStore
import com.ishanknijhawan.notefy.R
import com.ishanknijhawan.notefy.ViewModel.ViewModel
import kotlinx.android.synthetic.main.activity_archive.*
import kotlinx.android.synthetic.main.activity_main.*

class ArchiveActivity : AppCompatActivity() {

    lateinit var viewModel: ViewModel
    lateinit var getAllNotes: LiveData<List<Note>>
    lateinit var allNotes: List<Note>
    lateinit var noteAdapter: NoteAdapter
    lateinit var prefs: SharedPreferences
    lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val user = FirebaseAuth.getInstance().currentUser?.uid
        databaseReference = FireStore.getDatabase(user.toString())!!

        iv_back.setOnClickListener {
            finish()
        }

        val helper by lazy {

            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
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
                    note.archive = false
                    note.deleted = true

                    viewModel.update(allNotes[position])
                    Snackbar.make(cl_archive, "Note moved to bin", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#FFA500"))
                        .setAction("Undo")
                        {
                            note.archive = true
                            note.deleted = false
                            viewModel.update(note)
                        }
                        .show()

                }
            }
        }

        val helper2 by lazy {

            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
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
                    note.archive = false

                    viewModel.update(allNotes[position])
                    Snackbar.make(cl_archive, "Note unarchived", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#FFA500"))
                        .setAction("Undo")
                        {
                            note.archive = true
                            viewModel.update(note)
                        }
                        .show()

                }
            }
        }

        viewModel = ViewModelProviders.of(this).get(ViewModel::class.java)
        getAllNotes = viewModel.getArchivedNotes()

        getAllNotes.observe(this, Observer {
            allNotes = getAllNotes.value!!
            rv_archive_list.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            rv_archive_list.adapter = NoteAdapter(allNotes,this)
            noteAdapter = NoteAdapter(allNotes, this)

            if (prefs.getBoolean("switch_left",false).toString() == "true") {
                val swipe = ItemTouchHelper(helper)
                swipe.attachToRecyclerView(rv_archive_list)
            }
            if (prefs.getBoolean("switch_right",false).toString() == "true") {
                val swipe2 = ItemTouchHelper(helper2)
                swipe2.attachToRecyclerView(rv_archive_list)
            }

        })

//        val postListner = object : ValueEventListener {
//
//            override fun onCancelled(p0: DatabaseError) {
//            }
//
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                viewModel.deleteAllNotes()
//                dataSnapshot.children.forEach {
//                    val id = it.child("id").value.toString()
//                    val color = it.child("color").value.toString()
//                    val description = it.child("description").value.toString()
//                    val title = it.child("title").value.toString()
//                    val label = it.child("label").value.toString()
//                    val bookmark: Boolean = it.child("title").value
//                    val archive = it.child("label").value.toString()
//                    val note = Note(id.toLong(), title, description,label, color.toInt())
//                    viewModel.insert(note)
//                }
//            }
//        }
//        databaseReference.addValueEventListener(postListner)


    }

}

