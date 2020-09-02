package com.ishanknijhawan.notefy.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.divyanshu.draw.activity.DrawingActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.ishanknijhawan.notefy.Adapter.CheckListAdapter
import com.ishanknijhawan.notefy.Entity.Inception
import com.ishanknijhawan.notefy.Entity.Note
import com.ishanknijhawan.notefy.R
import com.ishanknijhawan.notefy.ViewModel.ViewModel
import io.github.ponnamkarthik.richlinkpreview.ViewListener
import kotlinx.android.synthetic.main.activity_text_note.*
import kotlinx.android.synthetic.main.fragment_main.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.hintTextColor
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnFastChooseColorListener
import java.io.*
import java.util.*


const val REQUEST_CODE_DRAW = 111
const val REQUEST_CODE_CAMERA = 7777
const val REQUEST_CODE_GALLERY = 69

class TextNoteActivity : AppCompatActivity() {

    lateinit var titleNote: EditText
    lateinit var contentNote: EditText
    lateinit var etAddCheck: EditText
    lateinit var viewModel: ViewModel
    lateinit var bitmap: Bitmap
    lateinit var outputStream: OutputStream
    var pathString: String = ""

    var finalColor: Int = -1

    var bigArchive: Boolean = false
    var kingPin: Boolean = false
    var bigDelete: Boolean = false
    var animals: MutableList<Inception> = mutableListOf()
    var touchHelper: ItemTouchHelper? = null

    var dateTime = ""
    var mYear: Int = 0
    var mMonth: Int = 0
    var mDay: Int = 0
    var mHour: Int = 0
    var mMinute: Int = 0

    val itemTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(UP or DOWN, END) {

                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {

                    val adapter = recyclerView.adapter as CheckListAdapter
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition

                    Collections.swap(animals, from, to)
                    // 3. Tell adapter to render the model update.
                    adapter.notifyItemMoved(from, to)

                    return true
                }

                override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if(actionState == ACTION_STATE_DRAG || actionState == ACTION_STATE_SWIPE){
                        viewHolder?.itemView?.elevation = 8F
                        //viewHolder?.itemView?.alpha = 1F
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.elevation = 0F
                    //viewHolder.itemView.alpha = 1F
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                                      direction: Int) {
                    animals.removeAt(viewHolder.adapterPosition)
                    (rv_check_list.adapter as CheckListAdapter).notifyDataSetChanged()
                }
            }
        ItemTouchHelper(simpleItemTouchCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_note)

        window.navigationBarColor = finalColor
        window.statusBarColor = finalColor
        ll_toolbar1.elevation = 16F

        viewModel = ViewModelProviders.of(this).get(ViewModel::class.java)

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        val bottomSheetBehavior2 = BottomSheetBehavior.from(bottomSheet2)

        titleNote = findViewById(R.id.et_note_title)
        contentNote = findViewById(R.id.et_note_content)
        etAddCheck = findViewById(R.id.et_add_note)

        rv_check_list.visibility = View.GONE
        rv_check_list_done.visibility = View.GONE
        etAddCheck.visibility = View.GONE
        divider.visibility = View.GONE
        tv_status.visibility = View.GONE
        ivAddTNA.visibility = View.GONE
        richLinkView.visibility = View.GONE

        val rq = intent.getStringExtra("REQUEST_CODE")
        val rq2 = intent.getStringExtra("CHECK_LIST")
        val rq3 = intent.getStringExtra("DRAW")
        val rq4 = intent.getStringExtra("IMAGE_CAMERA")
        val rq5 = intent.getStringExtra("IMAGE_FILE")
        val rq6 = intent.getStringArrayListExtra("AUDIO")

        val cardSize = intent.getIntExtra("CARD_SIZE", 0)
        val pathStringFromMain = intent.getStringExtra("PATH_TO_IMAGE")

        if (rq2 == "opened_from_check_list") {
            animals = mutableListOf()
            rv_check_list.visibility = View.VISIBLE
            etAddCheck.visibility = View.VISIBLE
            contentNote.visibility = View.GONE

            rv_check_list.layoutManager = LinearLayoutManager(this)
            rv_check_list.adapter =
                CheckListAdapter(animals, this)
            etAddCheck.setOnEditorActionListener(onEditorListener)

        }

        if (rq3 == "opened_from_drawing") {
            val intent = Intent(this, DrawingActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_DRAW)
        }

        bs_recording.setOnClickListener {
            if ((ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                        != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                        != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.RECORD_AUDIO
                )
                        != PackageManager.PERMISSION_GRANTED)
            ) {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.RECORD_AUDIO
                    ), 1006
                )
            } else {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Try saying something!")

                try {
                    startActivityForResult(intent, 1009)
                }catch (e: java.lang.Exception){
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        bs_draw.setOnClickListener {
            if ((ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                        != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                        != PackageManager.PERMISSION_GRANTED)
            ) {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 1000
                )
            } else {
                val intent = Intent(this, DrawingActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_DRAW)
            }
        }

        if (rq4 == "opened_from_camera") {
            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CODE_CAMERA)
        }

        bs_camera.setOnClickListener {
            if ((ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                        != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                        != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                )
                        != PackageManager.PERMISSION_GRANTED)
            ) {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA
                    ), 1001
                )
            } else {
                val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_CODE_CAMERA)
            }
        }

        if (rq5 == "opened_from_file") {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY)
        }

        try {
            if (rq6!!.size > 0){
                contentNote.setText(rq6[0])
            }
        } catch (e:NullPointerException){}

        bs_choose_file.setOnClickListener {
            if ((ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                        != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                        != PackageManager.PERMISSION_GRANTED)
            ) {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 1002
                )
            } else {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY)
            }
        }

        if (rq == "opened_from_main_activity") {
            animals = mutableListOf()
            val arc = intent.getStringExtra("ARC")
            bigArchive = arc == "true"

            val bool = intent.getStringExtra("BOOL")
            kingPin = bool == "true"

            if (bool == "true") {
                iv_pin.setImageResource(android.R.color.transparent)
                iv_pin.setImageResource(R.drawable.ic_thumbtack)
            } else if (bool == "false") {
                Log.i("BG", "setting this bg")
                iv_pin.setImageResource(android.R.color.transparent)
                iv_pin.setImageResource(R.drawable.ic_tack_save_button_empty)
            }

            if (pathStringFromMain != "") {
                ivAddTNA.visibility = View.VISIBLE
                bs_camera.visibility = View.GONE
                bs_choose_file.visibility = View.GONE
                bs_draw.visibility = View.GONE

                pathString = pathStringFromMain!!
                val imgFile = File(pathStringFromMain)
                if (imgFile.exists()) {
                    val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                    ivAddTNA.setImageBitmap(bitmap)
                }
            }

            val title = intent.getStringExtra("INTENT_TITLE")
            val description = intent.getStringExtra("INTENT_NOTE")
            finalColor = intent.getIntExtra("INTENT_COLOR", -1)

            val connectivityManager: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            val check =  activeNetworkInfo != null && activeNetworkInfo.isConnected

//            if(check){
//                if (description!!.contains("https")){
//                    //TODO: Enable rich link previews here :)
//                    richLinkView.setLink(
//                        description,
//                        object : ViewListener {
//                            override fun onSuccess(status: Boolean) {
//                                //Toast.makeText(this@TextNoteActivity, "converted", Toast.LENGTH_SHORT).show()
//                            }
//
//                            override fun onError(e: Exception) {
//                                Toast.makeText(
//                                    this@TextNoteActivity,
//                                    e.printStackTrace().toString(),
//                                    Toast.LENGTH_LONG
//                                ).show()
//                            }
//                        })
//                    richLinkView.visibility = View.VISIBLE
//                }
//            }

            if (finalColor == 0)
                finalColor = -1

            titleNote.setText(title)
            contentNote.setText(description)

            titleNote.hintTextColor = darkenColorHint(finalColor)
            contentNote.hintTextColor = darkenColorHint(finalColor)
            etAddCheck.hintTextColor = darkenColorHint(finalColor)

            lll.backgroundColor = finalColor
            bottomSheet.backgroundColor = finalColor
            bottomSheet2.backgroundColor = finalColor
            ll_toolbar1.backgroundColor = finalColor
            titleNote.backgroundColor = finalColor
            contentNote.backgroundColor = finalColor
            window.statusBarColor = finalColor
            window.navigationBarColor = finalColor
            cl_textNote.backgroundColor = finalColor
            reminder_chip.chipBackgroundColor = ColorStateList.valueOf(finalColor)
            reminder_chip.chipStrokeColor = ColorStateList.valueOf(darkenColor(finalColor))
            etAddCheck.backgroundColor = finalColor

            ColorPicker(this).setDefaultColorButton(finalColor)


            if (cardSize > 0) {
                rv_check_list.visibility = View.VISIBLE
                etAddCheck.visibility = View.VISIBLE
                contentNote.visibility = View.GONE

                rv_check_list.layoutManager = LinearLayoutManager(this)
                rv_check_list.adapter =
                    CheckListAdapter(animals, this)
                etAddCheck.setOnEditorActionListener(onEditorListener)

                animals.add(
                    Inception(
                        intent.getStringExtra("0"),
                        intent.getBooleanExtra("FIRST_CHECK", false)
                    )
                )

                for (i in 1 until cardSize) {
                    animals.add(
                        Inception(
                            intent.getStringExtra(i.toString()),
                            intent.getBooleanExtra((-i).toString(), false)
                        )
                    )
                }
            }

            if (arc == "false") {
                iv_archive.setImageResource(android.R.color.transparent)
                iv_archive.setImageResource(R.drawable.ic_archive_black_24dp)
            } else if (arc == "true") {
                Log.i("BG", "setting this bg")
                iv_archive.setImageResource(android.R.color.transparent)
                iv_archive.setImageResource(R.drawable.ic_unarchive_black_24dp)
            }
        } else {
            iv_pin.setImageResource(R.drawable.ic_tack_save_button_empty)
            iv_archive.setImageResource(R.drawable.ic_archive_black_24dp)
        }

        iv_reminder.setOnClickListener {
            datePickerFunction()
        }

        addMenu.setOnClickListener {
            if (cardSize > 0)
                bs_tickBoxes.text = "Hide tickboxes"
            else
                bs_tickBoxes.text = "Tick boxes"

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        menu_menu.setOnClickListener {
            bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
        }

        reminder_chip.setOnCheckedChangeListener { compoundButton, b ->
            Toast.makeText(this, "Checked $b", Toast.LENGTH_SHORT).show()
        }
        //test commit
        reminder_chip.visibility = View.GONE

        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // React to state change
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        addMenu.setImageResource(R.drawable.ic_add_square_button)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        //tv_state.text = "Expanded"
                        //addMenu.setImageResource(R.drawable.ic_downarrow)
                        addMenu.setOnClickListener {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
//                        menu_menu.setOnClickListener {
//                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//                            bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
//                        }
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        //tv_state.text = "collapsed"
                        addMenu.setImageResource(R.drawable.ic_add_square_button)
                        addMenu.setOnClickListener {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {
                        //tv_state.text = "dragging..."
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        //tv_state.text = "settling"
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // React to dragging events
            }
        })

        bottomSheetBehavior2.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // React to state change
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        menu_menu.setImageResource(R.drawable.ic_menu)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        //tv_state.text = "Expanded"
                        //menu_menu.setImageResource(R.drawable.ic_downarrow)
//                        addMenu.setOnClickListener {
//                            bottomSheetBehavior2.state = BottomSheetBehavior.STATE_COLLAPSED
//                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//                        }
                        menu_menu.setOnClickListener {
                            bottomSheetBehavior2.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        //tv_state.text = "collapsed"
                        menu_menu.setImageResource(R.drawable.ic_menu)
                        menu_menu.setOnClickListener {
                            bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {
                        //tv_state.text = "dragging..."
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        //tv_state.text = "settling"
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // React to dragging events
            }
        })

        iv_back.setOnClickListener {
            if (rq == "opened_from_main_activity") {
                updateNote()
            } else {
                saveNote()
            }
            finish()
        }

        iv_archive.setOnClickListener {

            when {
                !bigArchive && !kingPin -> {
                    iv_archive.setImageResource(R.drawable.ic_unarchive_black_24dp)
                    bigArchive = true
                    Toast.makeText(this, "Note Archived", Toast.LENGTH_SHORT).show()
                    if (rq == "opened_from_main_activity") {
                        updateNote()
                    } else {
                        saveNote()
                    }
                }
                kingPin -> {
                    iv_archive.setImageResource(R.drawable.ic_unarchive_black_24dp)
                    bigArchive = true
                    kingPin = false
                    Toast.makeText(this, "Note unpinned and Archived", Toast.LENGTH_SHORT).show()
                    if (rq == "opened_from_main_activity") {
                        updateNote()
                    } else {
                        saveNote()
                    }
                }
                else -> {
                    iv_archive.setImageResource(R.drawable.ic_archive_black_24dp)
                    bigArchive = false
                    Toast.makeText(this, "Note Unarchived", Toast.LENGTH_SHORT).show()
                    if (rq == "opened_from_main_activity") {
                        updateNote()
                    } else {
                        saveNote()
                    }
                }
            }

            finish()
        }

        bs_delete.setOnClickListener {
            bigDelete = true

            when {
                bigArchive -> {
                    bigArchive = false
                    Toast.makeText(this, "Note unarchived and Trashed", Toast.LENGTH_SHORT).show()
                }
                kingPin -> {
                    kingPin = false
                    Toast.makeText(this, "Note unpinned and Trashed", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Note moved to bin", Toast.LENGTH_SHORT).show()
                }
            }

            if (rq == "opened_from_main_activity") {
                updateNote()
            } else {
                saveNote()
            }
            finish()
        }

        bs_duplicate.setOnClickListener {
            if (contentNote.text.isEmpty()){
                Snackbar.make(lll, "Can't generate copy of an empty note", Snackbar.LENGTH_LONG)
                    .setAnchorView(bottomSheet2)
                    .show()
            }
            else {
                saveNote()
                Snackbar.make(lll, "Duplicate note generated", Snackbar.LENGTH_SHORT)
                    .setAnchorView(bottomSheet2)
                    .show()
            }
        }

        bs_share.setOnClickListener {
            if (contentNote.text.isEmpty() && titleNote.text.isEmpty())
                Snackbar.make(lll, "Can't share empty note", Snackbar.LENGTH_LONG)
                    .setAnchorView(bottomSheet2)
                    .show()
            else{
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, contentNote.text.toString())
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        }

        bs_tickBoxes.setOnClickListener {
            if (bs_tickBoxes.text == "Hide tickboxes") {
                var text = ""
                for (i in 0 until cardSize) {
                    text += "${animals[i].inputName}\n"
                }
                rv_check_list.visibility = View.GONE
                etAddCheck.visibility = View.GONE
                contentNote.visibility = View.VISIBLE
                contentNote.setText(text)
                animals = mutableListOf()
                bs_tickBoxes.text = "Tick boxes"

            } else {
                val list = contentNote.text.toString().split("\n")
                for (i in list.indices) {
                    animals.add(
                        Inception(
                            list[i],
                            false
                        )
                    )
                }
                rv_check_list.visibility = View.VISIBLE
                etAddCheck.visibility = View.VISIBLE
                contentNote.visibility = View.GONE
                contentNote.setText("")

                rv_check_list.layoutManager = LinearLayoutManager(this)
                rv_check_list.adapter =
                    CheckListAdapter(animals, this)
                etAddCheck.setOnEditorActionListener(onEditorListener)
                bs_tickBoxes.text = "Hide tickboxes"
            }
        }


        iv_pin.setOnClickListener {

            when {
                !kingPin && !bigArchive -> {
                    iv_pin.setImageResource(R.drawable.ic_thumbtack)
                    kingPin = true
                }

                bigArchive -> {
                    iv_archive.setImageResource(R.drawable.ic_archive_black_24dp)
                    iv_pin.setImageResource(R.drawable.ic_thumbtack)
                    kingPin = true
                    bigArchive = false
                    Toast.makeText(this, "Note unarchived and Pinned", Toast.LENGTH_SHORT).show()
                    if (rq == "opened_from_main_activity") {
                        updateNote()
                    } else {
                        saveNote()
                    }
                    finish()
                }
                else -> {
                    iv_pin.setImageResource(R.drawable.ic_tack_save_button_empty)
                    kingPin = false
                    //Toast.makeText(this,"Note unpinned",Toast.LENGTH_SHORT).show()
                }
            }

//            if (iv_pin.background.constantState == resources.getDrawable(R.drawable.ic_tack_save_button_empty).constantState){
//                iv_pin.setImageResource(R.drawable.ic_thumbtack)
//                kingPin = true
//                updateNote()
//            }
//            else if(iv_pin.background.constantState == resources.getDrawable(R.drawable.ic_thumbtack).constantState) {
//                iv_pin.setImageResource(R.drawable.ic_tack_save_button_empty)
//                kingPin = false
//                updateNote()
//            }

        }


        val rb = resources.obtainTypedArray(R.array.rainbow)

        bs_colours.setOnClickListener {

            val colorPicker = ColorPicker(this)
            colorPicker.setOnFastChooseColorListener(object : OnFastChooseColorListener {
                override fun setOnFastChooseColorListener(
                    position: Int,
                    color: Int
                ) { // put code
                    finalColor = rb.getColor(position, -1)
                    setColors(rb, finalColor)
                }

                override fun onCancel() { // put code
                    colorPicker.dismissDialog()
                }
            })
                .setColors(R.array.rainbow)
                .setColumns(4)
                .setTitlePadding(5, 5, 10, 10)
                .setColorButtonTickColor(Color.parseColor("#222222"))
                .setDefaultColorButton(finalColor)
                .setColorButtonMargin(12, 5, 12, 5)
                .setTitle("Note color")
                .setRoundColorButton(true)
                .show()
        }

        itemTouchHelper.attachToRecyclerView(rv_check_list)

        touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            UP or DOWN, RIGHT){

            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder
            ): Boolean {
                val sourcePosition = p1.adapterPosition
                val targetPosition = p2.adapterPosition
                Collections.swap(animals, sourcePosition, targetPosition)
                (rv_check_list.adapter as CheckListAdapter).notifyItemMoved(sourcePosition, targetPosition)
                return true
            }

            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                var buffer = animals[p0.adapterPosition].inputName
                var bufferCheck = animals[p0.adapterPosition].inputCheck

                animals.removeAt(p0.adapterPosition)
                (rv_check_list.adapter as CheckListAdapter).notifyDataSetChanged()
                //(rv_check_list.adapter as CheckListAdapter).notifyItemRemoved(p1)
                //(rv_check_list.adapter as CheckListAdapter).notifyItemRangeChanged(p1, animals.size)
            }

        })

        //touchHelper!!.attachToRecyclerView(rv_check_list)
    }

    fun startDragging(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    var onEditorListener = TextView.OnEditorActionListener { textView, i, keyEvent ->
        if (i == EditorInfo.IME_ACTION_DONE) {
            if (etAddCheck.text.isEmpty())
                Toast.makeText(this, " Invalid input ", Toast.LENGTH_SHORT).show()
            else {
                animals.add(animals.size, Inception(etAddCheck.text.toString(), false))
                CheckListAdapter(animals, this).notifyItemInserted(animals.size)
                (rv_check_list.adapter as CheckListAdapter).notifyDataSetChanged()
                rv_check_list.scrollToPosition(animals.size)
                etAddCheck.setText("")
            }
        }
        return@OnEditorActionListener true
    }


    private fun datePickerFunction() { // Get Current Date
        val c: Calendar = Calendar.getInstance()
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                dateTime = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                //Call Time Picker Here
                timePicker()
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }

    private fun timePicker() { // Get Current Time
        val c = Calendar.getInstance()
        mHour = c[Calendar.HOUR_OF_DAY]
        mMinute = c[Calendar.MINUTE]
        // Launch Time Picker Dialog
        val timePickerDialog = TimePickerDialog(
            this,
            OnTimeSetListener { view, hourOfDay, minute ->
                mHour = hourOfDay
                mMinute = minute
                Toast.makeText(this, "$dateTime $hourOfDay:$minute", Toast.LENGTH_SHORT).show()
            }, mHour, mMinute, false
        )
        timePickerDialog.show()
    }

    private fun setColors(rb: TypedArray, color: Int) {
        lll.backgroundColor = color
        et_note_title.backgroundColor = color
        et_note_content.backgroundColor = color
        bottomSheet.backgroundColor = color
        bottomSheet2.backgroundColor = color
        ll_toolbar1.backgroundColor = color
        cl_textNote.backgroundColor = color
        reminder_chip.chipBackgroundColor = ColorStateList.valueOf(color)
        reminder_chip.chipStrokeColor = ColorStateList.valueOf(darkenColor(color))
        rv_check_list.backgroundColor = color
        etAddCheck.backgroundColor = color

        titleNote.hintTextColor = darkenColorHint(color)
        contentNote.hintTextColor = darkenColorHint(color)
        etAddCheck.hintTextColor = darkenColorHint(color)


        if (color != -1)
            window.navigationBarColor = color
        else
            window.navigationBarColor = Color.parseColor("#FFFFFF")
        window.statusBarColor = color

        rb.recycle()
    }

    @ColorInt
    fun darkenColor(@ColorInt color: Int): Int {
        return Color.HSVToColor(FloatArray(3).apply {
            Color.colorToHSV(color, this)
            this[2] *= 0.9f
        })
    }

    @ColorInt
    fun darkenColorHint(@ColorInt color: Int): Int {
        return Color.HSVToColor(FloatArray(3).apply {
            Color.colorToHSV(color, this)
            this[2] *= 0.7f
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val rq = intent.getStringExtra("REQUEST_CODE")
        if (rq == "opened_from_main_activity") {
            updateNote()
        } else {
            saveNote()
        }
        finish()
        super.onBackPressed()
    }

    private fun saveNote() {

        val finalTitle = titleNote.text.toString()
        val content = contentNote.text.toString()

        if (finalColor != -1)
            window.statusBarColor = finalColor

        if (finalTitle.isEmpty() && content.isEmpty() && animals.isEmpty() && pathString.isEmpty()) {
            Toast.makeText(this, "Empty note discarded", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.insert(
                Note(
                    title = finalTitle,
                    description = content,
                    archive = bigArchive,
                    label = "home",
                    pinned = kingPin,
                    deleted = bigDelete,
                    color = finalColor,
                    checkList = animals,
                    pathToImage = pathString
                )
            )

        }
    }

    private fun updateNote() {

        val finalTitle = titleNote.text.toString()
        val content = contentNote.text.toString()

        val note = Note(
            title = finalTitle,
            description = content,
            archive = bigArchive,
            label = "home",
            pinned = kingPin,
            deleted = bigDelete,
            color = finalColor,
            checkList = animals,
            pathToImage = pathString
        )

        note.id = intent.getLongExtra("INTENT_NOTE_ID", -1)

        if (finalTitle == "" && content == "" && animals.isEmpty() && pathString.isEmpty()) {
            viewModel.delete(note)
            Toast.makeText(this, "Empty note discarded", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.update(note)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (data != null && resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_DRAW -> {
                    val result = data.getByteArrayExtra("bitmap")
                    bitmap = BitmapFactory.decodeByteArray(result, 0, result!!.size)
                    ivAddTNA.setImageBitmap(bitmap)
                    ivAddTNA.visibility = View.VISIBLE
                    saveImage(bitmap)
                }
                REQUEST_CODE_CAMERA -> {
                    val bitmap2 = data.extras?.get("data") as Bitmap
                    ivAddTNA.setImageBitmap(bitmap2)
                    ivAddTNA.visibility = View.VISIBLE
                    saveImage(bitmap2)
                }
                REQUEST_CODE_GALLERY -> {
                    val imageUri: Uri? = data.data
                    val imageStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    ivAddTNA.setImageBitmap(selectedImage)
                    ivAddTNA.visibility = View.VISIBLE

                    val filepath = Environment.getExternalStorageDirectory()
                    val dir = File(filepath.absolutePath + "/NotiFy/")
                    dir.mkdir()
                    val currentTime = System.currentTimeMillis().toString()
                    val file = File(dir, "$currentTime.jpg")
                    pathString = filepath.absolutePath + "/NotiFy/" + "$currentTime.jpg"
                    try {
                        outputStream = FileOutputStream(file)
                    }catch (e: FileNotFoundException){
                        e.printStackTrace()
                    }
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                    outputStream.flush()
                    outputStream.close()
                }
                1009 -> {
                    if (resultCode == Activity.RESULT_OK){
                        try {
                            val result: ArrayList<String>? = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                            var temp = ""
                            contentNote.setText(result!![0])
                        } catch (e: NullPointerException){}
                    }
                }
                else -> {
                    TextNoteActivity().finish()
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    val intent = Intent(this, DrawingActivity::class.java)
                    startActivityForResult(intent, REQUEST_CODE_DRAW)

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            1002 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    val photoPickerIntent = Intent(Intent.ACTION_PICK)
                    photoPickerIntent.type = "image/*"
                    startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY)

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            1001 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                ) {
                    val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, REQUEST_CODE_CAMERA)

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            1006 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                ) {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Try saying something!")

                    try {
                        startActivityForResult(intent, 1009)
                    }catch (e: java.lang.Exception){
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun saveImage(bitmap: Bitmap) {
        val filepath = Environment.getExternalStorageDirectory()
        val dir = File(filepath.absolutePath + "/NotiFy/")
        dir.mkdir()
        val currentTime = System.currentTimeMillis().toString()
        val file = File(dir, "$currentTime.jpg")
        pathString = filepath.absolutePath + "/NotiFy/" + "$currentTime.jpg"
        try {
            outputStream = FileOutputStream(file)
        }catch (e: FileNotFoundException){
            e.printStackTrace()
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }

}
