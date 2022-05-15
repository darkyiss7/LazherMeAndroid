package fr.isen.lazherme

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import fr.isen.lazherme.databinding.ActivityHistoryBinding
import fr.isen.lazherme.home.HomeActivity

private lateinit var database : FirebaseDatabase
private lateinit var myRef: DatabaseReference
private lateinit var userKey: String
private lateinit var userEmail: String
private lateinit var gameList : ArrayList<String>
private lateinit var arrayAdapterGame: HistoryAdapter
private lateinit var binding : ActivityHistoryBinding
class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        gameList = arrayListOf<String>()
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        userKey = intent.getStringExtra("userKey").toString()
        userEmail = intent.getStringExtra("userEmail").toString()
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        getGameSpecs(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
    private fun getGameSpecs(context: Context){
        val ref = myRef.child("Users").child(userKey).child("games")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    gameList.clear()
                    for (game in snapshot.children) {
                        gameList.add(game.key.toString())
                        arrayAdapterGame= HistoryAdapter(gameList)
                        binding.gameRecyclerView.layoutManager = LinearLayoutManager(context)
                        binding.gameRecyclerView.adapter = arrayAdapterGame
                    }
                }
                else{
                    binding.noGameText.isVisible = true
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("uid",userKey)
        intent.putExtra("email",userEmail)
        startActivity(intent)
        super.onBackPressed()
    }
}