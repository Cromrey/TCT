package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "proyectos")
data class Proyecto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val cliente: String,
    val descripcion: String,
    val locacion: String,
    val fechaInicio: Long,
    val estado: String // "Preproducción", "Rodaje", "Postproducción", "Completado"
)

@Entity(
    tableName = "sesiones_rodaje",
    foreignKeys = [
        ForeignKey(
            entity = Proyecto::class,
            parentColumns = ["id"],
            childColumns = ["proyectoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["proyectoId"])]
)
data class SesionRodaje(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val proyectoId: Int,
    val nombreEscena: String,
    val fechaRodaje: Long,
    val horaInicio: String,
    val formato: String, // e.g., "4K 24fps ProRes", "1080p 60fps"
    val camaraPrincipal: String, // e.g., "Sony FX3", "RED Komodo"
    val estado: String // "Pendiente", "Grabando", "Grabado", "Problema"
)

@Entity(
    tableName = "checklist_items",
    foreignKeys = [
        ForeignKey(
            entity = Proyecto::class,
            parentColumns = ["id"],
            childColumns = ["proyectoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["proyectoId"])]
)
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val proyectoId: Int,
    val item: String,
    val completado: Boolean,
    val categoria: String // "Cámara", "Audio", "Iluminación", "General"
)
