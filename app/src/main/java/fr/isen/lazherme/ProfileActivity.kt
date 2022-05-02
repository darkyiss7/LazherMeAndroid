package fr.isen.lazherme

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import com.google.firebase.database.*
import fr.isen.lazherme.databinding.ActivityProfileBinding

private lateinit var binding: ActivityProfileBinding
private lateinit var userEmail:String
private lateinit var userKey:String
private lateinit var userName:String
private lateinit var database : FirebaseDatabase
private lateinit var myRef: DatabaseReference
class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("Users")
        userEmail=intent.getStringExtra("userEmail").toString()
        userKey=intent.getStringExtra("userKey").toString()
        binding.emailText.text = userEmail
        binding.PseudoLayout.isVisible = false
        binding.checkButton.isVisible = false
        binding.cancelButton.isVisible = false
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
            setNewPseudo()
            binding.PseudoLayout.isVisible = false
            binding.editButton.isVisible = true
            binding.usernameText.isVisible = true
            binding.checkButton.isVisible = false
            binding.cancelButton.isVisible = false
            getGameSpecs()
        }
    }

    private fun setNewPseudo() {
        myRef.child(userKey).child("username").setValue(binding.newPseudo.text.toString())
    }
    private fun getGameSpecs(){
        val ref = myRef.child(userKey)
        ref.child("username").get().addOnSuccessListener {
            userName = it.value.toString()
            binding.usernameText.text = userName
            Log.d("username :" , userName)
        }.addOnFailureListener{
        }
    }

    override fun onStart() {
        getGameSpecs()
        super.onStart()
    }
    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("uid",userKey)
        intent.putExtra("email",userEmail)
        startActivity(intent)
    }
}