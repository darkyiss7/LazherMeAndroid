package fr.isen.lazherme.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import fr.isen.lazherme.*
import fr.isen.lazherme.R
import fr.isen.lazherme.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var database : FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var code : String
    private lateinit var userEmail : String
    private lateinit var userKey : String
    private lateinit var userName : String

    private lateinit var bottomNavView : BottomNavigationView

    override fun onStart() {
        getUserName()
        super.onStart()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        myRef = database.reference

        userEmail = intent.getStringExtra("email").toString()
        userKey = intent.getStringExtra("uid").toString()

        bottomNavView = binding.bottonNavigation

        val createFragment = CreateFragment()

        val joinFragment = JoinFragment()

        setFragment(createFragment)

        bottomNavView.setOnItemReselectedListener {
            when(it.itemId){
                R.id.create -> {
                    setFragment(createFragment)
                }
                R.id.join -> {
                    setFragment(joinFragment)
                }
            }
        }

    }

    private fun setFragment(fragment : Fragment) {
        supportFragmentManager.beginTransaction().apply {
            var data = Bundle()
            val ref = myRef.child("Users").child(userKey)
            ref.child("username").get().addOnSuccessListener {
                userName = it.value.toString()
                data.putString("userKey",userKey)
                data.putString("userEmail",userEmail)
                data.putString("userName",userName)
                fragment.arguments=data
                replace(R.id.frame,fragment)
                commit()
            }.addOnFailureListener{
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
                 finish()
                 return true
             }
             R.id.historique ->{
                 val intent = Intent(this, HistoryActivity::class.java)
                 intent.putExtra("userKey",userKey)
                 intent.putExtra("userEmail",userEmail)
                 startActivity(intent)
                 finish()
                 return true
             }
             R.id.dexonnexion ->{
                 AlertDialog.Builder(this)
                     .setMessage("Voulez vous vraiment vous deconnecter ?")
                     .setPositiveButton("Oui",
                         DialogInterface.OnClickListener { dialog, whichButton ->
                             val intent = Intent(this, SignInActivity::class.java)
                             FirebaseAuth.getInstance().signOut()
                             startActivity(intent)
                             finish()
                             Toast.makeText(this, "Déconnexion...", Toast.LENGTH_SHORT).show()
                         })
                     .setNegativeButton("Non", null).show()

                 return true
             }
             else -> super.onOptionsItemSelected(item)
         }
     }

/*
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
         myRef.child("Games").child(codeGame).child("players").child(userKey).child("username").setValue(userName)
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
         finish()
     }
     */


     override fun onBackPressed() {

         AlertDialog.Builder(this)
             .setMessage("Se deconnecter de l'equippement ? (retour a la selection)")
             .setPositiveButton("Oui",
                 DialogInterface.OnClickListener { dialog, whichButton ->
                     val intent = Intent(this, BluetoothService::class.java)
                     intent.putExtra("idServ","2")
                     startService(intent)
                     finish()
                     super.onBackPressed()
                     Toast.makeText(this, "Déconnexion...", Toast.LENGTH_SHORT).show()
                 })
             .setNegativeButton("Non", null).show()

     }
    private fun getUserName(){
        val ref = myRef.child("Users").child(userKey)
        ref.child("username").get().addOnSuccessListener {
            userName = it.value.toString()
            supportActionBar?.title = userName
            supportActionBar?.setIcon(R.drawable.ic_baseline_person_24_white)
            supportActionBar?.setDisplayShowHomeEnabled(true);

        }.addOnFailureListener{
        }
    }
}