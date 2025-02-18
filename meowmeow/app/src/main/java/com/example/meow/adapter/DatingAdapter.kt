package com.example.meow.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meow.activity.MessageActivity
import com.example.meow.databinding.ItemUserLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.example.meow.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DatingAdapter(val  context: Context, val list : ArrayList<UserModel>) : RecyclerView.Adapter<DatingAdapter.DatingViewHolder>() {
    inner class DatingViewHolder(val binding: ItemUserLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatingViewHolder {
        return DatingViewHolder(
            ItemUserLayoutBinding.inflate(
                LayoutInflater.from(context),
                parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DatingViewHolder, position: Int) {
        holder.binding.textView2.text = list[position].userName
        holder.binding.breed.text = list[position].breeds
        holder.binding.sex.text = list[position].gender
        holder.binding.age.text = list[position].age

        Glide.with(context).load(list[position].image).centerCrop().into(holder.binding.userImage)

        holder.binding.chat.setOnClickListener {
            val inte = Intent(context, MessageActivity::class.java)
            inte.putExtra("userId",list[position].number)
            context.startActivity(inte)
        }

        val userId = list[position].number

        checkFollowingStatus(userId!!, holder.binding.btnLike)

        holder.binding.btnLike.setOnClickListener {
            if (holder.binding.btnLike.text.toString() == "Follow") {

                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(userId!!)
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(userId)
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                            }

                                        }

                                }
                            }
                        }
                }

            } else {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(userId!!)
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(userId)
                                        .child("Followers").child(it1.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }
                                }
                            }
                        }
                }
            }
        }

    }
    private fun checkFollowingStatus(userId: String, btnLike: Button) {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }

        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(userId).exists()){
                    btnLike.text = "Following"
                }
                else{
                    btnLike.text = "Follow"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}

