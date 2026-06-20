package com.example.homeworkone

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.homeworkone.utilities.Constants
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup

class MenuActivity : AppCompatActivity() {

    private lateinit var menu_TOGGLE_control: MaterialButtonToggleGroup
    private lateinit var menu_TOGGLE_speed: MaterialButtonToggleGroup
    private lateinit var menu_BTN_start: MaterialButton
    private lateinit var menu_BTN_highScores: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        val root = findViewById<View>(R.id.menu_root)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViews()
        initViews()
    }

    private fun findViews() {
        menu_TOGGLE_control = findViewById(R.id.menu_TOGGLE_control)
        menu_TOGGLE_speed = findViewById(R.id.menu_TOGGLE_speed)
        menu_BTN_start = findViewById(R.id.menu_BTN_start)
        menu_BTN_highScores = findViewById(R.id.menu_BTN_highScores)
    }

    private fun initViews() {
        menu_BTN_start.setOnClickListener {
            startGame()
        }
        menu_BTN_highScores.setOnClickListener {
            openLeaderboard()
        }
    }

    private fun openLeaderboard() {
        val intent = Intent(this, LeaderboardActivity::class.java)
        startActivity(intent)
    }

    private fun startGame() {
        val controlMode = if (menu_TOGGLE_control.checkedButtonId == R.id.menu_BTN_gyro) {
            Constants.ControlModes.GYRO
        } else {
            Constants.ControlModes.BUTTONS
        }

        val delay = if (menu_TOGGLE_speed.checkedButtonId == R.id.menu_BTN_extreme) {
            500L // 0.5 seconds
        } else {
            1000L // 1.0 seconds
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(Constants.BundleKeys.CONTROL_MODE_KEY, controlMode)
            putExtra(Constants.BundleKeys.DELAY_KEY, delay)
        }
        startActivity(intent)
    }
}
