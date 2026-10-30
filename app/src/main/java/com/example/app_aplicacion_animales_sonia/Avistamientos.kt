package com.example.app_aplicacion_animales_sonia

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "avistamientos")
data class Avistamientos(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombreAnimal: String,
    val especie: String,
    val rutaImagen: String? = null, // Guarda la Uri o path local de la imagen
    val rutaAudio: String? = null,    // Guarda la Uri o path local del audio
    val fecha: String,
    val lugar: String
)