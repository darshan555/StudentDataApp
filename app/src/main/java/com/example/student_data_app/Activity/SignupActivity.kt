package com.example.student_data_app.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.student_data_app.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class SignupActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    val users = arrayOf("student", "admin")
    var myUser: String = ""
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        val userType = findViewById<Spinner>(R.id.makeUserType)
        val username = findViewById<EditText>(R.id.ctUsernameET)
        val name = findViewById<EditText>(R.id.ctnameET)
        val email = findViewById<EditText>(R.id.ctemailET)
        val password = findViewById<EditText>(R.id.ctPasswordET)
        val degree = findViewById<EditText>(R.id.degreeET)
        val loginTxtBtn = findViewById<TextView>(R.id.loginTV)
        val signupButton = findViewById<Button>(R.id.signupBTN)

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, users)
        userType.adapter = spinnerAdapter

        userType.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val user = users[position]
                if (user == "student") {
                    name.visibility = View.VISIBLE
                    email.visibility = View.VISIBLE
                    username.visibility = View.VISIBLE
                    degree.visibility = View.VISIBLE
                    password.visibility = View.VISIBLE
                    myUser = "student"
                } else {
                    name.visibility = View.VISIBLE
                    username.visibility = View.VISIBLE
                    email.visibility = View.VISIBLE
                    degree.visibility = View.GONE
                    password.visibility = View.VISIBLE
                    myUser = "admin"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val myGmail = intent.getStringExtra("email")
        val myName = intent.getStringExtra("name")
        val myUid = intent.getStringExtra("uid")
        name.setText(myName)
        email.setText(myGmail)

        loginTxtBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        signupButton.setOnClickListener {
            val usernameText = username.text.toString()
            val passwordText = password.text.toString()
            val emailText = email.text.toString()
            val nameText = name.text.toString()
            val degreeText = degree.text.toString()
            val userTypeText = userType.selectedItem.toString()

            val uid = myUid ?: UUID.randomUUID().toString()

            if (myUser == "student" || myUser == "admin") {
                val userData = hashMapOf(
                    "username" to usernameText,
                    "password" to passwordText,
                    "name" to nameText,
                    "degree" to degreeText.takeIf { myUser == "student" },
                    "email" to emailText,
                    "userType" to userTypeText,
                    "userId" to ""
                )
                db.collection("user")
                    .whereEqualTo("username", usernameText)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.isEmpty) {
                            val userCollection = db.collection("user")
                            val documentReference = if (uid != null) {
                                userCollection.document(uid)
                            } else {
                                userCollection.document()
                            }
                            documentReference
                                .set(userData)
                                .addOnSuccessListener {
                                    val userId = documentReference.id
                                    userData["userId"] = userId
                                    documentReference.update("userId", userId)
                                    username.text.clear()
                                    email.text.clear()
                                    password.text.clear()
                                    name.text.clear()
                                    degree.text.clear()

                                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestIdToken(getString(R.string.client_id))
                                        .requestEmail()
                                        .build()
                                    GoogleSignIn.getClient(this,gso).signOut()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to add user data!", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Username already exists!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to check username existence!", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please select UserType", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onResume() {
        super.onResume()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        GoogleSignIn.getClient(this,gso).signOut()
    }
}
