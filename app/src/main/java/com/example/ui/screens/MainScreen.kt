package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.ChecklistItem
import com.example.data.model.Proyecto
import com.example.data.model.SesionRodaje
import com.example.ui.viewmodel.ProductionViewModel

// Colores Temáticos Cinematográficos Profesionales
val SlateDark = Color(0xFF0F111A)      // Fondo medianoche
val SlateCard = Color(0xFF1B1E2E)      // Tarjeta azul oscuro grisáceo
val GoldAccent = Color(0xFFFFB300)     // Detalle dorado (cálido, claqueta)
val NeonRed = Color(0xFFFF3B30)        // Grabación activa (REC)
val EmeraldGreen = Color(0xFF34C759)   // Rodaje completado / OK
val OceanBlue = Color(0xFF2F80ED)      // Preproducción / info
val SoftWhite = Color(0xFFF2F2F7)      // Texto claro
val LightGray = Color(0xFF8E8E93)      // Texto secundario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: ProductionViewModel,
    modifier: Modifier = Modifier
) {
    val proyectos by viewModel.proyectos.collectAsStateWithLifecycle()
    val selectedProyecto by viewModel.selectedProyecto.collectAsStateWithLifecycle()
    val sesiones by viewModel.sesionesDeRodajeSelected.collectAsStateWithLifecycle()
    val checklist by viewModel.checklistSelected.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf(0) } // 0: Proyectos, 1: En Set, 2: Equipos, 3: Aprendizaje
    var showAddProjectDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = SlateDark,
                modifier = Modifier
                    .border(width = 0.5.dp, color = Color.White.copy(alpha = 0.12f))
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(Icons.Filled.PlayArrow, contentDescription = "Proyectos") },
                    label = { Text("Proyectos", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GoldAccent,
                        selectedTextColor = GoldAccent,
                        unselectedIconColor = LightGray,
                        unselectedTextColor = LightGray,
                        indicatorColor = SlateCard
                    ),
                    modifier = Modifier.testTag("nav_proyectos")
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Videocam, contentDescription = "En Set")
                            if (sesiones.any { it.estado == "Grabando" }) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .background(NeonRed)
                                )
                            }
                        }
                    },
                    label = { Text("En Set 🔴", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GoldAccent,
                        selectedTextColor = GoldAccent,
                        unselectedIconColor = LightGray,
                        unselectedTextColor = LightGray,
                        indicatorColor = SlateCard
                    ),
                    modifier = Modifier.testTag("nav_set")
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(Icons.Filled.CheckCircle, contentDescription = "Equipos") },
                    label = { Text("Logística", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GoldAccent,
                        selectedTextColor = GoldAccent,
                        unselectedIconColor = LightGray,
                        unselectedTextColor = LightGray,
                        indicatorColor = SlateCard
                    ),
                    modifier = Modifier.testTag("nav_equipos")
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    icon = { Icon(Icons.Filled.Info, contentDescription = "Aprendizaje") },
                    label = { Text("Aprende", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GoldAccent,
                        selectedTextColor = GoldAccent,
                        unselectedIconColor = LightGray,
                        unselectedTextColor = LightGray,
                        indicatorColor = SlateCard
                    ),
                    modifier = Modifier.testTag("nav_aprende")
                )
            }
        },
        containerColor = SlateDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Cabecera Principal con Identidad Corporativa: Corporación TCT
            HeaderSection(
                proyectoName = selectedProyecto?.nombre ?: "Sin Proyecto",
                proyectos = proyectos,
                onProyectoSelected = { viewModel.selectProyecto(it.id) },
                onAddProjectClick = { showAddProjectDialog = true }
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

            // Contenido según la pestaña seleccionada
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (activeTab) {
                    0 -> ProjectsTab(
                        proyectos = proyectos,
                        selectedProyecto = selectedProyecto,
                        sesiones = sesiones,
                        checklist = checklist,
                        onProyectoSelected = { viewModel.selectProyecto(it.id) },
                        onAddProjectClick = { showAddProjectDialog = true },
                        onDeleteProyecto = { viewModel.eliminarProyecto(it) },
                        onActualizarEstado = { p, est -> viewModel.actualizarProyectoEstado(p, est) }
                    )
                    1 -> ActiveSetTab(
                        selectedProyecto = selectedProyecto,
                        sesiones = sesiones,
                        onToggleEstado = { viewModel.alternarEstadoSesion(it) },
                        onAgregarSesion = { escena, hora, formato, camara ->
                            viewModel.agregarSesion(escena, hora, formato, camara)
                        },
                        onEliminarSesion = { viewModel.eliminarSesion(it) }
                    )
                    2 -> LogisticsTab(
                        selectedProyecto = selectedProyecto,
                        checklistItems = checklist,
                        onToggleItem = { item, isChecked ->
                            viewModel.alternarChecklistItem(item.id, isChecked)
                        },
                        onAgregarItem = { item, cat ->
                            viewModel.agregarChecklistItem(item, cat)
                        },
                        onEliminarItem = { viewModel.eliminarChecklistItem(it) }
                    )
                    3 -> LearningTab()
                }
            }
        }
    }

    // Diálogo para Agregar Proyecto
    if (showAddProjectDialog) {
        AddProjectDialog(
            onDismiss = { showAddProjectDialog = false },
            onConfirm = { nombre, cliente, desc, loc, estado ->
                viewModel.agregarProyecto(nombre, cliente, desc, loc, estado)
                showAddProjectDialog = false
            }
        )
    }
}

// ==========================================
// CABECERA CON COMPONENTE DE SELECCIÓN
// ==========================================
@Composable
fun HeaderSection(
    proyectoName: String,
    proyectos: List<Proyecto>,
    onProyectoSelected: (Proyecto) -> Unit,
    onAddProjectClick: () -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SlateDark)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(GoldAccent)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CORPORACIÓN TCT",
                    fontSize = 11.sp,
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            // Selector de Proyecto Interactivo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { dropdownExpanded = true }
                    .padding(vertical = 2.dp)
            ) {
                Text(
                    text = proyectoName,
                    fontSize = 18.sp,
                    color = SoftWhite,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Cambiar proyecto",
                    tint = SoftWhite,
                    modifier = Modifier.size(24.dp)
                )
            }

            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false },
                modifier = Modifier
                    .background(SlateCard)
                    .border(width = 0.5.dp, color = Color.White.copy(alpha = 0.12f))
                    .widthIn(max = 300.dp)
            ) {
                Text(
                    text = "Proyectos Activos",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightGray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                proyectos.forEach { p ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(p.nombre, color = SoftWhite, fontWeight = FontWeight.SemiBold)
                                Text(p.cliente, color = LightGray, fontSize = 11.sp)
                            }
                        },
                        onClick = {
                            onProyectoSelected(p)
                            dropdownExpanded = false
                        },
                        trailingIcon = {
                            val color = when (p.estado) {
                                "Rodaje" -> NeonRed
                                "Preproducción" -> OceanBlue
                                "Postproducción" -> GoldAccent
                                else -> EmeraldGreen
                            }
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }
                    )
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                DropdownMenuItem(
                    text = { Text("Nuevo Proyecto...", color = GoldAccent, fontWeight = FontWeight.Bold) },
                    leadingIcon = { Icon(Icons.Filled.Add, contentDescription = "Agregar", tint = GoldAccent) },
                    onClick = {
                        onAddProjectClick()
                        dropdownExpanded = false
                    },
                    modifier = Modifier.testTag("add_project_dropdown_btn")
                )
            }
        }

        // Indicador Visual de Grabación Activa en general
        val isRecording = proyectos.any { p -> p.estado == "Rodaje" }
        if (isRecording) {
            val infiniteTransition = rememberInfiniteTransition(label = "pulsing")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "rec_alpha"
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(NeonRed.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NeonRed.copy(alpha = alpha))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "RODAJE",
                    fontSize = 11.sp,
                    color = NeonRed,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "STANDBY",
                    fontSize = 11.sp,
                    color = LightGray,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

// ==========================================
// PESTAÑA 1: PROYECTOS (ESTADO & DETALLES)
// ==========================================
@Composable
fun ProjectsTab(
    proyectos: List<Proyecto>,
    selectedProyecto: Proyecto?,
    sesiones: List<SesionRodaje>,
    checklist: List<ChecklistItem>,
    onProyectoSelected: (Proyecto) -> Unit,
    onAddProjectClick: () -> Unit,
    onDeleteProyecto: (Proyecto) -> Unit,
    onActualizarEstado: (Proyecto, String) -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf<Proyecto?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Visual Cinemático Hero
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_production_1783215745561),
                    contentDescription = "TCT Hero Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Capa Degradada para que el texto sea legible
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, SlateDark.copy(alpha = 0.85f)),
                                startY = 50f
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Monitoreo Audiovisual",
                        color = SoftWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Control total de grabación y equipos para sets profesionales",
                        color = LightGray,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Resumen Interactivo del Proyecto Seleccionado
        if (selectedProyecto != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 0.5.dp, color = Color.White.copy(alpha = 0.08f), shape = RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "PROYECTO SELECCIONADO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = selectedProyecto.nombre,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SoftWhite
                                )
                                Text(
                                    text = "Cliente: ${selectedProyecto.cliente}",
                                    fontSize = 12.sp,
                                    color = LightGray
                                )
                            }

                            // Botón de eliminar
                            IconButton(
                                onClick = { showDeleteConfirm = selectedProyecto },
                                modifier = Modifier.testTag("delete_project_btn")
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "Eliminar proyecto", tint = NeonRed)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = selectedProyecto.descripcion,
                            fontSize = 13.sp,
                            color = SoftWhite.copy(alpha = 0.85f),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Fila de Info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Place, contentDescription = "Locación", tint = GoldAccent, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Locación", fontSize = 11.sp, color = LightGray)
                                }
                                Text(selectedProyecto.locacion, fontSize = 13.sp, color = SoftWhite, fontWeight = FontWeight.SemiBold)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Adjust, contentDescription = "Fase", tint = GoldAccent, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Fase Actual", fontSize = 11.sp, color = LightGray)
                                }
                                // Selector de Fase Interactivo
                                var phaseExpanded by remember { mutableStateOf(false) }
                                Box {
                                    Text(
                                        text = selectedProyecto.estado,
                                        fontSize = 13.sp,
                                        color = when (selectedProyecto.estado) {
                                            "Rodaje" -> NeonRed
                                            "Preproducción" -> OceanBlue
                                            "Postproducción" -> GoldAccent
                                            else -> EmeraldGreen
                                        },
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clickable { phaseExpanded = true }
                                            .border(0.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                    DropdownMenu(
                                        expanded = phaseExpanded,
                                        onDismissRequest = { phaseExpanded = false },
                                        modifier = Modifier.background(SlateCard)
                                    ) {
                                        listOf("Preproducción", "Rodaje", "Postproducción", "Completado").forEach { est ->
                                            DropdownMenuItem(
                                                text = { Text(est, color = SoftWhite) },
                                                onClick = {
                                                    onActualizarEstado(selectedProyecto, est)
                                                    phaseExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Métricas de Progreso Real
                        val totalEscenas = sesiones.size
                        val escenasGrabadas = sesiones.count { it.estado == "Grabado" }
                        val progEscenas = if (totalEscenas > 0) escenasGrabadas.toFloat() / totalEscenas else 0f

                        val totalChecklist = checklist.size
                        val checklistListos = checklist.count { it.completado }
                        val progChecklist = if (totalChecklist > 0) checklistListos.toFloat() / totalChecklist else 0f

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Progreso de Escenas
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Escenas/Sesiones Grabadas", fontSize = 12.sp, color = LightGray)
                                    Text("$escenasGrabadas de $totalEscenas (${(progEscenas * 100).toInt()}%)", fontSize = 12.sp, color = SoftWhite, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { progEscenas },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                    color = EmeraldGreen,
                                    trackColor = Color.White.copy(alpha = 0.1f)
                                )
                            }

                            // Progreso de Checklist de Logística
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Logística y Equipamiento", fontSize = 12.sp, color = LightGray)
                                    Text("$checklistListos de $totalChecklist (${(progChecklist * 100).toInt()}%)", fontSize = 12.sp, color = SoftWhite, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { progChecklist },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                    color = GoldAccent,
                                    trackColor = Color.White.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Listado de Proyectos del Grid para alternar rápido
        item {
            Text(
                text = "TODOS LOS PROYECTOS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = LightGray,
                letterSpacing = 1.sp
            )
        }

        items(proyectos) { p ->
            val isSelected = p.id == selectedProyecto?.id
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) SlateCard else SlateCard.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onProyectoSelected(p) }
                    .border(
                        width = if (isSelected) 1.dp else 0.5.dp,
                        color = if (isSelected) GoldAccent else Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .testTag("project_item_${p.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = p.nombre,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) GoldAccent else SoftWhite
                        )
                        Text(
                            text = "Cliente: ${p.cliente} | Locación: ${p.locacion}",
                            fontSize = 11.sp,
                            color = LightGray
                        )
                    }

                    // Chip de estado decorativo
                    val colorChip = when (p.estado) {
                        "Rodaje" -> NeonRed
                        "Preproducción" -> OceanBlue
                        "Postproducción" -> GoldAccent
                        else -> EmeraldGreen
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(colorChip.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = p.estado,
                            color = colorChip,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Botón grande para añadir proyecto
        item {
            Button(
                onClick = onAddProjectClick,
                colors = ButtonDefaults.buttonColors(containerColor = SlateCard),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .testTag("create_project_footer_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Add, contentDescription = "Añadir Proyecto", tint = GoldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("REGISTRAR NUEVO PROYECTO TCT", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }

    // Modal de confirmación para eliminar
    if (showDeleteConfirm != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            containerColor = SlateCard,
            title = { Text("¿Eliminar Proyecto?", color = SoftWhite) },
            text = { Text("Esto borrará permanentemente '${showDeleteConfirm?.nombre}' junto con todas sus sesiones de grabación y checklists asociados. Esta acción no se puede deshacer.", color = LightGray) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm?.let { onDeleteProyecto(it) }
                        showDeleteConfirm = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = NeonRed)
                ) {
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text("Cancelar", color = SoftWhite)
                }
            }
        )
    }
}

// ==========================================
// PESTAÑA 2: EN SET (CONTROL DE GRABACIÓN)
// ==========================================
@Composable
fun ActiveSetTab(
    selectedProyecto: Proyecto?,
    sesiones: List<SesionRodaje>,
    onToggleEstado: (SesionRodaje) -> Unit,
    onAgregarSesion: (String, String, String, String) -> Unit,
    onEliminarSesion: (SesionRodaje) -> Unit
) {
    var showAddSessionDialog by remember { mutableStateOf(false) }

    if (selectedProyecto == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Filled.Movie, contentDescription = null, modifier = Modifier.size(64.dp), tint = LightGray)
            Spacer(modifier = Modifier.height(16.dp))
            Text("No hay proyecto seleccionado", color = SoftWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("Selecciona o crea un proyecto en la pestaña anterior para empezar a monitorizar el set.", color = LightGray, fontSize = 13.sp, textAlign = TextAlign.Center)
        }
        return
    }

    Scaffold(
        containerColor = SlateDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSessionDialog = true },
                containerColor = GoldAccent,
                contentColor = SlateDark,
                modifier = Modifier.testTag("add_scene_fab")
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir Escena")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(NeonRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MONITOR EN TIEMPO REAL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftWhite,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Proyecto: ${selectedProyecto.nombre}",
                        fontSize = 14.sp,
                        color = LightGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "💡 Toca cualquier escena para cambiar su estado interactivo de filmación.",
                        fontSize = 11.sp,
                        color = GoldAccent,
                        lineHeight = 15.sp
                    )
                }
            }

            if (sesiones.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.Movie, contentDescription = null, tint = LightGray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No hay escenas registradas para rodaje", color = SoftWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Usa el botón de abajo a la derecha (+) para registrar las escenas del guión.", color = LightGray, fontSize = 11.sp, textAlign = TextAlign.Center)
                    }
                }
            }

            items(sesiones) { sesion ->
                InteractiveSessionCard(
                    sesion = sesion,
                    onCardClick = { onToggleEstado(sesion) },
                    onDeleteClick = { onEliminarSesion(sesion) }
                )
            }
        }
    }

    if (showAddSessionDialog) {
        AddSessionDialog(
            onDismiss = { showAddSessionDialog = false },
            onConfirm = { escena, hora, formato, camara ->
                onAgregarSesion(escena, hora, formato, camara)
                showAddSessionDialog = false
            }
        )
    }
}

@Composable
fun InteractiveSessionCard(
    sesion: SesionRodaje,
    onCardClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    // Configurar colores visuales según el estado interactivo de la grabación
    val (statusColor, statusBg, statusLabel, pulseIcon) = when (sesion.estado) {
        "Grabando" -> Quad(NeonRed, NeonRed.copy(alpha = 0.15f), "GRABANDO 🔴", true)
        "Grabado" -> Quad(EmeraldGreen, EmeraldGreen.copy(alpha = 0.15f), "GRABADO ✅", false)
        "Problema" -> Quad(GoldAccent, GoldAccent.copy(alpha = 0.15f), "RETOMA ⚠️", false)
        else -> Quad(LightGray, Color.White.copy(alpha = 0.05f), "PENDIENTE", false)
    }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .border(
                width = if (sesion.estado == "Grabando") 1.5.dp else 0.5.dp,
                color = statusColor.copy(alpha = 0.6f),
                shape = RoundedCornerShape(10.dp)
            )
            .testTag("scene_card_${sesion.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Contenido textual
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Estado visual interactivo
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            color = statusColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = sesion.horaInicio,
                        color = LightGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = sesion.nombreEscena,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftWhite
                )

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Videocam, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(sesion.camaraPrincipal, color = LightGray, fontSize = 11.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Settings, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(sesion.formato, color = LightGray, fontSize = 11.sp)
                    }
                }
            }

            // Acciones a la derecha
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Indicador pulsante REC si está grabando
                if (pulseIcon) {
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.2f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "icon_alpha"
                    )
                    Icon(
                        imageVector = Icons.Filled.PlayArrow, // Icono estándar compatible
                        contentDescription = "REC activo",
                        tint = NeonRed.copy(alpha = alpha),
                        modifier = Modifier
                            .size(28.dp)
                            .padding(end = 4.dp)
                    )
                }

                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.testTag("delete_scene_btn_${sesion.id}")
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar escena", tint = LightGray.copy(alpha = 0.6f))
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = SlateCard,
            title = { Text("¿Eliminar Escena?", color = SoftWhite) },
            text = { Text("¿Seguro que deseas eliminar la escena '${sesion.nombreEscena}'? Esto alterará el plan de rodaje.", color = LightGray) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = NeonRed)
                ) {
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar", color = SoftWhite)
                }
            }
        )
    }
}

data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

// ==========================================
// PESTAÑA 3: LOGÍSTICA & EQUIPOS
// ==========================================
@Composable
fun LogisticsTab(
    selectedProyecto: Proyecto?,
    checklistItems: List<ChecklistItem>,
    onToggleItem: (ChecklistItem, Boolean) -> Unit,
    onAgregarItem: (String, String) -> Unit,
    onEliminarItem: (ChecklistItem) -> Unit
) {
    var showAddItemDialog by remember { mutableStateOf(false) }

    if (selectedProyecto == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(64.dp), tint = LightGray)
            Spacer(modifier = Modifier.height(16.dp))
            Text("No hay proyecto seleccionado", color = SoftWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("Selecciona o crea un proyecto en la pestaña de Proyectos para administrar su logística.", color = LightGray, fontSize = 13.sp, textAlign = TextAlign.Center)
        }
        return
    }

    Scaffold(
        containerColor = SlateDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddItemDialog = true },
                containerColor = GoldAccent,
                contentColor = SlateDark,
                modifier = Modifier.testTag("add_gear_fab")
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir Equipo")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(GoldAccent)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CHECKLIST DE LOGÍSTICA Y EQUIPAMIENTO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftWhite,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Proyecto: ${selectedProyecto.nombre}",
                        fontSize = 14.sp,
                        color = LightGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "💡 Controla que todo el material esté en el camión de TCT antes de salir a rodar en locación.",
                        fontSize = 11.sp,
                        color = GoldAccent,
                        lineHeight = 15.sp
                    )
                }
            }

            val categorias = listOf("Cámara", "Audio", "Iluminación", "General")
            categorias.forEach { categoria ->
                val itemsCategoria = checklistItems.filter { it.categoria == categoria }

                item {
                    Text(
                        text = categoria.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (itemsCategoria.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SlateCard.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Sin items para $categoria. Añade usando el botón (+).",
                                color = LightGray,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                } else {
                    items(itemsCategoria) { itemCheck ->
                        LogisticsItemCard(
                            item = itemCheck,
                            onToggle = { onToggleItem(itemCheck, it) },
                            onDelete = { onEliminarItem(itemCheck) }
                        )
                    }
                }
            }
        }
    }

    if (showAddItemDialog) {
        AddLogisticsItemDialog(
            onDismiss = { showAddItemDialog = false },
            onConfirm = { item, cat ->
                onAgregarItem(item, cat)
                showAddItemDialog = false
            }
        )
    }
}

@Composable
fun LogisticsItemCard(
    item: ChecklistItem,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (item.completado) SlateCard.copy(alpha = 0.5f) else SlateCard
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = if (item.completado) EmeraldGreen.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp)
            )
            .testTag("gear_item_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = item.completado,
                    onCheckedChange = onToggle,
                    colors = CheckboxDefaults.colors(
                        checkedColor = EmeraldGreen,
                        uncheckedColor = LightGray,
                        checkmarkColor = SlateDark
                    ),
                    modifier = Modifier.testTag("gear_checkbox_${item.id}")
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.item,
                    fontSize = 14.sp,
                    color = if (item.completado) LightGray else SoftWhite,
                    textDecoration = if (item.completado) TextDecoration.LineThrough else TextDecoration.None,
                    fontWeight = if (item.completado) FontWeight.Normal else FontWeight.Medium
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_gear_btn_${item.id}")
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Borrar item",
                    tint = LightGray.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ==========================================
// PESTAÑA 4: APRENDIZAJE (GUÍA & QUIZ TRIVIA)
// ==========================================
@Composable
fun LearningTab() {
    var selectedConcept by remember { mutableStateOf<AudiovisualConcept?>(null) }
    var score by remember { mutableStateOf(0) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var quizAnswered by remember { mutableStateOf(false) }

    val concepts = listOf(
        AudiovisualConcept(
            titulo = "🎬 La Claqueta en Set",
            resumen = "Sincroniza audio y video de manera infalible en postproducción.",
            descripcion = "La claqueta no es solo un adorno. Sirve para dar una señal visual clara (el golpe) y una señal auditiva instantánea (el 'clack') que permite al editor alinear perfectamente la pista de la grabadora externa de audio con la pista de video de la cámara. Además, rotula la escena, plano y toma para ordenar el material."
        ),
        AudiovisualConcept(
            titulo = "🎤 Audio Limpio (-12dB)",
            resumen = "Monitorear niveles en set para evitar la saturación destructiva.",
            descripcion = "En el audio digital, superar los 0dB provoca clipping (saturación digital destructiva que arruina el sonido). Un estándar profesional en el set de Corporación TCT es monitorear los niveles de voz para que promenien entre -18dB y -12dB, dejando un 'headroom' saludable para gritos o subidas de tono inesperadas."
        ),
        AudiovisualConcept(
            titulo = "💡 Esquema de Tres Puntos",
            resumen = "Estructura básica para iluminar rostros y entrevistas.",
            descripcion = "Se compone de:\n1. Luz Principal (Key Light): Ilumina el rostro con mayor intensidad, usualmente a 45 grados de la cámara.\n2. Luz de Relleno (Fill Light): Suaviza las sombras duras de la luz principal.\n3. Luz de Recorte (Backlight / Contra): Se coloca detrás para perfilar al personaje y separarlo del fondo."
        ),
        AudiovisualConcept(
            titulo = "🎥 Regla de 180° (Eje Cinematográfico)",
            resumen = "Mantener la orientación visual correcta entre dos personajes.",
            descripcion = "Al filmar una conversación, se traza una línea imaginaria (eje) entre los personajes. La cámara debe quedarse siempre del mismo lado de esa línea. Cruzar el eje (saltarse la regla) confunde al espectador, ya que parecerá que los personajes miran en la misma dirección en lugar de mirarse de frente."
        )
    )

    val questions = listOf(
        QuizQuestion(
            pregunta = "¿Cuál es el nivel promedio ideal de audio digital en set para evitar saturación?",
            opciones = listOf("A) Sobrepasar los +6dB", "B) Entre -18dB y -12dB", "C) Silenciarlo a -80dB", "D) Siempre a 0dB exactos"),
            correctIndex = 1,
            explicacion = "¡Exacto! El rango de -18dB a -12dB otorga el headroom necesario para que las voces no saturen destructivamente si hay picos inesperados."
        ),
        QuizQuestion(
            pregunta = "¿Para qué sirve el golpe físico de una claqueta en la grabación de TCT?",
            opciones = listOf("A) Para despertar al crew", "B) Para asustar a los actores", "C) Sincronizar audio y video en edición", "D) Como marcador de descanso"),
            correctIndex = 2,
            explicacion = "¡Genial! Sincroniza perfectamente el pico físico visual del choque de la madera con el pico audible de audio en la línea de tiempo."
        ),
        QuizQuestion(
            pregunta = "En iluminación, ¿cuál es la función principal de la 'Luz de Recorte' (Contra)?",
            opciones = listOf("A) Quemar el fondo", "B) Suavizar arrugas", "C) Separar al sujeto del fondo perfilando su silueta", "D) Reemplazar la luz del sol"),
            correctIndex = 2,
            explicacion = "¡Buena! La contra (backlight) genera un perfil de luz en los hombros y cabello, separando visualmente al personaje del fondo de manera tridimensional."
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CENTRO DE APRENDIZAJE INTERACTIVO",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftWhite,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "Capacitación rápida de set para asistentes de dirección e iluminación de TCT.",
                    fontSize = 13.sp,
                    color = LightGray
                )
            }
        }

        // Listado de Conceptos Expandibles
        item {
            Text(
                text = "GLOSARIO RÁPIDO DE SET",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent,
                letterSpacing = 1.5.sp
            )
        }

        items(concepts) { concept ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedConcept = concept }
                    .border(width = 0.5.dp, color = Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(10.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(concept.titulo, color = SoftWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(concept.resumen, color = LightGray, fontSize = 12.sp)
                    }
                    Icon(Icons.Filled.ArrowForward, contentDescription = "Ver más", tint = GoldAccent)
                }
            }
        }

        // TRIVIA DE APRENDIZAJE ACTIVO
        item {
            Text(
                text = "MINI-QUIZ DE AUTOAPRENDIZAJE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        item {
            val q = questions[currentQuestionIndex]
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 0.5.dp, color = Color.White.copy(alpha = 0.12f), shape = RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PREGUNTA ${currentQuestionIndex + 1} de ${questions.size}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Puntaje: $score",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = q.pregunta,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftWhite,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Opciones de respuesta interactiva
                    q.opciones.forEachIndexed { idx, opcion ->
                        val optionBg = when {
                            quizAnswered && idx == q.correctIndex -> EmeraldGreen.copy(alpha = 0.18f)
                            quizAnswered && idx == selectedOptionIndex && idx != q.correctIndex -> NeonRed.copy(alpha = 0.18f)
                            idx == selectedOptionIndex -> GoldAccent.copy(alpha = 0.12f)
                            else -> Color.White.copy(alpha = 0.03f)
                        }

                        val optionBorder = when {
                            quizAnswered && idx == q.correctIndex -> EmeraldGreen
                            quizAnswered && idx == selectedOptionIndex && idx != q.correctIndex -> NeonRed
                            idx == selectedOptionIndex -> GoldAccent
                            else -> Color.White.copy(alpha = 0.08f)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(optionBg)
                                .border(0.5.dp, optionBorder, RoundedCornerShape(8.dp))
                                .clickable(enabled = !quizAnswered) {
                                    selectedOptionIndex = idx
                                }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = opcion,
                                color = if (quizAnswered && idx == q.correctIndex) EmeraldGreen else SoftWhite,
                                fontSize = 13.sp,
                                fontWeight = if (idx == selectedOptionIndex || (quizAnswered && idx == q.correctIndex)) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    // Respuesta evaluada
                    if (quizAnswered) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedOptionIndex == q.correctIndex) EmeraldGreen.copy(alpha = 0.08f) else NeonRed.copy(alpha = 0.08f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (selectedOptionIndex == q.correctIndex) "¡Buenazo! 🇵🇪" else "¡Pucha, casi! 🇵🇪",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedOptionIndex == q.correctIndex) EmeraldGreen else GoldAccent
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = q.explicacion,
                                    fontSize = 12.sp,
                                    color = LightGray,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                // Siguiente pregunta
                                if (currentQuestionIndex < questions.size - 1) {
                                    currentQuestionIndex++
                                } else {
                                    // Reiniciar
                                    currentQuestionIndex = 0
                                    score = 0
                                }
                                selectedOptionIndex = null
                                quizAnswered = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (currentQuestionIndex < questions.size - 1) "Siguiente Pregunta" else "Volver a Jugar",
                                color = SlateDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                if (selectedOptionIndex != null) {
                                    quizAnswered = true
                                    if (selectedOptionIndex == q.correctIndex) {
                                        score += 10
                                    }
                                }
                            },
                            enabled = selectedOptionIndex != null,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Comprobar Respuesta", color = if (selectedOptionIndex != null) SlateDark else LightGray, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Diálogo informativo para los conceptos del glosario
    if (selectedConcept != null) {
        Dialog(onDismissRequest = { selectedConcept = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 0.5.dp, color = GoldAccent, shape = RoundedCornerShape(14.dp))
                    .padding(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = selectedConcept?.titulo ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = selectedConcept?.descripcion ?: "",
                        fontSize = 14.sp,
                        color = SoftWhite,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { selectedConcept = null },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        modifier = Modifier.align(Alignment.End),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Entendido", color = SlateDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

data class AudiovisualConcept(val titulo: String, val resumen: String, val descripcion: String)
data class QuizQuestion(val pregunta: String, val opciones: List<String>, val correctIndex: Int, val explicacion: String)

// ==========================================
// FORMULARIOS DE CREACIÓN (DIALOGS INTERACTIVOS)
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (nombre: String, cliente: String, descripcion: String, locacion: String, estado: String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var cliente by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var locacion by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("Preproducción") }
    var expandedEstado by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 0.5.dp, color = Color.White.copy(alpha = 0.12f), shape = RoundedCornerShape(14.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "NUEVO PROYECTO AUDIOVISUAL",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent,
                    letterSpacing = 1.sp
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del proyecto (e.g. Spot Verano TCT)", color = LightGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SoftWhite,
                        unfocusedTextColor = SoftWhite,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_project_name_field")
                )

                OutlinedTextField(
                    value = cliente,
                    onValueChange = { cliente = it },
                    label = { Text("Cliente / Entidad (e.g. PromPerú)", color = LightGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SoftWhite,
                        unfocusedTextColor = SoftWhite,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_project_client_field")
                )

                OutlinedTextField(
                    value = locacion,
                    onValueChange = { locacion = it },
                    label = { Text("Locación (e.g. Lima o Chanchamayo)", color = LightGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SoftWhite,
                        unfocusedTextColor = SoftWhite,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_project_location_field")
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción literaria o metas de producción", color = LightGray) },
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SoftWhite,
                        unfocusedTextColor = SoftWhite,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_project_desc_field")
                )

                // Dropdown de fase
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = estado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fase de Producción", color = LightGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SoftWhite,
                            unfocusedTextColor = SoftWhite,
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                        ),
                        trailingIcon = {
                            Icon(Icons.Filled.ArrowDropDown, "dropdown", tint = SoftWhite)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedEstado = true }
                    )
                    DropdownMenu(
                        expanded = expandedEstado,
                        onDismissRequest = { expandedEstado = false },
                        modifier = Modifier.background(SlateCard)
                    ) {
                        listOf("Preproducción", "Rodaje", "Postproducción", "Completado").forEach { est ->
                            DropdownMenuItem(
                                text = { Text(est, color = SoftWhite) },
                                onClick = {
                                    estado = est
                                    expandedEstado = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = SoftWhite)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (nombre.isNotBlank() && cliente.isNotBlank()) {
                                onConfirm(nombre, cliente, descripcion, locacion, estado)
                            }
                        },
                        enabled = nombre.isNotBlank() && cliente.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("confirm_add_project_btn")
                    ) {
                        Text("Registrar Proyecto", color = SlateDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddSessionDialog(
    onDismiss: () -> Unit,
    onConfirm: (escena: String, hora: String, formato: String, camara: String) -> Unit
) {
    var escena by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("08:00 AM") }
    var formato by remember { mutableStateOf("4K 24fps S-Log3") }
    var camara by remember { mutableStateOf("Sony FX3") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 0.5.dp, color = Color.White.copy(alpha = 0.12f), shape = RoundedCornerShape(14.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "NUEVA ESCENA PARA EL PLAN DE RODAJE",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent,
                    letterSpacing = 1.sp
                )

                OutlinedTextField(
                    value = escena,
                    onValueChange = { escena = it },
                    label = { Text("Nombre o Descripción de la Toma (e.g. Escena 2: Entrevista)", color = LightGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SoftWhite,
                        unfocusedTextColor = SoftWhite,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_scene_name_field")
                )

                OutlinedTextField(
                    value = hora,
                    onValueChange = { hora = it },
                    label = { Text("Hora programada (e.g. 10:30 AM)", color = LightGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SoftWhite,
                        unfocusedTextColor = SoftWhite,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_scene_time_field")
                )

                OutlinedTextField(
                    value = camara,
                    onValueChange = { camara = it },
                    label = { Text("Cámara principal (e.g. Sony FX3, RED Komodo)", color = LightGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SoftWhite,
                        unfocusedTextColor = SoftWhite,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_scene_camera_field")
                )

                OutlinedTextField(
                    value = formato,
                    onValueChange = { formato = it },
                    label = { Text("Formato de Grabación (e.g. 4K 120fps)", color = LightGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SoftWhite,
                        unfocusedTextColor = SoftWhite,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_scene_format_field")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = SoftWhite)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (escena.isNotBlank()) {
                                onConfirm(escena, hora, formato, camara)
                            }
                        },
                        enabled = escena.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("confirm_add_scene_btn")
                    ) {
                        Text("Añadir Escena", color = SlateDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLogisticsItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (item: String, categoria: String) -> Unit
) {
    var item by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Cámara") }
    var expandedCat by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 0.5.dp, color = Color.White.copy(alpha = 0.12f), shape = RoundedCornerShape(14.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "AÑADIR MATERIAL AL CHECKLIST",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent,
                    letterSpacing = 1.sp
                )

                OutlinedTextField(
                    value = item,
                    onValueChange = { item = it },
                    label = { Text("Nombre del equipo (e.g. Trípode Sachtler)", color = LightGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SoftWhite,
                        unfocusedTextColor = SoftWhite,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_gear_name_field")
                )

                // Dropdown de Categoría
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría de Equipamiento", color = LightGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SoftWhite,
                            unfocusedTextColor = SoftWhite,
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                        ),
                        trailingIcon = {
                            Icon(Icons.Filled.ArrowDropDown, "dropdown", tint = SoftWhite)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedCat = true }
                    )
                    DropdownMenu(
                        expanded = expandedCat,
                        onDismissRequest = { expandedCat = false },
                        modifier = Modifier.background(SlateCard)
                    ) {
                        listOf("Cámara", "Audio", "Iluminación", "General").forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, color = SoftWhite) },
                                onClick = {
                                    categoria = cat
                                    expandedCat = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = SoftWhite)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (item.isNotBlank()) {
                                onConfirm(item, categoria)
                            }
                        },
                        enabled = item.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("confirm_add_gear_btn")
                    ) {
                        Text("Añadir", color = SlateDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
