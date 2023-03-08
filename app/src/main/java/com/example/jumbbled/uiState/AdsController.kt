package com.example.jumbbled.uiState

import android.app.Activity
import android.content.ContextWrapper
import com.example.jumbbled.R

//
//import android.app.Activity
//import android.content.Context
//import android.content.ContextWrapper
//import androidx.compose.runtime.Composable
//import com.google.android.gms.ads.AdError
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.FullScreenContentCallback
//import com.google.android.gms.ads.LoadAdError
//import com.google.android.gms.ads.interstitial.InterstitialAd
//import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
//
//var mInterstitialAd: InterstitialAd? = null
//
//fun loadInterstitial(context: Context) {
//    InterstitialAd.load(
//        context,
//        "ca-app-pub-3940256099942544/6300978111", //Change this with your own AdUnitID!
//        AdRequest.Builder().build(),
//        object : InterstitialAdLoadCallback() {
//            override fun onAdFailedToLoad(adError: LoadAdError) {
//                mInterstitialAd = null
//            }
//
//            override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                mInterstitialAd = interstitialAd
//            }
//        }
//    )
//}
//
//fun showInterstitial(context: Context, onAdDismissed: () -> Unit) {
//    val activity = context.findActivity()
//
//    if (mInterstitialAd != null && activity != null) {
//        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
//            override fun onAdFailedToShowFullScreenContent(e: AdError) {
//                mInterstitialAd = null
//            }
//
//            override fun onAdDismissedFullScreenContent() {
//                mInterstitialAd = null
//
//                loadInterstitial(context)
//                onAdDismissed()
//            }
//        }
//        mInterstitialAd?.show(activity)
//    }
//}
//

//


//package com.example.adnetwork

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

var mInterstitialAd: InterstitialAd? = null

// load the interstitial ad
fun loadInterstitial(context: Context) {
    InterstitialAd.load(
        context,
        context.getString(R.string.ad_id_interstitial),
        AdRequest.Builder().build(),
        object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
                Log.d("MainActivity", adError.message)
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
                Log.d("MainActivity", "Ad was loaded.")
            }
        }
    )
}

// add the interstitial ad callbacks
fun addInterstitialCallbacks(context: Context) {
    mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            Log.d("MainActivity", "Ad failed to show.")
        }

        override fun onAdShowedFullScreenContent() {
            mInterstitialAd = null
            Log.d("MainActivity", "Ad showed fullscreen content.")

            loadInterstitial(context)
        }

        override fun onAdDismissedFullScreenContent() {
            Log.d("MainActivity", "Ad was dismissed.")
        }
    }
}

// show the interstitial ad
fun showInterstitial(context: Context) {
    val activity = context.findActivity()

    if (mInterstitialAd != null) {
        mInterstitialAd?.show(activity!!)
        Log.w("MainActivity", "The interstitial ad ready.")
    } else {
        Log.d("MainActivity", "The interstitial ad wasn't ready yet.")
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun removeInterstitial() {
    mInterstitialAd?.fullScreenContentCallback = null
    mInterstitialAd = null
}