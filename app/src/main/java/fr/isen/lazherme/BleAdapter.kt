package fr.isen.lazherme

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.isen.lazherme.R


class BleAdapter(private val bleliste: ArrayList<ScanResult>,val clickListener : (BluetoothDevice) -> (Unit)) : RecyclerView.Adapter<BleAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNom : TextView = itemView.findViewById(R.id.nomTextView)
        val textAddresse : TextView = itemView.findViewById(R.id.addresseTextView)
        val rssi : FloatingActionButton = itemView.findViewById(R.id.rssiView)

    }



    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = bleliste[position]
        if (item.device.name!=null){
            holder.textNom.text = item.device.name
        }else{
            holder.textNom.text = "Nom inconnu"
        }
        holder.textAddresse.text = item.device.address
        holder.rssi.setImageBitmap(textAsBitmap(item.rssi.toString(),20f, Color.WHITE))
        holder.rssi.setBackgroundTintList(
            ColorStateList.valueOf(Color
                .parseColor("#FFFFFF")));
        holder.itemView.setOnClickListener{
            clickListener(item.device)
        }

    }

    fun pickColor(rssi : Int):String{
        var color: String
        when{
            rssi<(-100) -> color= R.color.purple_dark.toString()
            rssi<(-75) -> color= R.color.purple_dark.toString()
            rssi<(-50) -> color= R.color.purple_dark.toString()
            rssi<(-25) -> color= R.color.purple_dark.toString()
            rssi<(-10) -> color= R.color.purple_dark.toString()
            rssi<(-5) -> color= R.color.purple_dark.toString()
            else -> color= R.color.purple_dark.toString()
        }
        return color
    }
    fun addToList(res:ScanResult){
        val index:Int = bleliste.indexOfFirst{ it.device.address==res.device.address }
        if(index == -1){
            bleliste.add(res)
        }else{
            bleliste[index]=res
            notifyItemInserted(bleliste.size - 1)
        }
        bleliste.sortBy { kotlin.math.abs(it.rssi) }
    }
    fun textAsBitmap(text: String?, textSize: Float, textColor: Int): Bitmap? {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = textSize
        paint.color = textColor
        paint.textAlign = Paint.Align.LEFT
        val baseline = -paint.ascent() // ascent() is negative
        val width = (paint.measureText(text) + 0.0f).toInt() // round
        val height = (baseline + paint.descent() + 0.0f).toInt()
        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        canvas.drawText(text!!, 0f, baseline, paint)
        return image
    }

    override fun getItemCount(): Int {
        return bleliste.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val View = LayoutInflater.from(parent.context).inflate(R.layout.list_items_ble, parent, false)
        return ViewHolder(View)
    }
}