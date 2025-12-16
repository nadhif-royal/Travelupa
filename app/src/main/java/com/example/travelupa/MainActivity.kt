package com.example.travelupa

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.travelupa.ui.theme.TravelupaTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

sealed class Screen(val route: String) {
    object Greeting : Screen("greeting")
    object Login : Screen("login")
    object Home : Screen("home")
    object Gallery : Screen("gallery")
}

class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase
    private lateinit var imageDao: ImageDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "travelupa-database"
        ).build()
        imageDao = db.imageDao()

        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val firestore = FirebaseFirestore.getInstance()

        setContent {
            TravelupaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        currentUser = currentUser,
                        firestore = firestore,
                        imageDao = imageDao,
                        context = this
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    currentUser: FirebaseUser?,
    firestore: FirebaseFirestore,
    imageDao: ImageDao,
    context: Context
) {
    val navController = rememberNavController()
    val startScreen = if (currentUser != null) Screen.Home.route else Screen.Greeting.route

    NavHost(navController = navController, startDestination = startScreen) {
        composable(Screen.Greeting.route) {
            GreetingScreen(
                onStart = {
                    navController.navigate(Screen.Login.route) { popUpTo(Screen.Greeting.route) { inclusive = true } }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                }
            )
        }

        composable(Screen.Home.route) {
            RekomendasiTempatScreen(
                onBackToLogin = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Greeting.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                },
                onUploadData = { nama, deskripsi, uri ->
                    uploadImageToFirestore(firestore, imageDao, context, nama, deskripsi, uri)
                },
                onGoToGallery = {
                    navController.navigate(Screen.Gallery.route)
                }
            )
        }

        composable(Screen.Gallery.route) {
            GalleryScreen(
                imageDao = imageDao,
                onImageSelected = { },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

fun uploadImageToFirestore(
    firestore: FirebaseFirestore,
    imageDao: ImageDao,
    context: Context,
    nama: String,
    deskripsi: String,
    imageUri: Uri?
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            var finalImagePath = ""

            if (imageUri != null) {
                val localPath = saveImageLocally(context, imageUri)
                finalImagePath = localPath

                val imageEntity = ImageEntity(
                    localPath = localPath,
                    tempatWisataId = nama
                )
                imageDao.insert(imageEntity)
            }

            val tempatData = hashMapOf(
                "nama" to nama,
                "deskripsi" to deskripsi,
                "gambarUriString" to finalImagePath
            )

            firestore.collection("tempat_wisata").document(nama)
                .set(tempatData)
                .addOnSuccessListener {
                    Log.d("Firestore", "Data berhasil disimpan")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Gagal menyimpan", e)
                }

        } catch (e: Exception) {
            Log.e("Upload", "Error uploading data", e)
        }
    }
}

fun saveImageLocally(context: Context, uri: Uri): String {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "img_${System.currentTimeMillis()}_${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, fileName)

        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        Log.e("ImageSave", "Error saving image", e)
        ""
    }
}