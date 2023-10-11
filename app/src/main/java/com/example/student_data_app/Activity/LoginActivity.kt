package com.example.student_data_app.Activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.student_data_app.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    var myUser:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //FireBase = vedpandya789@gmail.com
        setContentView(R.layout.activity_main)

        val userType = findViewById<Spinner>(R.id.userTypeSp)
        val username = findViewById<EditText>(R.id.usernameET)
        val password = findViewById<EditText>(R.id.passwordET)
        val loginButton = findViewById<Button>(R.id.loginBTN)
        val signupTxtBtn = findViewById<TextView>(R.id.signupTV)
        val googleLogin = findViewById<ImageView>(R.id.googleLIV)

        val users = arrayOf("student", "admin")

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,users)
        userType.adapter = spinnerAdapter

        userType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val user = users[position]
                if(user == "student"){
                    username.visibility = View.VISIBLE
                    password.visibility = View.VISIBLE
                    myUser = "student"
                }else{
                    username.visibility = View.VISIBLE
                    password.visibility = View.VISIBLE
                    myUser = "admin"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        loginButton.setOnClickListener {
            val usernameText = username.text.toString()
            val passwordText = password.text.toString()
            val selectedUserType = userType.selectedItem.toString()

            db.collection("user")
                .whereEqualTo("username", usernameText)
                .whereEqualTo("password", passwordText)
                .whereEqualTo("userType", selectedUserType)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentSnapshot = querySnapshot.documents[0]
                        val userId = documentSnapshot.id
                        val intent: Intent

                        if (selectedUserType == "student") {
                            intent = Intent(this, StudentActivity::class.java)
                            username.text.clear()
                            password.text.clear()
                        } else{
                            intent = Intent(this, AdminActivity::class.java)
                            username.text.clear()
                            password.text.clear()
                        }
                        intent.putExtra("userId", userId)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Please select valid usertype", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleLogin.setOnClickListener {
            val signInClient =  googleSignInClient.signInIntent
            luncher.launch(signInClient)
        }


        signupTxtBtn.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
    private val luncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? = task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            db.collection("user")
                                .document(user.uid)
                                .get()
                                .addOnSuccessListener { documentSnapshot ->
                                    if (documentSnapshot.exists()) {
                                        val userType = documentSnapshot.getString("userType")
                                        if (myUser == userType) {
                                            when (myUser) {
                                                "student" -> {
                                                    val intent = Intent(this, StudentActivity::class.java)
                                                    intent.putExtra("userId", user.uid)
                                                    startActivity(intent)
                                                    finish()
                                                }
                                                "admin" -> {
                                                    val intent = Intent(this, AdminActivity::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }
                                        } else {
                                            Toast.makeText(this, "Please select valid User", Toast.LENGTH_SHORT).show()
                                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                .requestIdToken(getString(R.string.client_id))
                                                .requestEmail()
                                                .build()
                                            GoogleSignIn.getClient(this,gso).signOut()
                                        }
                                    } else {
                                        val email = account?.email
                                        val name = account?.displayName
                                        val intent = Intent(this, SignupActivity::class.java)
                                        intent.putExtra("name",name)
                                        intent.putExtra("email",email)
                                        intent.putExtra("uid",user.uid)
                                        startActivity(intent)
                                        finish()
                                        Toast.makeText(this, "Please Sign up First", Toast.LENGTH_SHORT).show()

                                    }
                                }
                                .addOnFailureListener { queryError ->
                                    Toast.makeText(
                                        this,
                                        "Failed to check user type: $queryError",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            Toast.makeText(this, "User is null", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Failed to sign in", Toast.LENGTH_LONG).show()
                        Log.e("TAG1111", "Failed to sign in: " + task.exception)
                    }
                }
            }
        } else {
            Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_LONG).show()
            Log.e("TAG1111", "Google Sign-In failed")
        }
    }

}