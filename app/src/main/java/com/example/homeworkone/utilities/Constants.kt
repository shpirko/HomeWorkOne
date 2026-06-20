package com.example.homeworkone.utilities

object Constants {
    object GameConfig {
        const val OBSTACLE_DELAY_SECONDS = 1L
        const val LANES_COUNT = 5
        const val ROWS_COUNT = 5
        const val INITIAL_LIVES = 3
        const val COIN_SCORE = 2
        const val OBSTACLE_TYPE = 1
        const val COIN_TYPE = 2
    }

    object BundleKeys {
        const val SCORE_KEY = "SCORE_KEY"
        const val MESSAGE_KEY = "MESSAGE_KEY"
        const val CONTROL_MODE_KEY = "CONTROL_MODE_KEY"
        const val DELAY_KEY = "DELAY_KEY"
    }

    object ControlModes {
        const val BUTTONS = "BUTTONS"
        const val GYRO = "GYRO"
    }

    object SP_KEYS {
        const val PLAYLIST_KEY: String = "PLAYLIST_KEY"
        const val DATA_FILE: String = "DATA_FILE"
    }
}
