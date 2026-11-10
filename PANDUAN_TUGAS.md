# Panduan ThreeD Visual Realism Suite

Project ini mengembangkan ThreeDViewing dengan OpenGL 3.3 core, Java OOP, LWJGL, JOML, dan shader GLSL.

## Menjalankan project

1. Ekstrak ZIP ke sebuah folder.
2. Di NetBeans, pilih **File → Open Project** dan pilih folder `ThreeDViewing` yang berisi `pom.xml`.
3. Tunggu NetBeans memuat dependency Maven, lalu jalankan project.
4. Tutup jendela visualizer sebelum menjalankan ulang agar penggunaan memori tetap rendah.

## Kontrol visualizer

| Tombol | Fungsi |
| --- | --- |
| B | Ganti Phong dan Blinn-Phong |
| F | Ganti flat dan smooth shading |
| Z | Nyalakan atau matikan depth test Z-Buffer |
| C | Nyalakan atau matikan back-face culling |
| T | Nyalakan atau matikan texture mapping |
| I | Ganti nearest dan bilinear dengan mipmap |
| M | Nyalakan atau matikan shadow mapping |
| 1–6 | Ganti jenis proyeksi |
| S | Ganti stereo anaglyph |
| + / - | Ubah field of view |
| Klik kiri dan geser | Putar kamera |
| Scroll | Zoom kamera |
| Esc | Tutup visualizer |

Status fitur muncul pada panel Output NetBeans.

## Implementasi

- Phong dan Blinn-Phong dihitung pada fragment shader.
- Flat shading memakai normal face; smooth shading memakai normal vertex hasil normalisasi posisi vertex kubus.
- Z-Buffer memakai depth test OpenGL; back-face culling menghapus face belakang.
- Tekstur checker PNG dimuat memakai STBImage, UV disediakan pada setiap vertex, dan mipmap dibuat saat upload.
- Shadow mapping memakai dua render pass: depth pass dari posisi lampu ke depth FBO 2048 × 2048, lalu lighting pass dari kamera. Fragment shader memakai PCF 3 × 3 dan slope-scaled bias.
- Model demonstrasi berupa kubus bertekstur di atas ground plane. Gambar checker disertakan pada `src/main/resources/textures/checker.png`.

## Checklist laporan yang perlu dilengkapi

Tambahkan nama, NIM, kelas, dan tautan repository GitHub. Ambil screenshot aplikasi untuk:

1. Phong dan Blinn-Phong.
2. Flat dan smooth shading.
3. Z-Buffer serta back-face culling dalam kondisi ON dan OFF.
4. Texture mapping dengan nearest dan bilinear mipmap.
5. Shadow mapping dalam kondisi ON dan OFF.

Catat FPS dari komputer yang digunakan serta amati shadow acne atau peter panning saat mengubah sudut kamera. Jangan mengisi angka FPS tanpa pengukuran pada perangkat sendiri.
