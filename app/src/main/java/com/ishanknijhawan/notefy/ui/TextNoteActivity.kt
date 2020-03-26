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
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ishanknijhawan.notefy.Adapter.CheckListAdapter
import com.ishanknijhawan.notefy.Entity.BooleanHelper
import com.ishanknijhawan.notefy.Entity.Inception
import com.ishanknijhawan.notefy.Entity.Note
import com.ishanknijhawan.notefy.R
import com.ishanknijhawan.notefy.ViewModel.ViewModel
import kotlinx.android.synthetic.main.activity_text_note.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.hintTextColor
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnFastChooseColorListener
import java.util.*
import kotlin.collections.ArrayList


class TextNoteActivity : AppCompatActivity() {

    lateinit var titleNote: EditText
    lateinit var contentNote: EditText
    lateinit var etAddCheck: EditText
    lateinit var viewModel: ViewModel
    var finalColor: Int = -1

    var bigArchive: Boolean = false
    var kingPin: Boolean = false
    var bigDelete: Boolean = false
    var animals: MutableList<Inception> = mutableListOf()

    var dateTime = ""
    var mYear: Int = 0
    var mMonth: Int = 0
    var mDay: Int = 0
    var mHour: Int = 0
    var mMinute: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_note)

        window.navigationBarColor = finalColor
        window.statusBarColor = finalColor

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

        val rq = intent.getStringExtra("REQUEST_CODE")
        val rq2 = intent.getStringExtra("CHECK_LIST")
        val cardSize = intent.getIntExtra("CARD_SIZE",0)

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

        if (rq == "opened_from_main_activity") {
            animals = mutableListOf()
            val arc = intent.getStringExtra("ARC")
            bigArchive = arc == "true"

            val bool = intent.getStringExtra("BOOL")
            kingPin = bool == "true"

            if (bool == "true") {
                iv_pin.setImageResource(android.R.color.transparent)
                iv_pin.setImageResource(R.drawable.ic_push_pin_black_final)
            } else if (bool == "false") {
                Log.i("BG", "setting this bg")
                iv_pin.setImageResource(android.R.color.transparent)
                iv_pin.setImageResource(R.drawable.ic_push_pin_final)
            }

            val title = intent.getStringExtra("INTENT_TITLE")
            val description = intent.getStringExtra("INTENT_NOTE")
            finalColor = intent.getIntExtra("INTENT_COLOR", -1)

            if (finalColor == 0)
                finalColor = -1

            titleNote.setText(title)
            contentNote.setText(description)

            titleNote.hintTextColor = darkenColorHint(finalColor)
            contentNote.hintTextColor = darkenColorHint(finalColor)
            etAddCheck.hintTextColor = darkenColorHint(finalColor)

            lll.backgroundColor = finalColor
            ll_bs1.backgroundColor = finalColor
            ll_bs2.backgroundColor = finalColor
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


            if (cardSize > 0){
                rv_check_list.visibility = View.VISIBLE
                etAddCheck.visibility = View.VISIBLE
                contentNote.visibility = View.GONE

                rv_check_list.layoutManager = LinearLayoutManager(this)
                rv_check_list.adapter =
                    CheckListAdapter(animals, this)
                etAddCheck.setOnEditorActionListener(onEditorListener)

                animals.add(Inception(intent.getStringExtra("0"), intent.getBooleanExtra("FIRST_CHECK", false)))

                for (i in 1 until cardSize){
                    animals.add(Inception(intent.getStringExtra(i.toString()), intent.getBooleanExtra((-i).toString(), false)))
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
            iv_pin.setImageResource(R.drawable.ic_push_pin_final)
            iv_archive.setImageResource(R.drawable.ic_archive_black_24dp)
        }

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
            Toast.makeText(this, "Checked $b", Toast.LENGTH_SHORT).show()
        }
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
                !bigArchive -> {
                    iv_archive.setImageResource(R.drawable.ic_unarchive_black_24dp)
                    bigArchive = true
                    Toast.makeText(this, "Note Archived", Toast.LENGTH_SHORT).show()
                    updateNote()
                }
                kingPin -> {
                    iv_archive.setImageResource(R.drawable.ic_unarchive_black_24dp)
                    bigArchive = true
                    Toast.makeText(this, "Note unpinned and Archived", Toast.LENGTH_SHORT).show()
                    updateNote()
                }
                else -> {
                    iv_archive.setImageResource(R.drawable.ic_archive_black_24dp)
                    bigArchive = false
                    Toast.makeText(this, "Note Unarchived", Toast.LENGTH_SHORT).show()
                    updateNote()
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

        if (cardSize > 0)
            bs_tickBoxes.text = "Hide tickboxes"
        else
            bs_tickBoxes.text = "Tick boxes"

        bs_tickBoxes.setOnClickListener {
            if (cardSize > 0){
                var text = ""
                for (i in 0 until cardSize){
                    text += "${animals[i].inputName}\n"
                }
                rv_check_list.visibility = View.GONE
                etAddCheck.visibility = View.GONE
                contentNote.visibility = View.VISIBLE
                contentNote.setText(text)
                animals = mutableListOf()
            }
            else {
                val list = contentNote.text.toString().split("\n")
                for (i in list.indices){
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
            }
        }


        iv_pin.setOnClickListener {

            when {
                !kingPin && !bigArchive -> {
                    iv_pin.setImageResource(R.drawable.ic_push_pin_black_final)
                    kingPin = true
                    Toast.makeText(this, "Note Pinned", Toast.LENGTH_SHORT).show()
                    updateNote()
                }
                bigArchive -> {
                    iv_archive.setImageResource(R.drawable.ic_archive_black_24dp)
                    iv_pin.setImageResource(R.drawable.ic_push_pin_black_final)
                    kingPin = true
                    bigArchive = false
                    Toast.makeText(this, "Note unarchived and Pinned", Toast.LENGTH_SHORT).show()
                    updateNote()
                    finish()
                }
                else -> {
                    iv_pin.setImageResource(R.drawable.ic_push_pin_final)
                    kingPin = false
                    //Toast.makeText(this,"Note unpinned",Toast.LENGTH_SHORT).show()
                    updateNote()
                }
            }

//            if (iv_pin.background.constantState == resources.getDrawable(R.drawable.ic_push_pin_final).constantState){
//                iv_pin.setImageResource(R.drawable.ic_push_pin_black_final)
//                kingPin = true
//                updateNote()
//            }
//            else if(iv_pin.background.constantState == resources.getDrawable(R.drawable.ic_push_pin_black_final).constantState) {
//                iv_pin.setImageResource(R.drawable.ic_push_pin_final)
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
        ll_bs1.backgroundColor = color
        ll_bs2.backgroundColor = color
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

        if (finalTitle.isEmpty() && content.isEmpty() && animals.isEmpty()) {
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
                    checkList = animals
                )
            )

        }
    }

    private fun updateNote() {
        val rq = intent.getStringExtra("REQUEST_CODE")

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
            checkList = animals
        )

        note.id = intent.getLongExtra("INTENT_NOTE_ID", -1)

        if (finalTitle == "" && content == "" && animals.isEmpty()) {
            viewModel.delete(note)
            Toast.makeText(this, "Empty note discarded", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.update(note)
        }
    }

}
