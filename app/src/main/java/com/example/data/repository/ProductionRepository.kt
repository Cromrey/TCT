package com.example.data.repository

import com.example.data.database.ProductionDao
import com.example.data.model.ChecklistItem
import com.example.data.model.Proyecto
import com.example.data.model.SesionRodaje
import kotlinx.coroutines.flow.Flow

class ProductionRepository(private val productionDao: ProductionDao) {

    val allProyectos: Flow<List<Proyecto>> = productionDao.getAllProyectos()
    val allSesiones: Flow<List<SesionRodaje>> = productionDao.getAllSesiones()

    fun getProyectoById(id: Int): Flow<Proyecto?> = productionDao.getProyectoById(id)

    fun getSesionesForProyecto(proyectoId: Int): Flow<List<SesionRodaje>> =
        productionDao.getSesionesForProyecto(proyectoId)

    fun getChecklistForProyecto(proyectoId: Int): Flow<List<ChecklistItem>> =
        productionDao.getChecklistForProyecto(proyectoId)

    suspend fun insertProyecto(proyecto: Proyecto): Long = productionDao.insertProyecto(proyecto)

    suspend fun updateProyecto(proyecto: Proyecto) = productionDao.updateProyecto(proyecto)

    suspend fun deleteProyecto(proyecto: Proyecto) = productionDao.deleteProyecto(proyecto)

    suspend fun insertSesion(sesion: SesionRodaje): Long = productionDao.insertSesion(sesion)

    suspend fun updateSesion(sesion: SesionRodaje) = productionDao.updateSesion(sesion)

    suspend fun updateSesionEstado(sesionId: Int, estado: String) =
        productionDao.updateSesionEstado(sesionId, estado)

    suspend fun deleteSesion(sesion: SesionRodaje) = productionDao.deleteSesion(sesion)

    suspend fun insertChecklistItem(item: ChecklistItem): Long = productionDao.insertChecklistItem(item)

    suspend fun updateChecklistItem(item: ChecklistItem) = productionDao.updateChecklistItem(item)

    suspend fun updateChecklistItemCompletado(itemId: Int, completado: Boolean) =
        productionDao.updateChecklistItemCompletado(itemId, completado)

    suspend fun deleteChecklistItem(item: ChecklistItem) = productionDao.deleteChecklistItem(item)
}
