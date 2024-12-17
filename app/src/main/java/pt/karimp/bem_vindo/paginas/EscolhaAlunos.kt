package pt.karimp.bem_vindo.paginas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.karimp.bem_vindo.API.LanguageSelector
import pt.karimp.bem_vindo.API.getTranslations
import pt.karimp.bem_vindo.R
import pt.karimp.bem_vindo.models.User
import pt.karimp.bem_vindo.ui.theme.BottomNavBar
import pt.karimp.bem_vindo.ui.theme.ProfessorNavbar

@Composable
fun EscolhaAlunos(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val db = FirebaseFirestore.getInstance()
    var currentUserDocumentId by remember { mutableStateOf("") }
    var alunosSemProfessor by remember { mutableStateOf(listOf<User>()) }
    val userEmail = FirebaseAuth.getInstance().currentUser?.email
    var userData by remember { mutableStateOf<User?>(null) }
    var selectedLanguage by remember { mutableStateOf("fr") } // Idioma inicial em Francês
    val translations =
        getTranslations(selectedLanguage) // Obter traduções com base no idioma selecionado
    LaunchedEffect(Unit) {

        if (currentUser != null) {
            try {
                val userDocSnapshot = db.collection("users")
                    .whereEqualTo("email", currentUser.email).get().await()
                if (!userDocSnapshot.isEmpty) {
                    currentUserDocumentId = userDocSnapshot.documents.first().id
                    alunosSemProfessor = db.collection("users")
                        .whereEqualTo("tipo", "Aluno")
                        .whereEqualTo("professor", "").get().await().toObjects(User::class.java)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            // Icons in the top right corner
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
                .padding(innerPadding).padding(5.dp)
                .background(color = Color(0xFFA1B8CC),shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.TopCenter
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                alunosSemProfessor.forEach { aluno ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.mipmap.ic_aluno),
                            contentDescription = "Aluno",
                            modifier = Modifier.size(40.dp),
                            tint = Color.Unspecified
                        )
                        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                            Text(text = aluno.nome, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF005B7F))
                            Text(text = aluno.cidade, style = MaterialTheme.typography.bodyMedium,color = Color(0xFF005B7F))
                        }
                        Row (modifier = Modifier.padding(start = 25.dp)){
                            val flagRes = when (aluno.lingua) {
                            "fr" -> R.mipmap.flag_fr
                            "en" -> R.mipmap.flag_en
                            "es" -> R.mipmap.flag_es
                            else -> R.mipmap.flag_pt
                        }
                            Icon(
                                painter = painterResource(id = flagRes),
                                contentDescription = "Lingua",
                                modifier = Modifier.size(40.dp),
                                tint = Color.Unspecified
                            )
                            IconButton(onClick = {
                                db.collection("users").whereEqualTo("email", aluno.email).get().addOnSuccessListener { querySnapshot ->
                                    if (!querySnapshot.isEmpty) {
                                        querySnapshot.documents.first().reference.update("professor", currentUserDocumentId)
                                    }
                                }
                                navController.navigate("escolhaAlunos")

                            }) {
                                Icon(
                                    painter = painterResource(id = R.mipmap.ic_checkbox),
                                    contentDescription = "Selecionar Aluno",
                                    tint = Color.Unspecified,
                                )
                            } }


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

