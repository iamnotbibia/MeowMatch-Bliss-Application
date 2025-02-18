package com.example.meow.auth

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.meow.MainActivity
import com.example.meow.R
import com.example.meow.databinding.ActivityRegisterBinding
import com.example.meow.model.UserModel
import com.example.meow.utils.Config
import com.example.meow.utils.Config.hideDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class RegisterActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRegisterBinding

    private var imageUri: Uri? = null

    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it

        binding.userImage.setImageURI(imageUri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        select image
        binding.userImage.setOnClickListener {
            selectImage.launch("image/*")
        }

        binding.saveData.setOnClickListener {
            validateData()
        }


    }

    private fun validateData() {
        if (binding.userName.text.toString().isEmpty() ||
            binding.name.text.toString().isEmpty() ||
            binding.userGmail.text.toString().isEmpty() ||
            binding.userCity.text.toString().isEmpty() ||
            imageUri == null
        ) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
        } else if (!binding.termCondition.isChecked) {
            Toast.makeText(this, "Please accept terms and conditions", Toast.LENGTH_SHORT).show()
        } else {
            uploadImage()
        }
    }

    private fun uploadImage() {
        Config.showDialog(this)

        val storageRef = FirebaseStorage.getInstance().getReference("Profile")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("profile.jpg")


        storageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    storeData(it)
                }.addOnFailureListener {
                    hideDialog()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                hideDialog()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeData(imageUrl: Uri?) {
        val data = UserModel(
            userName = binding.userName.text.toString(),
            name = binding.name.text.toString(),
            image = imageUrl.toString(),
            email = binding.userGmail.text.toString(),
            city = binding.userCity.text.toString(),
            breeds = binding.userBreed.text.toString(),
            gender = binding.userSex.text.toString(),
            age = binding.userAge.text.toString(),
            number = FirebaseAuth.getInstance().currentUser!!.phoneNumber!!
        )

        FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
            .setValue(data).addOnCompleteListener {
                hideDialog()
                if (it.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                        Toast.makeText(this, "User register successful", Toast.LENGTH_SHORT).show()
                } else {
                        Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }

    }
}


