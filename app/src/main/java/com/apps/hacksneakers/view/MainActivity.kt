package com.apps.hacksneakers.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.apps.hacksneakers.R
import com.apps.hacksneakers.adapter.ShoesAdapter
import com.apps.hacksneakers.model.ShoeModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var mDataBase: DatabaseReference
    private lateinit var shoeList:ArrayList<ShoeModel>
    private lateinit var mAdapter:ShoesAdapter
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAuth = FirebaseAuth.getInstance()
        shoeList = arrayListOf<ShoeModel>()
        /**initialized*/
        mAdapter = ShoesAdapter(this@MainActivity,shoeList)
        recyclerShoeData.layoutManager = GridLayoutManager(this,2)
        recyclerShoeData.setHasFixedSize(true)
        /**getData firebase*/
        loadImageProfile()

        refresh.setOnClickListener {
            shoeList.clear()
            getShoesData()
        }
        fab_scan.setOnClickListener{
            startActivity(Intent(this@MainActivity, ScanActivity::class.java))
        }

        img_profile.setOnClickListener {
            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
        }
        btn_search.setOnClickListener {
            searchByName()
        }

    }

    private var searchValue = ""
    private fun searchByName() {
        searchValue = search_value.text.toString().trim()

        val ref = FirebaseDatabase.getInstance().getReference("Shoe")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // Checking if the value exists
                if (snapshot.exists()){
                    shoeList.clear()
                    // looping through to values
                    for (i in snapshot.children){
                        val shoe = i.getValue(ShoeModel::class.java)
                        // checking if the name searched is available and adding to the array list
                        if (shoe!!.name == searchValue ){
                            shoeList.add(shoe)
                        }
                    }
                    //setting data to recyclerview
                    mAdapter.submitList(shoeList)
                    recyclerShoeData.adapter = mAdapter
                } else{
                    Toast.makeText(applicationContext, "Data does not exist", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun loadImageProfile() {
        val firebaseUser = firebaseAuth.currentUser!!
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val name = "${snapshot.child("name").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"

                    hello.text = "Hello, $name"

                    try {
                        Glide.with(this@MainActivity)
                            .load(profileImage)
                            .placeholder(R.drawable.user_demo)
                            .into(img_profile)

                    }catch (e: Exception){

                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun getShoesData() {
        mDataBase = FirebaseDatabase.getInstance().getReference("Shoe")
        mDataBase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (shoeSnapshot in snapshot.children){
                        val shoe = shoeSnapshot.getValue(ShoeModel::class.java)
                        shoeList.add(shoe!!)
                    }

                    recyclerShoeData.adapter = mAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,
                    error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
}
