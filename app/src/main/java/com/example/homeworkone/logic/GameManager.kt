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

    // obstacles[row][lane] - 0: empty, 1: obstacle, 2: coin
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

    enum class CollisionType {
        NONE, OBSTACLE, COIN
    }

    /**
     * Ticks the game state:
     * 1. Move obstacles down
     * 2. Spawn new obstacle/coin in top row
     * 3. Check for collision in current car lane
     * 4. Update score
     *
     * Returns the type of collision that occurred.
     */
    fun tick(): CollisionType {
        if (isGameOver)
            return CollisionType.NONE

        score++

        // Move obstacles down
        for (row in Constants.GameConfig.ROWS_COUNT - 1 downTo 1) {
            for (lane in 0 until Constants.GameConfig.LANES_COUNT) {
                obstacles[row][lane] = obstacles[row - 1][lane]
            }
        }

        // Spawn new item randomly in top row
        spawnItem()

        // Check collision at the car's lane in the bottom row
        val itemAtCar = obstacles[Constants.GameConfig.ROWS_COUNT - 1][carLane]
        
        var result = CollisionType.NONE
        
        if (itemAtCar == Constants.GameConfig.OBSTACLE_TYPE) {
            lives--
            result = CollisionType.OBSTACLE
            // Remove the obstacle after collision
            obstacles[Constants.GameConfig.ROWS_COUNT - 1][carLane] = 0
        } else if (itemAtCar == Constants.GameConfig.COIN_TYPE) {
            score += Constants.GameConfig.COIN_SCORE
            result = CollisionType.COIN
            // Remove the coin after collecting
            obstacles[Constants.GameConfig.ROWS_COUNT - 1][carLane] = 0
        }

        return result
    }

    private fun spawnItem() {
        // Clear top row
        for (lane in 0 until Constants.GameConfig.LANES_COUNT) {
            obstacles[0][lane] = 0
        }
        
        // Randomly spawn one obstacle, one coin, or nothing
        val randomVal = Random.nextInt(100)
        if (randomVal < 30) { // 30% chance for obstacle
            val lane = Random.nextInt(Constants.GameConfig.LANES_COUNT)
            obstacles[0][lane] = Constants.GameConfig.OBSTACLE_TYPE
        } else if (randomVal < 50) { // 20% chance for coin
            val lane = Random.nextInt(Constants.GameConfig.LANES_COUNT)
            obstacles[0][lane] = Constants.GameConfig.COIN_TYPE
        }
    }

    fun getObstacleAt(row: Int, lane: Int): Int {
        return obstacles[row][lane]
    }
}
