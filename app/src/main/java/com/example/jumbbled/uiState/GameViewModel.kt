package com.example.jumbbled.uiState

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.jumbbled.MainActivity
import com.example.jumbbled.data.MAX_NO_OF_WORDS
import com.example.jumbbled.data.SCORE_INCREASE
import com.example.jumbbled.data.allWords
import kotlinx.coroutines.flow.*

class GameViewModel: ViewModel() {

    private  val _uiState = MutableStateFlow(GameUiState())

    val uiState : StateFlow<GameUiState> = _uiState.asStateFlow()

    var userGuess by mutableStateOf("")



    private lateinit var currentWord : String
    var lengthOfWord : MutableList<String> = mutableListOf()

    private var _usedWord : MutableSet<String> = mutableSetOf()
    var usedWord : Set<String> = _usedWord



    private fun pickRandomWordAndShuffle(): String {
        lengthOfWord.shuffle()
        currentWord = lengthOfWord.random()
        Log.d("CurrentWord",currentWord)
        if(_usedWord.contains(currentWord)){
            return pickRandomWordAndShuffle()
        }else{
            _usedWord.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }

    private fun shuffleCurrentWord(word : String):String{
        var tempWord = word.toCharArray()
        tempWord.shuffle()

        while (tempWord.toString() == word){
            tempWord.shuffle()
        }
        Log.d("ShuffledWord",tempWord.toString())
        return String(tempWord) // is same as tempWord.toString()
    }

    fun  resetGame(){
        _usedWord.clear()
        _uiState.value = GameUiState(currentScrambledWord = "", isGuessedWordWrong = false, currentWordCount = 0, score = 0, isGameOver = false, isGameStart = false)
        updateUserGuess("")
    }

    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
//        Log.d("hello", "$userGuess /// $guessedWord")
    }

    fun checkUserGuess(){
        if(userGuess.trim().equals(currentWord,ignoreCase = true)){

            val updateScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updateScore)
        }else{
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
    }

    private fun updateGameState(updatedScore: Int) {

        if (_usedWord.size == MAX_NO_OF_WORDS){
            //Last round in the game, update isGameOver to true, don't pick a new word
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    currentWordCount = currentState.currentWordCount.inc(),
                    isGameOver = true
                )
            }
        }else{
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentWordCount = currentState.currentWordCount.inc(),
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    score = updatedScore
                )
            }
            updateUserGuess("")
        }

    }

    fun skipWord(){
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }

    fun easyBtnClicked(){
        lengthOfWord = allWords.filter { s -> s.length <= 5 } as MutableList<String>
//        currentWord = lengthOfWord.random()
//        pickRandomWordAndShuffle()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle(), isGameStart = true)


//        Log.d("from easyBtn", lengthOfWord.toString())
    }
    fun mediumBtnClicked(){
        lengthOfWord = allWords.filter { s -> s.length in  5..7} as MutableList<String>
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle(), isGameStart = true)

//        Log.d("from mediumBtn", lengthOfWord.toString())
    }
    fun hardBtnClicked(){
        lengthOfWord = allWords.filter { s -> s.length >= 8 } as MutableList<String>
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle(), isGameStart = true)

//        Log.d("from hardBtn", lengthOfWord.toString())
    }



//    init {
//        resetGame()
//    }
}