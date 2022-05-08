package fr.isen.lazherme


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fr.isen.lazherme.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("uid", firebaseAuth.currentUser!!.uid)
                        intent.putExtra("email", firebaseAuth.currentUser!!.email)
                        intent.putExtra("codeStart", "0")
                        startActivity(intent)
                        Toast.makeText(this, "Connexion reussie !", Toast.LENGTH_SHORT).show()
                        /*  val intentTest = Intent(this, HomeActivity::class.java)
                         intentTest.putExtra("email",firebaseAuth.currentUser!!.email)
                         intentTest.putExtra("uid",firebaseAuth.currentUser!!.uid)
                         startActivity(intentTest)*/
                    } else {
                        Toast.makeText(this, it.exception?.message.toString(), Toast.LENGTH_SHORT)
                            .show()

                    }
                }
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs !!", Toast.LENGTH_SHORT)
                    .show()

            }
        }
    }


    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", firebaseAuth.currentUser!!.email)
            intent.putExtra("uid", firebaseAuth.currentUser!!.uid)
            intent.putExtra("codeStart", "0")
            startActivity(intent)
            /*val intentTest = Intent(this, HomeActivity::class.java)
            intentTest.putExtra("email",firebaseAuth.currentUser!!.email)
            intentTest.putExtra("uid",firebaseAuth.currentUser!!.uid)
            intent.putExtra("codeStart","0")
            startActivity(intentTest)*/
        }
    }
}