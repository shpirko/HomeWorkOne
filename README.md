# HomeWork One - Car Racing Game

An advanced Android car racing game developed as part of HomeWork 1 & 2. The game evolved from a basic 3-lane button-controlled game into a feature-rich experience with sensors, maps, and persistence.

## Key Features & Improvements (HW2)

### 🎮 Gameplay & Controls
*   **Wider Road**: Expanded from 3 lanes to a **5-lane road** for more challenging navigation.
*   **Sensor Mode**: Integrated Accelerometer support.
    *   **Tilt Left/Right**: Steer the car between lanes.
    *   **Tilt Forward/Backward (Bonus)**: Dynamically control the speed of the game.
*   **Odometer**: Added a distance/score counter to track progress during the race.
*   **Coins**: Scattered coins on the road to provide score bonuses.

### 🔊 Multimedia & UI
*   **Custom Icon**: Personalized app icon featuring a blue car on a black background.
*   **Crash Sounds**: Added synthetic audio feedback using `ToneGenerator` for high-volume "Crash" and "Coin" sounds.
*   **Vibration**: Haptic feedback on collisions.

### 🏆 Leaderboard & Persistence
*   **Top 10 Scores**: The leaderboard now sorts and displays only the top 10 best performances.
*   **Persistence**: Scores and locations are saved locally using `SharedPreferences` and `Gson`.
*   **Google Maps Integration**: 
    *   **Real Location Tracking**: Each high score is saved with the **actual GPS coordinates** of the player at the time of submission.
    *   **Permission Handling**: Integrated runtime location permission requests and `FusedLocationProviderClient` for accurate tracking.
    *   **Interactive Map**: Clicking a score on the leaderboard zooms the map into the precise location where the record was achieved.

### 📂 Navigation
*   **Menu Screen**: A main entry point with options to choose between Buttons or Sensors mode and different speed settings.
*   **Scoreboard Navigation**: Added a "Menu" button to the leaderboard for easy return to the main screen.

---
*Developed by Shaked Shpirko*
