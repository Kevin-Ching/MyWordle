package com.example.mywordle

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.min

class WordleAdapter(val context: Context, val answer: String, val guesses: MutableList<String>): RecyclerView.Adapter<WordleAdapter.ViewHolder>() {

    var currentPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_guess, parent, false)
        val counts: MutableMap<Char, Int> = mutableMapOf()
        for (char in answer) {
            if (!counts.containsKey(char)) {
                counts[char] = 1
            } else {
                counts[char] = counts[char]?.plus(1)!!
            }
        }
        return ViewHolder(view, counts)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val guess = guesses[position]
        holder.bind(guess)
        if (position < currentPosition) {
            holder.check(guess)
        }
    }

    override fun getItemCount(): Int {
        return guesses.size
    }

    inner class ViewHolder(itemView: View, val counts: MutableMap<Char, Int>) : RecyclerView.ViewHolder(itemView) {

        val letter1: TextView = itemView.findViewById(R.id.tvLetter1)
        val letter2: TextView = itemView.findViewById(R.id.tvLetter2)
        val letter3: TextView = itemView.findViewById(R.id.tvLetter3)
        val letter4: TextView = itemView.findViewById(R.id.tvLetter4)
        val letter5: TextView = itemView.findViewById(R.id.tvLetter5)
        val letters: MutableList<TextView> = mutableListOf(letter1, letter2, letter3, letter4, letter5)

        fun bind(guess: String) {
            val black = context.resources.getColor(R.color.black)
            for (tv in letters) {
                tv.setBackgroundResource(R.drawable.border)
                tv.setTextColor(black)
                tv.text = ""
            }
            for (i in 0 until min(5, guess.length)) {
                val currentLetterTV = letters[i]
                currentLetterTV.text = guess[i].toString().uppercase()
                currentLetterTV.setBackgroundResource(R.drawable.border_selected)
            }
        }

        fun check(guess: String) {
            val white = context.resources.getColor(R.color.white)
            val green = context.resources.getColor(R.color.green)
            val yellow = context.resources.getColor(R.color.yellow)
            val gray = context.resources.getColor(R.color.gray)

            val countsCopy: MutableMap<Char, Int> = mutableMapOf()
            countsCopy.putAll(counts)

            val checked = BooleanArray(5)
            for (i in 0 until 5) {
                val currentLetter = guess[i]
                val currentLetterTV = letters[i]
                currentLetterTV.setTextColor(white)

                if (currentLetter == answer[i]) {
                    checked[i] = true
                    countsCopy[currentLetter] = countsCopy[currentLetter]?.minus(1)!!
                    currentLetterTV.setBackgroundColor(green)
                }
            }
            for (i in 0 until 5) {
                if (!checked[i]) {
                    val currentLetter = guess[i]
                    val currentLetterTV = letters[i]

                    if (countsCopy[currentLetter] != null && countsCopy[currentLetter]!! > 0) {
                        countsCopy[currentLetter] = countsCopy[currentLetter]?.minus(1)!!
                        currentLetterTV.setBackgroundColor(yellow)
                    } else {
                        currentLetterTV.setBackgroundColor(gray)
                    }
                }
            }
            currentPosition++
        }
    }
}