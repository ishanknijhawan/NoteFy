package com.ishanknijhawan.notefy.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ishanknijhawan.notefy.Db.NoteDatabase
import com.ishanknijhawan.notefy.Entity.Note
import com.ishanknijhawan.notefy.R
import com.ishanknijhawan.notefy.ui.MainActivity
import com.ishanknijhawan.notefy.ui.TextNoteActivity
import kotlinx.android.synthetic.main.activity_text_note.view.*
import kotlinx.android.synthetic.main.item_note_layout.view.*
import org.jetbrains.anko.backgroundColor


class NoteAdapter(var items: List<Note>, val context: Context)
    : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_note_layout,parent,false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mDrawable = ContextCompat.getDrawable(context, R.drawable.white_oval_background)
        mDrawable?.colorFilter = PorterDuffColorFilter(items[position].color,PorterDuff.Mode.MULTIPLY)

        holder.itemNoteLayout.background = mDrawable
        holder.tvTitleView.text = items[position].title
        holder.tvNoteView.text = items[position].description
        holder.itemNoteLayout.cardElevation = 0F



        if (items[position].color != -1)
            holder.itemNoteLayout.strokeColor = items[position].color
        else
            holder.itemNoteLayout.strokeColor = Color.parseColor("#DCDCDC")

        if (holder.tvTitleView.text.isEmpty()){
            holder.tvTitleView.visibility = View.GONE
        }

        if (holder.tvNoteView.text.isEmpty()){
            holder.tvNoteView.visibility = View.GONE
        }

        if (!items[position].deleted){
            holder.itemView.setOnClickListener {
                //Toast.makeText(context,"Clicked",Toast.LENGTH_SHORT).show()
                val intent = Intent(context,TextNoteActivity::class.java)
                intent.putExtra("REQUEST_CODE","poochi")
                intent.putExtra("INTENT_TITLE",holder.tvTitleView.text.toString())
                intent.putExtra("INTENT_NOTE",holder.tvNoteView.text.toString())
                intent.putExtra("INTENT_NOTE_ID",items[position].id)
                intent.putExtra("INTENT_COLOR",items[position].color)

                if (items[position].bookmark)
                    intent.putExtra("BOOL","true")
                else
                    intent.putExtra("BOOL","false")

                if (items[position].archive)
                    intent.putExtra("ARC","true")
                else
                    intent.putExtra("ARC","false")

                Log.i("QWE","value of title in Adapter is ${holder.tvTitleView.text}")
                Log.i("QWE","value of note in Adapter is ${holder.tvNoteView.text}")
                Log.i("QWE","value of color in Adapter is ${items[position].color}")
                Log.i("QWE","value of bookmark is ${items[position].bookmark}")
                Log.i("QWE","value of archive is ${items[position].archive}")

                context.startActivity(intent)
            }

        }
    }

}

class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val tvTitleView =itemView.tv_title
    val tvNoteView = itemView.tv_note
    val itemBackground1 = itemView.lll
    val itemBackground2 = itemView.ll_bs1
    val itemBackground3 = itemView.ll_bs2
    val itemBackground4 = itemView.lll
    val itemBackground5 = itemView.ll_toolbar1

    val itemNoteLayout = itemView.note_layout_cardview
}