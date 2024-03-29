package com.example.mywordle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors

class MainActivity : AppCompatActivity() {

    lateinit var rvGuesses: RecyclerView
    lateinit var etGuess: EditText
    lateinit var adapter: WordleAdapter

    val possibleAnswers: MutableList<String> = mutableListOf()
    val allowedWords: MutableList<String> = mutableListOf()
    lateinit var answer: String
    var currentGuess = ""
    val guesses: MutableList<String> = mutableListOf()
    var currentPosition = 0
    var gameOver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        possibleAnswers.addAll(readFileIntoList("possible_answers.txt"))
        allowedWords.addAll(readFileIntoList("allowed_guesses.txt"))
        allowedWords.addAll(possibleAnswers)
        for (i in 0 until 6) {
            guesses.add("")
        }
        answer = possibleAnswers.random()
        Log.i(TAG, "Answer: $answer")

        rvGuesses = findViewById(R.id.rvGuesses)
        etGuess = findViewById(R.id.etGuess)
        adapter = WordleAdapter(this, answer, guesses)
        rvGuesses.adapter = adapter
        rvGuesses.layoutManager = LinearLayoutManager(this)

        etGuess.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//                Log.i(TAG, "beforeTextChanged")
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!gameOver) {
//                    Log.i(TAG, "onTextChanged")
                    if (s.length > 5) {
                        etGuess.setText(s.substring(0, 5))
                    }
                    currentGuess = s.toString()
                    guesses[currentPosition] = currentGuess
                    adapter.notifyItemChanged(currentPosition)
                }
            }

            override fun afterTextChanged(s: Editable) {
//                Log.i(TAG, "afterTextChanged")
            }

        })
    }

    private lateinit var popupView: View
    private lateinit var popupTV: TextView
    private lateinit var popupWindow: PopupWindow

    fun submitAction(view: View) {
        if (!gameOver) {
            if (currentGuess.length == 5) {
                currentGuess = currentGuess.lowercase()
                if (!allowedWords.contains(currentGuess)) {
                    Toast.makeText(this, "Not in word list", Toast.LENGTH_SHORT).show()
                } else {
                    popupView = LayoutInflater.from(this).inflate(R.layout.popup_result, null)
                    popupTV = popupView.findViewById(R.id.tvResult)
                    val width = ConstraintLayout.LayoutParams.MATCH_PARENT
                    val height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    val focusable = true
                    popupWindow = PopupWindow(popupView, width, height, focusable)

                    val viewHolder: WordleAdapter.ViewHolder =
                        rvGuesses.findViewHolderForAdapterPosition(currentPosition)
                                as WordleAdapter.ViewHolder
                    viewHolder.check(currentGuess)
                    if (currentGuess == answer) {
                        gameOver = true
                        popupTV.text = String.format("You won in %s turns!", currentPosition + 1)
                        popupWindow.showAtLocation(rvGuesses, Gravity.CENTER, 0, 0)
                    } else if (currentPosition == 5) {
                        popupTV.text = "You lost"
                        popupWindow.showAtLocation(rvGuesses, Gravity.CENTER, 0, 0)
                    } else {
                        currentPosition++
                        currentGuess = ""
                        etGuess.text.clear()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Please start a new game", Toast.LENGTH_SHORT).show()
        }
    }

    fun restartAction(view: View) {
        answer = possibleAnswers.random()
        Log.i(TAG, "Answer: $answer")
        currentGuess = ""
        currentPosition = 0
        guesses.clear()
        for (i in 0 until 6) {
            guesses.add("")
        }
        adapter = WordleAdapter(this, answer, guesses)
        rvGuesses.adapter = adapter
        etGuess.text.clear()
        gameOver = false
    }

    fun readFileIntoList(file: String): MutableList<String> {
        var lines = mutableListOf<String>()
        try {
            lines = BufferedReader(InputStreamReader(assets.open(file), StandardCharsets.UTF_8))
                .lines().collect(Collectors.toList())
        } catch (e: IOException) {
            Log.e(TAG, "Could not read file into a list")
            e.printStackTrace()
        }
        return lines;
    }

    companion object {
        const val TAG = "MainActivity"
    }
}