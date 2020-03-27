package com.ishanknijhawan.notefy.Adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ishanknijhawan.notefy.Entity.Inception
import com.ishanknijhawan.notefy.R
import kotlinx.android.synthetic.main.card_ist_note_layout.view.*
import org.jetbrains.anko.textColor

class CardListAdapter(val items: MutableList<Inception>, val context: Context)
    : RecyclerView.Adapter<ViewHolder3>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder3 {
        return ViewHolder3(LayoutInflater.from(context).inflate(R.layout.card_ist_note_layout, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder3, position: Int) {
        holder.textView.text = items[position].inputName

        if (items[position].inputCheck)
            holder.checkBox.setImageResource(R.drawable.ic_checkbox_card)
        else
            holder.checkBox.setImageResource(R.drawable.ic_check_box_empty_card)

        holder.checkBox.isEnabled = false

        if (items[position].inputCheck){
            holder.textView.paintFlags = holder.textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
    }

}

class ViewHolder3(view: View): RecyclerView.ViewHolder(view) {
    val checkBox: ImageView = view.cb_checkbox_card
    val textView: TextView = view.tv_card_list
}
