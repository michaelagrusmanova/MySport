package com.example.mysport

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

internal class ActivityAdapter(private var itemsList: List<ActivityModel>) :
    /**
     * Adapter: rozložení položek v RecyclerView, hlásí události
     * Viewholder: uložení a vyhledání položek + detekce kliků na položky
     */
    RecyclerView.Adapter<ActivityAdapter.MyViewHolder>() {
    private var onClickDeleteItem: ((ActivityModel) -> Unit)? = null

    fun setOnClickDeleteItem(callback: (ActivityModel) -> Unit) {
        this.onClickDeleteItem = callback
    }

    private var onClickUpdateItem: ((ActivityModel) -> Unit)? = null

    fun setOnClickUpdateItem(callback: (ActivityModel) -> Unit) {
        this.onClickUpdateItem = callback
    }

    /**
     * Nadefinování položek layoutu ve ViewHolderu.
     */
    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemName: TextView = view.findViewById(R.id.tvName)
        var itemDistance: TextView = view.findViewById(R.id.tvDistance)
        var itemDuration: TextView = view.findViewById(R.id.tvDuration)
        var itemNotes: TextView = view.findViewById(R.id.tvNotes)
        var itemPhoto: ImageView = view.findViewById(R.id.imageViewItemPhoto)
        var btnUpdate: Button = view.findViewById(R.id.btnUpdate)
        var btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    /**
     * Spojení layoutu s ViewHolderem, z jakého layoutu položky jsou.
     * Vytvoření ViewHolderu.
     */
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_items, parent, false)
        return MyViewHolder(itemView)
    }

    /**
     * Nabindování vlastností položky do ViewHolderu.
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemsList[position]
        holder.itemName.text = "Aktivita: " + item.name
        holder.itemDistance.text = "Vzdálenost: " + item.distance
        holder.itemDuration.text = "Trvání: " + item.duration
        holder.itemNotes.text = "Poznámka: " + item.notes
        val bitmapImage = item.image?.let { BitmapFactory.decodeByteArray(item.image,
            0, it.size) }
        holder.itemPhoto.setImageBitmap(bitmapImage)
        holder.btnDelete.setOnClickListener { onClickDeleteItem?.invoke(item) }
        holder.btnUpdate.setOnClickListener { onClickUpdateItem?.invoke(item) }
    }

    /**
     * Vrátí počet položek seznamu.
     */
    override fun getItemCount(): Int {
        return itemsList.size
    }

}