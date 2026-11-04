package com.example.app_aplicacion_animales_sonia

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import java.io.File

class AgregarAnimalActivity : ComponentActivity() {

    private var uriImagenSeleccionada: String? = null
    private var uriSonidoSeleccionado: String? = null
    private var fotoPath: String? = null
    private var archivoFotoActual: File? = null

    // Ubicación GPS
    private lateinit var clienteUbicacion: FusedLocationProviderClient
    private var latitud: Double? = null
    private var longitud: Double? = null
    private lateinit var tvUbicacion: TextView

    private val pedirPermisosUbicacion = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        val fina = resultado[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val aproximada = resultado[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fina || aproximada) obtenerUbicacion()
        else Toast.makeText(this, "Sin permiso no puedo saber dónde estás", Toast.LENGTH_SHORT).show()
    }

    private fun tienePermisoUbicacion(): Boolean {
        val fina = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val aprox = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fina == PackageManager.PERMISSION_GRANTED ||
                aprox == PackageManager.PERMISSION_GRANTED
    }

    private fun pulsarBotonUbicacion() {
        if (tienePermisoUbicacion()) obtenerUbicacion()
        else pedirPermisosUbicacion.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion() {
        clienteUbicacion
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { ubicacion: Location? ->
                if (ubicacion != null) {
                    latitud = ubicacion.latitude
                    longitud = ubicacion.longitude
                    tvUbicacion.visibility = View.VISIBLE
                    tvUbicacion.text = "Ubicación: " +
                            "%.4f".format(ubicacion.latitude) +
                            ", " + "%.4f".format(ubicacion.longitude)
                } else {
                    Toast.makeText(this, "No se ha podido obtener la ubicación", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // (1) pedir el permiso de cámara
    private val pedirPermisoCamara = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) lanzarCamara()
        else Toast.makeText(this, "Sin permiso no puedo hacer la foto", Toast.LENGTH_SHORT).show()
    }

    // (2) abrir la cámara y recibir si se hizo la foto
    private val hacerFoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito: Boolean ->
        val archivo = archivoFotoActual
        if (exito && archivo != null) {
            fotoPath = archivo.absolutePath
            uriImagenSeleccionada = Uri.fromFile(archivo).toString()
            val imgVistaPrevia = findViewById<ImageView>(R.id.imgVistaPrevia)
            imgVistaPrevia.setImageBitmap(Fotos.cargar(fotoPath))
            imgVistaPrevia.visibility = View.VISIBLE
        } else {
            archivo?.delete()
        }
    }

    // (3) selector de fotos de la galería usando PickVisualMedia
    private val elegirDeGaleria = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val ruta = Fotos.copiarDesdeUri(this, uri)
            if (ruta != null) {
                fotoPath = ruta
                uriImagenSeleccionada = Uri.fromFile(File(ruta)).toString()
                val imgVistaPrevia = findViewById<ImageView>(R.id.imgVistaPrevia)
                imgVistaPrevia.setImageBitmap(Fotos.cargar(ruta))
                imgVistaPrevia.visibility = View.VISIBLE
            }
        }
    }

    private fun pulsarBotonFoto() {
        val permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permiso == PackageManager.PERMISSION_GRANTED) lanzarCamara()
        else pedirPermisoCamara.launch(Manifest.permission.CAMERA)
    }

    private fun lanzarCamara() {
        val archivo = Fotos.crearArchivo(this)
        archivoFotoActual = archivo
        hacerFoto.launch(Fotos.uriDe(this, archivo))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_animal)

        // Inicializamos FusedLocationProviderClient
        clienteUbicacion = LocationServices.getFusedLocationProviderClient(this)

        // 1. Enganchamos TODAS las vistas del XML por su ID
        val editNombre = findViewById<EditText>(R.id.editNombreAnimal)
        val editCategoria = findViewById<EditText>(R.id.editCategoriaAnimal)
        val editImagen = findViewById<EditText>(R.id.editImagenAnimal)
        val editSonido = findViewById<EditText>(R.id.editSonidoAnimal)

        val btnTomarFotoCamara = findViewById<Button>(R.id.btnTomarFotoCamara)
        val btnSeleccionarFoto = findViewById<Button>(R.id.btnSeleccionarFoto)
        val btnSeleccionarAudio = findViewById<Button>(R.id.btnSeleccionarAudio)
        val btnUbicacion = findViewById<Button>(R.id.btnUbicacion)
        tvUbicacion = findViewById(R.id.tvUbicacion)

        val imgVistaPrevia = findViewById<ImageView>(R.id.imgVistaPrevia)
        val txtAudioSeleccionado =
            findViewById<TextView>(R.id.txtAudioSeleccionado)

        val btnGuardar = findViewById<Button>(R.id.btnGuardarAnimal)
        val btnVolver = findViewById<Button>(R.id.btnVolverAgregar)

        // Autocompletar la categoría si viene desde la lista
        val categoriaRecibida = intent.getStringExtra("CATEGORIA_EXTRA")
        if (!categoriaRecibida.isNullOrEmpty()) {
            editCategoria.setText(categoriaRecibida)
            editCategoria.isEnabled = false
        }

        // 3. Registrar el selector de audio
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
                    txtAudioSeleccionado.visibility = View.VISIBLE
                    Toast.makeText(this, "¡Audio seleccionado con éxito!", Toast.LENGTH_SHORT).show()
                }
            }

        // 4. Listeners para los botones
        btnTomarFotoCamara.setOnClickListener {
            pulsarBotonFoto()
        }

        btnSeleccionarFoto.setOnClickListener {
            elegirDeGaleria.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        btnSeleccionarAudio.setOnClickListener {
            selectorAudio.launch(arrayOf("audio/*"))
        }

        btnUbicacion.setOnClickListener {
            pulsarBotonUbicacion()
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

                // --- AQUÍ GUARDAMOS DIRECTAMENTE EN ROOM ---
                val dao = AppDatabase.obtener(this).animalesDao()
                lifecycleScope.launch {
                    val animalRoom = Animales(
                        nombre = nombre,
                        especie = categoria,
                        rutaImagen = uriImagenSeleccionada,
                        rutaAudio = uriSonidoSeleccionado,
                        idImagen = idImagenFinal,
                        idSonido = idSonidoFinal,
                        fotoPath = fotoPath,
                        latitud = latitud,
                        longitud = longitud
                    )
                    dao.insertarAnimal(animalRoom)

                    // Limpiamos las variables para que el siguiente elemento no reutilice foto ni ubicación
                    fotoPath = null
                    latitud = null
                    longitud = null
                    imgVistaPrevia.setImageDrawable(null)
                    imgVistaPrevia.visibility = View.GONE

                    Toast.makeText(
                        this@AgregarAnimalActivity,
                        "¡Animal guardado con éxito!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }
}