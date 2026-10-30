package com.example.app_aplicacion_animales_sonia

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Animales::class, Avistamientos::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun animalesDao(): AnimalesDAO


    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                val nueva = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "animales_base_de_datos"
                ).build()
                instancia = nueva
                nueva
            }
        }
    }
}