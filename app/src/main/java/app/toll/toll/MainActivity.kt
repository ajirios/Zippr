package com.zippr.zippr

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.zippr.zippr.R
import com.zippr.zippr.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    companion object Content {
        var countries: List<Country> = mutableListOf(
            Country("Afghanistan", "AF", "+93", "afghanistan", "Naira", "NGN", "English"),
            Country("Albania", "NG", "+355", "albania", "Naira", "NGN", "English"),
            Country("Algeria", "NG", "+213", "algeria", "Naira", "NGN", "English"),
            Country("American Samoa", "NG", "+1684", "nigeria", "Naira", "NGN", "English"),
            Country("Andorra", "NG", "+376", "nigeria", "Naira", "NGN", "English"),
            Country("Angola", "NG", "+244", "angola", "Naira", "NGN", "English"),
            Country("Anguilla", "NG", "+1264", "nigeria", "Naira", "NGN", "English"),
            Country("Antarctica", "NG", "+672", "nigeria", "Naira", "NGN", "English"),
            Country("Antigua and Barbuda", "NG", "+1268", "nigeria", "Naira", "NGN", "English"),
            Country("Argentina", "AR", "+54", "argentina", "Pesos", "NGN", "English"),
            Country("Armenia", "NG", "+374", "armenia", "Naira", "NGN", "English"),
            Country("Aruba", "NG", "+297", "nigeria", "Naira", "NGN", "English"),
            Country("Australia", "AU", "+61", "australia", "Dollar", "AUD", "English"),
            Country("Austria", "AU", "+43", "austria", "Dollar", "AUD", "English"),
            Country("Azerbaijan", "AZ", "+994", "azerbaijan", "Dollar", "AUD", "English"),
            Country("Bahamas", "BH", "+1242", "bahamas", "Dollar", "BHD", "English"),
            Country("Bahrain", "BH", "+973", "bahrain", "Dollar", "BHD", "English"),
            Country("Bangladesh", "BH", "+880", "bangladesh", "Dollar", "BHD", "English"),
            Country("Barbados", "BH", "+1246", "barbados", "Dollar", "BHD", "English"),
            Country("Belarus", "BH", "+375", "belarus", "Dollar", "BHD", "English"),
            Country("Belgium", "BH", "+32", "belgium", "Dollar", "BHD", "English"),
            Country("Belize", "BH", "+501", "belize", "Dollar", "BHD", "English"),
            Country("Benin", "BH", "+229", "benin", "Dollar", "BHD", "English"),
            Country("Bermuda", "BH", "+1441", "bahamas", "Dollar", "BHD", "English"),
            Country("Bhutan", "BH", "+975", "bahamas", "Dollar", "BHD", "English"),
            Country("Bolivia", "BH", "+591", "bolivia", "Dollar", "BHD", "English"),
            Country("Bosnia and Herzegovina", "BH", "+387", "bosnia_herzegovina", "Dollar", "BHD", "English"),
            Country("Botswana", "BH", "+267", "botswana", "Dollar", "BHD", "English"),
            Country("Brazil", "BH", "+55", "brazil", "Dollar", "BHD", "English"),
            Country("British Indian Ocean Territories", "BH", "+246", "bahamas", "Dollar", "BHD", "English"),
            Country("British Virgin Islands", "BH", "+1284", "bahamas", "Dollar", "BHD", "English"),
            Country("Brunei", "BH", "+673", "bahamas", "Dollar", "BHD", "English"),
            Country("Bulgaria", "BH", "+359", "bulgaria", "Dollar", "BHD", "English"),
            Country("Burkina Faso", "BH", "+226", "burkinafaso", "Dollar", "BHD", "English"),
            Country("Burundi", "BH", "+257", "burundi", "Dollar", "BHD", "English"),
            Country("Cambodia", "BH", "+855", "cambodia", "Dollar", "BHD", "English"),
            Country("Cameroon", "BH", "+237", "cameroon", "Dollar", "BHD", "English"),
            Country("Canada", "CA", "+1", "canada", "Dollar", "CAD", "English"),
            Country("Cape Verde", "CA", "+238", "capeverde", "Dollar", "CAD", "English"),
            Country("Cayman Islands", "CA", "+1345", "canada", "Dollar", "CAD", "English"),
            Country("Central African Republic", "CA", "+236", "cafr", "Dollar", "CAD", "English"),
            Country("Chad", "TD", "+235", "chad", "Dollar", "CAD", "English"),
            Country("Chile", "CL", "+56", "chile", "Dollar", "CAD", "English"),
            Country("China", "CN", "+86", "china", "Yen", "CNY", "Chinese"),
            Country("Christmas Island", "CN", "+61", "china", "Yen", "CAD", "English"),
            Country("Cocos Islands", "CN", "+61", "china", "Yen", "CAD", "English"),
            Country("Colombia", "CO", "+57", "colombia", "Yen", "CAD", "English"),
            Country("Comoros", "CN", "+269", "china", "Yen", "CAD", "English"),
            Country("Cook Islands", "CN", "+682", "china", "Yen", "CAD", "English"),
            Country("Costa Rica", "CN", "+506", "costarica", "Yen", "CAD", "English"),
            Country("Croatia", "CN", "+385", "chad", "Yen", "CAD", "English"),
            Country("Cuba", "CN", "+53", "china", "Yen", "CAD", "English"),
            Country("Curacao", "CN", "+599", "china", "Yen", "CAD", "English"),
            Country("Cyprus", "CN", "+357", "cyprus", "Yen", "CAD", "English"),
            Country("Czech Republic", "CN", "+420", "czechia", "Yen", "CAD", "English"),
            Country("Democratic Republic of the Congo", "CN", "+243", "drc", "Yen", "CAD", "English"),
            Country("Denmark", "CN", "+45", "denmark", "Yen", "CAD", "English"),
            Country("Djibouti", "CN", "+253", "denmark", "Yen", "CAD", "English"),
            Country("Dominica", "CN", "+1767", "dominica", "Yen", "CAD", "English"),
            Country("Dominican Republic", "CN", "+1", "dominican_republic", "Yen", "CAD", "English"),
            Country("East Timor", "CN", "+670", "denmark", "Yen", "CAD", "English"),
            Country("Ecuador", "CN", "+593", "ecuador", "Yen", "CAD", "English"),
            Country("Egypt", "CN", "+20", "egypt", "Yen", "CAD", "English"),
            Country("El Salvador", "CN", "+503", "elsalvador", "Yen", "CAD", "English"),
            Country("Equatorial Guinea", "CN", "+240", "equatorial_guinea", "Yen", "CAD", "English"),
            Country("Eritrea", "CN", "+291", "eritrea", "Yen", "CAD", "English"),
            Country("Estonia", "CN", "+372", "estonia", "Yen", "CAD", "English"),
            Country("Ethiopia", "CN", "+251", "ethiopia", "Yen", "CAD", "English"),
            Country("Falkland Islands", "CN", "+500", "ethiopia", "Yen", "CAD", "English"),
            Country("Faroe Islands", "CN", "+298", "ethiopia", "Yen", "CAD", "English"),
            Country("Fiji", "CN", "+679", "fiji", "Yen", "CAD", "English"),
            Country("Finland", "CN", "+358", "finland", "Yen", "CAD", "English"),
            Country("France", "FR", "+33", "france", "Yen", "CAD", "English"),
            Country("French Guiana", "CN", "+594", "finland", "Yen", "CAD", "English"),
            Country("French Polynesia", "CN", "+689", "finland", "Yen", "CAD", "English"),
            Country("Gabon", "CN", "+241", "gabon", "Yen", "CAD", "English"),
            Country("Gambia", "CN", "+220", "gambia", "Yen", "CAD", "English"),
            Country("Georgia", "CN", "+995", "georgia", "Yen", "CAD", "English"),
            Country("Germany", "DK", "+49", "germany", "Yen", "CAD", "English"),
            Country("Ghana", "GH", "+233", "ghana", "Yen", "CAD", "English"),
            Country("Gibraltar", "CN", "+350", "gibraltar", "Yen", "CAD", "English"),
            Country("Greece", "DK", "+30", "greece", "Yen", "CAD", "English"),
            Country("Greenland", "DK", "+299", "greenland", "Yen", "CAD", "English"),
            Country("Grenada", "DK", "+1473", "grenada", "Yen", "CAD", "English"),
            Country("Guadeloupe", "DK", "+590", "germany", "Yen", "CAD", "English"),
            Country("Guam", "DK", "+1671", "germany", "Yen", "CAD", "English"),
            Country("Guatemala", "DK", "+502", "germany", "Yen", "CAD", "English"),
            Country("Guernsey", "DK", "+441481", "germany", "Yen", "CAD", "English"),
            Country("Guinea", "DK", "+224", "guinea", "Yen", "CAD", "English"),
            Country("Guinea-Bissau", "DK", "+245", "germany", "Yen", "CAD", "English"),
            Country("Guyana", "DK", "+592", "germany", "Yen", "CAD", "English"),
            Country("Haiti", "DK", "+509", "haiti", "Yen", "CAD", "English"),
            Country("Honduras", "DK", "+504", "honduras", "Yen", "CAD", "English"),
            Country("Hong Kong", "DK", "+852", "germany", "Yen", "CAD", "English"),
            Country("Hungary", "DK", "+36", "hungary", "Yen", "CAD", "English"),
            Country("Iceland", "DK", "+354", "iceland", "Yen", "CAD", "English"),
            Country("India", "DK", "+91", "india", "Yen", "CAD", "English"),
            Country("Indonesia", "DK", "+62", "indonesia", "Yen", "CAD", "English"),
            Country("Iran", "DK", "+98", "iran", "Yen", "CAD", "English"),
            Country("Iraq", "DK", "+964", "iraq", "Yen", "CAD", "English"),
            Country("Ireland", "DK", "+353", "ireland", "Yen", "CAD", "English"),
            Country("Isle of Man", "DK", "+44", "iran", "Yen", "CAD", "English"),
            Country("Israel", "DK", "+972", "israel", "Yen", "CAD", "English"),
            Country("Italy", "DK", "+39", "italy", "Yen", "CAD", "English"),
            Country("Ivory Coast", "DK", "+225", "ivorycoast", "Yen", "CAD", "English"),
            Country("Jamaica", "DK", "+1876", "jamaica", "Yen", "CAD", "English"),
            Country("Japan", "DK", "+81", "japan", "Yen", "CAD", "English"),
            Country("Jersey", "DK", "+441534", "japan", "Yen", "CAD", "English"),
            Country("Jordan", "DK", "+962", "jordan", "Yen", "CAD", "English"),
            Country("Kazakhstan", "DK", "+7", "kazakhstan", "Yen", "CAD", "English"),
            Country("Kenya", "DK", "+254", "kenya", "Yen", "CAD", "English"),
            Country("Kiribati", "DK", "+686", "kenya", "Yen", "CAD", "English"),
            Country("Kosovo", "DK", "+383", "kenya", "Yen", "CAD", "English"),
            Country("Kuwait", "DK", "+965", "kuwait", "Yen", "CAD", "English"),
            Country("Kyrgyzstan", "DK", "+996", "kyrgyzstan", "Yen", "CAD", "English"),
            Country("Laos", "DK", "+856", "laos", "Yen", "CAD", "English"),
            Country("Latvia", "DK", "+371", "latvia", "Yen", "CAD", "English"),
            Country("Lebanon", "DK", "+961", "lebanon", "Yen", "CAD", "English"),
            Country("Lesotho", "DK", "+266", "laos", "Yen", "CAD", "English"),
            Country("Liberia", "DK", "+231", "liberia", "Yen", "CAD", "English"),
            Country("Libya", "DK", "+218", "libya", "Yen", "CAD", "English"),
            Country("Liechtenstein", "DK", "+423", "liechtenstein", "Yen", "CAD", "English"),
            Country("Lithuania", "DK", "+370", "lithuania", "Yen", "CAD", "English"),
            Country("Luxembourg", "DK", "+352", "luxembourg", "Yen", "CAD", "English"),
            Country("Macau", "DK", "+853", "laos", "Yen", "CAD", "English"),
            Country("Macedonia", "DK", "+389", "laos", "Yen", "CAD", "English"),
            Country("Madagascar", "DK", "+261", "madagascar", "Yen", "CAD", "English"),
            Country("Malawi", "DK", "+265", "malawi", "Yen", "CAD", "English"),
            Country("Malaysia", "DK", "+60", "malaysia", "Yen", "CAD", "English"),
            Country("Maldives", "DK", "+960", "laos", "Yen", "CAD", "English"),
            Country("Mali", "DK", "+223", "mali", "Yen", "CAD", "English"),
            Country("Malta", "DK", "+356", "malta", "Yen", "CAD", "English"),
            Country("Marshall Islands", "DK", "+692", "laos", "Yen", "CAD", "English"),
            Country("Martinique", "DK", "+596", "laos", "Yen", "CAD", "English"),
            Country("Mauritania", "DK", "+222", "laos", "Yen", "CAD", "English"),
            Country("Mauritius", "DK", "+230", "laos", "Yen", "CAD", "English"),
            Country("Mayotte", "DK", "+262", "laos", "Yen", "CAD", "English"),
            Country("Mexico", "DK", "+52", "mexico", "Yen", "CAD", "English"),
            Country("Micronesia", "DK", "+691", "laos", "Yen", "CAD", "English"),
            Country("Moldova", "DK", "+373", "moldova", "Yen", "CAD", "English"),
            Country("Monaco", "DK", "+377", "monaco", "Yen", "CAD", "English"),
            Country("Mongolia", "DK", "+976", "mongolia", "Yen", "CAD", "English"),
            Country("Montenegro", "DK", "+382", "laos", "Yen", "CAD", "English"),
            Country("Montserrat", "DK", "+1664", "laos", "Yen", "CAD", "English"),
            Country("Morocco", "DK", "+212", "morocco", "Yen", "CAD", "English"),
            Country("Mozambique", "DK", "+258", "mozambique", "Yen", "CAD", "English"),
            Country("Myanmar", "DK", "+95", "myanmar", "Yen", "CAD", "English"),
            Country("Namibia", "DK", "+264", "namibia", "Yen", "CAD", "English"),
            Country("Nauru", "DK", "+674", "laos", "Yen", "CAD", "English"),
            Country("Nepal", "DK", "+977", "nepal", "Yen", "CAD", "English"),
            Country("Netherlands", "DK", "+31", "netherlands", "Yen", "CAD", "English"),
            Country("Netherlands Antilles", "DK", "+599", "netherlands", "Yen", "CAD", "English"),
            Country("New Caledonia", "DK", "+687", "nepal", "Yen", "CAD", "English"),
            Country("New Zealand", "DK", "+64", "newzealand", "Yen", "CAD", "English"),
            Country("Nicaragua", "DK", "+505", "nicaragua", "Yen", "CAD", "English"),
            Country("Niger", "DK", "+227", "niger", "Yen", "CAD", "English"),
            Country("Nigeria", "NG", "+234", "nigeria2", "Naira", "NGN", "English"),
            Country("Niue", "DK", "+683", "nepal", "Yen", "CAD", "English"),
            Country("North Korea", "DK", "+850", "nepal", "Yen", "CAD", "English"),
            Country("Northern Mariana Islands", "DK", "+1670", "nepal", "Yen", "CAD", "English"),
            Country("Norway", "DK", "+47", "norway", "Yen", "CAD", "English"),
            Country("Oman", "DK", "+968", "oman", "Yen", "CAD", "English"),
            Country("Pakistan", "DK", "+92", "pakistan", "Yen", "CAD", "English"),
            Country("Palau", "DK", "+680", "pakistan", "Yen", "CAD", "English"),
            Country("Palestine", "DK", "+970", "pakistan", "Yen", "CAD", "English"),
            Country("Panama", "DK", "+507", "panama", "Yen", "CAD", "English"),
            Country("Papua New Guinea", "DK", "+675", "papua_new_guinea", "Yen", "CAD", "English"),
            Country("Paraguay", "DK", "+595", "paraguay", "Yen", "CAD", "English"),
            Country("Peru", "DK", "+51", "peru", "Yen", "CAD", "English"),
            Country("Philippines", "DK", "+63", "philippines", "Yen", "CAD", "English"),
            Country("Pitcairn", "DK", "+64", "pakistan", "Yen", "CAD", "English"),
            Country("Poland", "DK", "+48", "poland", "Yen", "CAD", "English"),
            Country("Portugal", "DK", "+351", "portugal", "Yen", "CAD", "English"),
            Country("Puerto Rico", "DK", "+1", "puerto_rico", "Yen", "CAD", "English"),
            Country("Qatar", "DK", "+974", "qatar", "Yen", "CAD", "English"),
            Country("Republic of the Congo", "DK", "+242", "congo", "Yen", "CAD", "English"),
            Country("Reunion", "DK", "+262", "pakistan", "Yen", "CAD", "English"),
            Country("Romania", "DK", "+40", "romania", "Yen", "CAD", "English"),
            Country("Russia", "DK", "+7", "russia", "Yen", "CAD", "English"),
            Country("Rwanda", "DK", "+250", "rwanda", "Yen", "CAD", "English"),
            Country("Saint Barthelemy", "DK", "+590", "romania", "Yen", "CAD", "English"),
            Country("Saint Helena", "DK", "+290", "romania", "Yen", "CAD", "English"),
            Country("Saint Kitts and Nevis", "DK", "+1869", "romania", "Yen", "CAD", "English"),
            Country("Saint Lucia", "DK", "+1758", "romania", "Yen", "CAD", "English"),
            Country("Saint Martin", "DK", "+590", "romania", "Yen", "CAD", "English"),
            Country("Saint Pierre and Miquelon", "DK", "+508", "romania", "Yen", "CAD", "English"),
            Country("Saint Vincent and the Grenadines", "DK", "+1784", "romania", "Yen", "CAD", "English"),
            Country("Samoa", "DK", "+685", "romania", "Yen", "CAD", "English"),
            Country("San Marino", "DK", "+378", "romania", "Yen", "CAD", "English"),
            Country("Sao Tome and Principe", "DK", "+239", "romania", "Yen", "CAD", "English"),
            Country("Saudi Arabia", "DK", "+966", "saudi_arabia", "Yen", "CAD", "English"),
            Country("Senegal", "DK", "+221", "senegal", "Yen", "CAD", "English"),
            Country("Serbia", "DK", "+381", "serbia", "Yen", "CAD", "English"),
            Country("Seychelles", "DK", "+248", "seychelles", "Yen", "CAD", "English"),
            Country("Sierra Leone", "DK", "+232", "sierraleone", "Yen", "CAD", "English"),
            Country("Singapore", "DK", "+65", "singapore", "Yen", "CAD", "English"),
            Country("Sint Maarten", "DK", "+1721", "romania", "Yen", "CAD", "English"),
            Country("Slovakia", "DK", "+421", "slovakia", "Yen", "CAD", "English"),
            Country("Slovenia", "DK", "+386", "slovenia", "Yen", "CAD", "English"),
            Country("Solomon Islands", "DK", "+677", "romania", "Yen", "CAD", "English"),
            Country("Somalia", "DK", "+252", "somalia", "Yen", "CAD", "English"),
            Country("South Africa", "DK", "+27", "southafrica", "Yen", "CAD", "English"),
            Country("South Korea", "DK", "+82", "southkorea", "Yen", "CAD", "English"),
            Country("South Sudan", "DK", "+211", "romania", "Yen", "CAD", "English"),
            Country("Spain", "DK", "+34", "spain", "Yen", "CAD", "English"),
            Country("Sri Lanka", "DK", "+94", "srilanka", "Yen", "CAD", "English"),
            Country("Sudan", "DK", "+249", "sudan", "Yen", "CAD", "English"),
            Country("Suriname", "DK", "+597", "suriname", "Yen", "CAD", "English"),
            Country("Svalbard and Jan Mayen", "DK", "+47", "romania", "Yen", "CAD", "English"),
            Country("Swaziland", "DK", "+268", "romania", "Yen", "CAD", "English"),
            Country("Sweden", "DK", "+46", "sweden", "Yen", "CAD", "English"),
            Country("Switzerland", "DK", "+41", "switzerland", "Yen", "CAD", "English"),
            Country("Syria", "DK", "+963", "romania", "Yen", "CAD", "English"),
            Country("Taiwan", "DK", "+886", "romania", "Yen", "CAD", "English"),
            Country("Tajikistan", "DK", "+992", "tajikistan", "Yen", "CAD", "English"),
            Country("Tanzania", "DK", "+255", "tanzania", "Yen", "CAD", "English"),
            Country("Thailand", "DK", "+66", "thailand", "Yen", "CAD", "English"),
            Country("Togo", "DK", "+228", "togo", "Yen", "CAD", "English"),
            Country("Tokelau", "DK", "+690", "romania", "Yen", "CAD", "English"),
            Country("Tonga", "DK", "+676", "romania", "Yen", "CAD", "English"),
            Country("Trinidad and Tobago", "DK", "+1868", "trinidad_and_tobago", "Yen", "CAD", "English"),
            Country("Tunisia", "DK", "+216", "tunisia", "Yen", "CAD", "English"),
            Country("Turkey", "DK", "+90", "turkey", "Yen", "CAD", "English"),
            Country("Turkmenistan", "DK", "+993", "turkmenistan", "Yen", "CAD", "English"),
            Country("Turks and Caicos Islands", "DK", "+1649", "romania", "Yen", "CAD", "English"),
            Country("Tuvalu", "DK", "+688", "romania", "Yen", "CAD", "English"),
            Country("U.S. Virgin Islands", "DK", "+1340", "romania", "Yen", "CAD", "English"),
            Country("Uganda", "DK", "+256", "uganda", "Yen", "CAD", "English"),
            Country("Ukraine", "DK", "+380", "ukraine", "Yen", "CAD", "English"),
            Country("United Arab Emirates", "DK", "+971", "uae", "Yen", "CAD", "English"),
            Country("United Kingdom", "UK", "+44", "united_kingdom", "Pound", "GBP", "English"),
            Country("United States", "US", "+1", "united_states", "Dollar", "USD", "English"),
            Country("Uruguay", "DK", "+598", "uruguay", "Yen", "CAD", "English"),
            Country("Uzbekistan", "DK", "+998", "uzbekistan", "Yen", "CAD", "English"),
            Country("Vanuatu", "DK", "+678", "romania", "Yen", "CAD", "English"),
            Country("Vatican", "DK", "+379", "vatican_city", "Yen", "CAD", "English"),
            Country("Venezuela", "DK", "+58", "venezuela", "Yen", "CAD", "English"),
            Country("Vietnam", "DK", "+84", "vietnam", "Yen", "CAD", "English"),
            Country("Wallis and Futuna", "DK", "+681", "romania", "Yen", "CAD", "English"),
            Country("Western Sahara", "DK", "+212", "romania", "Yen", "CAD", "English"),
            Country("Yemen", "DK", "+967", "yemen", "Yen", "CAD", "English"),
            Country("Zambia", "DK", "+260", "zambia", "Yen", "CAD", "English"),
            Country("Zimbabwe", "DK", "+263", "zimbabwe", "Yen", "CAD", "English")
        );
    }

    lateinit var mainBinding: ActivityMainBinding;

    private lateinit var recyclerView: RecyclerView;
    private lateinit var viewAdapter: CountryRecyclerAdapter;
    private lateinit var viewManager: RecyclerView.LayoutManager;

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
        val currentUser = FirebaseAuth.getInstance().currentUser;

        if (currentUser != null) {
            // Launch DashboardActivity
            val intent = Intent(this, NavigationActivity::class.java);
            startActivity(intent);
            finish();
        } else {

            mainBinding = ActivityMainBinding.inflate(layoutInflater);
            setContentView(mainBinding.root);

            window?.apply {
                // Set status bar background to white
                statusBarColor = android.graphics.Color.WHITE

                // Set light status bar icons (dark text/icons)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }

            /*
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
            }*/

            mainBinding.countrySearchEditText.requestFocus();

            viewManager = LinearLayoutManager(this);
            viewAdapter = CountryRecyclerAdapter(Content.countries, this);
            recyclerView = findViewById<RecyclerView>(R.id.countryRecyclerView).apply {
                setHasFixedSize(true);
                layoutManager = viewManager;
                adapter = viewAdapter;
            }


            FirebaseApp.initializeApp(this);
            val score = readFromFirebaseRealTimeDatabase();
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