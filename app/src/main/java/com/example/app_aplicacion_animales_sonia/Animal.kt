package com.example.app_aplicacion_animales_sonia

import android.graphics.Bitmap
import android.net.Uri
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


// esta clase tiene los atributos que se pide o se muestra de cada animal
//admiten audios e imágenes, tanto locales como de forma externa
data class Animal(
    val nombre: String,
    val categoria: String,
    val idImagen: Int? = null,//es el identificador para resursos en res/drawable
    val uriImagen: String? = null,//Ruta URI para imágenes seleccionadas del dispositivo
    val idSonido: Int? = null,//Identificador para sonidos en res/raw
    val uriSonido: String? = null//Ruta URI para sonidos seleccionados del dispositivo
)

//Objeto para mantener la lista global de animales en memoria
//mientras la app esté abierta
object AlmacenAnimales {
    //lista mutable global
    var listaAnimales = mutableListOf<Animal>()

    //datos iniciales por defecto si la app se abre por primera vez
    private val listaPorDefecto = listOf(
        Animal("León", "Mamíferos", idImagen = R.drawable.leon, idSonido = R.raw.sonido_leon),
        Animal("Loro", "Aves", idImagen = R.drawable.loro, idSonido = R.raw.sonido_loro),
        Animal("Pato", "Aves", idImagen = R.drawable.pato, idSonido = R.raw.sonido_pato),
        Animal("Perro", "Mamíferos", idImagen = R.drawable.perro, idSonido = R.raw.sonido_perro),
        Animal("Elefante", "Mamíferos", idImagen = R.drawable.elefante, idSonido = R.raw.sonido_elefante),
        Animal("Águila", "Aves", idImagen = R.drawable.aguila, idSonido = R.raw.sonido_aguila)
    )

    // 1.cargamos los datos y se lee de SharedPreferences al arrancar la app
    fun cargarDatos(contexto: Context) {
        val prefs = contexto.getSharedPreferences("MisAnimalesPrefs", Context.MODE_PRIVATE)
        val json = prefs.getString("lista_animales_key", null)

        if (json != null) {
            //si ya hay datos guardados, convertimos el JSON de texto a nuestra lista de objetos Animal
            val type = object : TypeToken<MutableList<Animal>>() {}.type
            listaAnimales = Gson().fromJson(json, type)
        } else {
            //si es la primera vez que abre la app, se cargan los 6 animales por defecto
            listaAnimales = listaPorDefecto.toMutableList()
            guardarDatos(contexto)
        }
    }

    // 2. guardamos los datos, convierte la lista a JSON y la guarda en el disco
    fun guardarDatos(contexto: Context) {
        val prefs = contexto.getSharedPreferences("MisAnimalesPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(listaAnimales) // Convierte la lista a un String en formato JSON

        editor.putString("lista_animales_key", json)
        editor.apply() // Guarda de forma asíncrona
    }
}