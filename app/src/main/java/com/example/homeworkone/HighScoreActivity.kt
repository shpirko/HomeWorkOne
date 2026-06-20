package com.example.homeworkone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.homeworkone.models.ScoreList
import com.example.homeworkone.models.ScoreRecord
import com.example.homeworkone.utilities.Constants
import com.example.homeworkone.utilities.SharedPreferencesManagerV3
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import java.util.Locale

import android.text.Editable
import android.text.TextWatcher

class HighScoreActivity : AppCompatActivity() {

    private lateinit var highScore_LBL_score: MaterialTextView
    private lateinit var highScore_ET_name: TextInputEditText
    private lateinit var highScore_ET_location: TextInputEditText
    private lateinit var highScore_BTN_submit: MaterialButton
    private lateinit var highScore_LBL_location_status: MaterialTextView

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var score: Int = 0
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            getLocation()
        } else {
            highScore_LBL_location_status.text = "Location permission denied."
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_high_score)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.highScore_LAY_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        score = intent.getIntExtra(Constants.BundleKeys.SCORE_KEY, 0)

        findViews()
        initViews()
        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            getLocation()
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            // Try to get last location first
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                    highScore_LBL_location_status.text = "Location found!"
                    highScore_BTN_submit.isEnabled = true
                } else {
                    // If last location is null, request current location
                    val cts = CancellationTokenSource()
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                        .addOnSuccessListener { currLocation ->
                            if (currLocation != null) {
                                lat = currLocation.latitude
                                lon = currLocation.longitude
                                highScore_LBL_location_status.text = "Location found!"
                                highScore_BTN_submit.isEnabled = true
                            } else {
                                highScore_LBL_location_status.text = "Could not find location."
                                highScore_BTN_submit.isEnabled = true // Allow anyway
                            }
                        }
                }
            }
        }
    }

    private fun findViews() {
        highScore_LBL_score = findViewById(R.id.highScore_LBL_score)
        highScore_ET_name = findViewById(R.id.highScore_ET_name)
        highScore_ET_location = findViewById(R.id.highScore_ET_location)
        highScore_BTN_submit = findViewById(R.id.highScore_BTN_submit)
        highScore_LBL_location_status = findViewById(R.id.highScore_LBL_location_status)
    }

    private fun initViews() {
        highScore_LBL_score.text = "Your Score: $score"
        highScore_BTN_submit.isEnabled = false // Disable until location is found

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (highScore_ET_name.text.toString().isNotEmpty()) {
                    highScore_BTN_submit.isEnabled = true
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        highScore_ET_name.addTextChangedListener(textWatcher)
        highScore_ET_location.addTextChangedListener(textWatcher)

        highScore_BTN_submit.setOnClickListener {
            val name = highScore_ET_name.text.toString()
            val customLocation = highScore_ET_location.text.toString()

            if (name.isNotEmpty()) {
                if (customLocation.isNotEmpty()) {
                    // Use Geocoder for custom location
                    val geocoder = Geocoder(this, Locale.getDefault())
                    try {
                        val addresses = geocoder.getFromLocationName(customLocation, 1)
                        if (!addresses.isNullOrEmpty()) {
                            lat = addresses[0].latitude
                            lon = addresses[0].longitude
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                saveScore(name, score)
                goToGameOver()
            } else {
                highScore_ET_name.error = "Please enter your name"
            }
        }
    }

    private fun saveScore(name: String, score: Int) {
        val sp = SharedPreferencesManagerV3.getInstance()
        val gson = Gson()
        
        // Load existing scores
        val json = sp.getString(Constants.SP_KEYS.PLAYLIST_KEY, "")
        val scoreList = if (json.isEmpty()) {
            ScoreList()
        } else {
            gson.fromJson(json, ScoreList::class.java)
        }
        
        // Add new score with real location
        scoreList.scores.add(ScoreRecord(name, score, lat, lon))
        
        // Save back
        val newJson = gson.toJson(scoreList)
        sp.putString(Constants.SP_KEYS.PLAYLIST_KEY, newJson)
    }

    private fun goToGameOver() {
        val intent = Intent(this, ScoreActivity::class.java).apply {
            putExtra(Constants.BundleKeys.SCORE_KEY, score)
            putExtra(Constants.BundleKeys.MESSAGE_KEY, "Score Saved!")
            // Carry over other data if needed
            putExtra(Constants.BundleKeys.CONTROL_MODE_KEY, getIntent().getStringExtra(Constants.BundleKeys.CONTROL_MODE_KEY))
            putExtra(Constants.BundleKeys.DELAY_KEY, getIntent().getLongExtra(Constants.BundleKeys.DELAY_KEY, 1000L))
        }
        startActivity(intent)
        finish()
    }
}