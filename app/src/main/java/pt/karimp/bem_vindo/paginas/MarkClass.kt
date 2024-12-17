package pt.karimp.bem_vindo.paginas
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pt.karimp.bem_vindo.API.LanguageSelector
import pt.karimp.bem_vindo.API.getTranslations
import pt.karimp.bem_vindo.models.User
import pt.karimp.bem_vindo.R
import pt.karimp.bem_vindo.models.Aula
import pt.karimp.bem_vindo.ui.theme.BottomNavBar
import pt.karimp.bem_vindo.ui.theme.ProfessorNavbar
import java.util.Calendar
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement


@Composable
fun MarkClassPage(
    navController: NavController,
    alunoId: String
) {
    var dataHora by remember { mutableStateOf(Timestamp.now()) }
    var presencaConfirmada by remember { mutableStateOf(false) }
    var nivel by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedLanguage by remember { mutableStateOf("fr") } // Idioma inicial em Francês
    val translations = getTranslations(selectedLanguage) // Obter traduções com base no idioma selecionado
    val userEmail = FirebaseAuth.getInstance().currentUser?.email
    var userData by remember { mutableStateOf<User?>(null) }
    var currentUserDocumentId by remember { mutableStateOf("") }
    val context = LocalContext.current
    var alunoData by remember { mutableStateOf<User?>(null) }
    val db = FirebaseFirestore.getInstance()

    if (userEmail != null) {
        LaunchedEffect(Unit) {
            try {
                db.collection("users").document(alunoId).get().addOnSuccessListener(
                    fun(document) {
                        if (document.exists()) {
                            alunoData = document.toObject(User::class.java)
                        }
                    }
                )
                db.collection("users")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val document = querySnapshot.documents.first()
                            currentUserDocumentId = document.id
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
            } catch (e: Exception) {
                error = "Erro desconhecido"
                loading = false
            }
        }
    } else {
        error = "Nenhum usuário logado"
        loading = false
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
        bottomBar = { if(userData?.tipo == "Professor") ProfessorNavbar(navController = navController, currentUserDocumentId) else BottomNavBar(navController = navController, currentUserDocumentId) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(5.dp)
                .background(color = Color(0xFFA1B8CC), shape = RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.mipmap.ic_naula),
                    contentDescription = "Calendario de Marcação",
                    tint = Color.Unspecified,
                )
                Text(
                    text = "Aula para  ${alunoData?.nome} ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF005B7F),
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp)
                )
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (error != null) {
                    Text(text = error ?: "Erro desconhecido", color = Color.Red)
                }

                // Campo para seleção de data e hora
                Text("Data e Hora:", fontSize = 18.sp, color = Color(0xFF005B7F), modifier = Modifier.padding(top = 10.dp))
                Button(
                    onClick = {
                        showDateTimePicker(context) { newDateTime ->
                            dataHora = newDateTime
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005B7F) )
                ) {
                    Text("Selecionar Data e Hora: ${dataHora.toDate()}", color = Color.White)
                }


                // Campo para nível
                Text("Nível:", fontSize = 18.sp, color = Color(0xFF005B7F), modifier = Modifier.padding(top = 10.dp))
                TextField(
                    value = nivel,
                    onValueChange = { nivel = it },
                    label = { Text("Nível da aula") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor =Color(0xFF005B7F),
                        unfocusedContainerColor = Color(0xFF005B7F),
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Botão de salvar aula
                Button(
                    onClick = {
                        loading = true
                        val aula = Aula(
                            professor = currentUserDocumentId,  // Substitua pelo nome do professor autenticado
                            criacao = Timestamp.now(),
                            dataEHora = dataHora,
                            aluno = alunoId,
                            presencaConfirmada = presencaConfirmada,
                            presente = false,  // Inicialmente, a presença pode ser falsa
                            avaliacao = 0,
                            nivel = nivel,
                            sala = ""
                        )
                        val db = FirebaseFirestore.getInstance()
                        db.collection("aulas")
                            .add(aula)
                            .addOnSuccessListener {
                                loading = false
                                navController.navigate("homeProfessor")
                            }
                            .addOnFailureListener {
                                loading = false
                                error = "Erro ao marcar aula."
                            }

                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF136C1A) )
                ) {
                    Text("Marcar a Aula", color = Color.White)
                }
            }
        }
    }
}


fun showDateTimePicker(context: Context, onDateTimeSelected: (Timestamp) -> Unit) {
    val calendar = Calendar.getInstance()

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    onDateTimeSelected(Timestamp(calendar.time))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // Use formato de 24 horas
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}
