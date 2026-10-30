package com.example.app_aplicacion_animales_sonia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // Instancia de tu DAO para acceder a Room
    private lateinit var dao: AnimalesDAO


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)//aqui conectamos archivo con el cod kotlin con xml
        setContentView(R.layout.activity_main)//aqui se vincula la actividad con su interfaz XML
        /*
        //carga la lista guardada en SharedPreferences, o los datos por defecto
        AlmacenAnimales.cargarDatos(this)
        */

        // Inicializamos la base de datos de Room, sustituimos el SharedPreferences, por esto
        dao = AppDatabase.obtener(this).animalesDao()

        // Carga inicial: Si la BD está vacía o tenía los datos incompletos de pruebas anteriores, insertamos la lista por defecto completa de Animal.kt
        lifecycleScope.launch {
            val todos = dao.obtenerTodasAnimales()
            val nombres = todos.map { it.nombre }
            if (todos.isEmpty() || !nombres.contains("León")) {
                // Borramos registros viejos de prueba si existían
                for (animalViejo in todos) {
                    dao.borrarAnimal(animalViejo)
                }
                guardarDatosIniciales()
            }
        }

        //1. Enganchamos lso botones del XML mediante su ID a las variables de kotlin
        val btnMamiferos = findViewById<Button>(R.id.btnMamiferos)
        val btnAves = findViewById<Button>(R.id.btnAves)
        val btnPeces = findViewById<Button>(R.id.btnPeces)
        val btnSalir = findViewById<Button>(R.id.btnSalir)

        //2. LISTENERS de los botones de categorías para abrir las listas filtradas
        btnMamiferos.setOnClickListener {//escuchamos los clics de lso botones
            abrirListaConCategoria("Mamíferos")
        }

        btnAves.setOnClickListener {
            abrirListaConCategoria("Aves")
        }

        btnPeces.setOnClickListener {
            abrirListaConCategoria("Peces")
        }

        //3. Botón Salir: Cierra la app
        //se pone la aplicacion en segundo plano y va a la pantalla de inicio
        btnSalir.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
    }

    //4. Función auxiliar, para la reutilizacion de la navegación
    //pasando la categoria mediante extras en el intent
    private fun abrirListaConCategoria(categoria: String) {
        val intent = Intent(this, ListaAnimalesActivity::class.java)
        //ponemos una etiqueta con la categoria seleccionada dentro del Intent,
        //y saber que categoria mostrar
        intent.putExtra("CATEGORIA_EXTRA", categoria)
        startActivity(intent)
    }

    //Añadimos un método de comprobación/ ejemplo del ROOM
    //para insertar un animal con imagen y audio desde aquí o desde otra pantalla
    private fun guardarAnimalesEjemplo(
        nombre: String,
        especie: String,
        rutaImagen: String?,
        rutaAudio: String?
    ) {
        lifecycleScope.launch {
            val nuevoAnimal = Animales(
                nombre = nombre,
                especie = especie,
                rutaImagen = rutaImagen,
                rutaAudio = rutaAudio
            )
            dao.insertarAnimal(nuevoAnimal)
        }
    }

    // Carga inicial de todos los animales por defecto de Animal.kt si la BD está vacía
    private suspend fun guardarDatosIniciales() {
        dao.insertarAnimal(
            Animales(
                nombre = "León",
                especie = "Mamíferos",
                idImagen = R.drawable.leon,
                idSonido = R.raw.sonido_leon
            )
        )
        dao.insertarAnimal(
            Animales(
                nombre = "Elefante",
                especie = "Mamíferos",
                idImagen = R.drawable.elefante,
                idSonido = R.raw.sonido_elefante
            )
        )
        dao.insertarAnimal(
            Animales(
                nombre = "Perro",
                especie = "Mamíferos",
                idImagen = R.drawable.perro,
                idSonido = R.raw.sonido_perro
            )
        )
        dao.insertarAnimal(
            Animales(
                nombre = "Loro",
                especie = "Aves",
                idImagen = R.drawable.loro,
                idSonido = R.raw.sonido_loro
            )
        )
        dao.insertarAnimal(
            Animales(
                nombre = "Pato",
                especie = "Aves",
                idImagen = R.drawable.pato,
                idSonido = R.raw.sonido_pato
            )
        )
        dao.insertarAnimal(
            Animales(
                nombre = "Águila",
                especie = "Aves",
                idImagen = R.drawable.aguila,
                idSonido = R.raw.sonido_aguila
            )
        )
        dao.insertarAnimal(
            Animales(
                nombre = "Pez Payaso",
                especie = "Peces",
                idImagen = R.drawable.pato,
                idSonido = null
            )
        )
    }

}