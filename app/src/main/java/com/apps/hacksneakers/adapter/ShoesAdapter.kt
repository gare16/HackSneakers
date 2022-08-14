package com.apps.hacksneakers.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.apps.hacksneakers.R
import com.apps.hacksneakers.databinding.ItemListBinding
import com.apps.hacksneakers.model.ShoeModel
import com.apps.hacksneakers.view.NewActivity

class ShoesAdapter(
    var c: Context, var shoeList:ArrayList<ShoeModel>
): ListAdapter<ShoeModel, ShoesAdapter.ShoeViewHolder>(COMPARATOR){

    private object COMPARATOR: DiffUtil.ItemCallback<ShoeModel>(){
        override fun areItemsTheSame(oldItem: ShoeModel, newItem: ShoeModel): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: ShoeModel, newItem: ShoeModel): Boolean {
            return oldItem.uid == newItem.uid
        }

    }

    inner class ShoeViewHolder(var v: ItemListBinding): RecyclerView.ViewHolder(v.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoeViewHolder {
        val inflter = LayoutInflater.from(parent.context)
        val v = DataBindingUtil.inflate<ItemListBinding>(
            inflter, R.layout.item_list,parent,
            false)
        return ShoeViewHolder(v)

    }

    override fun onBindViewHolder(holder: ShoeViewHolder, position: Int) {
        val newList = shoeList[position]
        holder.v.isShoes = shoeList[position]
        holder.v.root.setOnClickListener {
            val shoeId = newList.id
            val img = newList.img
            val name = newList.name
            val info = newList.info
            val type = newList.type
            val price = newList.price


            /**set Data*/
            val mIntent = Intent(c, NewActivity::class.java)
            mIntent.putExtra("shoeId",shoeId)
            mIntent.putExtra("img",img)
            mIntent.putExtra("name",name)
            mIntent.putExtra("info",info)
            mIntent.putExtra("type",type)
            mIntent.putExtra("price",price)

            c.startActivity(mIntent)
        }
    }

    override fun getItemCount(): Int {
        return  shoeList.size
    }

}