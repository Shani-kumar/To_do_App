package com.example.to_do_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase



class Notesadapter(val items:ArrayList<usermodel>) : RecyclerView.Adapter<NotesViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_note,parent,false)
       val btn= view.findViewById<ImageFilterView>(R.id.deletebutton)
        val viewHolder= NotesViewHolder(view)
//        view.setOnClickListener{
//            listner.onItemClicked(items[viewHolder.adapterPosition])
//        }
        btn.setOnClickListener {
//            listner.onItemClicked(items[viewHolder.adapterPosition])
            val documentIdToDelete = items[viewHolder.adapterPosition].firestoreId
            var fstore = Firebase.firestore
            fstore = FirebaseFirestore.getInstance()
            fstore.collection("note")
                .document(documentIdToDelete)
                .delete()
                .addOnSuccessListener {
//                // Document successfully deleted
//                notesRef.remove()
                // Re-add the snapshot listener
                    items.removeAt(viewHolder.adapterPosition)
                  notifyDataSetChanged()
//                // Manually refresh the RecyclerView
//                binding.recyclerview.adapter?.notifyDataSetChanged()

                }

                .addOnFailureListener { exception ->
                    // Handle delete failure
//                    android.widget.Toast.makeText(this, "Failed to delete", android.widget.Toast.LENGTH_LONG).show()
                }

        }

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
    fun onItemClicked(item: usermodel)
}