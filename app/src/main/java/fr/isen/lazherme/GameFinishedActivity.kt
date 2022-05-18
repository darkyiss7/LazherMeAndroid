package fr.isen.lazherme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.*
import fr.isen.lazherme.databinding.ActivityGameFinishedBinding
import fr.isen.lazherme.home.HomeActivity

private lateinit var binding : ActivityGameFinishedBinding
private lateinit var userKey : String
private lateinit var userEmail : String
private lateinit var userTeam : String
private lateinit var code : String
private lateinit var score : String
private lateinit var userkills : String
private lateinit var deaths : String
private lateinit var exp:String
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
        getExp()
        sendToStm("c")

        binding.homeButton.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("uid", userKey)
            intent.putExtra("email", userEmail)
            finish()
            startActivity(intent)
        }
    }
    private fun getUserStats(){
        val ref = myRef.child("Games").child(code).child("players").child(userKey)
        ref.child("death").get().addOnSuccessListener {
            binding.statDeath.text = "Morts:" + it.value.toString()

        }.addOnFailureListener{
        }
        ref.child("kill").get().addOnSuccessListener {
            binding.statKills.text = "Kills:" + it.value.toString()
            userkills = it.value.toString()
            binding.statScore.text = "Score:" + (it.value.toString().toInt()*100).toString()
        }.addOnFailureListener{
        }

    }
    private fun getExp(){
        val ref = myRef.child("Users").child(userKey)
        ref.child("exp").get().addOnSuccessListener {
            exp = it.value.toString()
            binding.progressBar3.max = 100
            binding.ptsExp2.text =  exp + "exp + " + (userkills.toInt()*100).toString()
            binding.lvlText2.text = "LVL " + ((exp.toInt()/100)).toString()
            binding.lvlSupText2.text = "LVL " + ((exp.toInt()/100)+1).toString()
            binding.lvlSupExpText2.text = "encore " + (100-(exp.toInt()%100)).toString()
            binding.progressBar3.progress = exp.toInt()%100
            Log.d("exp",exp)

        }.addOnFailureListener{
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("uid", userKey)
        intent.putExtra("email", userEmail)
        finish()
        startActivity(intent)
        super.onBackPressed()
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
    private fun sendToStm(id:String) {
        val intent = Intent(this,BluetoothService::class.java)
        intent.putExtra("idServ","1")
        intent.putExtra("idTeam",id)
        startService(intent)
    }
}