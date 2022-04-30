package fr.isen.lazherme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import fr.isen.lazherme.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var database : FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var code:String
    private lateinit var userEmail:String
    private lateinit var userKey:String
    private var count = 2
    private var mode = 0
    private var temps = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        binding.texteNombre.text = count.toString()
        binding.texteMode.text = getString(R.string.MME)
        binding.texteTemps.text = temps.toString()
        userEmail = intent.getStringExtra("email").toString()
        supportActionBar?.title = userEmail.substringBefore("@")
        supportActionBar?.setIcon(R.drawable.ic_baseline_person_24)
        userKey = intent.getStringExtra("uid").toString()
        binding.button2.setOnClickListener{
            code = getRandomString(5)
            myRef.child("Games").child(code).child("gameSpecs").child("ownerEmail").setValue(userEmail)
            myRef.child("Games").child(code).child("gameSpecs").child("gameCode").setValue(code)
            myRef.child("Games").child(code).child("gameSpecs").child("gameMode").setValue(mode)
            myRef.child("Games").child(code).child("gameSpecs").child("playerMax").setValue(count)
            myRef.child("Games").child(code).child("gameSpecs").child("timeMax").setValue(temps)
            myRef.child("Games").child(code).child("gameSpecs").child("playersInGame").setValue(1)
            myRef.child("Games").child(code).child("gameSpecs").child("gameState").setValue(0)
            myRef.child("Games").child(code).child("players").child(userKey).child("email").setValue(userEmail)
            myRef.child("Games").child(code).child("players").child(userKey).child("team").setValue("blue")
            myRef.child("Games").child(code).child("players").child(userKey).child("kill").setValue(0)
            myRef.child("Games").child(code).child("players").child(userKey).child("death").setValue(0)
            myRef.child("Games").child(code).child("players").child(userKey).child("idInGame").setValue(0)
            myRef.child("Users").child(intent.getStringExtra("uid").toString()).child("games").child(code).setValue(0)
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("code",code)
            intent.putExtra("userKey",userKey)
            intent.putExtra("userEmail",userEmail)
            startActivity(intent)
        }
        binding.button3.setOnClickListener{
            var gamecode = binding.code.text.toString()
            if (gamecode == "") {
                Toast.makeText(this, "Veuillez saisir un code", Toast.LENGTH_SHORT).show();
            }else
                checkGame(this)
        }
        binding.boutonModeDroite.setOnClickListener{changemode(1)}
        binding.boutonModeGauche.setOnClickListener{changemode(0)}
        binding.boutonNombreDroite.setOnClickListener{augmenteNombre()}
        binding.boutonNombreGauche.setOnClickListener{diminuerNombre()}
        binding.boutonTempsDroite.setOnClickListener{augmenterTemps()}
        binding.boutonTempsGauche.setOnClickListener{diminuerTemps()}
    }
    private fun diminuerTemps() {
        temps -= 10
        if (temps<10) temps=30
        binding.texteTemps.text = temps.toString()
    }

    private fun augmenterTemps() {
        temps += 10
        if (temps>30) temps=10
        binding.texteTemps.text = temps.toString()
    }
    private fun diminuerNombre() {
        count -= 2
        if (count<2) count=12
        binding.texteNombre.text = count.toString()
    }

    private fun augmenteNombre() {
        count += 2
        if (count>12) count=2
        binding.texteNombre.text = count.toString()
    }

    private fun changemode(gameMode:Int) {
        if (gameMode==0){
            if (mode==0){
                binding.texteMode.text = getString(R.string.FFA)
                mode=1
            }else{
                binding.texteMode.text = getString(R.string.MME)
                mode=0
            }
        }
        if(gameMode==1){
            if (mode==1){
            binding.texteMode.text = getString(R.string.MME)
                mode=0
        }else{
            binding.texteMode.text = getString(R.string.FFA)
                mode=1
        }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ble,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("userKey",userKey)
                intent.putExtra("userEmail",userEmail)
                startActivity(intent)
                return true
            }
            R.id.historique ->{
                val intent = Intent(this, HistoryActivity::class.java)
                intent.putExtra("userKey",userKey)
                intent.putExtra("userEmail",userEmail)
                startActivity(intent)
                return true
            }
            R.id.dexonnexion ->{
                val intent = Intent(this, SignInActivity::class.java)
                FirebaseAuth.getInstance().signOut()
                startActivity(intent)
                Toast.makeText(this, "Déconnexion...", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
    private fun checkGame(context:Context) {
        val ref = myRef.child("Games").child(binding.code.text.toString())
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var grandId = 0
                if(snapshot.exists()){
                    for (userSnapchot in snapshot.child("players").children) {
                        var id = userSnapchot.child("idInGame").value.toString()
                        if (id.toInt()>grandId){
                            grandId = id.toInt()
                        }
                    }
                     ref.child("gameSpecs").child("playersInGame").get().addOnSuccessListener {
                         var playersInGame = it.value.toString()
                         openGame(playersInGame.toInt(),grandId)
                    }.addOnFailureListener{
                    }

                }else{
                    Toast.makeText(context, "Partie introuvable", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun openGame(playersInGame : Int,grandId : Int) {
        var codeGame = binding.code.text.toString()
        myRef.child("Games").child(codeGame).child("players").child(userKey).child("email").setValue(userEmail)
        myRef.child("Games").child(codeGame).child("players").child(userKey).child("team").setValue("red")
        myRef.child("Games").child(codeGame).child("players").child(userKey).child("kill").setValue(0)
        myRef.child("Games").child(codeGame).child("players").child(userKey).child("death").setValue(0)
        myRef.child("Games").child(codeGame).child("players").child(userKey).child("idInGame").setValue(grandId+1)
        myRef.child("Games").child(codeGame).child("gameSpecs").child("playersInGame").setValue(playersInGame+1)
        myRef.child("Users").child(intent.getStringExtra("uid").toString()).child("games").child(codeGame).setValue(0)
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("code",binding.code.text.toString())
        intent.putExtra("userKey",userKey)
        intent.putExtra("userEmail",userEmail)
        startActivity(intent)
    }

}