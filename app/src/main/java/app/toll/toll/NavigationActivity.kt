package app.toll.toll

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import app.toll.toll.databinding.ActivityNavigationBinding
import app.toll.toll.model.Contact

class NavigationActivity : AppCompatActivity() {
    lateinit var navigationBinding: ActivityNavigationBinding;
    lateinit var bottomNavigationView: BottomNavigationView;

    companion object addresses {
        var destinations: MutableList<Place> = mutableListOf<Place>();
        var rides: MutableList<Order> = mutableListOf<Order>();
        var messages: MutableList<Contact> = mutableListOf<Contact>();
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        navigationBinding = ActivityNavigationBinding.inflate(layoutInflater);
        setContentView(navigationBinding.root);

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

        navigationBinding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.search -> {
                    changeFragment(MapsFragment());
                    true;
                }
                R.id.favourites -> {
                    changeFragment(FavouritesFragment());
                    true;
                }
                R.id.rides -> {
                    changeFragment(RidesFragment());
                    true;
                }
                R.id.inbox -> {
                    changeFragment(InboxFragment());
                    true;
                }
                R.id.profile -> {
                    changeFragment(ProfileFragment());
                    true;
                }
                else -> false
            }
        }
    }

    private fun changeFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
        fragmentTransaction.commit();
    }

    fun onClickDrawerNavigationOpen(view: View) {

    }

    fun onClickSpinner(view: View) {}

}