package fr.isen.lazherme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
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
    }
    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("uid",userKey)
        intent.putExtra("email",userEmail)
        startActivity(intent)
    }
}