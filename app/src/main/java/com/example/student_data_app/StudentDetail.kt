package com.example.student_data_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StudentDetail : AppCompatActivity() {
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_detail)

        val username = findViewById<EditText>(R.id.admUsernameET)
        val password = findViewById<EditText>(R.id.admPasswordET)
        val degree = findViewById<EditText>(R.id.admDegreeET)
        val update = findViewById<Button>(R.id.updateStuBTN)

        val uid = intent.getStringExtra("uid")

        val usersCollection = db.collection("user")

        if (uid != null) {
            usersCollection.document(uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val usernameText = documentSnapshot.getString("username")
                        val passwordText = documentSnapshot.getString("password")
                        val degreeText = documentSnapshot.getString("degree")

                        username.setText(usernameText)
                        password.setText(passwordText)
                        degree.setText(degreeText)

                    } else {

                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        update.setOnClickListener{
            val updatedUsername = username.text.toString()
            val updatedPassword = password.text.toString()
            val updatedDegree = degree.text.toString()

            val updatedData = hashMapOf(
                "username" to updatedUsername,
                "password" to updatedPassword,
                "degree" to updatedDegree
            )
            if (uid != null) {
                usersCollection.document(uid!!)
                    .update(updatedData as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to update data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}