package com.example.homeworkone.logic

import com.example.homeworkone.utilities.Constants
import kotlin.random.Random

class GameManager(private val initialLives: Int = Constants.GameConfig.INITIAL_LIVES) {
    var score: Int = 0
        private set

    var lives: Int = initialLives
        private set

    var carLane: Int = Constants.GameConfig.LANES_COUNT / 2 // Middle lane
        private set

    // obstacles[row][lane] - 0: empty, 1: obstacle
    private val obstacles = Array(Constants.GameConfig.ROWS_COUNT) { IntArray(Constants.GameConfig.LANES_COUNT) }

    val isGameOver: Boolean
        get() = lives == 0

    fun moveCarLeft() {
        if (carLane > 0) {
            carLane--
        }
    }

    fun moveCarRight() {
        if (carLane < Constants.GameConfig.LANES_COUNT - 1) {
            carLane++
        }
    }

    /**
     * Ticks the game state:
     * 1. Check if there was a collision in the bottom row before moving (optional, but we'll do it after move for simplicity)
     * 2. Move obstacles down
     * 3. Spawn new obstacle in top row
     * 4. Check for collision in current car lane
     * 5. Update score
     *
     * Returns true if a collision occurred in this tick.
     */
    fun tick(): Boolean {
        if (isGameOver)
            return false

        score++

        // Move obstacles down
        for (row in Constants.GameConfig.ROWS_COUNT - 1 downTo 1) {
            for (lane in 0 until Constants.GameConfig.LANES_COUNT) {
                obstacles[row][lane] = obstacles[row - 1][lane]
            }
        }

        // Spawn new obstacle randomly in top row
        spawnObstacle()

        // Check collision: if there's an obstacle in the last row at the car's lane
        val collision = obstacles[Constants.GameConfig.ROWS_COUNT - 1][carLane] == 1
        if (collision) {
            lives--
        }

        return collision
    }

    private fun spawnObstacle() {
        // Clear top row
        for (lane in 0 until Constants.GameConfig.LANES_COUNT) {
            obstacles[0][lane] = 0
        }
        // Randomly spawn one obstacle or none
        if (Random.nextBoolean()) {
            val lane = Random.nextInt(Constants.GameConfig.LANES_COUNT)
            obstacles[0][lane] = 1
        }
    }

    fun getObstacleAt(row: Int, lane: Int): Int {
        return obstacles[row][lane]
    }
}
