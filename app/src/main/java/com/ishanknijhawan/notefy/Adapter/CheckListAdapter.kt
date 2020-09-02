package com.ishanknijhawan.notefy.Adapter

import android.content.Context
import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ishanknijhawan.notefy.Entity.Inception
import com.ishanknijhawan.notefy.R
import com.ishanknijhawan.notefy.ui.TextNoteActivity
import kotlinx.android.synthetic.main.checklist_note_layout.view.*
import org.jetbrains.anko.matchParent


class CheckListAdapter(val items: MutableList<Inception>, val context: TextNoteActivity) :
    RecyclerView.Adapter<ViewHolder2>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder2 {
        val itemView = (LayoutInflater.from(context).inflate(R.layout.checklist_note_layout, parent, false))
        val viewHolder = ViewHolder2(itemView)

        viewHolder.itemView.ivDrag.setOnTouchListener { view, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                context.startDragging(viewHolder)
            }
            return@setOnTouchListener true
        }

        return viewHolder
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder2, position: Int) {
        holder.mainText.setText(items[position].inputName)

//        holder.deleteButton.setOnClickListener {
//            items.removeAt(position)
//            notifyItemRemoved(position)
//            notifyItemRangeChanged(position, items.size)
//        }

        holder.checkBox.isChecked = items[position].inputCheck

        if (holder.checkBox.isChecked){
            holder.mainText.paintFlags = holder.mainText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        holder.checkBox.setOnClickListener {
            items[position].inputCheck = holder.checkBox.isChecked
            if (holder.checkBox.isChecked){
                holder.mainText.paintFlags = holder.mainText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            else
                holder.mainText.paintFlags = (holder.mainText.paintFlags) and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
//        holder.editTextCB.setOnEditorActionListener { textView, i, keyEvent ->
//            if (i == EditorInfo.IME_ACTION_DONE){
//                items[position].inputName = holder.editTextCB.text.toString()
//                Toast.makeText(context,"item updated", Toast.LENGTH_SHORT).show()
//                //items.add(position+1, Inception("", false))
//                //notifyDataSetChanged()
//            }
//            return@setOnEditorActionListener true
//        }

        holder.editTextCB.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                items[position].inputName = p0.toString()
            }

        })
    }

}

class ViewHolder2(view: View) : RecyclerView.ViewHolder(view) {
    val checkBox: CheckBox = view.cb_checkbox
    val mainText = view.et_check_list
    //val deleteButton = view.iv_delete
    val editTextCB = view.et_check_list
}
