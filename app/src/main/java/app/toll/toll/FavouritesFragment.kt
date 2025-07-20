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
    ): View {
        favouritesBinding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return favouritesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewManager = LinearLayoutManager(requireActivity())
        viewAdapter = FavouritesRecyclerAdapter(NavigationActivity.addresses.destinations)
        favouritesBinding.favouritesRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        reloadFragment()

        requireActivity().window?.apply {
            statusBarColor = android.graphics.Color.WHITE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        favouritesBinding.button4.setOnClickListener {
            val newPlace = Place("303 Adelaide N. St.", "Toronto, ON, Canada", 43.809284, -81.2093883, "")
            NavigationActivity.addresses.destinations.add(newPlace)
            viewAdapter.notifyDataSetChanged()
            reloadFragment()
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