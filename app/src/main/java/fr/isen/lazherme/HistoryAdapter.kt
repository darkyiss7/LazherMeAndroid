package fr.isen.lazherme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(private val gameListe: java.util.ArrayList<String>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView : TextView = itemView.findViewById(R.id.codeTitre)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val View = LayoutInflater.from(parent.context).inflate(R.layout.list_games, parent, false)
        return ViewHolder(View)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = gameListe[position]
        holder.textView.text = user
    }

    override fun getItemCount(): Int {
        return gameListe.size
    }
}