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
import app.toll.toll.databinding.FragmentRidesBinding


class RidesFragment : Fragment() {
    lateinit var ridesBinding: FragmentRidesBinding;

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
        ridesBinding = FragmentRidesBinding.inflate(inflater, container, false)
        return ridesBinding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewManager = LinearLayoutManager(requireActivity())
        viewAdapter = RidesRecyclerAdapter(NavigationActivity.addresses.rides)
        ridesBinding.ridesRecyclerView.apply {
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

        ridesBinding.button4.setOnClickListener {
            val order = Order(null, null, null,
                Location(48.03938, -81.8388377, "1001 Fanshawe Boulevard, London, ON, Canada"),
                Location(43.199282, -81.7027367, "393 Centrepiece Rd., Mississauga, ON, Canada"),
                "requested", 9.17,
                "$", null, null, null, null, null,
                null, null, null, null, "Fri June 24, 2024 8:24 PM");
            NavigationActivity.rides.add(order);
            viewAdapter.notifyDataSetChanged();
            reloadFragment();
        }

        val order = Order(null, null, null,
            Location(48.03938, -81.8388377, "1001 Fanshawe Boulevard, London, ON, Canada"),
            Location(43.199282, -81.7027367, "1001 Fanshawe Park Rd. W., London, ON, Canada"),
            "requested", 9.17,
            "$", null, null, null, null, null,
            null, null, null, null, "Fri June 24, 2024 8:24 PM");
        NavigationActivity.rides.add(order);
        val order2 = Order(null, null, null,
            Location(48.03938, -81.8388377, "1001 Fanshawe Boulevard, London, ON, Canada"),
            Location(43.199282, -81.7027367, "393 Centrepiece Rd., Mississauga, ON, Canada"),
            "requested", 9.17,
            "$", null, null, null, null, null,
            null, null, null, null, "Fri June 24, 2024 8:24 PM");
        NavigationActivity.rides.add(order2);
        viewAdapter.notifyDataSetChanged()
        reloadFragment();
    }

    fun reloadFragment() {
        if (NavigationActivity.addresses.rides.isEmpty()) {
            ridesBinding.ridesFilledView.visibility = View.GONE;
            ridesBinding.ridesEmptyView.visibility = View.VISIBLE;
        }
        else {
            ridesBinding.ridesFilledView.visibility = View.VISIBLE;
            ridesBinding.ridesEmptyView.visibility = View.GONE;
        }
    }
}