package fr.isen.lazherme

import android.R.attr.duration
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import fr.isen.lazherme.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var database : FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        val code = getRandomString(5)
        val userEmail = intent.getStringExtra("email")
        binding.button2.setOnClickListener{
            myRef.child("Games").child(code).child("ownerEmail").setValue(userEmail)
            myRef.child("Games").child(code).child("gameCode").setValue(code)
            myRef.child("Games").child(code).child("teamBlue").child("player").setValue(userEmail)
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("code",code)
            startActivity(intent)
        }
        binding.button3.setOnClickListener{
            var bool : Boolean
            bool = false
            myRef.child("Games").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(binding.code.text.toString())){
                        bool=true
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            if (bool){
                myRef.child("Games").child(binding.code.text.toString()).child("teamRed").child("player").setValue(userEmail)
                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra("code",code)
                startActivity(intent)
            }
            if(!bool){

                val toast = Toast.makeText(this, "message", Toast.LENGTH_LONG)
                toast.show()
            }

        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ble,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        return super.onOptionsItemSelected(item)
    }
    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}