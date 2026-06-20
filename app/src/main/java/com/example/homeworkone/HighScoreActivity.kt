package com.example.homeworkone

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.homeworkone.models.ScoreList
import com.example.homeworkone.models.ScoreRecord
import com.example.homeworkone.utilities.Constants
import com.example.homeworkone.utilities.SharedPreferencesManagerV3
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson

class HighScoreActivity : AppCompatActivity() {

    private lateinit var highScore_LBL_score: MaterialTextView
    private lateinit var highScore_ET_name: TextInputEditText
    private lateinit var highScore_BTN_submit: MaterialButton

    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_high_score)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.highScore_LAY_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        score = intent.getIntExtra(Constants.BundleKeys.SCORE_KEY, 0)

        findViews()
        initViews()
    }

    private fun findViews() {
        highScore_LBL_score = findViewById(R.id.highScore_LBL_score)
        highScore_ET_name = findViewById(R.id.highScore_ET_name)
        highScore_BTN_submit = findViewById(R.id.highScore_BTN_submit)
    }

    private fun initViews() {
        highScore_LBL_score.text = "Your Score: $score"

        highScore_BTN_submit.setOnClickListener {
            val name = highScore_ET_name.text.toString()
            if (name.isNotEmpty()) {
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
        
        // Define Disney World locations
        val disneyLocations = listOf(
            Pair(28.4177, -81.5812), // Magic Kingdom
            Pair(28.3747, -81.5494), // Epcot
            Pair(28.3575, -81.5583), // Hollywood Studios
            Pair(28.3529, -81.5907)  // Animal Kingdom
        )
        val randomLocation = disneyLocations.random()

        // Load existing scores
        val json = sp.getString(Constants.SP_KEYS.PLAYLIST_KEY, "")
        val scoreList = if (json.isEmpty()) {
            ScoreList()
        } else {
            gson.fromJson(json, ScoreList::class.java)
        }
        
        // Add new score with random location
        scoreList.scores.add(ScoreRecord(name, score, randomLocation.first, randomLocation.second))
        
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