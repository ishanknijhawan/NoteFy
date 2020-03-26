package com.ishanknijhawan.notefy.Adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ishanknijhawan.notefy.Entity.BooleanHelper
import com.ishanknijhawan.notefy.Entity.Inception
import com.ishanknijhawan.notefy.R
import com.ishanknijhawan.notefy.ui.TextNoteActivity
import kotlinx.android.synthetic.main.activity_text_note.view.*
import kotlinx.android.synthetic.main.checklist_note_layout.view.*
import org.jetbrains.anko.textColor

class CheckListAdapter(val items: MutableList<Inception>, val context: Context) :
    RecyclerView.Adapter<ViewHolder2>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder2 {
        return ViewHolder2(LayoutInflater.from(context).inflate(R.layout.checklist_note_layout, parent, false))
    }

    override fun getItemCount(): Int = items.size

//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.mainText.text = items[position]
//
//        holder.deleteThis.setOnClickListener {
//            items.removeAt(position)
//            notifyItemRemoved(position)
//            notifyItemRangeChanged(position, items.size)
//        }
//    }

    override fun onBindViewHolder(holder: ViewHolder2, position: Int) {
        holder.mainText.setText(items[position].inputName)
        holder.deleteButton.setOnClickListener {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
        }

        holder.checkBox.isChecked = items[position].inputCheck

        holder.checkBox.setOnClickListener {
            if (holder.checkBox.isChecked){
                holder.mainText.textColor = Color.parseColor("#696969")
                holder.mainText.paintFlags = holder.mainText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            items[position].inputCheck = holder.checkBox.isChecked
        }
        holder.editTextCB.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE){
                items[position].inputName = holder.editTextCB.text.toString()
            }
            return@setOnEditorActionListener true
        }
    }

}

class ViewHolder2(view: View) : RecyclerView.ViewHolder(view) {
    val checkBox: CheckBox = view.cb_checkbox
    val mainText = view.et_check_list
    val deleteButton = view.iv_delete
    val editTextCB = view.et_check_list
}
