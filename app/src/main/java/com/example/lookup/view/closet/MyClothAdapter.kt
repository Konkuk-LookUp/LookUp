package com.example.lookup.view.closet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lookup.databinding.ClothrowBinding

class MyClothAdapter(val items:ArrayList<MyCloth>)
    : RecyclerView.Adapter<MyClothAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun OnItemClick(data: MyCloth,pos:Int)
    }
    var itemClickListener:OnItemClickListener?=null

    inner class ViewHolder(val binding: ClothrowBinding):RecyclerView.ViewHolder(binding.root){
        init{
            binding.item.setOnClickListener{
                itemClickListener?.OnItemClick(items[adapterPosition],adapterPosition)
            }
        }
    }

    fun moveItem(oldPos:Int,newPos:Int){
        val tmp = items[oldPos]
        items[oldPos]=items[newPos]
        items[newPos]=tmp
        notifyItemMoved(oldPos,newPos)
    }

    fun removeItem(pos:Int){
        items.removeAt(pos)
        notifyItemRemoved(pos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ClothrowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.clothImg.setImageResource(items[position].img)
        holder.binding.clothname.text = items[position].name
    }

}