package com.example.student_data_app.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.student_data_app.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StudentsDetail : AppCompatActivity() {
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students_detail)
        val username = findViewById<EditText>(R.id.usernameETa)
        val password = findViewById<EditText>(R.id.passwordETa)
        val email = findViewById<EditText>(R.id.emailETa)
        val name = findViewById<EditText>(R.id.nameETa)
        val degree = findViewById<EditText>(R.id.degreeETa)
        val update = findViewById<Button>(R.id.updateBTNa)

        var uid = intent.getStringExtra("userId")

        val usersCollection = db.collection("user")

        if (uid != null) {
            usersCollection.document(uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val usernameText = documentSnapshot.getString("username")
                        val passwordText = documentSnapshot.getString("password")
                        val emailText = documentSnapshot.getString("email")
                        val nameText = documentSnapshot.getString("name")
                        val degreeText = documentSnapshot.getString("degree")

                        username.setText(usernameText)
                        name.setText(nameText)
                        email.setText(emailText)
                        password.setText(passwordText)
                        degree.setText(degreeText)

                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        update.setOnClickListener{
            val updatedUsername = username.text.toString()
            val updatedName = name.text.toString()
            val updatedEmail = email.text.toString()
            val updatedPassword = password.text.toString()
            val updatedDegree = degree.text.toString()

            val updatedData = hashMapOf(
                "username" to updatedUsername,
                "name" to updatedName,
                "email" to updatedEmail,
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