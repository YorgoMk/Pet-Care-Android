package com.usj.petcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.usj.petcare.ui.PetCareApp
import com.usj.petcare.ui.theme.PetCareTheme

/** The only Activity. It sets the Compose content once; navigation happens inside PetCareApp. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetCareTheme {
                PetCareApp()
            }
        }
    }
}
