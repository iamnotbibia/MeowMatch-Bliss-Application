package com.example.meow.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.meow.adapter.DatingAdapter
import com.example.meow.databinding.FragmentDatingBinding
import com.example.meow.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction

class DatingFragment : Fragment() {

    private lateinit var binding: FragmentDatingBinding
    private lateinit var manager: CardStackLayoutManager
    val btnLike: Button? = null
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDatingBinding.inflate(layoutInflater)

        getData()

        return binding.root

    }

    private fun init2() {
        manager  = CardStackLayoutManager(requireContext(), object : CardStackListener{
            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }


            override fun onCardSwiped(direction: Direction?) {
                if (manager.topPosition == list!!.size){
                    Toast.makeText(requireContext(),"This is last card",Toast.LENGTH_SHORT).show()
                }

            }
            override fun onCardRewound() {

            }

            override fun onCardCanceled() {

            }

            override fun onCardAppeared(view: View?, position: Int) {

            }

            override fun onCardDisappeared(view: View?, position: Int) {

            }

        })
        manager.setVisibleCount(3)
        manager.setTranslationInterval(0.6f)
        manager.setScaleInterval(0.8f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
    }

    companion object{
        var list : ArrayList<UserModel>? = null
    }

    private fun getData() {
        FirebaseDatabase.getInstance().getReference("users")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("Mochi","onDataChange: ${snapshot.toString()}")
                    if(snapshot.exists()){
                        list = arrayListOf()
                        for(data in snapshot.children){
                            val model = data.getValue(UserModel::class.java)
                            if (model!!.number != FirebaseAuth.getInstance().currentUser!!.phoneNumber){
                                list!!.add(model)

                            }
                        }
                        list!!.shuffle()
                        init2()

                        binding.cardStackView.layoutManager = manager
                        binding.cardStackView.itemAnimator = DefaultItemAnimator()
                        binding.cardStackView.adapter = DatingAdapter(requireContext(), list!!)
                    }else{
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(),error.message,Toast.LENGTH_SHORT).show()
                }
            })
    }

}