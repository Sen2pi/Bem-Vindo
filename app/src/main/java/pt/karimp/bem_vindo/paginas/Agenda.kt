package pt.karimp.bem_vindo.paginas

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import com.google.firebase.Timestamp
import com.google.type.DateTime
import java.util.*
import pt.karimp.bem_vindo.API.LanguageSelector
import pt.karimp.bem_vindo.API.getTranslations
import pt.karimp.bem_vindo.R
import pt.karimp.bem_vindo.models.Aula
import pt.karimp.bem_vindo.models.User
import pt.karimp.bem_vindo.ui.theme.BottomNavBar
import pt.karimp.bem_vindo.utils.formatTimestamp
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun Agenda(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val currentUser = auth.currentUser
    val userEmail = FirebaseAuth.getInstance().currentUser?.email
    var aulas by remember { mutableStateOf<List<Aula>>(emptyList()) }
    var horarioPreferido by remember { mutableStateOf("") }
    var isEditingHorario by remember { mutableStateOf(false) }
    val horarios = listOf("09:00-12:00", "14:00-18:00", "21:00-00:00")
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var userData by remember { mutableStateOf<User?>(null) }
    var professorData by remember { mutableStateOf<User?>(null) }
    var currentUserDocumentId by remember { mutableStateOf("") }
    var professorDocumentId by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("fr") } // Idioma inicial em Francês
    val translations =
        getTranslations(selectedLanguage) // Obter traduções com base no idioma selecionado
    val customFontFamily = FontFamily(
        Font(R.font.winter_minie, FontWeight.Normal, FontStyle.Normal),
    )

    fun playSound(resourceId: Int) {
        val mediaPlayer = MediaPlayer.create(context, resourceId)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { it.release() }
    }

    suspend fun criarSalaVideochamada(db: FirebaseFirestore, aula: Aula): String? {
        return try {
            // Criação de link para sala no Jitsi Meet
            val meetLink =
                "https://meet.jit.si/bemvindo-aula" + UUID.randomUUID().toString().take(10)

            // Atualizar Firestore com o link da sala
            val aulaQuery = db.collection("aulas")
                .whereEqualTo("dataEHora", aula.dataEHora)
                .get()
                .await()

            aulaQuery.documents.forEach { document ->
                db.collection("aulas").document(document.id).update("sala", meetLink).await()
            }

            meetLink
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    suspend fun marcarPresenca(
        db: FirebaseFirestore,
        currentUserId: String,
        professorDocumentId: String,
        aula: Aula
    ) {
        try {
            val aulaQuery = db.collection("aulas")
                .whereEqualTo("aluno", currentUserId)
                .whereEqualTo("professor", professorDocumentId)
                .whereEqualTo("dataEHora", aula.dataEHora)
                .get()
                .await()
            aulaQuery.documents.forEach { document ->
                if (!aula.presencaConfirmada) {
                    playSound(R.raw.accept)
                    db.collection("aulas").document(document.id).update("presencaConfirmada", true)
                        .await()
                    criarSalaVideochamada(db, aula)
                } else {
                    playSound(R.raw.error)
                    db.collection("aulas").document(document.id).update("presencaConfirmada", false)
                        .await()
                    db.collection("aulas").document(document.id).update("sala", "").await()
                }

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
                navController.navigate("semprof")
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
                        // Carregar aulas do Firestore
                        val aulasSnapshot = db.collection("aulas").get().await()
                        aulas = aulasSnapshot.documents.mapNotNull { it.toObject(Aula::class.java) }

                        // Carregar horário preferido do usuário
                        val userSnapshot = db.collection("users")
                            .document(currentUserDocumentId) // Substituir por lógica para obter ID atual
                            .get().await()
                        horarioPreferido = userSnapshot.getString("preferenciaHorario") ?: ""
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    Scaffold(
        topBar = {
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
        if(userData?.professor == ""){
            navController.navigate("semprof")
        }
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


        ) {
            Column {
                Row(
                    modifier = Modifier
                        .background(
                            Color(0xFFA1B8CC),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(15.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.mipmap.ic_calendar_noday),
                            contentDescription = "Agenda",
                            modifier = Modifier.size(100.dp),
                            tint = Color.Unspecified
                        )
                        Text(
                            text = LocalDate.now().dayOfMonth.toString(), // Dia atual
                            color = Color(0xFF005B7F),
                            fontSize = 60.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = customFontFamily,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(top = 30.dp),
                        )
                    }

                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "${translations["hoje"]!!}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF005B7F),
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            text = "${translations["horario_titulo"]!!}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF005B7F),
                            modifier = Modifier.padding(top = 16.dp)
                        )

                        if (isEditingHorario) {
                            Column {
                                horarios.forEach { horario ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                horarioPreferido = horario
                                                isEditingHorario = false
                                                db
                                                    .collection("users")
                                                    .document(currentUserDocumentId)
                                                    .update("preferenciaHorario", horario)
                                                navController.navigate("agenda")
                                            }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = horarioPreferido == horario,
                                            onClick = {
                                                horarioPreferido = horario
                                                isEditingHorario = false
                                                db.collection("users")
                                                    .document(currentUserDocumentId)
                                                    .update("preferenciaHorario", horario)
                                                navController.navigate("agenda")
                                            }
                                        )
                                        Text(
                                            text = horario,
                                            fontSize = 20.sp,
                                            color = Color(0xFF005B7F)
                                        )

                                    }
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .clickable { isEditingHorario = true }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = horarioPreferido.ifEmpty { translations["horario"]!! },
                                    fontSize = 25.sp,
                                    color = Color(0xFF005B7F),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    painter = painterResource(id = R.mipmap.ic_edit),
                                    contentDescription = "Edit",
                                    modifier = Modifier.size(25.dp),
                                    tint = Color.Unspecified
                                )
                            }
                        }
                        LazyColumn() {
                            items(aulas.size) { index ->
                                val aula = aulas[index]
                                if (aula.dataEHora.toDate().toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate() == LocalDate.now()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(start = 15.dp, end = 15.dp)
                                        ) {
                                            Text(
                                                text = "${translations["aula"]} ${aula.dataEHora.toDate().toInstant()
                                                        .atZone(ZoneId.systemDefault())
                                                        .toLocalTime().getHour()}:00",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp,
                                                color = Color(0xFF005B7F)
                                            )
                                            Text(
                                                text = "${translations["nivel"]!!}: " + "${if(aula.nivel == "1" ) translations["basico"] else if(aula.nivel == "2" ) translations["iniciante"] else if(aula.nivel == "3" ) translations["avancado"] else if(aula.nivel == "4" ) translations["niv_mundial"] else if(aula.nivel == "5" ) translations["professional"] else translations["desconhecido"]}",
                                                fontSize = 18.sp,
                                                color = Color(0xFF005B7F)
                                            )
                                        }
                                        IconButton(onClick = {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                aula.sala.let {
                                                    // Abrir o link no navegador padrão
                                                    val intent =
                                                        Intent(
                                                            Intent.ACTION_VIEW,
                                                            Uri.parse(aula.sala)
                                                        )
                                                    context.startActivity(intent)
                                                }
                                            }
                                        }, modifier = Modifier.size(50.dp)) {
                                            Icon(
                                                painter = painterResource(id = R.mipmap.ic_camera),
                                                contentDescription = "ala Jitsi",
                                                tint = if (aula.presencaConfirmada == true) Color.Unspecified else Color.Gray
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    try {
                                                        marcarPresenca(
                                                            db = db,
                                                            currentUserId = currentUserDocumentId,
                                                            professorDocumentId = professorDocumentId,
                                                            aula = aula
                                                        )
                                                    } catch (e: Exception) {
                                                    }
                                                }
                                                navController.navigate("agenda")
                                            },
                                            modifier = Modifier.size(50.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(
                                                    id = if (aula.presencaConfirmada == true) R.mipmap.ic_agenda_filled else R.mipmap.ic_agenda_empty
                                                ),
                                                contentDescription = "Marcar Presença",
                                                tint = Color.Unspecified,
                                            )

                                        }

                                    }
                                }

                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier.background(
                        Color(0xFFA1B8CC),
                        shape = RoundedCornerShape(32.dp)
                    ), // Cantos arredondados)
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize().weight(1f)) {
                        items(aulas.size) { index ->
                            val aula = aulas[index]
                            if (aula.dataEHora.toDate().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate() >= LocalDate.now()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 15.dp, end = 15.dp)
                                    ) {
                                        Text(
                                            text = formatTimestamp(aula?.dataEHora),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = Color(0xFF005B7F)
                                        )
                                        Text(
                                            text = "${translations["nivel"]!!}: " + "${if(aula.nivel == "1" ) translations["basico"] else if(aula.nivel == "2" ) translations["iniciante"] else if(aula.nivel == "3" ) translations["avancado"] else if(aula.nivel == "4" ) translations["niv_mundial"] else if(aula.nivel == "5" ) translations["professional"] else translations["desconhecido"]}",
                                            fontSize = 18.sp,
                                            color = Color(0xFF005B7F)
                                        )
                                        Button(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_INSERT).apply {
                                                    data = CalendarContract.Events.CONTENT_URI
                                                    putExtra(
                                                        CalendarContract.Events.TITLE,
                                                        "Cours Bem-Vindo Niveau: ${aula.nivel}"
                                                    ) // Título do evento
                                                    putExtra(
                                                        CalendarContract.Events.DESCRIPTION,
                                                        "Cours Bem-Vindo Niveau: ${aula.nivel}" // Descrição do evento
                                                    )
                                                    putExtra(
                                                        CalendarContract.Events.EVENT_LOCATION,
                                                        "Online"
                                                    ) // Localização do evento
                                                    // Converter a Timestamp do Firebase para milissegundos
                                                    putExtra(
                                                        CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                                        aula.dataEHora.toDate().time // Hora de início em milissegundos
                                                    )
                                                    // Opcional: Definir duração do evento (2 horas neste exemplo)
                                                    putExtra(
                                                        CalendarContract.EXTRA_EVENT_END_TIME,
                                                        aula.dataEHora.toDate().time + 2 * 60 * 60 * 1000 // Hora de término
                                                    )
                                                }
                                                context.startActivity(intent) // Iniciar a Intent
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row {
                                                Icon(
                                                    painter = painterResource(id = R.mipmap.ic_calendar),
                                                    contentDescription = "Adicionar ao Calendário",
                                                    tint = Color.Unspecified,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Text(
                                                    text = "       ${translations["calendario"]!!}",
                                                    fontSize = 16.sp,
                                                    color = Color.White,
                                                )
                                            }
                                        }
                                    }
                                    Row {
                                        IconButton(onClick = {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                aula.sala.let {
                                                    // Abrir o link no navegador padrão
                                                    val intent =
                                                        Intent(
                                                            Intent.ACTION_VIEW,
                                                            Uri.parse(aula.sala)
                                                        )
                                                    context.startActivity(intent)
                                                }
                                            }
                                        }, modifier = Modifier.size(50.dp)) {
                                            Icon(
                                                painter = painterResource(id = R.mipmap.ic_camera),
                                                contentDescription = "ala Jitsi",
                                                tint = if (aula.presencaConfirmada == true) Color.Unspecified else Color.Gray
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    try {
                                                        marcarPresenca(
                                                            db = db,
                                                            currentUserId = currentUserDocumentId,
                                                            professorDocumentId = professorDocumentId,
                                                            aula = aula
                                                        )
                                                    } catch (e: Exception) {
                                                    }
                                                }
                                                navController.navigate("agenda")
                                            },
                                            modifier = Modifier.size(50.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(
                                                    id = if (aula.presencaConfirmada == true) R.mipmap.ic_agenda_filled else R.mipmap.ic_agenda_empty
                                                ),
                                                contentDescription = "Marcar Presença",
                                                tint = Color.Unspecified,
                                            )

                                        }
                                    }
                                }

                                Divider(
                                    color = Color(0xFF005B7F),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(horizontal = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

