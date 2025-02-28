package com.example.meow.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meow.activity.MessageActivity
import com.example.meow.databinding.UserItemLayoutBinding
import com.example.meow.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessageUserAdapter(val context: Context, val list: ArrayList<String>, val chatKey: List<String>) :
    RecyclerView.Adapter<MessageUserAdapter.MassageUserViewHolder>() {

    inner class MassageUserViewHolder(val binding : UserItemLayoutBinding)
        :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MassageUserViewHolder {
        return MassageUserViewHolder(
            UserItemLayoutBinding.inflate
                (LayoutInflater.from(context),parent,
                false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MassageUserViewHolder, position: Int) {

        FirebaseDatabase.getInstance().getReference("users")
            .child(list[position]).addListenerForSingleValueEvent(
                object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            val  data = snapshot.getValue(UserModel::class.java)

                            Glide.with(context).load(data!!.image).into(holder.binding.userImage)

                            holder.binding.userName.text = data.userName
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
                    }

                }
            )

        holder.itemView.setOnClickListener {
            val inte = Intent(context, MessageActivity::class.java)
            inte.putExtra("chat_id", chatKey[position])
            inte.putExtra("userId", list[position])
            context.startActivity(inte)
        }

    }


}