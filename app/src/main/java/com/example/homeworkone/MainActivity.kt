package com.example.homeworkone

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.homeworkone.logic.GameManager
import com.example.homeworkone.utilities.Constants
import com.example.homeworkone.utilities.SignalManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    private lateinit var main_IMG_cars: Array<AppCompatImageView>
    private lateinit var main_IMG_obstacles: Array<Array<AppCompatImageView>>
    private lateinit var main_LBL_score: MaterialTextView
    private lateinit var main_FAB_left: ExtendedFloatingActionButton
    private lateinit var main_FAB_right: ExtendedFloatingActionButton

    private lateinit var gameManager: GameManager
    private val handler = Handler(Looper.getMainLooper())
    private var isGameRunning = false

    private var controlMode: String = Constants.ControlModes.BUTTONS
    private var gameDelay: Long = 1000L

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastMovementTime: Long = 0L
    private val MOVEMENT_THRESHOLD = 3.0f // Tilt threshold
    private val MOVEMENT_COOLDOWN = 300L // MS between gyro moves

    private val gameRunnable = object : Runnable {
        override fun run() {
            if (isGameRunning) {
                val collision = gameManager.tick()
                when (collision) {
                    GameManager.CollisionType.OBSTACLE -> {
                        SignalManager.getInstance().toast("Crash!")
                        SignalManager.getInstance().vibrate(500)
                        SignalManager.getInstance().playCrashSound()
                    }
                    GameManager.CollisionType.COIN -> {
                        SignalManager.getInstance().toast("+${Constants.GameConfig.COIN_SCORE} Points!")
                        SignalManager.getInstance().playCoinSound()
                    }
                    GameManager.CollisionType.NONE -> {}
                }
                refreshUI()

                if (gameManager.isGameOver) {
                    stopGame()
                    val intent = Intent(this@MainActivity, HighScoreActivity::class.java).apply {
                        putExtra(Constants.BundleKeys.SCORE_KEY, gameManager.score)
                        putExtra(Constants.BundleKeys.CONTROL_MODE_KEY, controlMode)
                        putExtra(Constants.BundleKeys.DELAY_KEY, gameDelay)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    handler.postDelayed(this, gameDelay)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get settings from Intent
        controlMode = intent.getStringExtra(Constants.BundleKeys.CONTROL_MODE_KEY) ?: Constants.ControlModes.BUTTONS
        gameDelay = intent.getLongExtra(Constants.BundleKeys.DELAY_KEY, 1000L)

        SignalManager.init(this)
        gameManager = GameManager()
        
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        findViews()
        initViews()
    }

    private fun findViews() {
        main_LBL_score = findViewById(R.id.main_LBL_score)
        main_FAB_left = findViewById(R.id.main_FAB_left)
        main_FAB_right = findViewById(R.id.main_FAB_right)

        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2),
            findViewById(R.id.main_IMG_heart3)
        )

        main_IMG_cars = arrayOf(
            findViewById(R.id.main_IMG_car0),
            findViewById(R.id.main_IMG_car1),
            findViewById(R.id.main_IMG_car2),
            findViewById(R.id.main_IMG_car3),
            findViewById(R.id.main_IMG_car4)
        )

        main_IMG_obstacles = Array(Constants.GameConfig.ROWS_COUNT) { row ->
            Array(Constants.GameConfig.LANES_COUNT) { lane ->
                val id = resources.getIdentifier("main_IMG_obs_${lane}_$row", "id", packageName)
                findViewById(id)
            }
        }
    }

    private fun initViews() {
        if (controlMode == Constants.ControlModes.GYRO) {
            main_FAB_left.visibility = View.GONE
            main_FAB_right.visibility = View.GONE
        } else {
            main_FAB_left.setOnClickListener {
                gameManager.moveCarLeft()
                refreshUI()
            }
            main_FAB_right.setOnClickListener {
                gameManager.moveCarRight()
                refreshUI()
            }
        }
        refreshUI()
    }

    private fun startGame() {
        if (isGameRunning) return
        isGameRunning = true
        handler.postDelayed(gameRunnable, gameDelay)
        
        if (controlMode == Constants.ControlModes.GYRO) {
            accelerometer?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
            }
        }
    }

    private fun stopGame() {
        isGameRunning = false
        handler.removeCallbacks(gameRunnable)
        if (controlMode == Constants.ControlModes.GYRO) {
            sensorManager.unregisterListener(this)
        }
    }

    private fun refreshUI() {
        // Update Score
        main_LBL_score.text = gameManager.score.toString()

        // Update Hearts
        for (i in main_IMG_hearts.indices) {
            main_IMG_hearts[i].visibility = if (i < gameManager.lives) View.VISIBLE else View.INVISIBLE
        }

        // Update Car position
        for (i in main_IMG_cars.indices) {
            main_IMG_cars[i].visibility = if (i == gameManager.carLane) View.VISIBLE else View.INVISIBLE
        }

        // Update Obstacles and Coins
        for (row in 0 until Constants.GameConfig.ROWS_COUNT) {
            for (lane in 0 until Constants.GameConfig.LANES_COUNT) {
                val itemType = gameManager.getObstacleAt(row, lane)
                val imgView = main_IMG_obstacles[row][lane]
                when (itemType) {
                    Constants.GameConfig.OBSTACLE_TYPE -> {
                        imgView.setImageResource(R.drawable.ic_obstacle)
                        imgView.visibility = View.VISIBLE
                    }
                    Constants.GameConfig.COIN_TYPE -> {
                        imgView.setImageResource(R.drawable.ic_coin)
                        imgView.visibility = View.VISIBLE
                    }
                    else -> {
                        imgView.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopGame()
    }

    override fun onResume() {
        super.onResume()
        if (!gameManager.isGameOver) {
            startGame()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val currentTime = System.currentTimeMillis()

            // Handle Steering (X-axis)
            if (currentTime - lastMovementTime > MOVEMENT_COOLDOWN) {
                if (x > MOVEMENT_THRESHOLD) {
                    gameManager.moveCarLeft()
                    refreshUI()
                    lastMovementTime = currentTime
                } else if (x < -MOVEMENT_THRESHOLD) {
                    gameManager.moveCarRight()
                    refreshUI()
                    lastMovementTime = currentTime
                }
            }

            // Handle Speed (Y-axis) - Only in GYRO mode
            if (controlMode == Constants.ControlModes.GYRO) {
                // Map Y from [2, 8] to [MIN_DELAY, MAX_DELAY]
                // Y=2 is Fast (MIN_DELAY), Y=8 is Slow (MAX_DELAY)
                val clampedY = y.coerceIn(2f, 8f)
                val normY = (clampedY - 2f) / (8f - 2f) // 0 to 1
                gameDelay = (Constants.GameConfig.MIN_DELAY + normY * (Constants.GameConfig.MAX_DELAY - Constants.GameConfig.MIN_DELAY)).toLong()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}
