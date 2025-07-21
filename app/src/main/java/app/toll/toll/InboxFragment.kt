package app.toll.toll

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.toll.toll.databinding.FragmentInboxBinding
import app.toll.toll.databinding.FragmentRidesBinding
import app.toll.toll.model.Contact


class InboxFragment : Fragment() {
    lateinit var inboxBinding: FragmentInboxBinding;

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
        inboxBinding = FragmentInboxBinding.inflate(inflater, container, false)
        return inboxBinding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewManager = LinearLayoutManager(requireActivity())
        viewAdapter = InboxRecyclerAdapter(NavigationActivity.addresses.messages)
        inboxBinding.inboxRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        reloadFragment();

        requireActivity().window?.apply {
            statusBarColor = android.graphics.Color.WHITE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        inboxBinding.button4.setOnClickListener {
            //val newPlace = Place("303 Adelaide N. St.", "Toronto, ON, Canada", 43.809284, -81.2093883, "")
            //NavigationActivity.addresses.destinations.add(newPlace)
            NavigationActivity.addresses.messages.add(Contact("Jane Chipati", null, "Hey, are you coming today?", "4:46 PM"));
            NavigationActivity.addresses.messages.add(Contact("Justin Areo", null, "Hey, are you coming today?", "4:46 PM"));
            NavigationActivity.addresses.messages.add(Contact("Buddy K.", null, "Hey, are you coming today?", "4:46 PM"));
            NavigationActivity.addresses.messages.add(Contact("Chef Curry", null, "Hey, are you coming today?", "4:46 PM"));
            NavigationActivity.addresses.messages.add(Contact("Tana Wilde", null, "Hey, are you coming today?", "4:46 PM"));
            viewAdapter.notifyDataSetChanged();
            reloadFragment();
        }
    }

    fun reloadFragment() {
        if (NavigationActivity.addresses.messages.isEmpty()) {
            inboxBinding.inboxFilledView.visibility = View.GONE;
            inboxBinding.inboxEmptyView.visibility = View.VISIBLE;
        }
        else {
            inboxBinding.inboxFilledView.visibility = View.VISIBLE;
            inboxBinding.inboxEmptyView.visibility = View.GONE;
        }
    }
}