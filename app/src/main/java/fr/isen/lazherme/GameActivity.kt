package fr.isen.lazherme

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.firebase.database.*
import fr.isen.lazherme.databinding.ActivityGameBinding

private lateinit var binding: ActivityGameBinding
private lateinit var database : FirebaseDatabase
private lateinit var myRef: DatabaseReference
private lateinit var userList : ArrayList<String>
private lateinit var code : String

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        database = FirebaseDatabase.getInstance()
        code = intent.getStringExtra("code").toString()
        myRef = database.getReference("Games")
        binding = ActivityGameBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        binding.codeDisplay.text = code
        userList = arrayListOf<String>()
        getUserBlue()
        getUserRed()
        setContentView(binding.root)
    }

    private fun getUserBlue() {
        val ref = myRef.child(code).child("teamBlue")
        var arrayAdapter: ArrayAdapter<*>
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (userSnapchot in snapshot.children){
                        val email = userSnapchot.value
                        userList.add(email as String)
                    }
                    arrayAdapter = ArrayAdapter(
                        this@GameActivity,
                        android.R.layout.simple_list_item_1, userList
                    )
                    binding.blueList.adapter = arrayAdapter
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun getUserRed() {
        val ref = myRef.child(code).child("teamRed")
        var arrayAdapter: ArrayAdapter<*>
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (userSnapchot in snapshot.children){
                        val email = userSnapchot.value
                        userList.add(email as String)
                    }
                    arrayAdapter = ArrayAdapter(
                        this@GameActivity,
                        android.R.layout.simple_list_item_1, userList
                    )
                    binding.redList.adapter = arrayAdapter
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}