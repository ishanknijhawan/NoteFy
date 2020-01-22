package com.ishanknijhawan.notefy.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.room.Room
import com.ishanknijhawan.notefy.Adapter.NoteAdapter
import com.ishanknijhawan.notefy.Db.NoteDatabase
import com.ishanknijhawan.notefy.Entity.Note
import com.ishanknijhawan.notefy.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val db : NoteDatabase by lazy {
        Room.databaseBuilder(this,
            NoteDatabase::class.java,
            "note.db"
        ).allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    var noteList= arrayListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottom_app_bar)

        noteList = db.noteDao().getAllNotes() as ArrayList<Note>
        //window.navigationBarColor = Color.parseColor("#80FF9933")

        val adapter = NoteAdapter(noteList,this)
        rv_main_list.adapter = adapter

        //rv_main_list.layoutManager = GridLayoutManager(this,2)
        //rv_main_list.itemAnimator = DefaultItemAnimator()
        rv_main_list.layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)

        fabSpeedDial.setOnClickListener {
            val intent = Intent(this@MainActivity,
                TextNoteActivity::class.java)
            startActivityForResult(intent,1)
        }

        toolbar2.setOnMenuItemClickListener { arg0 ->
            if (arg0.itemId == R.id.action_search)
                Toast.makeText(this@MainActivity,"search",Toast.LENGTH_SHORT).show()
            else if (arg0.itemId == R.id.action_settings)
                Toast.makeText(this@MainActivity,"settings",Toast.LENGTH_SHORT).show()
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

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode ==1 && resultCode == Activity.RESULT_OK){
//            val finalTitle = intent.getStringExtra("FINAL_TITLE")
//            val finalContent2 = intent.getStringExtra("FINAL_CONTENT")
//                        db.noteDao().insert(
//                Note(title = finalTitle,
//                    description = finalContent2,
//                    archive = false,
//                    label = "home",
//                    bookmark = false)
//            )
//        }
//    }

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
                Toast.makeText(this,"shopping list",Toast.LENGTH_SHORT).show()
            }



        }
        return super.onOptionsItemSelected(item)
    }
}
