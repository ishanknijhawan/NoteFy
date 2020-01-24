package com.ishanknijhawan.notefy.ui

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.ishanknijhawan.notefy.Adapter.NoteAdapter
import com.ishanknijhawan.notefy.Db.NoteDatabase
import com.ishanknijhawan.notefy.Entity.Note
import com.ishanknijhawan.notefy.R
import kotlinx.android.synthetic.main.activity_text_note.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnFastChooseColorListener
import java.util.*


class TextNoteActivity : AppCompatActivity() {

    val db : NoteDatabase by lazy {
        Room.databaseBuilder(this,
            NoteDatabase::class.java,
            "note.db"
        ).allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
    var noteList= arrayListOf<Note>()

        lateinit var titleNote: EditText
        lateinit var contentNote: EditText
        var noteColor: Int = 0
        var finalColor: Int = -1
        var dateTime = ""
        var mYear: Int = 0
        var mMonth: Int = 0
        var mDay: Int= 0
        var mHour: Int= 0
        var mMinute: Int= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_note)

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        val bottomSheetBehavior2 = BottomSheetBehavior.from(bottomSheet2)

        iv_reminder.setOnClickListener {
            datePickerFunction()
        }

        addMenu.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        menu_menu.setOnClickListener {
            bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
        }

        reminder_chip.setOnCheckedChangeListener { compoundButton, b ->
            Toast.makeText(this,"Checked $b",Toast.LENGTH_SHORT).show()
        }

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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

        bottomSheetBehavior2.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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

        val rq = intent.getStringExtra("REQUEST_CODE")
        titleNote = findViewById(R.id.et_note_title)
        contentNote = findViewById(R.id.et_note_content)

        iv_back.setOnClickListener {
            if (rq == "poochi"){
                updateNote()
            }
            else {
                saveNote()
            }
        }

        iv_archive.setOnClickListener {
            Snackbar.make(it, "Added to archive", Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.parseColor("#FFA500"))
                .setAction("Undo")
                { Toast.makeText(this@TextNoteActivity,"clicked undo",Toast.LENGTH_SHORT).show() }
                .show()
        }

        iv_pin.setOnClickListener {
            Snackbar.make(it,"Note pinned", Snackbar.LENGTH_SHORT).show()
        }

        if (rq == "poochi"){
            val title = intent.getStringExtra("INTENT_TITLE")
            val description = intent.getStringExtra("INTENT_NOTE")
            //val color = intent.getIntExtra("INTENT_COLOR",Color.parseColor("#ffffff"))
            finalColor = intent.getIntExtra("INTENT_COLOR",-1)

            if (finalColor == 0)
                finalColor = -1


            Log.i("COLOR","color is $finalColor")
            if (finalColor != -1) {
                window.navigationBarColor = finalColor
            }

            Log.i("QWE","value of title in TNA is $title")
            Log.i("QWE","value of note in TNA is $description")
            Log.i("QWE","value of color in TNA is $finalColor")

            titleNote.setText(title)
            contentNote.setText(description)

            lll.backgroundColor = finalColor
            ll_bs1.backgroundColor = finalColor
            ll_bs2.backgroundColor = finalColor
            ll_toolbar1.backgroundColor = finalColor
            titleNote.backgroundColor = finalColor
            contentNote.backgroundColor = finalColor
            window.statusBarColor = darkenColor(finalColor)
            cl_textNote.backgroundColor = finalColor
            reminder_chip.chipBackgroundColor = ColorStateList.valueOf(finalColor)
            reminder_chip.chipStrokeColor = ColorStateList.valueOf(darkenColor(finalColor))

            ColorPicker(this).setDefaultColorButton(finalColor)

            if (finalColor != -1)
            window.navigationBarColor = darkenColor(finalColor)
        }

        val rb = resources.obtainTypedArray(R.array.rainbow)

        bs_colours.setOnClickListener {

            val colorPicker = ColorPicker(this)
            colorPicker.setOnFastChooseColorListener(object : OnFastChooseColorListener {
                override fun setOnFastChooseColorListener(
                    position: Int,
                    color: Int
                ) { // put code
                    finalColor = rb.getColor(position,-1)
                    setColors(rb,finalColor)
                }

                override fun onCancel() { // put code
                    colorPicker.dismissDialog()
                }
            })
                .setColors(R.array.rainbow)
                .setColumns(4)
                .setTitlePadding(5,5,10,10)
                .setColorButtonTickColor(Color.parseColor("#222222"))
                .setDefaultColorButton(finalColor)
                //.setColorButtonSize(40,40)
                .setColorButtonMargin(12,5,12,5)
                .setTitle("Note color")
                .setRoundColorButton(true)
                .show()
        }

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
                //*************Call Time Picker Here ********************
                tiemPicker()
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }

    private fun tiemPicker() { // Get Current Time
        val c = Calendar.getInstance()
        mHour = c[Calendar.HOUR_OF_DAY]
        mMinute = c[Calendar.MINUTE]
        // Launch Time Picker Dialog
        val timePickerDialog = TimePickerDialog(
            this,
            OnTimeSetListener { view, hourOfDay, minute ->
                mHour = hourOfDay
                mMinute = minute
                Toast.makeText(this,"$dateTime $hourOfDay:$minute",Toast.LENGTH_SHORT).show()
            }, mHour, mMinute, false
        )
        timePickerDialog.show()
    }

    private fun setColors(rb: TypedArray, color: Int) {
        lll.backgroundColor = color
        et_note_title.backgroundColor = color
        et_note_content.backgroundColor = color
        ll_bs1.backgroundColor = color
        ll_bs2.backgroundColor = color
        ll_toolbar1.backgroundColor = color
        cl_textNote.backgroundColor = color
        reminder_chip.chipBackgroundColor = ColorStateList.valueOf(color)
        reminder_chip.chipStrokeColor = ColorStateList.valueOf(darkenColor(color))

        if (color != -1)
        window.navigationBarColor = darkenColor(color)
        else
            window.navigationBarColor = Color.parseColor("#000000")
        window.statusBarColor = darkenColor(color)

        rb.recycle()
    }

    @ColorInt
    fun darkenColor(@ColorInt color: Int): Int {
        return Color.HSVToColor(FloatArray(3).apply {
            Color.colorToHSV(color, this)
            this[2] *= 0.9f
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val rq = intent.getStringExtra("REQUEST_CODE")
        super.onBackPressed()
        if (rq == "poochi"){
            updateNote()
        }
        else {
            saveNote()
        }

    }

    private fun saveNote() {
        val finalTitle = titleNote.text.toString()
        val content = contentNote.text.toString()

        if (finalColor != -1)
            window.statusBarColor = darkenColor(finalColor)

        if (finalTitle == "" && content == "") {
            Toast.makeText(this,"Empty note discarded",Toast.LENGTH_SHORT).show()
            //Snackbar.make(lll,"Empty note discarded",Snackbar.LENGTH_SHORT).show()
        }

        else {
            db.noteDao().insert(
                Note(title = finalTitle,
                    description = content,
                    archive = false,
                    label = "home",
                    bookmark = false,
                    color = finalColor)
            )

            Log.i("TAG","value of title is $finalTitle")
            Log.i("TAG","value of description is $content")
            Log.i("TAG","value of color while saving is $noteColor")
            finish()

            noteList = db.noteDao().getAllNotes() as ArrayList<Note>
            NoteAdapter(noteList,this).updateTasks(noteList)

        }

        startActivity(intentFor<MainActivity>().newTask().clearTask())
    }

    private fun updateNote() {
        val finalTitle = titleNote.text.toString()
        val content = contentNote.text.toString()
        val finalColor2 = intent.getIntExtra("INTENT_COLOR",-1)

        val note = Note(title = finalTitle,
            description = content,
            archive = false,
            label = "home",
            bookmark = false,
            color = finalColor)

        note.id = intent.getLongExtra("INTENT_NOTE_ID",-1)

        if (finalTitle == "" && content == "") {
            Toast.makeText(this,"Empty note discarded",Toast.LENGTH_SHORT).show()
            //Snackbar.make(lll,"Empty note discarded",Snackbar.LENGTH_SHORT).show()
        }

        else {
            db.noteDao().update(note)

            Log.i("TAG","value of title is $finalTitle")
            Log.i("TAG","value of description is $content")
            Log.i("TAG","value of color while updating is $noteColor")

            noteList = db.noteDao().getAllNotes() as ArrayList<Note>
            NoteAdapter(noteList,this).updateTasks(noteList)
        }

//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//        this.finish()
        startActivity(intentFor<MainActivity>().newTask().clearTask())
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        //return super.onCreateOptionsMenu(menu)
//        val inflator = menuInflater
//        inflator.inflate(R.menu.text_note_menu,menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val parentLayout = findViewById<View>(android.R.id.content)
//        val rq = intent.getStringExtra("REQUEST_CODE")
//        setContentView(R.layout.activity_text_note)
//        if (item.itemId == R.id.action_check_save) {
//            if (rq == "poochi"){
//                updateNote()
//            }
//            else {
//                saveNote()
//            }
////            intent.putExtra("TITLE",et_note_title.text.toString())
////            intent.putExtra("NOTE",et_note_content.text.toString())
//        }
//        else if (item.itemId == R.id.action_check_pin) {
//            Snackbar.make(parentLayout,"Note pinned",Snackbar.LENGTH_SHORT).show()
//        }
//        else if (item.itemId == R.id.action_check_archive) {
//            Snackbar.make(parentLayout, "Added to archive", Snackbar.LENGTH_LONG)
//                .setActionTextColor(Color.parseColor("#FFA500"))
//                .setAction("Undo")
//                { Toast.makeText(this@TextNoteActivity,"clicked undo",Toast.LENGTH_SHORT).show() }
//                .show()
//        }
//        return super.onOptionsItemSelected(item)
//    }

}
