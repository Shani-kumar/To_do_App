package com.example.to_do_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.to_do_app.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth;
    private lateinit var useremail:String
    private lateinit var userpassword:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener{
            val intent= Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            useremail=binding.emailLayouttext.text.toString()
            userpassword=binding.passwordLayouttext.text.toString()
            if(useremail.isBlank()){
                binding.emailLayout.error="Email can't be Blank"
            }
            if(userpassword.isEmpty()){
                binding.passwordLayout.error="Password can't be empty"
            }
            if(Checkformdetails(useremail,userpassword)){

                auth.signInWithEmailAndPassword(useremail, userpassword).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("Email",useremail)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
//
//    override fun onStart() {
//        super.onStart()
//        if(auth.currentUser != null){
//            Toast.makeText(this,"going",Toast.LENGTH_LONG).show()
//            var intent= Intent(this,MainActivity::class.java)
//
//            startActivity(intent)
//            finish()
//        }
//        else{
//                    Toast.makeText(this,"You can log in now",Toast.LENGTH_LONG).show()
//        }
//    }
    private fun Checkformdetails(useremail:String,userpassword:String): Boolean {

        return !(useremail.isBlank() || userpassword.isEmpty())
    }


}