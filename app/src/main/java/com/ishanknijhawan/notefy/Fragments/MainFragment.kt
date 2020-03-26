package com.ishanknijhawan.notefy.Fragments


import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
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


class MainFragment : Fragment() {

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
    lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main,container,false)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabSpeedDial)
        val bab = view.findViewById<BottomAppBar>(R.id.bottom_app_bar)

        (activity as AppCompatActivity?)!!.setSupportActionBar(bab)
        setHasOptionsMenu(true)

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val user = FirebaseAuth.getInstance().currentUser?.uid
        databaseReference = FireStore.getDatabase(user.toString())!!

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
                    note.deleted = true

                    viewModel.update(allNotes[position])
                    Snackbar.make(fragmentContainer, "Note moved to bin", Snackbar.LENGTH_LONG)
                        .apply {
                            view.layoutParams = (view.layoutParams as CoordinatorLayout.LayoutParams).apply {
                                setMargins(16, 16, 16, 16)
                            }
                            //view.background = resources.getDrawable(R.drawable.round_corner, null)
                        }
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
                    Snackbar.make(fragmentContainer, "Note archived", Snackbar.LENGTH_LONG)
                        .apply {
                            view.layoutParams = (view.layoutParams as CoordinatorLayout.LayoutParams).apply {
                                setMargins(16, 16, 16, 16)
                            }
                            //view.background = resources.getDrawable(R.drawable.round_corner, null)
                        }
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
            rv_main_list.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            rv_main_list.adapter = NoteAdapter(allNotes, this.requireContext())
            noteAdapter = NoteAdapter(allNotes, this.requireContext())

            if (prefs.getBoolean("switch_left",false).toString() == "true") {
                val swipe = ItemTouchHelper(helper)
                swipe.attachToRecyclerView(rv_main_list)
            }
            if (prefs.getBoolean("switch_right",false).toString() == "true") {
                val swipe2 = ItemTouchHelper(helper2)
                swipe2.attachToRecyclerView(rv_main_list)
            }

        })

        fab.setOnClickListener {
            val intent = Intent(this.requireContext(),
                TextNoteActivity::class.java)
            startActivityForResult(intent, MainActivity.add)
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_menu,menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.todo -> {
                val intent = Intent(this.requireContext(), TextNoteActivity::class.java)
                intent.putExtra("CHECK_LIST","opened_from_check_list")
                startActivity(intent)
            }
            R.id.attach_image -> {
                Toast.makeText(this.requireContext(),"attach image", Toast.LENGTH_SHORT).show()
            }
            R.id.draw_note -> {
                Toast.makeText(this.requireContext(),"draw note", Toast.LENGTH_SHORT).show()
            }
            R.id.recording -> {
                val gsoo = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                FinalLoginActivity.googleSignInClient = GoogleSignIn.getClient(this.requireContext(),gsoo)

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



}
