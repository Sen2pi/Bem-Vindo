package pt.karimp.bem_vindo.paginas

// Compose Foundation
import android.content.Context
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.*
import android.media.MediaPlayer
import androidx.compose.runtime.remember
// Compose Material3
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.buttonColors

// Compose Runtime
import androidx.compose.runtime.*

// Compose UI
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.res.painterResource

// Firebase Imports
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Navigation
import androidx.navigation.NavController

// Coroutines
import kotlinx.coroutines.tasks.await

// Meu Projeto
import pt.karimp.bem_vindo.API.getTranslations
import pt.karimp.bem_vindo.API.LanguageSelector
import pt.karimp.bem_vindo.R
import pt.karimp.bem_vindo.models.Nivel
import pt.karimp.bem_vindo.models.User
import pt.karimp.bem_vindo.ui.theme.BottomNavBar

@Composable
fun Aprender(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var currentUserDocumentId by remember { mutableStateOf("") }
    val context = LocalContext.current
    var nivelDocumentId by remember { mutableStateOf("") }
    // Estados
    var niveis by remember { mutableStateOf<List<Nivel>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var userData by remember { mutableStateOf<User?>(null) }
    var professorData by remember { mutableStateOf<User?>(null) }
    var selectedLanguage by remember { mutableStateOf("fr") } // Idioma inicial em Francês
    val translations =
        getTranslations(selectedLanguage) // Obter traduções com base no idioma selecionado
    val currentUser = auth.currentUser
    val currentEmail = currentUser?.email

    // Função para carregar dados do usuário e níveis
    LaunchedEffect(Unit) {
        try {
            loading = true

            // Carregar dados do usuário
            if (currentEmail != null) {
                val userDoc = db.collection("users")
                    .whereEqualTo("email", currentEmail)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()

                if (userDoc != null) {
                    currentUserDocumentId = userDoc.id
                    userData = userDoc.toObject(User::class.java)
                    val professorId = userDoc.getString("professor")

                    // Carregar dados do professor (se existir)
                    if (!professorId.isNullOrEmpty()) {
                        val professorDoc = db.collection("users")
                            .document(professorId)
                            .get()
                            .await()
                        professorData = professorDoc.toObject(User::class.java)
                    }
                }
            }

            // Carregar níveis
            val querySnapshot = db.collection("niveis").get().await()
            niveis = querySnapshot.documents
                .mapNotNull { it.toObject(Nivel::class.java) }
                .sortedBy { it.nivel } // Ordena os níveis pelo número
        } catch (e: Exception) {
            error = "Erro ao carregar dados: ${e.message}"
        } finally {
            loading = false
        }
    }

    Scaffold(
        topBar = {  // Icons in the top right corner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFA1B8CC)),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.logo_final1),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.TopCenter,
                    modifier = Modifier
                        .size(75.dp)
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 30.dp, end = 15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Language Selector
                LanguageSelector(
                    selectedLanguage = selectedLanguage,
                    onLanguageSelected = { selectedLanguage = it }
                )

                Spacer(modifier = Modifier.width(5.dp)) // Espaçamento entre os ícones

                // Profile Icon
                IconButton(onClick = { navController.navigate("profile") }) {
                    Icon(
                        painter = painterResource(id = R.mipmap.ic_perfil),
                        contentDescription = "Profile",
                        modifier = Modifier.size(50.dp),
                        tint = Color.Unspecified
                    )
                }

                Spacer(modifier = Modifier.width(5.dp)) // Espaçamento entre os ícones

                // Logoff Icon
                IconButton(onClick = { navController.navigate("login") }) {
                    Icon(
                        painter = painterResource(id = R.mipmap.ic_logout),
                        contentDescription = "Logoff",
                        modifier = Modifier.size(50.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        },
        bottomBar = { BottomNavBar(navController = navController, currentUserDocumentId) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Image(
            painter = painterResource(id = R.mipmap.azulejo1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.60f)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_caravela),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
            when {
                loading -> CircularProgressIndicator()
                error != null -> Text(error ?: "Erro desconhecido", color = Color.Red)
                niveis.isNotEmpty() -> NivelList(navController, niveis, userData, translations)
                else -> Text("Nenhum nível encontrado.", color = Color.Gray)
            }
        }
    }
}

@Composable
fun NivelList(
    navController: NavController,
    niveis: List<Nivel>,
    userData: User?,
    translations: Map<String, String>
) {
    val gridColumns = 3 // Número de colunas na grid
    val cellSize = 133.dp // Tamanho de cada botão
    val spacing = 20.dp // Espaçamento entre os itens
    val db = FirebaseFirestore.getInstance()

    // State para armazenar os documentos de nível carregados
    var nivelDocs by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }

    // Efeito para carregar dados do Firestore
    LaunchedEffect(niveis) {
        // Fetch os documentos do Firestore de uma vez
        val docMap = niveis.associate { nivel ->
            val nivelDoc = db.collection("niveis")
                .whereEqualTo("nivel", nivel.nivel)
                .get()
                .await()
                .documents
                .firstOrNull()
            nivel.nivel to nivelDoc?.id.orEmpty() // Associe o nível ao seu ID
        }
        nivelDocs = docMap // Atualize o estado com os IDs dos documentos
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        val listState = rememberLazyGridState()

        LazyVerticalGrid(
            state = listState,
            columns = GridCells.Fixed(gridColumns),
            verticalArrangement = Arrangement.spacedBy(spacing),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            modifier = Modifier.fillMaxSize()
        ) {
            items(niveis, key = { it.nivel }) { nivel ->
                val completado = (userData?.aprender ?: 0) >= nivel.nivel
                val nivelDocId = nivelDocs[nivel.nivel]
                NivelItem(nivel, completado, userData, translations) {
                    if (nivelDocId != null) {
                        navController.navigate("nivel/$nivelDocId")
                    }
                }
            }
        }
    }
}


@Composable
fun NivelItem(
    nivel: Nivel,
    completado: Boolean,
    userData: User?,
    translations: Map<String, String>,
    context: Context = LocalContext.current,
    onClick: () -> Unit
) {
    val desbloqueado = (userData?.aprender ?: 0) + 1 >= nivel.nivel
    val corBotao = when {
        completado -> Color(0xFF4CAF50) // Verde se completado
        desbloqueado -> Color(0xFFE9BD0D) // Amarelo se desbloqueado
        else -> Color(0xFFB0BEC5) // Cinza se bloqueado
    }

    // Controle da animação tipo radar
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing), // Duração de 2 segundos
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(contentAlignment = Alignment.Center) {
        // Radar animation only when unlocked and not completed
        if (desbloqueado && !completado) {
            Canvas(
                modifier = Modifier
                    .size(150.dp)
            ) {
                drawCircle(
                    color = Color(0xFFE9BD0D),
                    radius = size.minDimension / 2 * scale,
                    alpha = alpha
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = {
                    if (desbloqueado) {
                        onClick();
                        playStopSound(R.raw.button_pressed, context);
                    }
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = corBotao,
                    disabledContainerColor = corBotao
                ),
                enabled = desbloqueado // Botão desativado se bloqueado
            ) {
                if (nivel.final) {
                    Icon(
                        painter = painterResource(id = R.mipmap.ic_level),
                        contentDescription = "${translations["nivel"]} ${nivel.nivel}",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(100.dp)
                    )
                } else {
                    if (nivel.dificuldade == 1) {
                        Icon(
                            painter = painterResource(id = R.mipmap.ic_nata),
                            contentDescription = "${translations["nivel"]} ${nivel.nivel}",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(100.dp)
                        )
                    } else if(nivel.dificuldade == 2){
                        Icon(
                            painter = painterResource(id = R.mipmap.ic_nivel1),
                            contentDescription = "${translations["nivel"]} ${nivel.nivel}",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(100.dp)
                        )
                    }else if(nivel.dificuldade == 3){
                        Icon(
                            painter = painterResource(id = R.mipmap.ic_nivel2),
                            contentDescription = "${translations["nivel"]} ${nivel.nivel}",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(100.dp)
                        )
                    }else if(nivel.dificuldade == 4){
                        Icon(
                            painter = painterResource(id = R.mipmap.ic_nivel3),
                            contentDescription = "${translations["nivel"]} ${nivel.nivel}",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(100.dp)
                        )
                    }else if(nivel.dificuldade == 5){
                        Icon(
                            painter = painterResource(id = R.mipmap.ic_nivel4),
                            contentDescription = "${translations["nivel"]} ${nivel.nivel}",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(100.dp)
                        )
                    }

                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Nível ${nivel.nivel}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (desbloqueado) Color(0xFF005B7F) else Color.Gray,
            )
        }
        // Ícone do jogador apenas se desbloqueado (amarelo)
        if (desbloqueado && !completado) {
            Icon(
                painter = painterResource(id = R.mipmap.ic_player), // Ícone do jogador (caravela)
                contentDescription = "Jogador",
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = offsetX.dp), // Animação horizontal
                tint = Color.Unspecified // Mesmo tom de amarelo
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

fun playStopSound(resourceId: Int, context: Context) {
    val mediaPlayer = MediaPlayer.create(context, resourceId)
    mediaPlayer.start()
    mediaPlayer.setOnCompletionListener { it.release() }
}

