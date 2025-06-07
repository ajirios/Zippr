package app.toll.toll

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import app.toll.toll.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


class MainActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMainBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen
        val splashScreen = installSplashScreen();

        // Optional: Keep it longer (e.g., for loading)
        splashScreen.setKeepOnScreenCondition {
            false // or use a condition like `isLoading`
        }

        super.onCreate(savedInstanceState);


        // Example condition: check if user is logged in
        val isLoggedIn = false;

        if (isLoggedIn) {
            // Launch DashboardActivity
            val intent = Intent(this, NavigationActivity::class.java);
            startActivity(intent);
            finish();
        } else {

            mainBinding = ActivityMainBinding.inflate(layoutInflater);
            setContentView(mainBinding.root);


            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.apply {
                    // Makes the status bar completely transparent
                    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    statusBarColor = Color.TRANSPARENT
                }
            }


            FirebaseApp.initializeApp(this);
            val score = readFromFirebaseRealTimeDatabase();
            mainBinding.scoreTextView.text = "${score}";
            //writeToFirebaseRealTimeDatabase();

            val dbRef = FirebaseDatabase.getInstance().getReference("drivers");

            dbRef.addChildEventListener(object : ChildEventListener {
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    // Check if the changed node contains the "score" field
                    if (snapshot.hasChild("dashboard/location/latitude")) {
                        val updatedScore = snapshot.child("dashboard/location/latitude").getValue(Double::class.java);
                        val updatedLongitude = snapshot.child("dashboard/location/longitude").getValue(Double::class.java);
                        val userId = snapshot.key;
                        if (userId == "Z9071234597548301UMG") {
                            var displayable = "";
                            updatedScore?.let {
                                displayable += "$it";
                                Log.d("RTDB", "Score updated: $it");
                            }
                            updatedLongitude?.let {
                                displayable += ", $it";
                            }
                            //mainBinding.scoreTextView.text = displayable;
                        }
                    }
                }

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.hasChild("dashboard/location/latitude")) {
                        val updatedScore = snapshot.child("dashboard/location/latitude").getValue(Double::class.java);
                        val updatedLongitude = snapshot.child("dashboard/location/longitude").getValue(Double::class.java);
                        val userId = snapshot.key;
                        if (userId == "Z9071234597548301UMG") {
                            var displayable = "";
                            updatedScore?.let {
                                displayable += "$it";
                                Log.d("RTDB", "Score updated: $it");
                            }
                            updatedLongitude?.let {
                                displayable += ", $it";
                            }
                            //mainBinding.scoreTextView.text = displayable;
                        }
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {
                    Log.e("RTDB", "Listener cancelled", error.toException())
                }
            });
        }
    }

    fun writeToFirebaseRealTimeDatabase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        val user = mapOf("name" to "Alice", "score" to 90);

        dbRef.child("Z977082734123").setValue(user)
            .addOnSuccessListener { Log.d("RTDB", "User saved") }
            .addOnFailureListener { e -> Log.e("RTDB", "Error", e) }
    }

    fun readFromFirebaseRealTimeDatabase() : Int {
        var score: Int? = 0;
        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        dbRef.child("alice123").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java)
                score = snapshot.child("score").getValue(Int::class.java)
                Log.d("RTDB", "Name: $name, Score: $score");
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RTDB", "Error", error.toException())
            }
        });
        return score as Int;
    }


    fun onClickGetStarted(view: View) {
        val intent = Intent(this, LocationActivity::class.java);
        startActivity(intent);
    }
}