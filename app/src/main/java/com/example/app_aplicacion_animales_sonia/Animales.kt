package com.example.app_aplicacion_animales_sonia


import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Animales")
data class Animales(
    //autogenera el id
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val especie: String,
    val rutaImagen: String? = null, // Para fotos de la galería, capturas o cámara (URI)
    val rutaAudio: String? = null,  // Para audios grabados o seleccionados del teléfono (URI)
    val idImagen: Int? = null,      // Para fotos locales integradas (ej. R.drawable.pato)
    val idSonido: Int? = null,      // Para audios locales integrados (ej. R.raw.sonido)
    val latitud: Double? = null, //para la latitud
    val longitud: Double? = null,//para la longitud
    val fotoPath: String? = null //pra las fotos
)