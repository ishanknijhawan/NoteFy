package com.ishanknijhawan.notefy.Fragments


import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.RecordingCanvas
import android.media.MediaRecorder
import android.os.Bundle
import android.preference.PreferenceManager
import android.speech.RecognizerIntent
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.ishanknijhawan.notefy.Adapter.NoteAdapter
import com.ishanknijhawan.notefy.Entity.Note
import com.ishanknijhawan.notefy.FirebaseDatabase.FireStore
import com.ishanknijhawan.notefy.R
import com.ishanknijhawan.notefy.ViewModel.ViewModel
import com.ishanknijhawan.notefy.ui.FinalLoginActivity
import com.ishanknijhawan.notefy.ui.MainActivity
import com.ishanknijhawan.notefy.ui.TextNoteActivity
import kotlinx.android.synthetic.main.fragment_main.*
import org.jetbrains.anko.support.v4.startActivityForResult
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class MainFragment : Fragment() {

    companion object {
        const val add = 1
        const val edit = 2
    }

    //github repo link: https://github.com/ishanknijhawan/Note-Fy.git

    lateinit var viewModel: ViewModel
    lateinit var getAllNotes: LiveData<List<Note>>
    lateinit var getAllPinned: LiveData<List<Note>>
    var allNotes: List<Note> = listOf()
    var allPinned: List<Note> = listOf()
    lateinit var noteAdapter: NoteAdapter
    lateinit var prefs: SharedPreferences
    lateinit var databaseReference: DatabaseReference
    lateinit var dialog: AlertDialog
    lateinit var dialogView: View
    lateinit var mediaRecorder: MediaRecorder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabSpeedDial)
        val bab = view.findViewById<BottomAppBar>(R.id.bottom_app_bar)
        bab.elevation = 16F

        val iv3 = view.findViewById<ImageView>(R.id.imageView99)
        val tv3 = view.findViewById<TextView>(R.id.textView99)

        val tvPinned = view.findViewById<TextView>(R.id.tv_pinned)
        val tvOthers = view.findViewById<TextView>(R.id.tv_others)

//        if (allPinned.isEmpty() && allNotes.isEmpty()){
//            tvPinned.visibility = View.VISIBLE
//            tvOthers.visibility = View.VISIBLE
//        }

        (activity as AppCompatActivity?)!!.setSupportActionBar(bab)
        setHasOptionsMenu(true)

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val user = FirebaseAuth.getInstance().currentUser?.uid
        databaseReference = FireStore.getDatabase(user.toString())!!

        dialog = AlertDialog.Builder(this.requireContext()).create()
        dialogView = layoutInflater.inflate(R.layout.layout_dialog, null)

        val tvAddCamera = dialogView.findViewById<TextView>(R.id.tvOpenCamera)
        val tvChooseFile = dialogView.findViewById<TextView>(R.id.tvChooseFile)

        tvAddCamera.setOnClickListener {
            if ((ContextCompat.checkSelfPermission(
                    this.requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                        != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                    this.requireContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                        != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                    this.requireContext(),
                    android.Manifest.permission.CAMERA
                )
                        != PackageManager.PERMISSION_GRANTED)
            ) {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA
                    ), 75
                )
            } else {
                val intent = Intent(this.requireContext(), TextNoteActivity::class.java)
                intent.putExtra("IMAGE_CAMERA", "opened_from_camera")
                startActivity(intent)
                dialog.dismiss()
            }
        }

        tvChooseFile.setOnClickListener {
            if ((ContextCompat.checkSelfPermission(
                    this.requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                        != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                    this.requireContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                        != PackageManager.PERMISSION_GRANTED)
            ) {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 72
                )
            } else {
                val intent = Intent(this.requireContext(), TextNoteActivity::class.java)
                intent.putExtra("IMAGE_FILE", "opened_from_file")
                startActivity(intent)
                dialog.dismiss()
            }
        }

        val DeleteHelperNormal by lazy {

            object : ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
                override fun onMove(
                    p0: RecyclerView,
                    p1: RecyclerView.ViewHolder,
                    p2: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val note = allNotes[position]
                    val id = note.id
                    note.deleted = true

                    viewModel.update(allNotes[position])
                    Snackbar.make(fragmentContainer2, "Note moved to bin", Snackbar.LENGTH_LONG)
//                        .apply {
//                            view.layoutParams =
//                                (view.layoutParams as CoordinatorLayout.LayoutParams).apply {
//                                    setMargins(16, 16, 16, 16)
//                                }
//                            //view.background = resources.getDrawable(R.drawable.round_corner, null)
//                        }
                        .setAnchorView(fab)
                        .setActionTextColor(Color.parseColor("#c8c5ff"))
                        .setAction("Undo")
                        {
                            note.deleted = false
                            viewModel.update(note)
                        }
                        .show()

                }
            }
        }

        val DeletedHelperPinned by lazy {

            object : ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
                override fun onMove(
                    p0: RecyclerView,
                    p1: RecyclerView.ViewHolder,
                    p2: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val note = allPinned[position]
                    val id = note.id
                    note.deleted = true
                    note.pinned = false

                    viewModel.update(allPinned[position])
                    Snackbar.make(fragmentContainer2, "Note unpinned and deleted", Snackbar.LENGTH_LONG)
//                        .apply {
//                            view.layoutParams =
//                                (view.layoutParams as CoordinatorLayout.LayoutParams).apply {
//                                    setMargins(16, 16, 16, 16)
//                                }
//                            //view.background = resources.getDrawable(R.drawable.round_corner, null)
//                        }
                        .setAnchorView(fab)
                        .setActionTextColor(Color.parseColor("#c8c5ff"))
                        .setAction("Undo")
                        {
                            note.deleted = false
                            note.pinned = true
                            viewModel.update(note)
                        }
                        .show()

                }
            }
        }

        val ArchiveHelperPinner by lazy {

            object : ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.RIGHT) {
                override fun onMove(
                    p0: RecyclerView,
                    p1: RecyclerView.ViewHolder,
                    p2: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val note = allPinned[position]
                    val id = note.id
                    note.archive = true
                    note.pinned = false

                    viewModel.update(allPinned[position])
                    Snackbar.make(fragmentContainer2, "Note unpinned and archived", Snackbar.LENGTH_LONG)
//                        .apply {
//                            view.layoutParams =
//                                (view.layoutParams as CoordinatorLayout.LayoutParams).apply {
//                                    setMargins(16, 16, 16, 16)
//                                }
//                            //view.background = resources.getDrawable(R.drawable.round_corner, null)
//                        }
                        .setAnchorView(fab)
                        .setActionTextColor(Color.parseColor("#c8c5ff"))
                        .setAction("Undo")
                        {
                            note.archive = false
                            note.pinned = true
                            viewModel.update(note)
                        }
                        .show()

                }
            }
        }

        val ArchiveHelperNormal by lazy {

            object : ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.RIGHT) {
                override fun onMove(
                    p0: RecyclerView,
                    p1: RecyclerView.ViewHolder,
                    p2: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val note = allNotes[position]
                    val id = note.id
                    note.archive = true

                    viewModel.update(allNotes[position])

                    Snackbar.make(fragmentContainer2, "Note archived", Snackbar.LENGTH_LONG)
                        .setAnchorView(fab)
                        .setActionTextColor(Color.parseColor("#c8c5ff"))
                        .setAction("Undo")
                        {
                            note.archive = false
                            viewModel.update(note)
                        }
                        .show()

                }
            }
        }

        val helper3 by lazy {
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                0) {
                override fun onMove(
                    p0: RecyclerView,
                    p1: RecyclerView.ViewHolder,
                    p2: RecyclerView.ViewHolder
                ): Boolean {
                    val sourcePosition = p1.adapterPosition
                    val targetPosition = p2.adapterPosition
                    Collections.swap(allNotes, sourcePosition, targetPosition)
                    (rv_main_list.adapter as NoteAdapter).notifyItemMoved(sourcePosition, targetPosition)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                }
            }
        }


        viewModel = ViewModelProviders.of(this).get(ViewModel::class.java)
        getAllNotes = viewModel.getAllNotes()
        getAllPinned = viewModel.getPinnedNotes()

        getAllNotes.observe(this, Observer {

            allNotes = getAllNotes.value!!

            if (allNotes.isEmpty() && allPinned.isEmpty()){
                iv3.visibility = View.VISIBLE
                tv3.visibility = View.VISIBLE
                rv_main_list.visibility = View.GONE
                tvPinned.visibility = View.GONE
                tvOthers.visibility = View.GONE
            }
            else {
                iv3.visibility = View.GONE
                tv3.visibility = View.GONE
                rv_main_list.visibility = View.VISIBLE
//                tvPinned.visibility = View.VISIBLE
//                tvOthers.visibility = View.VISIBLE
            }


            rv_main_list.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            rv_main_list.adapter = NoteAdapter(allNotes, this.requireContext())
            noteAdapter = NoteAdapter(allNotes, this.requireContext())

            if (prefs.getBoolean("switch_left", false).toString() == "true") {
                val swipe = ItemTouchHelper(DeleteHelperNormal)
                swipe.attachToRecyclerView(rv_main_list)
            }
            if (prefs.getBoolean("switch_right", false).toString() == "true") {
                val swipe2 = ItemTouchHelper(ArchiveHelperNormal)
                swipe2.attachToRecyclerView(rv_main_list)
            }

        })

        getAllPinned.observe(this, Observer {

            allPinned = getAllPinned.value!!

            if (allPinned.isEmpty() && allNotes.isEmpty()){
                rv_pinned_list.visibility = View.GONE
                tvPinned.visibility = View.GONE
                tvOthers.visibility = View.GONE
                iv3.visibility = View.VISIBLE
                tv3.visibility = View.VISIBLE
            }
            else if(allPinned.isNotEmpty()) {
                rv_pinned_list.visibility = View.VISIBLE
                tvPinned.visibility = View.VISIBLE
                tvOthers.visibility = View.VISIBLE
                iv3.visibility = View.GONE
                tv3.visibility = View.GONE
            }


            rv_pinned_list.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            rv_pinned_list.adapter = NoteAdapter(allPinned, this.requireContext())
            noteAdapter = NoteAdapter(allPinned, this.requireContext())

            if (prefs.getBoolean("switch_left", false).toString() == "true") {
                val swipe = ItemTouchHelper(DeletedHelperPinned)
                swipe.attachToRecyclerView(rv_pinned_list)
            }
            if (prefs.getBoolean("switch_right", false).toString() == "true") {
                val swipe2 = ItemTouchHelper(ArchiveHelperPinner)
                swipe2.attachToRecyclerView(rv_pinned_list)
            }

        })

        fab.setOnClickListener {
            val intent = Intent(
                this.requireContext(),
                TextNoteActivity::class.java
            )
            startActivityForResult(intent, MainActivity.add)
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.todo -> {
                val intent = Intent(this.requireContext(), TextNoteActivity::class.java)
                intent.putExtra("CHECK_LIST", "opened_from_check_list")
                startActivity(intent)
            }
            R.id.attach_image -> {
                dialog.setView(dialogView)
                dialog.setCancelable(true)
                dialog.show()
            }
            R.id.draw_note -> {
                if ((ContextCompat.checkSelfPermission(
                        this.requireContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                            != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                        this.requireContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                            != PackageManager.PERMISSION_GRANTED)
                ) {
                    requestPermissions(
                        arrayOf(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), 69
                    )
                } else {
                    val intent = Intent(this.requireContext(), TextNoteActivity::class.java)
                    intent.putExtra("DRAW", "opened_from_drawing")
                    startActivity(intent)
                }
            }
            R.id.recording -> {
                if ((ContextCompat.checkSelfPermission(
                        this.requireContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                            != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                        this.requireContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                            != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                        this.requireContext(),
                        android.Manifest.permission.RECORD_AUDIO
                    )
                            != PackageManager.PERMISSION_GRANTED)
                ) {
                    requestPermissions(
                        arrayOf(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.RECORD_AUDIO
                        ), 42
                    )
                } else {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Try saying something!")



                    try {
                        startActivityForResult(intent, 1211)
                    }catch (e: Exception){
                        Toast.makeText(this.requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }


        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            1211 -> {
                if (resultCode == Activity.RESULT_OK && null != data){
                    val result: ArrayList<String>? = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val intent = Intent(this.requireContext(), TextNoteActivity::class.java)
                    intent.putStringArrayListExtra("AUDIO", result)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            69 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {

                    val intent = Intent(this.requireContext(), TextNoteActivity::class.java)
                    intent.putExtra("DRAW", "opened_from_drawing")
                    startActivity(intent)

                } else {
                    Toast.makeText(this.requireContext(), "Permission Denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            72 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    val intent = Intent(this.requireContext(), TextNoteActivity::class.java)
                    intent.putExtra("IMAGE_FILE", "opened_from_file")
                    startActivity(intent)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this.requireContext(), "Permission Denied", Toast.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                }
            }
            75 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                ) {
                    val intent = Intent(this.requireContext(), TextNoteActivity::class.java)
                    intent.putExtra("IMAGE_CAMERA", "opened_from_camera")
                    startActivity(intent)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this.requireContext(), "Permission Denied", Toast.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                }
            }
            42 -> {if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Try saying something!")

                try {
                    startActivityForResult(intent, 1211)
                }catch (e: Exception){
                    Toast.makeText(this.requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this.requireContext(), "Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
