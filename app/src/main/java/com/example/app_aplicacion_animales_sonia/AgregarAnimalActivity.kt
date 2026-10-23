package com.example.app_aplicacion_animales_sonia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts

class AgregarAnimalActivity : ComponentActivity() {

    private var uriImagenSeleccionada: String? = null
    private var uriSonidoSeleccionado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_animal)

        // 1. Enganchamos TODAS las vistas del XML por su ID
        val editNombre = findViewById<EditText>(R.id.editNombreAnimal)
        val editCategoria = findViewById<EditText>(R.id.editCategoriaAnimal)
        val editImagen = findViewById<EditText>(R.id.editImagenAnimal)
        val editSonido = findViewById<EditText>(R.id.editSonidoAnimal)

        val btnSeleccionarFoto = findViewById<Button>(R.id.btnSeleccionarFoto)
        val btnSeleccionarAudio = findViewById<Button>(R.id.btnSeleccionarAudio)
        val imgVistaPrevia = findViewById<ImageView>(R.id.imgVistaPrevia)
        val txtAudioSeleccionado =
            findViewById<TextView>(R.id.txtAudioSeleccionado) // <-- AQUÍ SE DECLARA

        val btnGuardar = findViewById<Button>(R.id.btnGuardarAnimal)
        val btnVolver = findViewById<Button>(R.id.btnVolverAgregar)

        // Autocompletar la categoría si viene desde la lista
        val categoriaRecibida = intent.getStringExtra("CATEGORIA_EXTRA")
        if (!categoriaRecibida.isNullOrEmpty()) {
            editCategoria.setText(categoriaRecibida)
            editCategoria.isEnabled = false
        }

        // 2. Registrar el selector de la galería
        // Registrar el selector para fotos con permisos persistentes
        val selectorGaleria =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                if (uri != null) {
                    try {
                        // Tomamos permisos de lectura persistentes para que no de crash en la lista
                        contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    uriImagenSeleccionada = uri.toString()
                    imgVistaPrevia.setImageURI(uri)
                    imgVistaPrevia.visibility = View.VISIBLE
                }
            }

        // 3. Registrar el selector de audio (usando la variable ya inicializada)
        val selectorAudio =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                if (uri != null) {
                    try {
                        contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    uriSonidoSeleccionado = uri.toString()

                    // Ahora ya no da error porque txtAudioSeleccionado se declaró arriba
                    txtAudioSeleccionado.visibility = View.VISIBLE

                    Toast.makeText(this, "¡Audio seleccionado con éxito!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        // 4. Listeners para los botones
        // Y en el listener del botón abrir foto:
        btnSeleccionarFoto.setOnClickListener {
            selectorGaleria.launch(arrayOf("image/*"))
        }

        btnSeleccionarAudio.setOnClickListener {
            selectorAudio.launch(arrayOf("audio/*"))
        }

        btnVolver.setOnClickListener {
            finish()
        }

        btnGuardar.setOnClickListener {
            val nombre = editNombre.text.toString().trim()
            val categoria = editCategoria.text.toString().trim()
            val nombreImagenTexto = editImagen.text.toString().trim().lowercase()
            val nombreSonidoTexto = editSonido.text.toString().trim().lowercase()

            if (nombre.isEmpty() || categoria.isEmpty()) {
                Toast.makeText(
                    this,
                    "El nombre y la categoría son obligatorios",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                var idImagenFinal: Int? = null
                if (uriImagenSeleccionada == null) {
                    if (nombreImagenTexto.isNotEmpty()) {
                        val idEncontrado =
                            resources.getIdentifier(nombreImagenTexto, "drawable", packageName)
                        idImagenFinal = if (idEncontrado != 0) idEncontrado else R.drawable.pato
                    } else {
                        idImagenFinal = R.drawable.pato
                    }
                }

                var idSonidoFinal: Int? = null
                if (uriSonidoSeleccionado == null && nombreSonidoTexto.isNotEmpty()) {
                    val idSonidoEncontrado =
                        resources.getIdentifier(nombreSonidoTexto, "raw", packageName)
                    if (idSonidoEncontrado != 0) {
                        idSonidoFinal = idSonidoEncontrado
                    }
                }

                val nuevoAnimal = Animal(
                    nombre = nombre,
                    categoria = categoria,
                    uriImagen = uriImagenSeleccionada,
                    idImagen = idImagenFinal,
                    uriSonido = uriSonidoSeleccionado,
                    idSonido = idSonidoFinal
                )

                //1. añadimos el animal a la lista local
                AlmacenAnimales.listaAnimales.add(nuevoAnimal)

                //2. guardamos permanentemente en SharedPreferences
                AlmacenAnimales.guardarDatos(this)

                Toast.makeText(this, "¡Animal guardado con éxito!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}