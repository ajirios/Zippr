package app.toll.toll

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import app.toll.toll.databinding.ActivityRegisterBinding
import app.toll.toll.model.Favourites
import app.toll.toll.model.Home
import app.toll.toll.model.Inbox
import app.toll.toll.model.Orders
import app.toll.toll.model.Profile
import app.toll.toll.viewmodel.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    lateinit var registerBinding: ActivityRegisterBinding;

    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater);
        setContentView(registerBinding.root);


        window?.apply {
            // Set status bar background to white
            statusBarColor = android.graphics.Color.WHITE

            // Set light status bar icons (dark text/icons)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        auth = FirebaseAuth.getInstance()

        val emailInput = registerBinding.emailInput
        val passwordInput = registerBinding.passwordInput;
        val firstNameInput = registerBinding.firstNameInput;
        val middleNameInput = registerBinding.middleNameInput;
        val lastNameInput = registerBinding.lastNameInput;
        val registerButton = registerBinding.registerButton;

        registerButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val firstName = firstNameInput.text.toString().trim();
            val middleName = middleNameInput.text.toString().trim();
            val lastName = lastNameInput.text.toString().trim();

            if (email.isEmpty() || password.isEmpty() || firstName.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(firstName, middleName, lastName, email, password);

            val viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

            viewModel.isLoading.observe(this) { loading ->
                //if (loading) showLoadingSpinner() else hideLoadingSpinner()
            }

            viewModel.error.observe(this) { error ->
                error?.let {
                    //showErrorDialog(it)
                }
            }

            viewModel.registrationSuccess.observe(this) { success ->
                //if (success) navigateToHomeScreen()
            }

            val newUser = User(
                profile = Profile(
                    firstName = firstName,
                    middleName = middleName,
                    lastName = lastName,
                    email = email
                )
            )
            viewModel.registerUser(newUser)

        }
    }

    private fun registerUser(firstName: String, middleName: String, lastName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    // Create user object
                    val user = User(
                        Home(),
                        Favourites(),
                        Orders(),
                        Inbox(),
                        Profile(uid, firstName, middleName, lastName,
                            "$firstName $lastName", email, "America/Toronto", null, null, 0.00, null)
                    );

                    // Save user to Realtime Database
                    val database = FirebaseDatabase.getInstance().getReference("users");
                    database.child(uid).setValue(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, NavigationActivity::class.java))
                            finish();
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}