package com.example.projetkotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.projetkotlin.ui.theme.ProjetKotlinTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.projetkotlin.navigation.AuthNavigator
import androidx.compose.runtime.*
import com.example.projetkotlin.ui.splash.SplashScreen

//import com.example.projetkotlin.ui.theme.ProjetKotlinTheme

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProjetKotlinTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSplash by remember { mutableStateOf(true) }

                    if (showSplash) {
                        SplashScreen {
                            showSplash = false
                        }
                    } else {
                        AuthNavigator()
                    }
                }
            }
        }
    }
}

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        ProjetKotlinTheme {
            Greeting("Android")
        }
    }
