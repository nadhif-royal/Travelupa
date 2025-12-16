//package com.example.travelupa
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import androidx.navigation.compose.rememberNavController
//import com.example.travelupa.ui.theme.TravelupaTheme
//
//@Composable
//fun LandingPage(navController: NavController, modifier: Modifier = Modifier) {
//    Box(
//        modifier = modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Text(
//                text = "Selamat Datang di Travelupa!",
//                style = MaterialTheme.typography.headlineLarge.copy(
//                    fontWeight = FontWeight.Bold,
//                    color = MaterialTheme.colorScheme.primary
//                ),
//                textAlign = TextAlign.Center
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text(
//                text = "Solusi buat kamu yang lupa kemana-mana",
//                style = MaterialTheme.typography.bodyLarge,
//                textAlign = TextAlign.Center
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            Button(
//                onClick = { navController.navigate("destination_list") },
//                modifier = Modifier
//                    .width(200.dp)
//                    .align(Alignment.CenterHorizontally),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary
//                )
//            ) {
//                Text(text = "Mulai", color = MaterialTheme.colorScheme.onPrimary)
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun LandingPagePreview() {
//    TravelupaTheme {
//        // Kita menggunakan NavController palsu untuk Preview
//        val navController = rememberNavController()
//        LandingPage(navController = navController)
//    }
//}