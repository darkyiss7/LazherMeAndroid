package fr.isen.lazherme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.isen.lazherme.databinding.ActivityProfileBinding

private lateinit var binding: ActivityProfileBinding
private lateinit var userEmail:String
private lateinit var userKey:String
class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userEmail=intent.getStringExtra("userEmail").toString()
        userKey=intent.getStringExtra("userKey").toString()
        binding.usernameText.text = userEmail.substringBefore("@")
        binding.emailText.text = userEmail
    }
    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("uid",userKey)
        intent.putExtra("email",userEmail)
        startActivity(intent)
    }
}