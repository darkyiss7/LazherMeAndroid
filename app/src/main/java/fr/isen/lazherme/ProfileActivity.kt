package fr.isen.lazherme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.database.*
import fr.isen.lazherme.databinding.ActivityProfileBinding
import fr.isen.lazherme.home.HomeActivity

private lateinit var binding: ActivityProfileBinding
private lateinit var userEmail:String
private lateinit var userKey:String
private lateinit var userName:String
private lateinit var exp:String
private lateinit var database : FirebaseDatabase
private lateinit var myRef: DatabaseReference
class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("Users")
        userEmail=intent.getStringExtra("userEmail").toString()
        userKey=intent.getStringExtra("userKey").toString()
        binding.emailText.text = userEmail
        binding.PseudoLayout.isVisible = false
        binding.checkButton.isVisible = false
        binding.cancelButton.isVisible = false
        binding.backButton.setOnClickListener{
            onBackPressed()
        }
        binding.editButton.setOnClickListener{
            binding.PseudoLayout.isVisible = true
            binding.editButton.isVisible = false
            binding.usernameText.isVisible = false
            binding.checkButton.isVisible = true
            binding.cancelButton.isVisible = true
        }
        binding.cancelButton.setOnClickListener{
            binding.PseudoLayout.isVisible = false
            binding.editButton.isVisible = true
            binding.usernameText.isVisible = true
            binding.checkButton.isVisible = false
            binding.cancelButton.isVisible = false
        }
        binding.checkButton.setOnClickListener{
            val pseudo = binding.newPseudo.text.toString()

            if (pseudo.isNotEmpty()) {
                if (pseudo.length>=5) {
                    setNewPseudo()
                    binding.PseudoLayout.isVisible = false
                    binding.editButton.isVisible = true
                    binding.usernameText.isVisible = true
                    binding.checkButton.isVisible = false
                    binding.cancelButton.isVisible = false
                    getUserName()
                }
                else {
                    Toast.makeText(this,"Veuillez saisir un pseudo d'au moins 5 caractères", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            else {
                Toast.makeText(this,"Veuillez saisir un pseudo", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun setNewPseudo() {
        myRef.child(userKey).child("username").setValue(binding.newPseudo.text.toString())
    }
    private fun getUserName(){
        val ref = myRef.child(userKey)
        ref.child("username").get().addOnSuccessListener {
            userName = it.value.toString()
            binding.usernameText.text = userName

        }.addOnFailureListener{
        }
    }
    private fun getExp(){
        val ref = myRef.child(userKey)
        ref.child("exp").get().addOnSuccessListener {
            exp = it.value.toString()
            binding.progressBar2.max = 100
            binding.ptsExp.text = exp + " exp"
            binding.lvlText.text = "LVL " + ((exp.toInt()/100)).toString()
            binding.lvlSupText.text = "LVL " + ((exp.toInt()/100)+1).toString()
            binding.lvlSupExpText.text = "encore " + (100-(exp.toInt()%100)).toString()
            binding.progressBar2.progress = exp.toInt()%100

        }.addOnFailureListener{
        }
    }

    override fun onStart() {
        getUserName()
        getExp()
        super.onStart()
    }
    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("uid",userKey)
        intent.putExtra("email",userEmail)
        startActivity(intent)
        finish()
    }
}