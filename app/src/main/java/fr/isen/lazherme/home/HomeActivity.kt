package fr.isen.lazherme.home

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
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

        bottomNavView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.create -> {
                    setFragment(createFragment)
                    return@OnItemSelectedListener true
                }
                R.id.join -> {
                    setFragment(joinFragment)
                    return@OnItemSelectedListener true
                }
            }
            false
        })
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