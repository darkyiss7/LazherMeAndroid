package fr.isen.lazherme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import fr.isen.lazherme.databinding.ActivityGameStartedBinding
import java.util.concurrent.TimeUnit

private lateinit var database : FirebaseDatabase
private lateinit var myRef: DatabaseReference
private lateinit var binding: ActivityGameStartedBinding
class GameStartedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        binding = ActivityGameStartedBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.boutonArreter.setOnClickListener{
            arreterPartie()
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
                binding.texteTimer.text = "done!"
            }
        }.start()
    }

    private fun arreterPartie() {

    }
}
