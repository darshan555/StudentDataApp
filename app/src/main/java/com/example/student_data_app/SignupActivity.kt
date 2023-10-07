package com.example.student_data_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val userType = findViewById<Spinner>(R.id.makeUserType);
        val username = findViewById<EditText>(R.id.ctUsernameET);
        val password = findViewById<EditText>(R.id.ctPasswordET);
        val degree = findViewById<EditText>(R.id.degreeET);
        val loginTxtBtn = findViewById<TextView>(R.id.loginTV);
        val signupButton = findViewById<Button>(R.id.signupBTN);

        val users = arrayOf("Select User","student", "admin")

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,users)
        userType.adapter = spinnerAdapter

        userType.onItemSelectedListener = object :OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val user = users[position]
                if(user == "student"){
                    degree.visibility = View.VISIBLE
                }else if(user == "admin"){
                    degree.visibility = View.GONE
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        loginTxtBtn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

        signupButton.setOnClickListener{
           val usernameText = username.text.toString()
           val passwordText = password.text.toString()
            val degreeText = degree.text.toString()
            val userTypeText = userType.selectedItem.toString()


            if(degree.visibility == View.VISIBLE) {
                val userData = hashMapOf(
                    "username" to usernameText,
                    "password" to passwordText,
                    "degree" to degreeText,
                    "userType" to userTypeText,
                    "userId" to ""
                )
                db.collection("user")
                    .add(userData)
                    .addOnSuccessListener { documentReference ->
                        val userId = documentReference.id
                        userData["userId"] = userId
                        documentReference.update("userId", userId)
                        Toast.makeText(this, "Successfully Added with ID: $userId", Toast.LENGTH_SHORT).show()
                        username.text.clear()
                        password.text.clear()
                        degree.text.clear()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed !", Toast.LENGTH_SHORT).show()
                    }
            }else{
                val userData = hashMapOf(
                    "username" to usernameText,
                    "password" to passwordText,
                    "userType" to userTypeText,
                    "userId" to ""
                )
                db.collection("user")
                    .add(userData)
                    .addOnSuccessListener { documentReference ->
                        val userId = documentReference.id
                        userData["userId"] = userId
                        documentReference.update("userId", userId)
                        Toast.makeText(this, "Successfully Added with ID: $userId", Toast.LENGTH_SHORT).show()
                        username.text.clear()
                        password.text.clear()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed !", Toast.LENGTH_SHORT).show()
                    }
            }

        }

    }
}