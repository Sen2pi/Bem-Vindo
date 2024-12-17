package pt.karimp.bem_vindo.paginas

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
import pt.karimp.bem_vindo.ui.theme.topNavBar

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
        topBar = {  // Icons in the top right corner
            Row (modifier = Modifier
                .fillMaxWidth().background(color = Color(0xFFA1B8CC)),
            horizontalArrangement = Arrangement.Center){
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
                horizontalArrangement = Arrangement.End,

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
            } },
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
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            // Background Image




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
                    ?: "", selectedLanguage)

                if(professorData!=null){
                // Tutor Section
                TutorSection(
                    title = translations["tutor_title"]!!,
                    tutorName = "${professorData?.nome}",
                    city = "${professorData?.cidade}",
                    email = "${professorData?.email}"
                )
            }
                // Daily Phrase Section
                DailyPhrase(
                    title = translations["daily_phrase_title"]!!,
                    chosenLanguage = selectedLanguage,
                    userDocumentID = currentUserDocumentId
                )
            }
        }
    }
}

@Composable
fun ProgressSection(progress: Int, title: String, nivel: String, selectedLanguage: String) {
    val translations = getTranslations(selectedLanguage) // Obter traduções com base no idioma selecionado
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFA1B8CC), shape = RoundedCornerShape(12.dp)) ,// Define o fundo com cantos arredondados
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(8.dp),
            color = Color(0xFF005B7F)
        )
        LinearProgressIndicator(
            progress = progress / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(35.dp)
                .padding(15.dp),
            color = Color(0xFF005B7F),
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
        Text(
            text = "$progress%",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp),
            color = Color(0xFF005B7F)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            when (nivel) {
                "1" -> Image(
                    painter = painterResource(id = R.mipmap.ic_basico), // Exemplo com o ícone customizado
                    contentDescription = "Básico",
                    modifier = Modifier.size(50.dp)
                )
                "2" -> Image(
                    painter = painterResource(id = R.mipmap.ic_iniciante),
                    contentDescription = "Iniciante",
                    modifier = Modifier.size(50.dp)
                )
                "3" -> Image(
                    painter = painterResource(id = R.mipmap.ic_advanced), // Exemplo
                    contentDescription = "Avançado",
                    modifier = Modifier.size(50.dp)
                )
                "4" -> Image(
                    painter = painterResource(id = R.mipmap.ic_mundial), // Exemplo
                    contentDescription = "Nivel Mundial",
                    modifier = Modifier.size(50.dp)
                )
                "5" -> Image(
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
                text = "${if(nivel == "1" ) translations["basico"] else if(nivel == "2" ) translations["iniciante"] else if(nivel == "3" ) translations["avancado"] else if(nivel == "4" ) translations["niv_mundial"] else if(nivel == "5" ) translations["professional"] else translations["desconhecido"]}",
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
                color = Color(0xFF005B7F)
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
                    Text(text = "Nom: $tutorName", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF005B7F))
                    Text(text = "Ville: $city", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF005B7F))
                    Text(text = "Email: $email", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF005B7F))
                }
            }
        }
    }
}




