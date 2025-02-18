package com.example.meow.ui

import android.graphics.Bitmap.Config
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.meow.R
import com.example.meow.adapter.MessageUserAdapter
import com.example.meow.databinding.FragmentMessageBinding
import com.example.meow.ui.DatingFragment.Companion.list
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessageFragment : Fragment() {

    private lateinit var binding : FragmentMessageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMessageBinding.inflate(layoutInflater)

        getData()


        return binding.root
    }

    private fun getData() {


        val currentId = FirebaseAuth.getInstance().currentUser!!.phoneNumber

        FirebaseDatabase.getInstance().getReference("chats")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    var list = arrayListOf<String>()
                    var newlist = arrayListOf<String>()

                    for (data in snapshot.children){
                        if(data.key!!.contains(currentId!!)){

                            list.add(data.key!!.replace(currentId!!,""))
                            newlist.add(data.key!!)
                        }
                    }

//                    binding.recycleView.adapter =
//                        MessageUserAdapter(requireContext(), list,newlist)

                    try {
                        binding.recycleView.adapter =
                            MessageUserAdapter(requireContext(), list,newlist)
                    }catch (e: Exception){

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(),error.message,Toast.LENGTH_SHORT).show()
                }

            })
    }
}