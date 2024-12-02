package pt.karimp.bem_vindo.paginas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import pt.karimp.bem_vindo.API.LanguageSelector
import pt.karimp.bem_vindo.API.getTranslations
import pt.karimp.bem_vindo.R
import pt.karimp.bem_vindo.ui.theme.BottomNavBar
import android.media.MediaPlayer
import androidx.compose.animation.*
import androidx.compose.foundation.lazy.rememberLazyListState
import pt.karimp.bem_vindo.models.Message
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch


@Composable
fun Chat(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    var messageSent by remember { mutableStateOf(false) }
    val currentUser = auth.currentUser
    val db = FirebaseFirestore.getInstance()
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    val userEmail = FirebaseAuth.getInstance().currentUser?.email
    var messageText by remember { mutableStateOf(TextFieldValue("")) }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var userData by remember { mutableStateOf<User?>(null) }
    var professorData by remember { mutableStateOf<User?>(null) }
    var currentUserDocumentId by remember { mutableStateOf("") }
    var professorDocumentId by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("fr") }
    val translations = getTranslations(selectedLanguage)
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    suspend fun loadMessages(db: FirebaseFirestore, currentUserId: String) {
        try {

            val sentMessagesQuery = db.collection("messages")
                .whereEqualTo("fromUserId", currentUserId)
                .get().await()


            val receivedMessagesQuery = db.collection("messages")
                .whereEqualTo("toUserId", currentUserId)
                .get().await()


            val allMessages = mutableListOf<Message>()


            allMessages.addAll(sentMessagesQuery.documents.map { document ->
                document.toObject(Message::class.java) ?: Message()
            })

            allMessages.addAll(receivedMessagesQuery.documents.map { document ->
                document.toObject(Message::class.java) ?: Message()
            })

            messages = allMessages.sortedBy { it.timestamp }

            coroutineScope.launch {
                lazyListState.scrollToItem(messages.size - 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    LaunchedEffect(professorDocumentId) {
        try {
            // Chamada assíncrona com corrotinas
            val documentSnapshot = FirebaseFirestore.getInstance()
                .collection("users")
                .document(professorDocumentId)
                .get()
                .await()  // Usando .await() para esperar o resultado de forma suspensa

            if (documentSnapshot.exists()) {
                professorData = documentSnapshot.toObject(User::class.java)
            } else {
                error = "Usuário não encontrado"
            }
        } catch (e: Exception) {
            error = "Erro ao carregar dados: ${e.message}"
        } finally {
            loading = false
        }
    }
    LaunchedEffect(Unit) {
        if (currentUser != null) {
            try {
                val userDocSnapshot =
                    db.collection("users").whereEqualTo("email", currentUser.email).get().await()
                if (!userDocSnapshot.isEmpty) {
                    val userDoc = userDocSnapshot.documents.first()
                    currentUserDocumentId = userDoc.id
                    professorDocumentId = userDoc.getString("professor") ?: ""
                    val userType = userDoc.getString("tipo") ?: ""

                    if (userType == "Aluno") {

                        db.collection("users")
                            .whereEqualTo("email", userEmail)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                if (!querySnapshot.isEmpty) {
                                    val document = querySnapshot.documents.first()
                                    userData = document.toObject(User::class.java)
                                } else {
                                    error = "Usuário não encontrado"
                                }
                                loading = false
                            }
                            .addOnFailureListener {
                                error = "Erro ao carregar dados"
                                loading = false
                            }
                        // Carregar mensagens que envolvem o aluno ou o professor
                        loadMessages(db, currentUserDocumentId)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(5.dp)
                .background(
                    color = Color(0xFFA1B8CC), // Cor verde água
                    shape = RoundedCornerShape(32.dp), // Cantos arredondados

                )
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = lazyListState,
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(
                                align = if (message.fromUserId == currentUserDocumentId) {
                                    Alignment.CenterEnd // Alinhar à direita para mensagens enviadas
                                } else {
                                    Alignment.CenterStart // Alinhar à esquerda para mensagens recebidas
                                }
                            )
                    ) {

                        Column(
                            modifier = Modifier
                                .background(
                                    color = if (message.fromUserId == currentUserDocumentId) {
                                        Color(0xFF388E3C) // Verde escuro para mensagens enviadas
                                    } else {
                                        Color(0xFF81C784) // Azul claro para mensagens recebidas
                                    },
                                    shape = RoundedCornerShape(16.dp)

                                )
                                .padding(12.dp)
                                .width(300.dp)

                        ) {
                            // Nome do remetente
                            Text(
                                text = if (message.fromUserId == currentUserDocumentId) {
                                    "${userData?.nome}"
                                } else {
                                    "${professorData?.nome}"
                                },
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .padding(bottom = 4.dp)

                            )

                            // Alinhando o ícone e o texto da mensagem
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = if (message.fromUserId == currentUserDocumentId) {
                                    Arrangement.End // Alinha à direita para mensagens enviadas pelo usuário
                                } else {
                                    Arrangement.Start // Alinha à esquerda para mensagens enviadas pelo professor
                                }
                            ) {
                                // Ícone do usuário
                                if (message.fromUserId != currentUserDocumentId) {
                                    // Ícone à esquerda para mensagens do professor
                                    Icon(
                                        painter = painterResource(id =R.mipmap.ic_professor), // Ícone de usuário
                                        contentDescription = "Usuário",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(50.dp) // Tamanho do ícone
                                    )

                                    Spacer(modifier = Modifier.width(8.dp)) // Espaço entre o ícone e o texto
                                }

                                // Texto da mensagem
                                Text(
                                    text = message.message,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                                    color = Color.White,
                                    modifier = Modifier.weight(1f) // O texto ocupa o espaço restante
                                )

                                if (message.fromUserId == currentUserDocumentId) {
                                    // Ícone à direita para mensagens do usuário
                                    Spacer(modifier = Modifier.width(8.dp)) // Espaço entre o texto e o ícone
                                    Icon(
                                        painter = painterResource(id =R.mipmap.ic_aluno), // Ícone de usuário
                                        contentDescription = "Usuário",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(50.dp) // Tamanho do ícone
                                    )
                                }
                            }

                            // Data e hora da mensagem
                            Text(
                                text = formatTimestamp(message.timestamp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        }
                    }

                }
            }


            // Caixa de envio
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                // Caixa de texto
                BasicTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color(0xFF81C784), RoundedCornerShape(32.dp)) // Verde claro
                        .padding(16.dp),
                    textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 16.sp),
                    cursorBrush = SolidColor(Color.White),
                    maxLines = 1
                )

                // Ícone de envio sobreposto ao campo de texto
                IconButton(
                    onClick = {
                        if (messageText.text.isNotBlank() && professorData != null) {
                            sendMessage(
                                db = db,
                                fromUserId = currentUserDocumentId,
                                toUserId = professorDocumentId,
                                message = messageText.text,
                                type = "text"
                            )
                            messageText = TextFieldValue("")
                            messageSent = true // Alterar o estado para mostrar o ícone
                            navController.navigate("chat")
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .size(30.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar Texto",
                        tint = Color(0xFF388E3C)
                    )
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Timestamp): String {
    val sdf = java.text.SimpleDateFormat(
        "d 'de' MMMM 'de' yyyy 'às' HH:mm:ss",
        java.util.Locale("pt", "pt")
    )
    return sdf.format(timestamp.toDate())
}

fun sendMessage(
    db: FirebaseFirestore,
    fromUserId: String,
    toUserId: String,
    message: String,
    type: String
) {
    val newMessage = hashMapOf(
        "fromUserId" to fromUserId,
        "toUserId" to toUserId,
        "message" to message,
        "type" to type,
        "timestamp" to Timestamp.now(), // Automatically get current timestamp
        "read" to false
    )
    db.collection("messages").add(newMessage)
}