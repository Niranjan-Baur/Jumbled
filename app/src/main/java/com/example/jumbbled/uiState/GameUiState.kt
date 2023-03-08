package com.example.jumbbled.uiState

data class GameUiState(
    val currentScrambledWord: String = "",
    val isGuessedWordWrong: Boolean = false,
    val currentWordCount: Int = 0,
    val score: Int = 0,
    val isGameOver : Boolean = false,
    val isGameStart : Boolean = false
)