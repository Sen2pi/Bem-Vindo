package pt.karimp.bem_vindo.paginas
// Compose Foundation
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape

// Compose Material3
import androidx.compose.material3.*

import androidx.compose.runtime.* // Para estados e composição reativa
import androidx.compose.ui.Alignment

// Compose UI
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastJoinToString

// Firebase Imports
import com.google.firebase.firestore.FirebaseFirestore

// Navigation
import androidx.navigation.NavController
import com.google.api.Distribution
import com.google.firebase.auth.FirebaseAuth

// Coroutines
import kotlinx.coroutines.tasks.await
import pt.karimp.bem_vindo.API.LanguageSelector
import pt.karimp.bem_vindo.API.getTranslations
import pt.karimp.bem_vindo.R
import pt.karimp.bem_vindo.models.Frase
import pt.karimp.bem_vindo.models.Nivel
import pt.karimp.bem_vindo.models.User

@Composable
fun Jogo(nivel: String, navController: NavController) {
    // Firebase Firestore e Auth
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val currentUser = auth.currentUser
    // Estados
    var frases by remember { mutableStateOf<List<Frase>>(emptyList()) }
    var fraseAtualIndex by remember { mutableStateOf(0) }
    var respostaUsuario by remember { mutableStateOf(listOf<String>()) }
    var completado by remember { mutableStateOf(false) }
    var mostrarPopup by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var nivelData by remember { mutableStateOf<Nivel?>(null) }
    val currentEmail = auth.currentUser?.email
    var userData by remember { mutableStateOf<User?>(null) }
    var currentUserDocumentId by remember { mutableStateOf("") }
    val userEmail = FirebaseAuth.getInstance().currentUser?.email
    var selectedLanguage by remember { mutableStateOf("fr") } // Idioma inicial em Francês
    val translations =
        getTranslations(selectedLanguage) // Obter traduções com base no idioma selecionado
    val customFontFamily = FontFamily(
        Font(R.font.winter_minie, FontWeight.Normal, FontStyle.Normal),
    )

    LaunchedEffect(Unit) {
        if (currentUser != null) {
            try {
                val userDocSnapshot =
                    db.collection("users").whereEqualTo("email", currentUser.email).get().await()
                if (!userDocSnapshot.isEmpty) {
                    val userDoc = userDocSnapshot.documents.first()
                    currentUserDocumentId = userDoc.id
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
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // Carregar frases do Firestore
    LaunchedEffect(nivel) {
        try {
            loading = true
            val snapshot = db.collection("niveis")
                .document(nivel)
                .collection("frases")
                .get()
                .await()
            frases = snapshot.documents.mapNotNull { it.toObject(Frase::class.java) }
            val documentSnapshot = db.collection("niveis").document(nivel).get().await()
            nivelData = documentSnapshot.toObject(Nivel::class.java)
        } catch (e: Exception) {
            error = "Erro ao carregar as frases: ${e.message}"
        } finally {
            loading = false
        }
    }

    // Função para atualizar o contador 'aprender' do usuário
    fun atualizarAprender() {
        if (currentEmail != null) {
            db.collection("users")
                .whereEqualTo("email", currentEmail)
                .get()
                .addOnSuccessListener { documents ->
                    val userDocument = documents.firstOrNull()
                    userDocument?.let {
                        val currentAprender = it.getLong("aprender")?.toInt() ?: 0
                        val newAprender = currentAprender + 1
                        db.collection("users").document(it.id)
                            .update("aprender", newAprender)
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Erro ao atualizar aprender: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
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
    ) { innerPadding ->
        Image(
            painter = painterResource(id = R.mipmap.ic_ajulejo3),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .alpha(0.60f)
                .fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(5.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            when {
                loading -> CircularProgressIndicator()
                error != null -> Text(error ?: "Erro desconhecido", color = Color.Red)
                frases.isNotEmpty() -> {
                    val fraseAtual = frases[fraseAtualIndex]
                    val fraseCorreta = fraseAtual.frase1
                    val palavrasEmbaralhadas = fraseAtual.frase

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth().background(Color(0xFFA1B8CC), shape = RoundedCornerShape(16.dp)).padding(16.dp).alpha(0.60f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.mipmap.ic_abc), // Substitua pelo ID real do recurso do ícone
                            contentDescription = "Ícone de festa",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(100.dp)
                        )
                        Text(
                            text = "Complete: ${if(selectedLanguage == "fr") fraseAtual.frase1Fr else if(selectedLanguage == "en") fraseAtual.frase1En else fraseAtual.frase1Es}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF005B7F),
                            fontSize = 25.sp
                        )
                        // Divider to create a line separator
                        Divider(
                            color = Color(0xFF005B7F), // Color of the line
                            thickness = 10.dp,    // Thickness of the line
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Text(
                            text = respostaUsuario.joinToString(" "),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF136C1A),
                            fontFamily = customFontFamily,
                            fontSize = 50.sp
                        )
                        // Divider to create a line separator
                        Divider(
                            color = Color(0xFF005B7F), // Color of the line
                            thickness = 10.dp,    // Thickness of the line
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(100.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(palavrasEmbaralhadas) { palavra ->
                                Button(
                                    onClick = {
                                        if (palavra !in respostaUsuario) {
                                            respostaUsuario = respostaUsuario + palavra
                                        }
                                    },
                                    modifier = Modifier.padding(4.dp),
                                    enabled = palavra !in respostaUsuario,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF005B7F),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text(palavra, fontSize = 16.sp)
                                }
                            }
                        }
                    }

                    Column (modifier = Modifier.fillMaxWidth()){
                        Button(
                            onClick = {
                                if (respostaUsuario.joinToString(" ") == fraseCorreta) {
                                    completado = true
                                    mostrarPopup = true
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Resposta incorreta. Tente novamente.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF136C1A),
                                contentColor = Color.White
                            )
                        ) {
                            Text("${translations["confirm_button"]}")
                        }

                        Button(
                            onClick = {
                                respostaUsuario = emptyList()
                            },
                            modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF99812A),
                                contentColor = Color.White
                            )

                        ) {
                            Text("${translations["reset_response_button"]}")
                        }
                    }

                }

                completado -> {
                    Text(
                        "Parabéns! Você concluiu o nível.",
                        style = MaterialTheme.typography.bodyLarge
                    )

                }

                else -> Text("Nenhuma frase encontrada.", color = Color.Gray)
            }
        }
    }

    // Exibir popup ao completar o nível
    if (mostrarPopup) {
        AlertDialog(
            onDismissRequest = { mostrarPopup = false },
            containerColor = Color(0xFFA1B8CC),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.mipmap.ic_festa), // Substitua pelo ID real do recurso do ícone
                        contentDescription = "Ícone de festa",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${translations["parabens"]}", color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = R.mipmap.ic_festa), // Substitua pelo ID real do recurso do ícone
                        contentDescription = "Ícone de festa",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(50.dp)
                    )
                }
            },
            text = {
                Text(
                    "${translations["nivel_completed"]}",
                    color = Color.White,
                    fontSize = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nivelData?.nivel?.minus(1) == userData?.aprender) {
                            atualizarAprender()
                        }
                        mostrarPopup = false
                        navController.navigate("aprender")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF136C1A),
                        contentColor = Color.White
                    )
                ) {
                    Text("${translations["ir_niveis"]}")
                }
            },
            dismissButton = {
                Button(
                    onClick = { mostrarPopup = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8E1213),
                        contentColor = Color.White
                    )
                ) {
                    Text("${translations["cancel_button"]}")
                }
            }
        )
    }
}


