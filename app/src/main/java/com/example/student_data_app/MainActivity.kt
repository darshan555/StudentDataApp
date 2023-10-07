package com.example.student_data_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //FireBase = vedpandya789@gmail.com
        setContentView(R.layout.activity_main)

        val userType = findViewById<Spinner>(R.id.userTypeSp)
        val username = findViewById<EditText>(R.id.usernameET)
        val password = findViewById<EditText>(R.id.passwordET)
        val loginButton = findViewById<Button>(R.id.loginBTN)
        val signupTxtBtn = findViewById<TextView>(R.id.signupTV)

        val users = arrayOf("Select User","student", "admin")

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,users)
        userType.adapter = spinnerAdapter

        loginButton.setOnClickListener {
            val selectedUserType = userType.selectedItem.toString()
            val usernameText = username.text.toString()
            val passwordText = password.text.toString()

            db.collection("user")
                .whereEqualTo("username", usernameText)
                .whereEqualTo("password", passwordText)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {

                        val documentSnapshot = querySnapshot.documents[0]
                        val userId = documentSnapshot.id // Retrieve the user ID
                        val intent: Intent

                        if (selectedUserType == "student") {
                            intent = Intent(this, StudentActivity::class.java)
                        } else if (selectedUserType == "admin") {
                            intent = Intent(this, AdminActivity::class.java)
                        } else {
                            // Handle other user types or cases
                            return@addOnSuccessListener
                        }
                        intent.putExtra("userId", userId)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        signupTxtBtn.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
        }



    }

}