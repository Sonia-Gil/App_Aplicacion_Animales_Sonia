package com.example.app_aplicacion_animales_sonia

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.net.Uri

//Callback para avisar al MainActivity cuando borrar (private val onBorrarClick: (Animal) -> Unit)
class AnimalAdapter(
    private var listaAnimales: List<Animales>,
    private val onBorrarClick: (Animales) -> Unit
) :
    RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder>() {

    //Guardamos la referencia al MediaPlayer para detener sonidos anteriores
    //si se pulsa otro sonido
    private var mediaPlayerActual: MediaPlayer? = null

    //mantenemos las referencias de los elementos visuales de cada fila de la lista
    class AnimalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAnimal: ImageView = itemView.findViewById(R.id.imgAnimalFila)
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombreFila)
        val txtIconoSonido: TextView = itemView.findViewById(R.id.txtIconoSonido)
        val txtIconoBorrar: TextView = itemView.findViewById(R.id.txtIconoBorrar)
    }

    //
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.fila_animal, parent, false)
        return AnimalViewHolder(vista)
    }

    //conectamos los datos del animal en la posición actual con las vistas de la fila
    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val animal = listaAnimales[position]

        holder.txtNombre.text = animal.nombre
        holder.imgAnimal.setImageDrawable(null)

        //1.CARGA DE IMAGEN
        //gestionamos el URI del dispositivo con protecciones y recursos
        //locales como respaldo

        if (!animal.rutaImagen.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(animal.rutaImagen)
                holder.imgAnimal.setImageURI(uri)
            } catch (e: Exception) {
                e.printStackTrace()
                //si la Uri externa da error de permisos, ponemos la foto por defecto
                holder.imgAnimal.setImageResource(animal.idImagen ?: R.drawable.pato)
            }
        } else if (animal.idImagen != null) {
            holder.imgAnimal.setImageResource(animal.idImagen)
        } else {
            holder.imgAnimal.setImageResource(R.drawable.pato)
        }

        //2.MOSTRAMOS U OCULTAMOS EL ICONO DE SONIDO DEL NUEVO ANIMAL AÑADIDO (ES OPCIONAL)
        val tieneSonido =
            (animal.idSonido != null && animal.idSonido != 0) || !animal.rutaAudio.isNullOrEmpty()
        holder.txtIconoSonido.visibility = if (tieneSonido) View.VISIBLE else View.GONE


        // 3.LISTENER para la reproducción del audio
        //solo al pulsar el icono del altavoz
        holder.txtIconoSonido.setOnClickListener {
            try {
                //detenemos y liberamos cualquier reproducción activa anterior
                mediaPlayerActual?.stop()
                mediaPlayerActual?.release()
                mediaPlayerActual = null

                val contexto = holder.itemView.context

                //reproducimos audios desde la memoria del teléfono ( externo, URI)
                if (!animal.rutaAudio.isNullOrEmpty()) {
                    mediaPlayerActual = MediaPlayer().apply {
                        setDataSource(contexto, Uri.parse(animal.rutaAudio))
                        prepare()
                        start()
                    }
                }
                //reproducimos audio desde los recursos locales (internos-->res/raw)
                else if (animal.idSonido != null && animal.idSonido != 0) {
                    mediaPlayerActual = MediaPlayer.create(contexto, animal.idSonido)
                    mediaPlayerActual?.start()
                }

                //loberamos la memoria cuando el audio termine de sonar
                mediaPlayerActual?.setOnCompletionListener { mp ->
                    mp.release()
                    mediaPlayerActual = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        //LISTENER para borrar
        //1. Al pulsar la papelera 🗑️ de la fila
        holder.txtIconoBorrar.setOnClickListener {
            onBorrarClick(animal)
        }

        //2. Al dejar pulsada la fila entera
        holder.itemView.setOnLongClickListener {
            onBorrarClick(animal)
            true
        }
    }

    //devolvemos el núemro total de elementos a mostrar en la lista
    override fun getItemCount(): Int = listaAnimales.size

    // Método para recargar la lista cuando cambien los datos en Room
    fun actualizarLista(nuevaLista: List<Animales>) { // Debe ser List<Animales>
        this.listaAnimales = nuevaLista
        notifyDataSetChanged()
    }

}