package pt.karimp.bem_vindo.paginas
// Compose Foundation
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

// Compose Material3
import androidx.compose.material3.*

import androidx.compose.runtime.* // Para estados e composição reativa
import androidx.compose.ui.Alignment

// Compose UI
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastJoinToString

// Firebase Imports
import com.google.firebase.firestore.FirebaseFirestore

// Navigation
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

// Coroutines
import kotlinx.coroutines.tasks.await
import pt.karimp.bem_vindo.API.LanguageSelector
import pt.karimp.bem_vindo.API.getTranslations
import pt.karimp.bem_vindo.R
import pt.karimp.bem_vindo.models.Frase
import pt.karimp.bem_vindo.models.User

@Composable
fun Jogo(nivel: String, navController: NavController) {
    // Firebase Firestore e Auth
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    // Estados
    var frases by remember { mutableStateOf<List<Frase>>(emptyList()) }
    var fraseAtualIndex by remember { mutableStateOf(0) }
    var respostaUsuario by remember { mutableStateOf(listOf<String>()) }
    var completado by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var userData by remember { mutableStateOf<User?>(null) }
    var professorData by remember { mutableStateOf<User?>(null) }
    val userEmail = FirebaseAuth.getInstance().currentUser?.email
    var currentUserDocumentId by remember { mutableStateOf("") }
    var professorDocumentId by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("fr") } // Idioma inicial em Francês
    val translations =
        getTranslations(selectedLanguage) // Obter traduções com base no idioma selecionado
    val currentUser = auth.currentUser
    val currentEmail = currentUser?.email

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
                            .addOnSuccessListener {
                                Toast.makeText(context, "Aprender atualizado!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Erro ao atualizar aprender: ${e.message}", Toast.LENGTH_SHORT).show()
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                loading -> CircularProgressIndicator()
                error != null -> Text(error ?: "Erro desconhecido", color = Color.Red)
                frases.isNotEmpty() -> {
                    val fraseAtual = frases[fraseAtualIndex]
                    val fraseCorreta = fraseAtual.frase1
                    val palavrasEmbaralhadas = fraseAtual.frase // Embaralhar palavras

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "${translations["jogo_complete"]} ${if(selectedLanguage == "fr") fraseAtual.frase1Fr else if(selectedLanguage == "en") fraseAtual.frase1En else fraseAtual.frase1Es}",
                            style = MaterialTheme.typography.titleMedium
                        )

                        // Botões com palavras embaralhadas
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(100.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(palavrasEmbaralhadas) { palavra ->
                                Button(
                                    onClick = {
                                        // Adicionar palavra à resposta
                                        if (palavra !in respostaUsuario) {
                                            respostaUsuario = respostaUsuario + palavra
                                        }
                                    },
                                    modifier = Modifier.padding(4.dp),
                                    enabled = palavra !in respostaUsuario // Desabilitar palavra já usada
                                ) {
                                    Text(palavra)
                                }
                            }
                        }

                        // Exibir resposta formada pelo usuário
                        Text(
                            text = respostaUsuario.fastJoinToString(" "),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Botão de submissão
                        Button(
                            onClick = {
                                if (respostaUsuario.fastJoinToString(" ") == fraseCorreta) {
                                    completado = true
                                    atualizarAprender() // Atualizar o campo 'aprender' ao completar o nível
                                    navController.navigate("aprender")
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Resposta incorreta. Tente novamente.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Submeter")
                        }

                        // Botão de reset da resposta
                        Button(
                            onClick = {
                                respostaUsuario = emptyList() // Limpar a resposta do usuário
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Resetar Resposta")
                        }
                    }
                }
                completado -> {
                    Text("Parabéns! Você concluiu o nível.", style = MaterialTheme.typography.bodyLarge)
                }
                else -> Text("Nenhuma frase encontrada. ${nivel}", color = Color.Gray)
            }
        }
    }
}


