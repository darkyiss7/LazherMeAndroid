package fr.isen.lazherme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.*
import fr.isen.lazherme.databinding.ActivityGameFinishedBinding

private lateinit var binding : ActivityGameFinishedBinding
private lateinit var userKey : String
private lateinit var userEmail : String
private lateinit var userTeam : String
private lateinit var code : String
private lateinit var score : String
private lateinit var kills : String
private lateinit var deaths : String
private lateinit var scoreBleu : String
private lateinit var scoreRouge : String
private lateinit var myRef: DatabaseReference
private lateinit var database : FirebaseDatabase
class GameFinishedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        binding = ActivityGameFinishedBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        userKey = intent.getStringExtra("userKey").toString()
        userEmail = intent.getStringExtra("userEmail").toString()
        code = intent.getStringExtra("code").toString()
        userTeam = intent.getStringExtra("userTeam").toString()
        getGameSpecs()
        getUserStats()

        binding.homeButton.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("uid", userKey)
            intent.putExtra("email", userEmail)
            startActivity(intent)
        }
    }
    private fun getUserStats(){
        val ref = myRef.child("Games").child(code).child("players").child(userKey)
        ref.child("death").get().addOnSuccessListener {
            binding.statDeath.text = it.value.toString()

        }.addOnFailureListener{
        }
        ref.child("kill").get().addOnSuccessListener {
            binding.statKills.text = it.value.toString()
        }.addOnFailureListener{
        }

    }
    private fun getGameSpecs(){
        val ref = myRef.child("Games").child(code).child("gameSpecs")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                scoreBleu = snapshot.child("scoreBlue").value.toString()
                scoreRouge = snapshot.child("scoreRed").value.toString()
                if (scoreBleu == scoreRouge){
                    binding.textWin.text = "Egalité"
                    binding.backgroundView.setImageResource(R.color.green)
                }
                if (scoreBleu< scoreRouge){
                    if (userTeam=="blue"){
                        binding.textWin.text = "Défaite"
                        binding.backgroundView.setImageResource(R.color.red)
                    }else{
                        binding.textWin.text = "Victoire"
                        binding.backgroundView.setImageResource(R.color.green)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}