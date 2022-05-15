package fr.isen.lazherme.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import fr.isen.lazherme.GameActivity
import fr.isen.lazherme.R
import fr.isen.lazherme.databinding.FragmentCreateBinding

class CreateFragment : Fragment() {
    private lateinit var code: String
    private lateinit var userEmail: String
    private lateinit var userKey: String
    private lateinit var userName: String
    private lateinit var _binding : FragmentCreateBinding
    private val binding get() = _binding!!

    private var count = 2
    private var mode = 0
    private var temps = 10
    private lateinit var database : FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        // Inflate the layout for this fragment
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        binding.texteNombre.text = count.toString()
        binding.texteMode.text = getString(R.string.MME)
        binding.texteTemps.text = temps.toString()

        var data = arguments
        if (data != null){
            userKey = data.getString("userKey").toString()
            userName = data.getString("userName").toString()
            userEmail = data.getString("userEmail").toString()
        }
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
            myRef.child("Games").child(code).child("players").child(userKey).child("username").setValue(userName)
            myRef.child("Games").child(code).child("players").child(userKey).child("team").setValue("blue")
            myRef.child("Games").child(code).child("players").child(userKey).child("kill").setValue(0)
            myRef.child("Games").child(code).child("players").child(userKey).child("death").setValue(0)
            myRef.child("Games").child(code).child("players").child(userKey).child("idInGame").setValue(0)
            myRef.child("Users").child(userKey!!).child("games").child(code).setValue(0)
            myRef.child("Users").child(userKey!!).child("currentgame").setValue(code)
            val intent = Intent(activity, GameActivity::class.java)
            intent.putExtra("code",code)
            intent.putExtra("userKey",userKey)
            intent.putExtra("userEmail",userEmail)
            startActivity(intent)
        }
        binding.boutonModeDroite.setOnClickListener{changemode(1)}
        binding.boutonModeGauche.setOnClickListener{changemode(0)}
        binding.boutonNombreDroite.setOnClickListener{augmenteNombre()}
        binding.boutonNombreGauche.setOnClickListener{diminuerNombre()}
        binding.boutonTempsDroite.setOnClickListener{augmenterTemps()}
        binding.boutonTempsGauche.setOnClickListener{diminuerTemps()}
        return binding.root
    }

    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
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

}