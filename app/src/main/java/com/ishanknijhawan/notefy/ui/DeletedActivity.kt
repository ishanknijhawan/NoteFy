package com.ishanknijhawan.notefy.ui

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.ishanknijhawan.notefy.Adapter.NoteAdapter
import com.ishanknijhawan.notefy.Entity.Note
import com.ishanknijhawan.notefy.FirebaseDatabase.FireStore
import com.ishanknijhawan.notefy.R
import com.ishanknijhawan.notefy.ViewModel.ViewModel
import kotlinx.android.synthetic.main.activity_deleted.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar2

class DeletedActivity : AppCompatActivity() {

    lateinit var viewModel: ViewModel
    lateinit var getAllDeletedNotes: LiveData<List<Note>>
    lateinit var allDeleted: List<Note>
    lateinit var noteAdapter: NoteAdapter
    lateinit var prefs: SharedPreferences
    lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deleted)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val user = FirebaseAuth.getInstance().currentUser?.uid
        databaseReference = FireStore.getDatabase(user.toString())!!

        viewModel = ViewModelProviders.of(this).get(ViewModel::class.java)
        getAllDeletedNotes = viewModel.getDeletedNotes()

        getAllDeletedNotes.observe(this, Observer {
            allDeleted = getAllDeletedNotes.value!!
            rv_deleted_list.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            rv_deleted_list.adapter = NoteAdapter(allDeleted,this)
            noteAdapter = NoteAdapter(allDeleted, this)

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

        toolbar2.setOnMenuItemClickListener { arg0 ->
            when (arg0.itemId) {
                R.id.action_empty -> {
                    viewModel.deleteAllNotes()
                }
            }
            false
        }

    }

}
