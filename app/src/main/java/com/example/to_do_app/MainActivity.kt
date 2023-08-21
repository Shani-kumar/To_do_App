package com.example.to_do_app

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_app.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity(), NotesItemClicked  {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var fstore: FirebaseFirestore
    private lateinit var items: ArrayList<usermodel>
    private lateinit var userid: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerview.layoutManager= LinearLayoutManager(this)
        fstore = FirebaseFirestore.getInstance()
        auth=FirebaseAuth.getInstance()


        binding.btn.setOnClickListener {
            auth.signOut()
            var intent= Intent(this,SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
        val sharedPref=this?.getPreferences(Context.MODE_PRIVATE)?:return
        val isLogin=sharedPref.getString("Email","1")

        if(isLogin=="1") {
            val email=intent.getStringExtra("Email")
            if(email!=null){
                setText()
                with(sharedPref.edit()) {
                    putString("Email", email)
                    apply()
                }
            }
            else{
                var intent=Intent(this,SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
            setText()
        }
        else
        {
            setText()
        }

    }
//
    private fun setText(){
        auth=FirebaseAuth.getInstance()
        fstore= FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            userid = currentUser.uid

            val doc=fstore.collection("user").document(userid)
            doc.addSnapshotListener { snapshot, e ->
                if(e!=null){
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if ((snapshot != null) && snapshot.exists()) {
                    binding.submit.setOnClickListener {
                        val note = binding.input.text.toString()
                        binding.input.setText("")
                        addNoteToFirestore(note,userid)

                    }

                    retrieveNotesFromFirestore(userid)
                } else {
                    Log.d(ContentValues.TAG, "Current data: null")
                }
            }
//            binding.btn.setOnClickListener {
//                auth.signOut()
//                val intent = Intent(this, SignInActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
            // Continue with the rest of the code that depends on `userid`.
        } else {
            // Handle the case when the user is not authenticated or the authentication state is not resolved yet.
            // For example, you can redirect the user to the sign-in activity.
            var intent=Intent(this,SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
//        userid=auth.currentUser!!.uid

    }


//    private fun addNoteToFirestore(note: String) {
//        val um = usermodel(note)
//        fstore.collection("note").add(um).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Toast.makeText(this, "Data added", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
//            }
//        }.addOnFailureListener { e ->
//            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
//        }
//    }
    private fun addNoteToFirestore(note: String, documentId: String) {
        val um = usermodel(note)
        fstore.collection("note").document(documentId).set(um)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Data added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            }
    }

//    private fun retrieveNotesFromFirestore(documentId: String) {
//        val items = ArrayList<usermodel>()
//        fstore.collection("note").document(documentId)
//            .get()
//            .addOnSuccessListener { documentSnapshot ->
//                if (documentSnapshot.exists()) {
//                    val ussr = documentSnapshot.toObject(usermodel::class.java)
//                    ussr?.let {
//                        items.add(ussr)
//                        binding.recyclerview.adapter = Notesadapter(items)
//                    }
//                } else {
//                    Toast.makeText(this, "Document not found", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
//            }
//    }
private fun retrieveNotesFromFirestore(documentId: String) {
    val notesRef = fstore.collection("note").document(documentId)

    // Set up a real-time listener for the specific document
    notesRef.addSnapshotListener { documentSnapshot, exception ->
        if (exception != null) {
            Toast.makeText(this, "Error fetching notes: ${exception.message}", Toast.LENGTH_SHORT).show()
            return@addSnapshotListener
        }

        documentSnapshot?.let { snapshot ->
            if (snapshot.exists()) {
                val note = snapshot.toObject(usermodel::class.java)
                note?.let { retrievedNote ->
                    val notesList = ArrayList<usermodel>()
                    notesList.add(retrievedNote)
                    binding.recyclerview.adapter = Notesadapter(notesList)
                }
            } else {
                Toast.makeText(this, "Document not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


    override fun onItemClicked(item: String) {
        Toast.makeText(this,"Clicked item is $item",Toast.LENGTH_LONG).show()
    }

}