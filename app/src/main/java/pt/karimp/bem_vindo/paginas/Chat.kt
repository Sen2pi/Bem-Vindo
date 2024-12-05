package pt.karimp.bem_vindo.paginas

import android.media.MediaPlayer
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.karimp.bem_vindo.API.LanguageSelector
import pt.karimp.bem_vindo.R
import pt.karimp.bem_vindo.ui.theme.BottomNavBar
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import pt.karimp.bem_vindo.models.Message
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import io.appwrite.Client
import io.appwrite.services.Storage
import pt.karimp.bem_vindo.API.getTranslations
import pt.karimp.bem_vindo.utils.AudioPlayer
import pt.karimp.bem_vindo.utils.AudioRecorder
import pt.karimp.bem_vindo.utils.ChatInputSection
import pt.karimp.bem_vindo.utils.extractFileId

@Composable
fun Chat(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    var messageSent by remember { mutableStateOf(false) }
    val context = LocalContext.current
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
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var audioUrls by remember { mutableStateOf<String?>(null) }
    val appwrite = Client(context).setProject("674fb276000fbf815e06")
    val bucketID = "674fb37d000e1677bcfd"
    var isRecording by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("fr") } // Idioma inicial em Francês
    val translations =
        getTranslations(selectedLanguage) // Obter traduções com base no idioma selecionado
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.message_send_sound) }

    suspend fun deleteAudioFile(fileId: String) {
        try {
            val storage = Storage(appwrite)
            storage.deleteFile(bucketID, fileId) // Exclui o arquivo do Appwrite
            navController.navigate("chat")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun markMessagesAsRead(db: FirebaseFirestore, currentUserId: String) {
        try {
            // Carrega as mensagens onde o currentUser é o destinatário (toUserId)
            val unreadMessagesQuery = db.collection("messages")
                .whereEqualTo("toUserId", currentUserId)
                .whereEqualTo("read", false)
                .get()
                .await()

            // Atualiza o campo 'read' para true para cada mensagem não lida
            unreadMessagesQuery.documents.forEach { document ->
                db.collection("messages").document(document.id).update("read", true).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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
                        markMessagesAsRead(db, currentUserDocumentId)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    Scaffold(
        topBar = {  // Icons in the top right corner
            Row(
                modifier = Modifier
                    .padding(top = 20.dp)
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
                    MessageItem(
                        message,
                        currentUserDocumentId,
                        professorData?.nome ?: "",
                        userData?.nome ?: ""
                    )
                }
            }


            // Caixa de envio
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ChatInputSection(
                        audioUrls = audioUrls,
                        messageText = if (isRecording) TextFieldValue(translations["uploading_audio"]!!) else messageText,
                        onMessageTextChange = { messageText = it },
                        deleteAudioFile = { fileId ->
                            deleteAudioFile(extractFileId(audioUrls!!).toString())
                        },
                        onPlayAudio = { audioUrl ->
                            AudioPlayer(audioUrl)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botão de gravação
                    AudioRecorder(
                        onRecordStart = { IsRecording ->
                            isRecording = IsRecording
                        },
                        onRecordComplete = { _, audioUrl ->
                            audioUrls = audioUrl
                            messageText = TextFieldValue(audioUrl) // Atualiza com o URL do áudio
                            isRecording = false
                        },
                        bucketId = bucketID
                    )


                    // Ícone de envio sobreposto ao campo de texto
                    IconButton(
                        onClick = {
                            mediaPlayer.start()
                            if (messageText.text.isNotBlank() && professorData != null) {
                                if (messageText.text.startsWith("https://cloud.appwrite.io/v1/")) {
                                    sendMessage(
                                        db = db,
                                        fromUserId = currentUserDocumentId,
                                        toUserId = professorDocumentId,
                                        message = messageText.text,
                                        type = "audio",
                                        audioUrl = audioUrls.toString(),
                                    )
                                } else {
                                    sendMessage(
                                        db = db,
                                        fromUserId = currentUserDocumentId,
                                        toUserId = professorDocumentId,
                                        message = messageText.text,
                                        type = "text",
                                        audioUrl = audioUrls.toString(),
                                    )
                                }

                                messageText = TextFieldValue("")
                                messageSent = true // Alterar o estado para mostrar o ícone
                                navController.navigate("chat")
                            }
                        },
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(40.dp)
                    ) {

                        Icon(
                            painter = painterResource(id = R.mipmap.ic_send),
                            contentDescription = "Enviar Texto",
                            tint = Color.Unspecified
                        )
                    }
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
    type: String,
    audioUrl: String
) {
    val newMessage = hashMapOf(
        "fromUserId" to fromUserId,
        "toUserId" to toUserId,
        "message" to message,
        "type" to type,
        "audioUrl" to audioUrl,
        "timestamp" to Timestamp.now(), // Automatically get current timestamp
        "read" to false
    )
    db.collection("messages").add(newMessage)
}

@Composable
fun MessageItem(
    message: Message,
    currentUserDocumentId: String,
    professorName: String,
    userName: String
) {
    val isSentByCurrentUser = message.fromUserId == currentUserDocumentId
    val alignment = if (isSentByCurrentUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(alignment)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isSentByCurrentUser) Arrangement.End else Arrangement.Start
        ) {
            if (currentUserDocumentId != message.fromUserId) {
                // Ícone do professor (antes da caixa de mensagem)
                Icon(
                    painter = painterResource(id = R.mipmap.ic_professor),
                    contentDescription = "Ícone Professor",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(75.dp)
                )
            }
            // Caixa de mensagem
            Column(
                modifier = Modifier
                    .background(
                        color = if (isSentByCurrentUser) Color(0xFF388E3C) else Color(0xFF81C784),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Nome do remetente
                Text(
                    text = if (currentUserDocumentId == message.fromUserId) userName else professorName,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                    color = Color.White
                )

                // Exibição da mensagem ou áudio
                if (message.type == "audio" && message.audioUrl.isNotEmpty()) {
                    AudioPlayer(audioUrl = message.audioUrl)
                } else {
                    Text(
                        text = message.message,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                        color = Color.White
                    )
                }

                // Linha com timestamp e ícones de status
                Row(
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = formatTimestamp(message.timestamp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )

                    // Ícones de status (mensagem lida ou não)
                    if (currentUserDocumentId == message.toUserId) {
                        // Mensagem lida
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Mensagem lida",
                            tint = Color.Blue,
                            modifier = Modifier.size(16.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Mensagem lida",
                            tint = Color.Blue,
                            modifier = Modifier.size(16.dp)
                        )
                    } else if (currentUserDocumentId == message.fromUserId && !message.read) {
                        // Mensagem não lida
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Mensagem lida",
                            tint = Color.Blue,
                            modifier = Modifier.size(16.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Mensagem não lida",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Ícone do professor à esquerda e do aluno à direita
            if (currentUserDocumentId == message.fromUserId) {
                // Ícone do aluno (depois da caixa de mensagem)
                Icon(
                    painter = painterResource(id = R.mipmap.ic_aluno),
                    contentDescription = "Ícone Aluno",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(75.dp)
                )
            }
        }
    }
}

