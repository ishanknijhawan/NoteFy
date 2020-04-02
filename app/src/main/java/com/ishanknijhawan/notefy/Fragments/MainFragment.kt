package com.ishanknijhawan.notefy.Fragments


import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
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
import java.util.*


class MainFragment : Fragment() {

    companion object {
        const val add = 1
        const val edit = 2
    }

    //github repo link: https://github.com/ishanknijhawan/Note-Fy.git

    lateinit var viewModel: ViewModel
    lateinit var getAllNotes: LiveData<List<Note>>
    lateinit var getAllPinned: LiveData<List<Note>>
    lateinit var allNotes: List<Note>
    lateinit var allPinned: List<Note>
    lateinit var noteAdapter: NoteAdapter
    lateinit var prefs: SharedPreferences
    lateinit var databaseReference: DatabaseReference
    lateinit var dialog: AlertDialog
    lateinit var dialogView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabSpeedDial)
        val bab = view.findViewById<BottomAppBar>(R.id.bottom_app_bar)
        bab.elevation = 8F

        val iv3 = view.findViewById<ImageView>(R.id.imageView99)
        val tv3 = view.findViewById<TextView>(R.id.textView99)

        val tvPinned = view.findViewById<TextView>(R.id.tv_pinned)
        val tvOthers = view.findViewById<TextView>(R.id.tv_others)

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
                        .setActionTextColor(Color.parseColor("#FFA500"))
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
                        .setActionTextColor(Color.parseColor("#FFA500"))
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
                        .setActionTextColor(Color.parseColor("#FFA500"))
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

                    val snackbar = Snackbar.make(fragmentContainer2, "Note archived", Snackbar.LENGTH_LONG)
//                        .apply {
//                            view.layoutParams =
//                                (view.layoutParams as CoordinatorLayout.LayoutParams).apply {
//                                    setMargins(16, 16, 16, 16)
//                                }
//                            //view.background = resources.getDrawable(R.drawable.round_corner, null)
//                        }
                        .setAnchorView(fab)
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

            if (allNotes.isEmpty()){
                iv3.visibility = View.VISIBLE
                tv3.visibility = View.VISIBLE
            }
            else {
                iv3.visibility = View.GONE
                tv3.visibility = View.GONE
            }

            if (allNotes.isEmpty()){
                rv_main_list.visibility = View.GONE
                tvPinned.visibility = View.GONE
                tvOthers.visibility = View.GONE
            }
            else {
                rv_main_list.visibility = View.VISIBLE
                tvPinned.visibility = View.VISIBLE
                tvOthers.visibility = View.VISIBLE
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

            if (allPinned.isEmpty()){
                rv_pinned_list.visibility = View.GONE
                tvPinned.visibility = View.GONE
                tvOthers.visibility = View.GONE
                iv3.visibility = View.VISIBLE
                tv3.visibility = View.VISIBLE
            }
            else {
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
                val gsoo = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                FinalLoginActivity.googleSignInClient =
                    GoogleSignIn.getClient(this.requireContext(), gsoo)

                FinalLoginActivity.auth = FirebaseAuth.getInstance()

                FinalLoginActivity.auth.signOut()
                FinalLoginActivity.googleSignInClient.signOut()
                //FinalLoginActivity.googleSignInClient.signOut()
                val intent = Intent(this.requireContext(), FinalLoginActivity::class.java)
                startActivity(intent)
                //finish()
                //Toast.makeText(this,"shopping list",Toast.LENGTH_SHORT).show()
            }


        }
        return super.onOptionsItemSelected(item)
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
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
