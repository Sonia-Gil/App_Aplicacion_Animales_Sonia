package com.example.app_aplicacion_animales_sonia

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.widget.Button
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ListaAnimalesActivity : AppCompatActivity() {

    private lateinit var adapter: AnimalAdapter
    private lateinit var dao: AnimalesDAO // Instancia de Room
    private var categoriaSeleccionada: String = "Mamíferos"

    //punto de entrada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_animales)

        // AQUÍ INICIALIZAS LA BASE DE DATOS:
        dao = AppDatabase.obtener(this).animalesDao()

        val txtTitulo = findViewById<TextView>(R.id.txtTituloCategoria)
        val imgHeader = findViewById<ImageView>(R.id.imgCategoriaHeader)
        val rvAnimales = findViewById<RecyclerView>(R.id.rvAnimales)
        val btnAgregarAca = findViewById<Button>(R.id.btnAgregarAca)
        val btnVolver = findViewById<Button>(R.id.btnVolverMenu)

        //1.recuperamos la categoría que se envió desde el intent
        categoriaSeleccionada = intent.getStringExtra("CATEGORIA_EXTRA") ?: "Mamíferos"
        txtTitulo.text = categoriaSeleccionada

        //2.asignamos la imagen según la categoría seleccionada
        when (categoriaSeleccionada.lowercase()) {
            "mamíferos", "mamiferos" -> imgHeader.setImageResource(R.drawable.imagen_pelo)
            "aves" -> imgHeader.setImageResource(R.drawable.imagen_plumas)
            "peces" -> imgHeader.setImageResource(R.drawable.imagen_escamas)
            else -> imgHeader.setImageResource(R.drawable.pato)
        }

        //3.cofiguramos el RecyclerView que es en lista vertical
        rvAnimales.layoutManager = LinearLayoutManager(this)

        // Inicializamos el adaptador pasándole la función de borrado para Room
        adapter = AnimalAdapter(emptyList()) { animalABorrar ->
            borrarAnimal(animalABorrar)
        }
        rvAnimales.adapter = adapter

        //4. Botón Volver al menú principal
        btnVolver.setOnClickListener {
            finish()
        }

        //5.abrimos el formulario Animal pasando la categoría de forma fija
        btnAgregarAca.setOnClickListener {
            val intent = Intent(this, AgregarAnimalActivity::class.java)
            intent.putExtra("CATEGORIA_EXTRA", categoriaSeleccionada)
            startActivity(intent)
        }
    }

    //se ejecuta cada vez que volvemos a esta pantalla
    override fun onResume() {
        super.onResume()
        cargarDatosDesdeRoom()


    }

    private fun cargarDatosDesdeRoom() {
        lifecycleScope.launch {
            // Leemos todos los animales guardados en Room
            val todosLosAnimales = dao.obtenerTodasAnimales()

            // Filtramos la lista comparando la especie/categoría
            val animalesFiltrados = todosLosAnimales.filter { animal ->
                animal.especie.trim().equals(categoriaSeleccionada.trim(), ignoreCase = true) ||
                        (categoriaSeleccionada.lowercase()
                            .contains("mamif") && animal.especie.lowercase().contains("mamif"))
            }

            // Actualizamos la lista en el adaptador
            adapter.actualizarLista(animalesFiltrados)
        }
    }

    // Método para BORRAR de Room
    private fun borrarAnimal(animal: Animales) {
        lifecycleScope.launch {
            dao.borrarAnimal(animal) // Borra la fila en SQLite
            cargarDatosDesdeRoom() // Recarga la lista en pantalla
        }
    }
}