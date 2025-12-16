package com.example.travelupa

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    imageDao: ImageDao,
    onImageSelected: (Uri) -> Unit,
    onBack: () -> Unit
) {
    val images by imageDao.getAllImages().collectAsState(initial = emptyList())

    var showAddImageDialog by remember { mutableStateOf(false) }
    var selectedImageEntity by remember { mutableStateOf<ImageEntity?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf<ImageEntity?>(null) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Galeri Travelupa") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddImageDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Image")
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(images) { image ->
                Image(
                    painter = rememberAsyncImagePainter(model = image.localPath),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .padding(4.dp)
                        .clickable {
                            selectedImageEntity = image
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }

        if (showAddImageDialog) {
            AddImageDialog(
                onDismiss = { showAddImageDialog = false },
                onImageAdded = { uri ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val localPath = saveImageLocally(context, uri)
                            val newImage = ImageEntity(localPath = localPath)
                            imageDao.insert(newImage)
                        } catch (e: Exception) {
                            Log.e("Gallery", "Error saving image", e)
                        }
                    }
                    showAddImageDialog = false
                }
            )
        }

        selectedImageEntity?.let { imageEntity ->
            ImageDetailDialog(
                imageEntity = imageEntity,
                onDismiss = { selectedImageEntity = null },
                onDelete = { imageToDelete ->
                    showDeleteConfirmation = imageToDelete
                    selectedImageEntity = null
                }
            )
        }

        showDeleteConfirmation?.let { imageToDelete ->
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = null },
                title = { Text("Hapus Gambar") },
                text = { Text("Yakin ingin menghapus gambar ini dari galeri?") },
                confirmButton = {
                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                imageDao.delete(imageToDelete)
                                val file = File(imageToDelete.localPath)
                                if (file.exists()) file.delete()
                            }
                            showDeleteConfirmation = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Hapus")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = null }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Composable
fun AddImageDialog(
    onDismiss: () -> Unit,
    onImageAdded: (Uri) -> Unit
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // Launcher Kamera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val uri = saveBitmapToUri(context, it)
            imageUri = uri
        }
    }

    // Launcher Izin Kamera (Baru Ditambahkan)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Jika izin diberikan, langsung buka kamera
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Gambar Baru") },
        text = {
            Column {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUri),
                        contentDescription = "Selected",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Galeri")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            // Cek Izin Kamera Dulu
                            val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

                            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                // Izin sudah ada -> Buka Kamera
                                cameraLauncher.launch(null)
                            } else {
                                // Belum ada izin -> Minta Izin
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Kamera")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { imageUri?.let { onImageAdded(it) } },
                enabled = imageUri != null
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun ImageDetailDialog(
    imageEntity: ImageEntity,
    onDismiss: () -> Unit,
    onDelete: (ImageEntity) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Image(
                painter = rememberAsyncImagePainter(model = imageEntity.localPath),
                contentDescription = "Detail",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
        },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Tutup") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onDelete(imageEntity) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            }
        }
    )
}

fun saveBitmapToUri(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "cam_${UUID.randomUUID()}.jpg")
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.close()
    return Uri.fromFile(file)
}