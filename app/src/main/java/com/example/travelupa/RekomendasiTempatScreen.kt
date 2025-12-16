package com.example.travelupa

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.io.File

data class TempatWisata(
    val nama: String,
    val deskripsi: String,
    val gambarResId: Int? = null,
    val gambarUriString: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RekomendasiTempatScreen(
    onBackToLogin: () -> Unit,
    onUploadData: (String, String, Uri?) -> Unit,
    onGoToGallery: () -> Unit
) {
    var daftarTempatWisata by remember {
        mutableStateOf(
            listOf(
                TempatWisata("Tumpak Sewu", "Air terjun tercantik di Jawa Timur.", gambarResId = R.drawable.tumpak_sewu),
                TempatWisata("Gunung Bromo", "Matahari terbitnya bagus banget.", gambarResId = R.drawable.gunung_bromo),
                TempatWisata("Gunung Arunika", "Ayo main roblox", gambarResId = R.drawable.mount_arunika)
            )
        )
    }

    var showTambahDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Travelupa") },
                actions = {
                    IconButton(onClick = onGoToGallery) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = "Galeri"
                        )
                    }
                    IconButton(onClick = onBackToLogin) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showTambahDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah Tempat Wisata")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(paddingValues)
        ) {
            items(daftarTempatWisata) { tempat ->
                TempatItemEditable(
                    tempat = tempat,
                    onDelete = {
                        daftarTempatWisata = daftarTempatWisata.filter { it != tempat }
                    }
                )
            }
        }

        if (showTambahDialog) {
            TambahTempatWisataDialog(
                onDismiss = { showTambahDialog = false },
                onTambah = { nama, deskripsi, gambarUri ->
                    onUploadData(nama, deskripsi, gambarUri)

                    val newTempat = TempatWisata(
                        nama = nama,
                        deskripsi = deskripsi,
                        gambarUriString = gambarUri?.toString()
                    )
                    daftarTempatWisata = daftarTempatWisata + newTempat
                    showTambahDialog = false
                }
            )
        }
    }
}

@Composable
fun TempatItemEditable(tempat: TempatWisata, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            val painter = if (tempat.gambarUriString != null) {
                val isLocalFile = tempat.gambarUriString.startsWith("/")
                val dataModel = if (isLocalFile) File(tempat.gambarUriString) else Uri.parse(tempat.gambarUriString)

                rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(dataModel)
                        .build()
                )
            } else if (tempat.gambarResId != null) {
                painterResource(id = tempat.gambarResId)
            } else {
                painterResource(id = R.drawable.ic_launcher_background)
            }

            Box {
                Image(
                    painter = painter,
                    contentDescription = tempat.nama,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), shape = RoundedCornerShape(50))
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = tempat.nama, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = tempat.deskripsi, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun TambahTempatWisataDialog(
    onDismiss: () -> Unit,
    onTambah: (String, String, Uri?) -> Unit
) {
    var nama by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var gambarUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        gambarUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Tambah Tempat Wisata") },
        text = {
            Column {
                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Tempat") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (gambarUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = gambarUri),
                        contentDescription = "Preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pilih Gambar")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nama.isNotEmpty() && deskripsi.isNotEmpty()) {
                        onTambah(nama, deskripsi, gambarUri)
                    }
                }
            ) {
                Text("Tambah")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}