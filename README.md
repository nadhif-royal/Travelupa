# 🌏 Travelupa

**Travelupa** adalah aplikasi Android modern untuk rekomendasi tempat wisata (khususnya di Jawa Timur). Aplikasi ini dikembangkan sebagai Proyek Akhir mata kuliah **Pengembangan Aplikasi Perangkat Bergerak**.

Aplikasi ini dibangun sepenuhnya menggunakan **Kotlin** dan **Jetpack Compose** (Material 3), serta menerapkan arsitektur modern yang mengintegrasikan penyimpanan lokal (**Room Database**) dan penyimpanan cloud (**Firebase**).

---

## ✨ Fitur Utama

Aplikasi ini mencakup implementasi teknis dari Bab 1 hingga Bab 9:

* **Modern UI/UX:** Antarmuka deklaratif menggunakan **Jetpack Compose** & **Material 3**.
* **Authentication:** Sistem Login pengguna yang aman menggunakan **Firebase Authentication**.
* **Cloud Database:** Sinkronisasi data tempat wisata menggunakan **Firebase Firestore**.
* **Local Database:** Penyimpanan data offline (Persistance) menggunakan **Room Database**.
* **Camera Integration:** Fitur ambil foto langsung dalam aplikasi menggunakan **CameraX**.
* **Image Gallery:** Menampilkan grid foto wisata dari penyimpanan lokal.
* **Navigation:** Alur navigasi yang mulus (Greeting -> Login -> Home -> Gallery) dengan **Jetpack Navigation**.
* **Asynchronous:** Penanganan proses latar belakang menggunakan **Kotlin Coroutines**.

---

## 🛠️ Teknologi & Library

Project ini dibangun menggunakan *tech stack* berikut:

* **Bahasa:** [Kotlin](https://kotlinlang.org/)
* **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
* **Backend as a Service:** [Firebase](https://firebase.google.com/) (Auth, Firestore)
* **Local Storage:** [Room Database](https://developer.android.com/training/data-storage/room) (SQLite Wrapper)
* **Camera:** [CameraX](https://developer.android.com/training/camerax)
* **Image Loading:** [Coil](https://coil-kt.github.io/coil/)
* **Navigation:** Navigation Compose
* **Concurrency:** Coroutines & Flow

---

## 📸 Screenshots

| Landing & Login | Home Screen (List) | Add Data (Dialog) |
|:---:|:---:|:---:|
| <img src="AssetsTravelupa/login_screen.jpg" width="200" /> | <img src="AssetsTravelupa/home_screen.jpg" width="200" /> | <img src="AssetsTravelupa/add_dialog.jpg" width="200" /> |

| Camera & Gallery | Room & Firebase Sync |
|:---:|:---:|
| <img src="AssetsTravelupa/gallery_screen.jpg" width="200" /> | <img src="AssetsTravelupa/firebase_console.jpg" width="200" /> |

*(Catatan: Gambar di atas diambil dari folder `AssetsTravelupa`)*

---

## 🚀 Cara Menjalankan Project

1.  **Clone Repositori ini**
    ```bash
    git clone [https://github.com/nadhif-royal/Travelupa.git](https://github.com/nadhif-royal/Travelupa.git)
    ```

2.  **Konfigurasi Firebase**
    * Project ini membutuhkan file `google-services.json` untuk terhubung ke Firebase.
    * Buat project di [Firebase Console](https://console.firebase.google.com/).
    * Aktifkan **Authentication** (Email/Password) dan **Firestore Database**.
    * Unduh `google-services.json` dan letakkan di folder `app/`.

3.  **Buka di Android Studio**
    * Pastikan menggunakan Android Studio versi terbaru (Koala/Ladybug recommended).
    * Tunggu proses *Gradle Sync* selesai.

4.  **Run Aplikasi**
    * Jalankan pada Emulator atau Device Fisik.
    * Pastikan memberikan **Izin Kamera** saat diminta.

---

## 👤 Author

**Nadhif Rif'at Rasendriya**
* Mahasiswa Teknik Informatika - Universitas Brawijaya
* NIM: 235150201111074

---

*Dibuat dengan ❤️ menggunakan Kotlin.*
