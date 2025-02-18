package com.example.meow.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.meow.R
import com.example.meow.activity.EditProfileActivity
import com.example.meow.auth.LoginActivity
import com.example.meow.databinding.FragmentProfileBinding
import com.example.meow.model.UserModel
import com.example.meow.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        Config.showDialog(requireContext())

        binding = FragmentProfileBinding.inflate(layoutInflater)


        FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!).get()
            .addOnSuccessListener {
                if (it.exists()){
                    val data = it.getValue(UserModel::class.java)

                    binding.userName.setText(data!!.userName.toString())
                    binding.name.setText(data!!.name.toString())
                    binding.gmail.setText(data!!.email.toString())
                    binding.city.setText(data!!.city.toString())
                    binding.breed.setText(data!!.breeds.toString())
                    binding.gender.setText(data!!.gender.toString())
                    binding.age.setText(data!!.age.toString())
                    binding.number.setText(data!!.number.toString())

                    val img  = data.image
                    Glide.with(requireContext()).load(img).placeholder(R.drawable.logo).into(binding.userImage)

                    Config.hideDialog()
                }
            }


        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        binding.editProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        return binding.root
    }

}