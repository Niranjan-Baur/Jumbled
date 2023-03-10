package com.example.jumbbled.uiState

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jumbbled.R
import com.example.jumbbled.ui.theme.JumbbledTheme
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.RequestConfiguration
import kotlinx.coroutines.delay
import java.util.Arrays


@Composable
fun GameScreen(modifier: Modifier = Modifier,gameViewModel: GameViewModel = viewModel()) {
    val gameUiState by gameViewModel.uiState.collectAsState()

    val context = LocalContext.current

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())

            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        BannersAds()
        GameStatus(wordCount = gameUiState.currentWordCount, score = gameUiState.score, isGameStarted = gameUiState.isGameStart)
        CircleProgress()
        DifficultyOption()
        GameLayout(currentScrambledWord = gameUiState.currentScrambledWord,onUserGuessChanged = { gameViewModel.updateUserGuess(it) },
            onKeyboardDone = { if(gameViewModel.usedWord.isNotEmpty()){
                gameViewModel.checkUserGuess()
            }
                             },userGuess = gameViewModel.userGuess, isGuessWrong = gameUiState.isGuessedWordWrong)

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            OutlinedButton(
                onClick = {
                    if (gameViewModel.usedWord.isNotEmpty()){
                        gameViewModel.skipWord()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(stringResource(R.string.skip))
            }

            Button(
                modifier = modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 8.dp),
                onClick = {
                    if (gameViewModel.lengthOfWord.isNotEmpty()){
                        gameViewModel.checkUserGuess()
                    }
                }
            ) {
                Text(stringResource(R.string.submit))
            }
        }

        if(gameUiState.isGameOver){
            FinalScoreDialog(score = gameUiState.score,
                onPlayAgain = {
                    gameViewModel.resetGame()
                    loadInterstitial(context)
                    showInterstitial(context)
            })
        }

    }
}

@Composable
fun GameStatus(wordCount: Int, isGameStarted: Boolean ,score: Int,modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.word_count, wordCount),
            fontSize = 18.sp,
        )

        if (isGameStarted) Timer()
        
        Text(
            modifier = Modifier,
            text = stringResource(R.string.score, score),
            fontSize = 18.sp,
        )
    }
}

@Composable
fun Timer(){
    var time by rememberSaveable() { mutableStateOf(0) }
    
    LaunchedEffect(true){
        while (true){
            delay(1000)
            time++
            Log.d("Timer", time.toString())
        }
    }

    val min = time / 60
    val sec = time % 60

    Column(modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(modifier = Modifier.fillMaxHeight(),
            textAlign = TextAlign.Justify,
            fontSize = 18.sp,
            text = "${"%02d".format(min)}:${"%02d".format(sec)}")

    }

}

@Composable
fun GameLayout(modifier: Modifier = Modifier,gameViewModel: GameViewModel = viewModel(), currentScrambledWord: String,onUserGuessChanged:(String)->Unit,onKeyboardDone: () -> Unit,isGuessWrong:Boolean,userGuess: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),

        ) {
        Text(
            text = currentScrambledWord,
            fontSize = 45.sp,
            modifier = modifier.align(Alignment.CenterHorizontally)
        )

        if (gameViewModel.usedWord.isEmpty()){
            Text(
                text = stringResource(R.string.wordAppear),
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }else{
            Text(
                text = stringResource(R.string.instructions),
                fontSize = 17.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        OutlinedTextField(
            value = userGuess ,
            singleLine = true,
            isError = isGuessWrong,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onUserGuessChanged,
            label = {
                if (isGuessWrong) {
                    Text(stringResource(R.string.wrong_guess))
                } else {
                    Text(stringResource(R.string.enter_your_word))
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onKeyboardDone() }
            ),
        )
    }
}

@Composable
fun CircleProgress(gameViewModel: GameViewModel = viewModel()){
    val gameUiState by gameViewModel.uiState.collectAsState()
    Log.d("UiState", "CircleProgress: ${gameUiState.currentWordCount/10}")
    Row(
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(RoundedCornerShape(10.dp))
            ,progress = (gameUiState.currentWordCount/10f)
            , color = Color(0xff6adbde)
        )
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun DifficultyOption(modifier: Modifier = Modifier,gameViewModel: GameViewModel = viewModel()){
    val gameUiState by gameViewModel.uiState.collectAsState()


    if(gameViewModel.usedWord.isEmpty()){
        Row(modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround) {
            Text(text = stringResource(id = R.string.chooseLevel), fontSize = 20.sp)
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(onClick = {
            gameViewModel.easyBtnClicked()
        }, colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFF82CD47)
        ), enabled = gameUiState.isEasyBtnClicked) {
            Text(text = stringResource(id = R.string.easy))
        }

        Button(onClick = { gameViewModel.mediumBtnClicked() }, colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFFFFC93C)
        ),enabled = gameUiState.isMidBtnClicked) {
            Text(text = stringResource(id = R.string.medium))
        }

        Button(onClick = { gameViewModel.hardBtnClicked()},colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFFFF4A4A)
        ),enabled = gameUiState.isHardBtnClicked) {
            Text(text = stringResource(id = R.string.hard))
        }
    }
}

/*
 * Creates and shows an AlertDialog with final score.
 */

@Composable
private fun FinalScoreDialog(
    score: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = viewModel()
) {
    val activity = (LocalContext.current as Activity)
    val gameUiState by gameViewModel.uiState.collectAsState()


    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
        },
        title = {
            if (gameUiState.score < 50){
                Text(stringResource(R.string.failed))
//                Text(stringResource(R.string.congratulations))

            }
            else if (gameUiState.score in 51..100){
                Text(stringResource(R.string.almostClose))
            }
            else{
                Text(stringResource(R.string.congratulations))
            }
        },
        text = { Text(stringResource(R.string.you_scored, score)) },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = {
                    activity.finish()
                    gameViewModel.resetGame()
                }
            ) {
                Text(text = stringResource(R.string.exit))
            }
        },
        confirmButton = {
            TextButton(
                onClick = onPlayAgain
            ) {
                Text(text = stringResource(R.string.play_again))
            }
        }
    )
}


@Composable
fun BannersAds() {
    // on below line creating a variable for location.
    // on below line creating a column for our maps.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
    ) {

        // on below line adding admob banner ads.
        AndroidView(
            // on below line specifying width for ads.
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                // on below line specifying ad view.
                AdView(context).apply {
                    // on below line specifying ad size
                    setAdSize(AdSize.BANNER)
                    // on below line specifying ad unit id
                    // currently added a test ad unit id.
                    // ca-app-pub-3940256099942544/6300978111

                    //  adUnitId = R.string.ad_id_banner.toString() // it's not work.

                    adUnitId = "ca-app-pub-3940256099942544/6300978111"

                    // calling load ad to load our ad.
                    RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("2E9BC8C03155B55E5286AE72E6003525"))
                    loadAd(AdRequest.Builder().build())
                }

            }
        )
    }
}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JumbbledTheme {
        GameScreen()
    }
}
