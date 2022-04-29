package fr.isen.lazherme

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import fr.isen.lazherme.databinding.ActivityGameStartedBinding
import java.util.concurrent.TimeUnit

private lateinit var database : FirebaseDatabase
private lateinit var myRef: DatabaseReference
private lateinit var binding: ActivityGameStartedBinding
private lateinit var arrayAdapterBlue: UserAdapter2
private lateinit var arrayAdapterRed: UserAdapter2
private lateinit var userListBlue : ArrayList<userData>
private lateinit var userListRed : ArrayList<userData>
private lateinit var userList : ArrayList<userUids>
private lateinit var code : String
private lateinit var userEmail : String
private lateinit var userTeam : String
private lateinit var ownerEmail : String
private lateinit var userKey : String
private lateinit var userDeaths : String
private lateinit var scoreBleu : String
private lateinit var scoreRouge : String
class GameStartedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        userListBlue = arrayListOf<userData>()
        userListRed = arrayListOf<userData>()
        userList = arrayListOf<userUids>()
        code = intent.getStringExtra("code").toString()
        userEmail = intent.getStringExtra("userEmail").toString()
        userTeam = intent.getStringExtra("userTeam").toString()
        userKey = intent.getStringExtra("userKey").toString()
        binding = ActivityGameStartedBinding.inflate(layoutInflater)
        getUsers(this)
        getGameSpecs()
        getuserStats()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothService.MY_ACTION)
        registerReceiver(broadcastReceiver, intentFilter)
        binding.boutonArreter.setOnClickListener{
            finPartie()
        }
        var time = intent.getStringExtra("time").toString()
        var timesec = time.toLong()*60000
        object : CountDownTimer(timesec, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.texteTimer.text = String.format("%02d : %02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                )
            }
            override fun onFinish() {
                finPartie()
            }
        }.start()
    }
    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val s1 = intent.getStringExtra("DATAPASSED")
            if (s1 != null) {
                Log.e("touché par", s1)
                myRef.child("Games").child(code).child("players").child(userKey).child("death").setValue(
                    userDeaths.toInt()+1)
                if(userTeam=="red"){
                    myRef.child("Games").child(code).child("gameSpecs").child("scoreBlue").setValue(
                        scoreBleu.toInt()+1)
                }
                if(userTeam=="blue"){
                    myRef.child("Games").child(code).child("gameSpecs").child("scoreRed").setValue(
                        scoreRouge.toInt()+1)
                }

            }
        }
    }
    private fun getuserStats(){
        val ref = myRef.child("Games").child(code).child("players").child(userKey)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userDeaths = snapshot.child("death").value.toString()
                userTeam = snapshot.child("team").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun getGameSpecs(){
        val ref = myRef.child("Games").child(code).child("gameSpecs")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var gamestate = snapshot.child("gameState").value
                if(gamestate.toString()=="2"){
                    ouvreLeaderboard()
                    Log.d("gamestate","fin partie")
                }
                scoreBleu = snapshot.child("scoreBlue").value.toString()
                scoreRouge = snapshot.child("scoreRed").value.toString()
                binding.scoreBleu.text = snapshot.child("scoreBlue").value.toString()
                binding.scoreRouge.text = snapshot.child("scoreRed").value.toString()
                ownerEmail = snapshot.child("ownerEmail").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun finPartie(){
        if(userEmail== ownerEmail){
            myRef.child("Games").child(code).child("gameSpecs").child("gameState").setValue(2)
        }
    }
    private fun ouvreLeaderboard(){
        val intent = Intent(this, GameFinishedActivity::class.java)
        intent.putExtra("code",code)
        Log.d("code started :",code)
        intent.putExtra("userKey", userKey)
        intent.putExtra("userEmail", userEmail)
        intent.putExtra("userTeam", userTeam)
        startActivity(intent)
    }
    private fun getUsers(context : Context) {
        val ref = myRef.child("Games").child(code).child("players")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userListBlue.clear()
                userListRed.clear()
                userList.clear()
                if(snapshot.exists()){
                    for (userSnapchot in snapshot.children){
                        var email = userSnapchot.child("email").getValue().toString()
                        var kills = userSnapchot.child("kill").getValue().toString()
                        var deaths = userSnapchot.child("death").getValue().toString()
                        email = email.substringBefore("@")
                        var team = userSnapchot.child("team").getValue().toString()
                        var id = userSnapchot.child("idInGame").getValue().toString()
                        userList.add(userUids(userSnapchot.key.toString(),id))
                        if (team=="blue"){
                            userListBlue.add(userData(email,kills,deaths))
                        }else{
                            userListRed.add(userData(email,kills,deaths))
                        }
                    }
                    arrayAdapterBlue = UserAdapter2(userListBlue)
                    arrayAdapterRed = UserAdapter2(userListRed)
                    binding.redListStarted.layoutManager = LinearLayoutManager(context)
                    binding.redListStarted.adapter = arrayAdapterRed
                    binding.blueListStarted.layoutManager = LinearLayoutManager(context)
                    binding.blueListStarted.adapter = arrayAdapterBlue
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    override fun onBackPressed() {
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
