package com.example.jumbbled

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jumbbled.ui.theme.JumbbledTheme
import com.example.jumbbled.uiState.*
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JumbbledTheme {
                // A surface container using the 'background' color from the theme

                Surface(
                    modifier = Modifier.fillMaxSize().fillMaxHeight(),
                ) {
                    GameScreen()
                }
            }
        }
        // initialize the Mobile Ads SDK
        MobileAds.initialize(this) { }

        // load the interstitial ad
        loadInterstitial(this)

        // add the interstitial ad callbacks
        addInterstitialCallbacks(this)
    }

    override fun onDestroy() {
        removeInterstitial()
        super.onDestroy()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JumbbledTheme {
        GameScreen()
    }
}