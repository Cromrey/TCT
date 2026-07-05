package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChecklistItem
import com.example.data.model.Proyecto
import com.example.data.model.SesionRodaje
import com.example.data.repository.ProductionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductionViewModel(private val repository: ProductionRepository) : ViewModel() {

    // Lista de todos los proyectos
    val proyectos: StateFlow<List<Proyecto>> = repository.allProyectos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ID del proyecto seleccionado actualmente para monitorizar
    private val _selectedProyectoId = MutableStateFlow<Int?>(null)
    val selectedProyectoId: StateFlow<Int?> = _selectedProyectoId.asStateFlow()

    // Proyecto seleccionado
    val selectedProyecto: StateFlow<Proyecto?> = _selectedProyectoId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else repository.getProyectoById(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Sesiones de rodaje asociadas al proyecto seleccionado
    val sesionesDeRodajeSelected: StateFlow<List<SesionRodaje>> = _selectedProyectoId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getSesionesForProyecto(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Checklist de equipos asociado al proyecto seleccionado
    val checklistSelected: StateFlow<List<ChecklistItem>> = _selectedProyectoId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getChecklistForProyecto(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Todas las sesiones de rodaje en el sistema
    val allSesiones: StateFlow<List<SesionRodaje>> = repository.allSesiones
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Inicializar datos demo si la base de datos está vacía
        prepopulateDemoDataIfNeeded()
    }

    private fun prepopulateDemoDataIfNeeded() {
        viewModelScope.launch {
            // Esperar primer valor de proyectos
            val currentList = repository.allProyectos.first()
            if (currentList.isEmpty()) {
                // Insertar Proyecto 1: Comercial Selva Central
                val p1Id = repository.insertProyecto(
                    Proyecto(
                        nombre = "Comercial Verano Selva Central",
                        cliente = "PromPerú",
                        descripcion = "Campaña audiovisual para promover el turismo ecológico en Chanchamayo, Pozuzo y Oxapampa. Tomas aéreas, caminatas ecológicas y gastronomía local.",
                        locacion = "Chanchamayo, Junín",
                        fechaInicio = System.currentTimeMillis() - 86400000 * 2, // Hace 2 días
                        estado = "Rodaje"
                    )
                ).toInt()

                // Sesiones Proyecto 1
                repository.insertSesion(
                    SesionRodaje(
                        proyectoId = p1Id,
                        nombreEscena = "C1: Toma aérea Catarata El Tirol",
                        fechaRodaje = System.currentTimeMillis() - 86400000,
                        horaInicio = "06:00 AM",
                        formato = "4K 60fps ProRes",
                        camaraPrincipal = "DJI Mavic 3 Pro",
                        estado = "Grabado"
                    )
                )
                repository.insertSesion(
                    SesionRodaje(
                        proyectoId = p1Id,
                        nombreEscena = "C2: Entrevista a Guía Asháninka",
                        fechaRodaje = System.currentTimeMillis(),
                        horaInicio = "10:30 AM",
                        formato = "4K 24fps S-Log3",
                        camaraPrincipal = "Sony FX3 (Cámara A)",
                        estado = "Grabando"
                    )
                )
                repository.insertSesion(
                    SesionRodaje(
                        proyectoId = p1Id,
                        nombreEscena = "C3: B-Roll de Cafetales de Altura",
                        fechaRodaje = System.currentTimeMillis() + 86400000,
                        horaInicio = "03:30 PM",
                        formato = "4K 120fps SlowMo",
                        camaraPrincipal = "Sony FX3 (Cámara B)",
                        estado = "Pendiente"
                    )
                )

                // Checklist Proyecto 1
                repository.insertChecklistItem(ChecklistItem(proyectoId = p1Id, item = "Baterías de Drones cargadas e inspeccionadas", completado = true, categoria = "Cámara"))
                repository.insertChecklistItem(ChecklistItem(proyectoId = p1Id, item = "Tarjetas CFexpress vacías (x3 de 160GB)", completado = true, categoria = "Cámara"))
                repository.insertChecklistItem(ChecklistItem(proyectoId = p1Id, item = "Filtros ND variables para lentes prime", completado = true, categoria = "Cámara"))
                repository.insertChecklistItem(ChecklistItem(proyectoId = p1Id, item = "Sistema de Audio Wireless PRO + Balitas de repuesto", completado = true, categoria = "Audio"))
                repository.insertChecklistItem(ChecklistItem(proyectoId = p1Id, item = "Luz LED Amaran COB 60d con difusor", completado = false, categoria = "Iluminación"))
                repository.insertChecklistItem(ChecklistItem(proyectoId = p1Id, item = "Repelente, bloqueador y protector de lluvia para cámaras", completado = false, categoria = "General"))


                // Insertar Proyecto 2: Spot Corporativo TCT
                val p2Id = repository.insertProyecto(
                    Proyecto(
                        nombre = "Video Corporativo TCT 2026",
                        cliente = "Corporación TCT",
                        descripcion = "Video institucional destacando los hitos y metas anuales de la empresa. Entrevistas con gerencia en set e insertos de oficinas de San Isidro.",
                        locacion = "Oficinas San Isidro, Lima",
                        fechaInicio = System.currentTimeMillis(),
                        estado = "Preproducción"
                    )
                ).toInt()

                // Sesiones Proyecto 2
                repository.insertSesion(
                    SesionRodaje(
                        proyectoId = p2Id,
                        nombreEscena = "V1: Discurso del Gerente General",
                        fechaRodaje = System.currentTimeMillis() + 86400000 * 3,
                        horaInicio = "09:00 AM",
                        formato = "4K 24fps S-Cinetone",
                        camaraPrincipal = "Sony FX6",
                        estado = "Pendiente"
                    )
                )
                repository.insertSesion(
                    SesionRodaje(
                        proyectoId = p2Id,
                        nombreEscena = "V2: B-Roll del equipo de Finanzas",
                        fechaRodaje = System.currentTimeMillis() + 86400000 * 3,
                        horaInicio = "11:30 AM",
                        formato = "1080p 60fps",
                        camaraPrincipal = "Sony FX3",
                        estado = "Pendiente"
                    )
                )

                // Checklist Proyecto 2
                repository.insertChecklistItem(ChecklistItem(proyectoId = p2Id, item = "Aprobación final de guión literario por Directiva", completado = true, categoria = "General"))
                repository.insertChecklistItem(ChecklistItem(proyectoId = p2Id, item = "Permiso de acceso firmado para Oficinas San Isidro", completado = true, categoria = "General"))
                repository.insertChecklistItem(ChecklistItem(proyectoId = p2Id, item = "Set up de Luces Aputure 300d II con softbox gigante", completado = false, categoria = "Iluminación"))
                repository.insertChecklistItem(ChecklistItem(proyectoId = p2Id, item = "Sennheiser MKH416 + Grabador de Audio Externo", completado = false, categoria = "Audio"))


                // Insertar Proyecto 3: Minidocumental Sabores del Rímac
                val p3Id = repository.insertProyecto(
                    Proyecto(
                        nombre = "Documental Sabores del Rímac",
                        cliente = "Municipalidad del Rímac",
                        descripcion = "Un recorrido por el distrito tradicional del Rímac rescatando las recetas de la cocina criolla más icónicas de antaño.",
                        locacion = "Jirón Trujillo, Rímac, Lima",
                        fechaInicio = System.currentTimeMillis() - 86400000 * 5,
                        estado = "Postproducción"
                    )
                ).toInt()

                // Sesiones Proyecto 3
                repository.insertSesion(
                    SesionRodaje(
                        proyectoId = p3Id,
                        nombreEscena = "D1: Preparación de Anticuchos en Carretilla",
                        fechaRodaje = System.currentTimeMillis() - 86400000 * 4,
                        horaInicio = "06:00 PM",
                        formato = "4K 24fps",
                        camaraPrincipal = "RED Komodo",
                        estado = "Grabado"
                    )
                )

                // Checklist Proyecto 3
                repository.insertChecklistItem(ChecklistItem(proyectoId = p3Id, item = "Permisos municipales de rodaje en vía pública", completado = true, categoria = "General"))
                repository.insertChecklistItem(ChecklistItem(proyectoId = p3Id, item = "Set de micrófonos lavalier para chefs", completado = true, categoria = "Audio"))

                // Establecer el primer proyecto como seleccionado por defecto
                _selectedProyectoId.value = p1Id
            } else {
                // Si ya hay proyectos, seleccionar el primero para que no empiece vacío
                _selectedProyectoId.value = currentList.firstOrNull()?.id
            }
        }
    }

    fun selectProyecto(id: Int?) {
        _selectedProyectoId.value = id
    }

    // --- ACCIONES DE PROYECTO ---
    fun agregarProyecto(nombre: String, cliente: String, descripcion: String, locacion: String, estado: String) {
        viewModelScope.launch {
            val nuevoId = repository.insertProyecto(
                Proyecto(
                    nombre = nombre,
                    cliente = cliente,
                    descripcion = descripcion,
                    locacion = locacion,
                    fechaInicio = System.currentTimeMillis(),
                    estado = estado
                )
            ).toInt()

            // Crear algunos items de checklist básicos por defecto
            repository.insertChecklistItem(ChecklistItem(proyectoId = nuevoId, item = "Cargar baterías de cámara y luces", completado = false, categoria = "General"))
            repository.insertChecklistItem(ChecklistItem(proyectoId = nuevoId, item = "Formatear tarjetas de almacenamiento", completado = false, categoria = "Cámara"))
            repository.insertChecklistItem(ChecklistItem(proyectoId = nuevoId, item = "Verificación de micrófonos y audio de respaldo", completado = false, categoria = "Audio"))

            // Seleccionar automáticamente el proyecto creado
            _selectedProyectoId.value = nuevoId
        }
    }

    fun actualizarProyectoEstado(proyecto: Proyecto, nuevoEstado: String) {
        viewModelScope.launch {
            repository.updateProyecto(proyecto.copy(estado = nuevoEstado))
        }
    }

    fun eliminarProyecto(proyecto: Proyecto) {
        viewModelScope.launch {
            repository.deleteProyecto(proyecto)
            val resto = repository.allProyectos.first()
            if (resto.isNotEmpty()) {
                _selectedProyectoId.value = resto.first().id
            } else {
                _selectedProyectoId.value = null
            }
        }
    }

    // --- ACCIONES DE SESIÓN DE RODAJE ---
    fun agregarSesion(nombreEscena: String, horaInicio: String, formato: String, camara: String) {
        val proyectoId = _selectedProyectoId.value ?: return
        viewModelScope.launch {
            repository.insertSesion(
                SesionRodaje(
                    proyectoId = proyectoId,
                    nombreEscena = nombreEscena,
                    fechaRodaje = System.currentTimeMillis(),
                    horaInicio = horaInicio,
                    formato = formato,
                    camaraPrincipal = camara,
                    estado = "Pendiente"
                )
            )
        }
    }

    fun alternarEstadoSesion(sesion: SesionRodaje) {
        val nuevoEstado = when (sesion.estado) {
            "Pendiente" -> "Grabando"
            "Grabando" -> "Grabado"
            "Grabado" -> "Problema"
            "Problema" -> "Pendiente"
            else -> "Pendiente"
        }
        viewModelScope.launch {
            repository.updateSesionEstado(sesion.id, nuevoEstado)
        }
    }

    fun eliminarSesion(sesion: SesionRodaje) {
        viewModelScope.launch {
            repository.deleteSesion(sesion)
        }
    }

    // --- ACCIONES DE CHECKLIST DE EQUIPO ---
    fun agregarChecklistItem(item: String, categoria: String) {
        val proyectoId = _selectedProyectoId.value ?: return
        viewModelScope.launch {
            repository.insertChecklistItem(
                ChecklistItem(
                    proyectoId = proyectoId,
                    item = item,
                    completado = false,
                    categoria = categoria
                )
            )
        }
    }

    fun alternarChecklistItem(itemId: Int, completado: Boolean) {
        viewModelScope.launch {
            repository.updateChecklistItemCompletado(itemId, completado)
        }
    }

    fun eliminarChecklistItem(item: ChecklistItem) {
        viewModelScope.launch {
            repository.deleteChecklistItem(item)
        }
    }
}

class ProductionViewModelFactory(private val repository: ProductionRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductionViewModel::class.java)) {
            return ProductionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
