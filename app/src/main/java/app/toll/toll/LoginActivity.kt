package app.toll.toll

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.toll.toll.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        window?.apply {
            statusBarColor = android.graphics.Color.WHITE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        auth = FirebaseAuth.getInstance()

        val emailInput = loginBinding.loginEmailInput;
        val passwordInput = loginBinding.loginPasswordInput;
        val loginButton = loginBinding.loginButton;
        val registerLink = loginBinding.registerLink;

        // Login button click
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        loginBinding.imageView31.setOnClickListener {
            finish();
        }

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        // Optional: Fetch user data from Realtime Database
                        val database = FirebaseDatabase.getInstance().getReference("users");
                        database.child(uid).get().addOnSuccessListener { snapshot ->
                            if (snapshot.exists()) {
                                val user = snapshot.getValue(User::class.java);
                                startActivity(Intent(this, NavigationActivity::class.java));
                                finish();
                            } else {
                                //Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener {
                            //Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    //Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}