package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChecklistItem
import com.example.data.model.Proyecto
import com.example.data.model.SesionRodaje
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductionDao {

    // --- Proyectos ---
    @Query("SELECT * FROM proyectos ORDER BY fechaInicio DESC")
    fun getAllProyectos(): Flow<List<Proyecto>>

    @Query("SELECT * FROM proyectos WHERE id = :id")
    fun getProyectoById(id: Int): Flow<Proyecto?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProyecto(proyecto: Proyecto): Long

    @Update
    suspend fun updateProyecto(proyecto: Proyecto)

    @Delete
    suspend fun deleteProyecto(proyecto: Proyecto)


    // --- Sesiones de Rodaje ---
    @Query("SELECT * FROM sesiones_rodaje WHERE proyectoId = :proyectoId ORDER BY fechaRodaje ASC")
    fun getSesionesForProyecto(proyectoId: Int): Flow<List<SesionRodaje>>

    @Query("SELECT * FROM sesiones_rodaje ORDER BY fechaRodaje ASC")
    fun getAllSesiones(): Flow<List<SesionRodaje>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSesion(sesion: SesionRodaje): Long

    @Update
    suspend fun updateSesion(sesion: SesionRodaje)

    @Query("UPDATE sesiones_rodaje SET estado = :estado WHERE id = :sesionId")
    suspend fun updateSesionEstado(sesionId: Int, estado: String)

    @Delete
    suspend fun deleteSesion(sesion: SesionRodaje)


    // --- Checklist de Equipos ---
    @Query("SELECT * FROM checklist_items WHERE proyectoId = :proyectoId ORDER BY categoria ASC, item ASC")
    fun getChecklistForProyecto(proyectoId: Int): Flow<List<ChecklistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItem(item: ChecklistItem): Long

    @Update
    suspend fun updateChecklistItem(item: ChecklistItem)

    @Query("UPDATE checklist_items SET completado = :completado WHERE id = :itemId")
    suspend fun updateChecklistItemCompletado(itemId: Int, completado: Boolean)

    @Delete
    suspend fun deleteChecklistItem(item: ChecklistItem)
}
