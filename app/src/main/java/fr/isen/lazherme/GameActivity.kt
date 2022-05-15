package fr.isen.lazherme

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import fr.isen.lazherme.databinding.ActivityGameBinding
import fr.isen.lazherme.home.HomeActivity


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
private lateinit var userName: String
private lateinit var userTeam : String
private lateinit var ownerEmail : String
private lateinit var playersInGame : String
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
        getPlayerInGame()
        getGameSpecs(this)
        estDansLaPartie(this)
        getUsers(this)
        getGameState(this)
        setContentView(binding.root)
        binding.boutonChangerEquipe.setOnClickListener{
            changerEquipe()
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
                    var userid =  snapshot.child("idInGame").value
                    changeTeamSTM(userid.toString())
                }else{
                    val intent = Intent(context, HomeActivity::class.java)
                    intent.putExtra("uid",userKey)
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
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    AlertDialog.Builder(context)
                        .setMessage("Voulez vous vraiment quitter cette partie ?")
                        .setPositiveButton("Oui",
                            DialogInterface.OnClickListener { dialog, whichButton ->
                                ref.removeValue()
                                myRef.child(code).child("gameSpecs").child("playersInGame").setValue(
                                    playersInGame.toInt()-1)
                                ref2.child(userKey).child("games").child(code).setValue(null)
                                if(ownerEmail== userEmail){
                                    myRef.child(code).setValue(null)
                                }
                                estDansLaPartie(this@GameActivity)
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
                if(snapshot.exists()){
                    if (snapshot.value.toString()=="1"){
                        ref2.child(userKey).child("games").child(code).setValue(1)
                        val intent = Intent(context, GameStartedActivity::class.java)
                        intent.putExtra("code",code)
                        intent.putExtra("userKey",userKey)
                        intent.putExtra("userEmail",userEmail)
                        intent.putExtra("time",temps)
                        startActivity(intent)
                    }
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
    private fun changerEquipe(){
            val ref = myRef.child(code).child("players").child(userKey)
            ref.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){

                            var team = snapshot.child("team").getValue().toString()
                            if (team=="blue"){
                                myRef.child(code).child("players").child(userKey).child("team").setValue("red")
                                binding.boutonChangerEquipe.setImageResource(R.drawable.ic_baseline_switch_left_24)
                                changeTeamSTM("a")

                            }else{
                                myRef.child(code).child("players").child(userKey).child("team").setValue("blue")
                                binding.boutonChangerEquipe.setImageResource(R.drawable.ic_baseline_switch_right_24)
                                changeTeamSTM("b")
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
        ref.child("playersInGame").get().addOnSuccessListener {
            playersInGame = it.value.toString()
        }.addOnFailureListener{
        }
        ref.child("playerMax").get().addOnSuccessListener {
            playerMax = it.value.toString()
            binding.texteJoueurMax.text = getString(R.string.joueurs_max,playersInGame,it.value)
        }.addOnFailureListener{
        }

        ref.child("timeMax").get().addOnSuccessListener {
            temps = it.value.toString()
            binding.texteTempsMax.text =  getString(R.string.temps_max,it.value.toString())
        }.addOnFailureListener{
        }
        ref.child("ownerEmail").get().addOnSuccessListener {
            binding.boutonCommencer.isVisible = it.value.toString()== userEmail
            binding.textView17.isVisible = it.value.toString()== userEmail
            ownerEmail =it.value.toString()
        }.addOnFailureListener{
        }
        myRef.child(code).child("players").child(userKey).child("team").get().addOnSuccessListener {
            userTeam = it.value.toString()
            if (it.value.toString()=="blue"){
                binding.boutonChangerEquipe.setImageResource(R.drawable.ic_baseline_switch_right_24)
                changeTeamSTM("b")
            }else{
                binding.boutonChangerEquipe.setImageResource(R.drawable.ic_baseline_switch_left_24)
                changeTeamSTM("a")
            }
        }.addOnFailureListener{
        }
        ref.child("gameState").get().addOnSuccessListener {
            if(it.value.toString()=="1"){
                val intent = Intent(context, GameStartedActivity::class.java)
                intent.putExtra("code",code)
                intent.putExtra("userKey",userKey)
                intent.putExtra("userEmail",userEmail)
                intent.putExtra("userTeam", userTeam)
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
                        var userkey = userSnapchot.child("username").value.toString()
                        val team = userSnapchot.child("team").value.toString()
                                if (team=="blue"){
                                    userListBlue.add(userkey)
                                }else{
                                    userListRed.add(userkey)
                                }
                            }
                    }
                    arrayAdapterBlue = UserAdapter(userListBlue)
                    arrayAdapterRed = UserAdapter(userListRed)
                    binding.redList.layoutManager = LinearLayoutManager(context)
                    binding.redList.adapter = arrayAdapterRed
                    binding.blueList.layoutManager = LinearLayoutManager(context)
                    binding.blueList.adapter = arrayAdapterBlue
                }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getPlayerInGame() {
        val ref = myRef.child(code).child("gameSpecs")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               playersInGame = snapshot.child("playersInGame").value.toString()
                playerMax = snapshot.child("playerMax").value.toString()
                binding.texteJoueurMax.text = getString(R.string.joueurs_max,playersInGame,
                    playerMax)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    override fun onStart() {
        super.onStart()

    }
    override fun onBackPressed() {
    }
    private fun changeTeamSTM(idTeam:String) {
        val intent = Intent(this,BluetoothService::class.java)
        intent.putExtra("idServ","1")
        intent.putExtra("idTeam",idTeam)
        startService(intent)
    }
}