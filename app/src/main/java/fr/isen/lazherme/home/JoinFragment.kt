package fr.isen.lazherme.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.*
import fr.isen.lazherme.GameActivity
import fr.isen.lazherme.databinding.FragmentJoinBinding

class JoinFragment : Fragment() {

    private lateinit var code: String
    private lateinit var userEmail: String
    private lateinit var userKey: String
    private lateinit var userName: String
    private lateinit var _binding : FragmentJoinBinding
    private val binding get() = _binding!!

    private lateinit var database : FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentJoinBinding.inflate(inflater, container, false)

        database = FirebaseDatabase.getInstance()
        myRef = database.reference

        var data = arguments
        if (data != null){
            userKey = data.getString("userKey").toString()
            userName = data.getString("userName").toString()
            userEmail = data.getString("userEmail").toString()
        }

        binding.rejoindreBouton.setOnClickListener{
            val code = binding.code.text.toString()

            if (code.isNotEmpty()) {
                checkGame(this)
            }
            else {
                Toast.makeText(context,"Veuillez saisir un code", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return binding.root
    }
    private fun checkGame(context: JoinFragment) {
        val ref = myRef.child("Games").child(binding.code.text.toString())
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
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
                    Toast.makeText(activity, "Partie introuvable", Toast.LENGTH_SHORT).show()
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
        myRef.child("Users").child(userKey).child("games").child(codeGame).setValue(0)
        val intent = Intent(context, GameActivity::class.java)
        intent.putExtra("code",binding.code.text.toString())
        intent.putExtra("userKey",userKey)
        intent.putExtra("userEmail",userEmail)
        startActivity(intent)
    }
}