package com.example.app_aplicacion_animales_sonia

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.widget.Button
import android.widget.ImageView

class ListaAnimalesActivity : ComponentActivity() {

    private lateinit var adapter: AnimalAdapter
    private var categoriaSeleccionada: String = "Mamíferos"

    //punto de entrada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_animales)

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
        val rvAnimales = findViewById<RecyclerView>(R.id.rvAnimales)

        //filtramos la lista completa de almacenAnimales comparando la categoría seleccionada
        val animalesFiltrados = AlmacenAnimales.listaAnimales.filter { animal ->
            animal.categoria.trim().equals(categoriaSeleccionada.trim(), ignoreCase = true) ||
                    (categoriaSeleccionada.lowercase().contains("mamif") && animal.categoria.lowercase().contains("mamif"))
        }

        //instanciamos y asignamos el adaptador actualizado con los animales filtrados
        adapter = AnimalAdapter(animalesFiltrados)
        rvAnimales.adapter = adapter
    }
}