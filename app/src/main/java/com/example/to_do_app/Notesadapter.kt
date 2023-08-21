package com.example.to_do_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class Notesadapter(private val items:ArrayList<usermodel>) : RecyclerView.Adapter<NotesViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_note,parent,false)
        val viewHolder= NotesViewHolder(view)
//        view.setOnClickListener{
//            listner.onItemClicked(items[viewHolder.adapterPosition])
//        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val currentitem=items[position]
        holder.titleView.text=items[position].notetext
    }
}

class NotesViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    val titleView=itemView.findViewById<TextView>(R.id.text)

}
interface NotesItemClicked{
    fun onItemClicked(item:String)
}