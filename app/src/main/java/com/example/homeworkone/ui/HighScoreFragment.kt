package com.example.homeworkone.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homeworkone.R
import com.example.homeworkone.interfaces.Callback_HighScoreClicked
import com.example.homeworkone.models.ScoreList
import com.example.homeworkone.models.ScoreRecord
import com.example.homeworkone.utilities.Constants
import com.example.homeworkone.utilities.SharedPreferencesManagerV3
import com.google.gson.Gson

class HighScoreFragment : Fragment() {

    private lateinit var highScore_LST_scores: RecyclerView
    var highScoreItemClicked: Callback_HighScoreClicked? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: View = inflater.inflate(R.layout.fragment_high_score, container, false)
        findViews(v)
        initViews()
        return v
    }

    private fun findViews(v: View) {
        highScore_LST_scores = v.findViewById(R.id.highScore_LST_scores)
    }

    private fun initViews() {
        val sp = SharedPreferencesManagerV3.getInstance()
        val gson = Gson()
        val json = sp.getString(Constants.SP_KEYS.PLAYLIST_KEY, "")
        val scoreList = if (json.isEmpty()) ScoreList() else gson.fromJson(json, ScoreList::class.java)

        // Sort scores descending
        scoreList.scores.sortByDescending { it.score }

        // Keep only top 10
        val top10Scores = scoreList.scores.take(10)

        highScore_LST_scores.layoutManager = LinearLayoutManager(context)
        highScore_LST_scores.adapter = ScoreAdapter(top10Scores) { score ->
            highScoreItemClicked?.highScoreItemClicked(score.lat, score.lon)
        }
    }

    inner class ScoreAdapter(
        private val scores: List<ScoreRecord>,
        private val onClick: (ScoreRecord) -> Unit
    ) : RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_score, parent, false)
            return ScoreViewHolder(view)
        }

        override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
            val item = scores[position]
            holder.name.text = item.name
            holder.scoreValue.text = item.score.toString()
            holder.itemView.setOnClickListener { onClick(item) }
        }

        override fun getItemCount(): Int = scores.size

        inner class ScoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.score_LBL_name)
            val scoreValue: TextView = view.findViewById(R.id.score_LBL_value)
        }
    }
}