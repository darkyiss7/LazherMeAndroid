package fr.isen.lazherme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter2(private val userListe: java.util.ArrayList<userData>) : RecyclerView.Adapter<UserAdapter2.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView : TextView = itemView.findViewById(R.id.codeTitre)
        val texteScore : TextView = itemView.findViewById(R.id.texteScore)
        val texteKills : TextView = itemView.findViewById(R.id.texteKills)
        val texteDeath : TextView = itemView.findViewById(R.id.texteDeath)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val View = LayoutInflater.from(parent.context).inflate(R.layout.list_users2, parent, false)
        return ViewHolder(View)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userListe[position]
        holder.textView.text = user.username
        holder.texteScore.text = "S "+(user.kills.toInt()*100).toString()
        holder.texteKills.text = "K " + user.kills
        holder.texteDeath.text = "D " + user.deaths
    }

    override fun getItemCount(): Int {
        return userListe.size
    }
}