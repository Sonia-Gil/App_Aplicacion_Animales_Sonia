package com.example.app_aplicacion_animales_sonia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)//aqui conectamos archivo con el cod kotlin con xml
        setContentView(R.layout.activity_main)//aqui se vincula la actividad con su interfaz XML

        //carga la lista guardada en SharedPreferences, o los datos por defecto
        AlmacenAnimales.cargarDatos(this)

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


}