package com.example.app_aplicacion_animales_sonia

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AnimalesDAO {

    //OPERACIONES PARA LOS ANIMALES
    @Insert
    suspend fun insertarAnimal(animales: Animales)

    @Query("SELECT * FROM Animales ORDER BY id DESC")
    suspend fun obtenerTodasAnimales(): List<Animales>

    @Delete
    suspend fun borrarAnimal(animales: Animales)


    //OPERACIONES PARA LOS AVISTAMIENTOS
    @Insert
    suspend fun insertarAvistamiento(avistamientos: Avistamientos)

    @Query("SELECT * FROM Avistamientos ORDER BY id DESC")
    suspend fun obtenerTodosAvistamientos(): List<Avistamientos>

    @Delete
    suspend fun borrarAvistamiento(avistamientos: Avistamientos)



}