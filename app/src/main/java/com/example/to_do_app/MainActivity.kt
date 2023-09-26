package com.example.to_do_app

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.to_do_app.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import java.util.Date


class MainActivity : AppCompatActivity(), NotesItemClicked  {


    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var fstore: FirebaseFirestore
    private lateinit var items: ArrayList<usermodel>
    private lateinit var userid: String


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.navsignout -> {
                auth.signOut()
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.yellow)))
        binding.recyclerview.layoutManager= LinearLayoutManager(this)
        fstore = FirebaseFirestore.getInstance()
        auth=FirebaseAuth.getInstance()



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



    private fun setText() {
        auth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            userid = currentUser.uid

            val collectionRef = fstore.collection("user")
            collectionRef.addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    // Your code here
                    binding.submit.setOnClickListener {
                        val note = binding.input.text.toString()
                        binding.input.setText("")
                        addNoteToFirestore(note, userid)
                        retrieveNotesFromFirestore(userid)

                    }
                    retrieveNotesFromFirestore(userid)


                } else {
                    Log.d(ContentValues.TAG, "Current data: null")
                }
            }
        } else {
            // Handle the case when the user is not authenticated or the authentication state is not resolved yet.
            // For example, you can redirect the user to the sign-in activity.
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

    }




private fun addNoteToFirestore(note: String,  userId: String) {

    val um = usermodel(note, false, Timestamp(Date()), userId, "") // Initialize firestoreId as empty

    fstore.collection("note")
        .add(um)
        .addOnSuccessListener { documentReference ->
            // Get the Firestore-generated ID
            val firestoreId = documentReference.id

            // Update the um instance with the firestoreId
            um.firestoreId = firestoreId

            // Now update the Firestore document with the firestoreId
            fstore.collection("note").document(firestoreId)
                .set(um)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Data added", Toast.LENGTH_SHORT).show()
                        retrieveNotesFromFirestore(userid)
                    } else {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
//                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
                }
        }
        .addOnFailureListener { e ->
//            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
}


    private fun retrieveNotesFromFirestore(userid: String) {
    val notesRef = fstore.collection("note").whereEqualTo("userid", userid).orderBy("completed",
        Query.Direction.ASCENDING)
        .orderBy("timestamp",Query.Direction.DESCENDING)

    // Set up a real-time listener for the query results
    notesRef.addSnapshotListener { querySnapshot, exception ->
        if (exception != null) {
//            Toast.makeText(this, "Error fetching notes}", Toast.LENGTH_SHORT).show()
            return@addSnapshotListener
        }

        querySnapshot?.let { snapshot ->
            val notesList = ArrayList<usermodel>() // Use proper capitalization
            for (document in snapshot.documents) {
                val note = document.toObject(usermodel::class.java)
                note?.let { retrievedNote ->
                    notesList.add(retrievedNote)
                }
            }

            // Update the RecyclerView adapter with the retrieved data
            binding.recyclerview.adapter = Notesadapter(notesList)
        }
    }
}



    override fun onItemClicked(item: usermodel) {
        val documentIdToDelete = item.firestoreId

        fstore.collection("note")
            .document(documentIdToDelete)
            .delete()
            .addOnSuccessListener {
//                // Document successfully deleted
//                notesRef.remove()
//                // Re-add the snapshot listener
                retrieveNotesFromFirestore(userid)
//                // Manually refresh the RecyclerView
//                binding.recyclerview.adapter?.notifyDataSetChanged()

                }

            .addOnFailureListener { exception ->
                // Handle delete failure
                Toast.makeText(this,"error while deleting  ",Toast.LENGTH_LONG).show()
            }
    }


}