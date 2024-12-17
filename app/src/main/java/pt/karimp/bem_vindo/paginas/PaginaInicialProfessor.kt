package pt.karimp.bem_vindo.paginas

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.karimp.bem_vindo.API.LanguageSelector
import pt.karimp.bem_vindo.API.getTranslations
import pt.karimp.bem_vindo.R
import pt.karimp.bem_vindo.models.User
import pt.karimp.bem_vindo.ui.theme.ProfessorNavbar
import pt.karimp.bem_vindo.ui.theme.getUnreadMessages
import pt.karimp.bem_vindo.utils.sendNewMessageNotification

@Composable
fun PaginaInicialProfessor(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    var selectedLanguage by remember { mutableStateOf("fr") }
    val context = LocalContext.current
    val translations = getTranslations(selectedLanguage)
    var userData by remember { mutableStateOf<User?>(null) }
    var currentUserDocumentId by remember { mutableStateOf("") }
    val currentUser = auth.currentUser
    val userEmail = currentUser?.email
    val db = FirebaseFirestore.getInstance()
    var alunos by remember { mutableStateOf(listOf<User>()) }

    fun getUnreadProfessorMessages(userId: String, alunoId:String, onMessagesCountChanged: (Int) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()

        // Obtenha as mensagens não lidas para o usuário conectado
        firestore.collection("messages")
            .whereEqualTo("toUserId", userId)
            .whereEqualTo("fromUserId",alunoId)
            .whereEqualTo("read", false)
            .get()
            .addOnSuccessListener { documents ->
                // Contabiliza as mensagens não lidas
                val unreadCount = documents.size()
                onMessagesCountChanged(unreadCount)
            }
            .addOnFailureListener { exception ->
                // Handle failure
                println("Error getting unread messages: ${exception.message}")
            }
    }
    LaunchedEffect(Unit) {
        try {
            val userDocSnapshot =
                db.collection("users").whereEqualTo("email", userEmail).get().await()
            if (!userDocSnapshot.isEmpty) {
                val userDoc = userDocSnapshot.documents.first()
                userData = userDoc.toObject(User::class.java)
                currentUserDocumentId = userDoc.id

                // Buscar alunos atribuídos ao professor
                val alunosSnapshot = db.collection("users")
                    .whereEqualTo("professor", currentUserDocumentId).get().await()
                alunos = alunosSnapshot.documents.mapNotNull { it.toObject(User::class.java) }
            }
        } catch (e: Exception) {
            Log.e("FirestoreDebug", "Erro ao carregar dados: ${e.message}")
        }
    }

    Scaffold(
        topBar = {
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
                    modifier = Modifier.size(75.dp)
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 30.dp, end = 15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                LanguageSelector(
                    selectedLanguage = selectedLanguage,
                    onLanguageSelected = { selectedLanguage = it }
                )
                IconButton(onClick = { navController.navigate("profile") }) {
                    Icon(
                        painter = painterResource(id = R.mipmap.ic_perfil),
                        contentDescription = "Profile",
                        modifier = Modifier.size(50.dp),
                        tint = Color.Unspecified
                    )
                }
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
        bottomBar = { ProfessorNavbar(navController, currentUserDocumentId) },
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
                .padding(5.dp)
                .background(color = Color(0xFFA1B8CC), shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(alunos) { aluno ->
                    var unreadMessagesCount by remember { mutableStateOf(0)}
                    var alunoDocId by remember { mutableStateOf("") }
                    db.collection("users").whereEqualTo("email", aluno.email).get().addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            alunoDocId = querySnapshot.documents.first().id
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Icon(
                                painter = painterResource(id = R.mipmap.ic_aluno),
                                contentDescription = "Aluno",
                                tint = Color.Unspecified,
                            )
                            val bandeira = when (aluno.lingua) {
                                "fr" -> R.mipmap.flag_fr
                                "en" -> R.mipmap.flag_en
                                "es" -> R.mipmap.flag_es
                                else -> R.mipmap.flag_pt
                            }
                            Icon(
                                painter = painterResource(id = bandeira),
                                contentDescription = "Lingua",
                                tint = Color.Unspecified
                            )
                        }

                        Column {
                            Text(
                                "Nome: ${aluno.nome}",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFF005B7F)
                            )
                            Text(
                                "Cidade: ${aluno.cidade}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF005B7F)
                            )
                            Text(
                                "Preferência de Horário: ${aluno.preferenciaHorario}",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF005B7F)
                            )
                            ProgressSection(
                                aluno.Pontuacao,
                                "Progresso do Aluno",
                                aluno.nivel,
                                aluno.lingua
                            )
                            Row {
                                getUnreadProfessorMessages(currentUserDocumentId, alunoDocId) { count ->
                                    unreadMessagesCount = count
                                    if (unreadMessagesCount > 0) {
                                        // Dispara a notificação se houver novas mensagens não lidas
                                        sendNewMessageNotification(context)
                                    }
                                }
                                IconButton(onClick = {
                                    navController.navigate("chatProfessor/${alunoDocId}")
                                }) {
                                    Box {
                                        Icon(
                                            painter = painterResource(id = R.mipmap.chat_for),
                                            contentDescription = "Selecionar Aluno",
                                            tint = Color.Unspecified,
                                            modifier = Modifier.size(40.dp)
                                        )
                                        if (unreadMessagesCount > 0) {
                                            Badge(
                                                modifier = Modifier
                                                    .align(Alignment.TopStart)
                                                    .padding(start = 4.dp, top = 4.dp)
                                            ) {
                                                Text(text = unreadMessagesCount.toString())
                                            }
                                        }
                                    }
                                }
                                IconButton(onClick = {
                                    navController.navigate("markClass/${alunoDocId}")
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.mipmap.ic_naula),
                                        contentDescription = "Selecionar Aluno",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                        }
                    }

                    Divider(
                        color = Color(0xFF005B7F), // Color of the line
                        thickness = 2.dp,    // Thickness of the line
                    )
                }

            }

        }
    }
}



