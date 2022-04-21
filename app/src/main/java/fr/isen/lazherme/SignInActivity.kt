package fr.isen.lazherme


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fr.isen.lazherme.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


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
                        myRef.child("Users").child(firebaseAuth.currentUser!!.uid).setValue(firebaseAuth.currentUser!!.email)
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("uid",firebaseAuth.currentUser!!.uid)
                        intent.putExtra("email",firebaseAuth.currentUser!!.email)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs !!", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser != null){
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("email",firebaseAuth.currentUser!!.email)
            startActivity(intent)
        }
    }
}