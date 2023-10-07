package com.example.student_data_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminActivity : AppCompatActivity() {
    val db = Firebase.firestore

    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        recyclerView = findViewById<RecyclerView>(R.id.studentDataRV)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val studentDataList = mutableListOf<StudentData>()

        db.collection("user")
            .whereEqualTo("userType","student")
            .get()
            .addOnSuccessListener { querySnapshot->
                for (documentSnapshot in querySnapshot.documents) {
                    val username = documentSnapshot.getString("username")
                    val password = documentSnapshot.getString("password")
                    val degree = documentSnapshot.getString("degree")
                    val userId = documentSnapshot.getString("userId")

                    val studentData = StudentData(username,password,degree,userId)
                    studentDataList.add(studentData)
                }
                val adapter = StudentDataAdapter(this@AdminActivity,studentDataList)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->

            }

    }
}