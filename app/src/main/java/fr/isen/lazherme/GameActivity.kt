package fr.isen.lazherme

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import fr.isen.lazherme.databinding.ActivityGameBinding


private lateinit var binding: ActivityGameBinding
private lateinit var database : FirebaseDatabase
private lateinit var myRef: DatabaseReference
private lateinit var ref2: DatabaseReference
private lateinit var userListBlue : ArrayList<String>
private lateinit var userListRed : ArrayList<String>
private lateinit var code : String
private lateinit var gameMode : String
private lateinit var playerMax : String
private lateinit var temps : String
private lateinit var userKey : String
private lateinit var userEmail : String
private lateinit var arrayAdapterBlue: UserAdapter
private lateinit var arrayAdapterRed: UserAdapter

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        database = FirebaseDatabase.getInstance()
        code = intent.getStringExtra("code").toString()
        myRef = database.getReference("Games")
        ref2 = database.getReference("Users")
        binding = ActivityGameBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        binding.codeDisplay.text = code
        userListBlue = arrayListOf<String>()
        userListRed = arrayListOf<String>()
        userKey = intent.getStringExtra("userKey").toString()
        userEmail = intent.getStringExtra("userEmail").toString()
        estDansLaPartie(this)
        getUsers(this)
        getGameState(this)
        setContentView(binding.root)
        binding.boutonChangerEquipe.setOnClickListener{ changerEquipe()
           }
        binding.boutonCommencer.setOnClickListener{
            lancerPartie()
        }
        binding.boutonQuitter.setOnClickListener{
            quitterPartie(this)
        }
    }

    private fun estDansLaPartie(context: Context) {
        val ref = myRef.child(code).child("players").child(userKey)
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                }else{
                    val intent = Intent(context, HomeActivity::class.java)
                    intent.putExtra("userKey",userKey)
                    intent.putExtra("email",userEmail)
                    startActivity(intent)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun quitterPartie(context: Context) {
        val ref = myRef.child(code).child("players").child(userKey)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    AlertDialog.Builder(context)
                        .setMessage("Voulez vous vraiment quitter cette partie ?")
                        .setPositiveButton("Oui",
                            DialogInterface.OnClickListener { dialog, whichButton ->
                                ref.removeValue()
                                ref2.child(userKey).child("currentGame").setValue("none")
                                val intent = Intent(context, HomeActivity::class.java)
                                intent.putExtra("userKey",userKey)
                                intent.putExtra("email",userEmail)
                                startActivity(intent)
                            })
                        .setNegativeButton("Non", null).show()

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun lancerPartie() {
        myRef.child(code).child("gameSpecs").child("gameState").setValue(1)
        myRef.child(code).child("gameSpecs").child("scoreBlue").setValue(0)
        myRef.child(code).child("gameSpecs").child("scoreRed").setValue(0)
    }
    private fun getGameState(context: Context){
        val ref = myRef.child(code).child("gameSpecs").child("gameState")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("gamestate",snapshot.value.toString())
                if(snapshot.exists()){
                    if (snapshot.value.toString()=="1"){
                        val intent = Intent(context, GameStartedActivity::class.java)
                        intent.putExtra("code",code)
                        intent.putExtra("userKey",userKey)
                        intent.putExtra("userEmail",userEmail)
                        intent.putExtra("time",temps)
                        startActivity(intent)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun changerEquipe(){
            val ref = myRef.child(code).child("players").child(userKey)
            ref.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){

                            var team = snapshot.child("team").getValue().toString()
                            if (team=="blue"){
                                userListBlue.remove(userEmail.substringBefore("@"))
                                myRef.child(code).child("players").child(userKey).child("team").setValue("red")

                            }else{
                                userListRed.remove(userEmail.substringBefore("@"))
                                myRef.child(code).child("players").child(userKey).child("team").setValue("blue")
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
    private fun getGameSpecs(context: Context) {
        val ref = myRef.child(code).child("gameSpecs")
        ref.child("gameMode").get().addOnSuccessListener {
            getGameMode(it.value.toString())
        }.addOnFailureListener{
        }
        ref.child("playerMax").get().addOnSuccessListener {
            binding.texteJoueurMax.text = getString(R.string.joueurs_max,it.value)
        }.addOnFailureListener{
        }
        ref.child("timeMax").get().addOnSuccessListener {
            temps = it.value.toString()
            binding.texteTempsMax.text =  getString(R.string.temps_max,it.value.toString())
        }.addOnFailureListener{
        }
        ref.child("ownerEmail").get().addOnSuccessListener {
            binding.boutonCommencer.isVisible = it.value.toString()== userEmail
        }.addOnFailureListener{
        }
        ref.child("gameState").get().addOnSuccessListener {
            if(it.value.toString()=="1"){
                val intent = Intent(context, GameStartedActivity::class.java)
                intent.putExtra("code",code)
                intent.putExtra("userKey",userKey)
                intent.putExtra("userEmail",userEmail)
                startActivity(intent)
            }
        }.addOnFailureListener{
        }
    }
    private fun getGameMode(int : String){
        if (int=="0") binding.texteModeJeu.text = getString(R.string.mode_jeu,getString(R.string.MME))
        if (int=="1") binding.texteModeJeu.text = getString(R.string.mode_jeu,getString(R.string.FFA))
    }
    private fun getUsers(context : Context) {
        val ref = myRef.child(code).child("players")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userListBlue.clear()
                userListRed.clear()
                if(snapshot.exists()){
                    for (userSnapchot in snapshot.children){
                        var email = userSnapchot.child("email").getValue().toString()
                        email = email.substringBefore("@")
                        var team = userSnapchot.child("team").getValue().toString()
                        if (team=="blue"){
                            userListBlue.add(email)
                        }else{
                            userListRed.add(email)
                        }
                    }
                    arrayAdapterBlue = UserAdapter(userListBlue)
                    arrayAdapterRed = UserAdapter(userListRed)
                    binding.redList.layoutManager = LinearLayoutManager(context)
                    binding.redList.adapter = arrayAdapterRed
                    binding.blueList.layoutManager = LinearLayoutManager(context)
                    binding.blueList.adapter = arrayAdapterBlue
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    override fun onStart() {
        super.onStart()
        getGameSpecs(this)
    }
    override fun onBackPressed() {
    }
}