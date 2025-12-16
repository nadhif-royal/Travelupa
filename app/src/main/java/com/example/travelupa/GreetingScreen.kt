package com.example.travelupa

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun GreetingScreen(
    onStart: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Selamat Datang di Travelupa!",
                style = MaterialTheme.typography.headlineMedium, // Material 3 dari h4
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Solusi buat kamu yang lupa kemana-mana",
                style = MaterialTheme.typography.titleLarge, // Material 3 dari h6
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = onStart,
            modifier = Modifier
                .width(360.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Text(text = "Mulai")
        }
    }
}