package com.bashirli.notebookkotlinsql

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.bashirli.notebookkotlinsql.databinding.RecyclerBinding

class Adapter(val arrayList: ArrayList<Model>) : RecyclerView.Adapter<Adapter.AdapterHolder>() {

    class AdapterHolder(val binding:RecyclerBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
    val recyclerBinding =RecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AdapterHolder(recyclerBinding)
    }

    override fun onBindViewHolder(holder: AdapterHolder, position: Int) {
    holder.binding.recyclerText.text=arrayList.get(position).name
        holder.binding.recyclerText.setOnClickListener(){
            val intent=Intent(it.context,NoteActivity::class.java)
            intent.putExtra("name",arrayList.get(position).name)
            intent.putExtra("id",arrayList.get(position).id.toString())
            intent.putExtra("info","old")
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}