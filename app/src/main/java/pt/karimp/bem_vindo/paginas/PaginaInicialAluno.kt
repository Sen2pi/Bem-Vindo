package pt.karimp.bem_vindo.paginas

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.karimp.bem_vindo.API.LanguageSelector
import pt.karimp.bem_vindo.API.getTranslations
import pt.karimp.bem_vindo.R

import pt.karimp.bem_vindo.ui.theme.BottomNavBar
import pt.karimp.bem_vindo.models.User

@Composable
fun PaginaInicialAluno(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    var selectedLanguage by remember { mutableStateOf("fr") } // Idioma inicial em Francês
    val translations = getTranslations(selectedLanguage) // Obter traduções com base no idioma selecionado
    var userData by remember { mutableStateOf<User?>(null) }
    var professorData by remember { mutableStateOf<User?>(null) }
    var currentUserDocumentId by remember { mutableStateOf("") }
    var professorDocumentId by remember { mutableStateOf("") }
    val currentUser = auth.currentUser
    val userEmail = FirebaseAuth.getInstance().currentUser?.email
    val db = FirebaseFirestore.getInstance()
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isEditing by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        try {
            val userDocSnapshot =
                db.collection("users").whereEqualTo("email", userEmail).get().await()

            if (!userDocSnapshot.isEmpty) {
                val userDoc = userDocSnapshot.documents.first()
                userData = userDoc.toObject(User::class.java)
                currentUserDocumentId = userDoc.id
                professorDocumentId = userDoc.getString("professor") ?: ""
            }
        } catch (e: Exception) {
            Log.e("FirestoreDebug", "Erro ao carregar usuário: ${e.message}")
        }
    }

    LaunchedEffect(professorDocumentId) {
        try {
            val documentSnapshot = FirebaseFirestore.getInstance()
                .collection("users")
                .document(professorDocumentId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                professorData = documentSnapshot.toObject(User::class.java)
            } else {
                error = "Usuário não encontrado"
            }
        } catch (e: Exception) {
            Log.e("FirestoreDebug", "Erro ao carregar professor: ${e.message}")
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.mipmap.azulejo1),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.60f)
            )

            // Icons in the top right corner
            Row(
                modifier = Modifier
                    .padding(16.dp)
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

            // Content Section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(25.dp),
                verticalArrangement = Arrangement.spacedBy(25.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.size(50.dp))
                // Progress Section
                ProgressSection(progress = userData?.progresso ?: 0, title = translations["progress_title"]!!, nivel = userData?.nivel
                    ?: "")

                // Tutor Section
                TutorSection(
                    title = translations["tutor_title"]!!,
                    tutorName = "${professorData?.nome}",
                    city = "${professorData?.cidade}",
                    email = "${professorData?.email}"
                )

                // Daily Phrase Section
                DailyPhrase(
                    title = translations["daily_phrase_title"]!!,
                    chosenLanguage = selectedLanguage
                )
            }
        }
    }
}

@Composable
fun ProgressSection(progress: Int, title: String, nivel: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp),
            color = Color(0xFF005B7F)
        )
        LinearProgressIndicator(
            progress = progress / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
        Text(
            text = "$progress%",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
            color = Color(0xFF005B7F)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            when (nivel) {
                "Básico" -> Image(
                    painter = painterResource(id = R.mipmap.ic_basico), // Exemplo com o ícone customizado
                    contentDescription = "Básico",
                    modifier = Modifier.size(50.dp)
                )
                "Iniciante" -> Image(
                    painter = painterResource(id = R.mipmap.ic_iniciante),
                    contentDescription = "Iniciante",
                    modifier = Modifier.size(50.dp)
                )
                "Avançado" -> Image(
                    painter = painterResource(id = R.mipmap.ic_advanced), // Exemplo
                    contentDescription = "Avançado",
                    modifier = Modifier.size(50.dp)
                )
                "Nivel Mundial" -> Image(
                    painter = painterResource(id = R.mipmap.ic_mundial), // Exemplo
                    contentDescription = "Nivel Mundial",
                    modifier = Modifier.size(50.dp)
                )
                "Profissional" -> Image(
                    painter = painterResource(id = R.mipmap.professional_foreground), // Exemplo
                    contentDescription = "Profissional",
                    modifier = Modifier.size(50.dp)
                )
                else -> Image(
                    painter = painterResource(id = R.mipmap.ic_basico), // Caso o nível não seja reconhecido
                    contentDescription = "Desconhecido",
                    modifier = Modifier.size(50.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$nivel",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF005B7F)
            )
        }
    }
}

@Composable
fun TutorSection(title: String, tutorName: String, city: String, email: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFA1B8CC))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_professor),
                    contentDescription = "Tutor Image",
                    modifier = Modifier.size(100.dp)
                )
                Column {
                    Text(text = "Nom: $tutorName", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                    Text(text = "Ville: $city", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                    Text(text = "Email: $email", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                }
            }
        }
    }
}




