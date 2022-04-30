package fr.isen.lazherme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.*

private lateinit var database : FirebaseDatabase
private lateinit var myRef: DatabaseReference
private lateinit var userKey: String
private lateinit var gameList : ArrayList<String>
private lateinit var arrayAdapterGame: HistoryAdapter
class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        gameList = arrayListOf<String>()
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        userKey = intent.getStringExtra("userKey").toString()
        getGameSpecs()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
    }
    private fun getGameSpecs(){
        val ref = myRef.child("Users").child(userKey).child("games")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (game in snapshot.children) {
                    gameList.add(game.value.toString())
                    arrayAdapterGame= HistoryAdapter(gameList)
                    binding.redList.layoutManager = LinearLayoutManager(context)
                    binding.redList.adapter = arrayAdapterRed
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}