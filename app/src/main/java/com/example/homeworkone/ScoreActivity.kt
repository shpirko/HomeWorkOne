package com.example.homeworkone

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.homeworkone.utilities.Constants
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class ScoreActivity : AppCompatActivity() {

    private lateinit var score_LBL_title: MaterialTextView
    private lateinit var score_BTN_newGame: MaterialButton
    private lateinit var score_BTN_menu: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_score)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.score_LAY_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViews()
        initViews()
    }

    private fun findViews() {
        score_LBL_title = findViewById(R.id.score_LBL_title)
        score_BTN_newGame = findViewById(R.id.score_BTN_newGame)
        score_BTN_menu = findViewById(R.id.score_BTN_menu)
    }

    private fun initViews() {
        val bundle: Bundle? = intent.extras

        val message = bundle?.getString(Constants.BundleKeys.MESSAGE_KEY, "Game Over!")
        val score = bundle?.getInt(Constants.BundleKeys.SCORE_KEY, 0)
        val controlMode = bundle?.getString(Constants.BundleKeys.CONTROL_MODE_KEY)
        val delay = bundle?.getLong(Constants.BundleKeys.DELAY_KEY, 1000L) ?: 1000L

        score_LBL_title.text = "$message\nScore: $score"

        score_BTN_newGame.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(Constants.BundleKeys.CONTROL_MODE_KEY, controlMode)
                putExtra(Constants.BundleKeys.DELAY_KEY, delay)
            }
            startActivity(intent)
            finish()
        }

        score_BTN_menu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
