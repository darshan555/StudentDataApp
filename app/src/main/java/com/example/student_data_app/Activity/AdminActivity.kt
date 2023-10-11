package com.example.student_data_app.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.student_data_app.R
import com.example.student_data_app.Model.StudentDataModel
import com.example.student_data_app.Adapter.StudentDataAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminActivity : AppCompatActivity() {
    val db = Firebase.firestore

    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        recyclerView = findViewById<RecyclerView>(R.id.studentDataRV)
        val signout = findViewById<Button>(R.id.signoutaBTN)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val studentDataList = mutableListOf<StudentDataModel>()

        db.collection("user")
            .whereEqualTo("userType","student")
            .get()
            .addOnSuccessListener { querySnapshot->
                for (documentSnapshot in querySnapshot.documents) {
                    val username = documentSnapshot.getString("username")
                    val name = documentSnapshot.getString("name")
                    val password = documentSnapshot.getString("password")
                    val degree = documentSnapshot.getString("degree")
                    val userId = documentSnapshot.getString("userId")

                    val studentData = StudentDataModel(username,name,password,degree,userId)
                    studentDataList.add(studentData)
                }
                val adapter = StudentDataAdapter(this@AdminActivity,studentDataList)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->

            }
        signout.setOnClickListener{
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build()

            GoogleSignIn.getClient(this,gso).signOut()
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

}