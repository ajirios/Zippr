package app.toll.toll

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.toll.toll.databinding.FragmentFavouritesBinding


class FavouritesFragment : Fragment() {
    lateinit var favouritesBinding: FragmentFavouritesBinding;

    private lateinit var recyclerView: RecyclerView;
    private lateinit var viewAdapter: RecyclerView.Adapter<*>;
    private lateinit var viewManager: RecyclerView.LayoutManager;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        viewManager = LinearLayoutManager(requireActivity());
        viewAdapter = FavouritesRecyclerAdapter(NavigationActivity.addresses.destinations);
        recyclerView = view.findViewById<RecyclerView>(R.id.favouritesRecyclerView).apply {
            setHasFixedSize(true);
            layoutManager = viewManager;
            adapter = viewAdapter;
        }

        reloadFragment();

        // Make status bar white with dark content (for Android 6.0+)
        requireActivity().window?.apply {
            // Set status bar background to white
            statusBarColor = android.graphics.Color.WHITE

            // Set light status bar icons (dark text/icons)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        favouritesBinding.button4.setOnClickListener {
            val newPlace = Place("303 Adelaide N. St.", "Toronto, ON, Canada", 43.809284, -81.2093883, "");
            NavigationActivity.addresses.destinations.add(newPlace);
            viewAdapter.notifyDataSetChanged();
            reloadFragment();
        }
    }

    fun reloadFragment() {
        if (NavigationActivity.addresses.destinations.isEmpty()) {
            favouritesBinding.favouritesFilledView.visibility = View.GONE;
            favouritesBinding.favouritesEmptyView.visibility = View.VISIBLE;
        }
        else {
            favouritesBinding.favouritesFilledView.visibility = View.VISIBLE;
            favouritesBinding.favouritesEmptyView.visibility = View.GONE;
        }
    }
}