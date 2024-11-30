package pt.karimp.bem_vindo.paginas

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import pt.karimp.bem_vindo.R
import pt.karimp.bem_vindo.ui.theme.BottomNavBar

@Composable
fun Profile(navController: NavController) {
    var userData by remember { mutableStateOf<User?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val userEmail = FirebaseAuth.getInstance().currentUser?.email

    // Fetch user data from Firestore if logged in
    if (userEmail != null) {
        LaunchedEffect(userEmail) {
            try {
                val db = FirebaseFirestore.getInstance()
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
        bottomBar = { BottomNavBar(navController = navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
                .padding(innerPadding)
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

            // Top Row for Profile, Logoff, and Trash icons
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Profile Icon
                IconButton(onClick = { navController.navigate("profile") }) {
                    Icon(
                        painter = painterResource(id = R.mipmap.ic_perfil),
                        contentDescription = "Profile",
                        modifier = Modifier.size(50.dp),
                        tint = Color.Unspecified
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

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

            // Main Content
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 50.dp)
                    .padding(25.dp)
                    .align(Alignment.TopStart)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (loading) {
                    CircularProgressIndicator()
                } else if (error != null) {
                    Text(
                        text = error ?: "Erro desconhecido",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                        color = Color.Red
                    )
                } else if (userData != null) {
                    // Editable Profile Information
                    userData?.let { user ->
                        Text(
                            text = "Profil",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        TextField(
                            value = user.nome,
                            onValueChange = { userData = userData?.copy(nome = it) },
                            label = { Text("Nom Complet", color = Color.White) },
                            enabled = isEditing,
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = user.morada,
                            onValueChange = { userData = userData?.copy(morada = it) },
                            label = { Text("Adresse", color = Color.White) },
                            enabled = isEditing,
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = user.codigoPostal,
                            onValueChange = { userData = userData?.copy(codigoPostal = it) },
                            label = { Text("Code Postale", color = Color.White) },
                            enabled = isEditing,
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = user.cidade,
                            onValueChange = { userData = userData?.copy(cidade = it) },
                            label = { Text("Ville", color = Color.White) },
                            enabled = isEditing,
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = user.nif,
                            onValueChange = { userData = userData?.copy(nif = it) },
                            label = { Text("Número D'identification Fiscal", color = Color.White) },
                            enabled = isEditing,
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = user.nss,
                            onValueChange = { userData = userData?.copy(nss = it) },
                            label = { Text("Número de Securité Social", color = Color.White) },
                            enabled = isEditing,
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = user.telefone,
                            onValueChange = { userData = userData?.copy(telefone = it) },
                            label = { Text("Número de Telefone", color = Color.White) },
                            enabled = isEditing,
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = user.email,
                            onValueChange = { userData = userData?.copy(email = it) },
                            label = { Text("Email", color = Color.White) },
                            enabled = isEditing,
                            modifier = Modifier.fillMaxWidth()
                        )
                        // Edit and Save Button
                        Row {
                            val context = LocalContext.current
                            fun saveUserData(user: User) {
                                val db = FirebaseFirestore.getInstance()
                                val userEmail = FirebaseAuth.getInstance().currentUser?.email

                                if (userEmail != null) {
                                    db.collection("users")
                                        .whereEqualTo("email", userEmail) // Encontra o documento do usuário logado
                                        .get()
                                        .addOnSuccessListener { querySnapshot ->
                                            val document = querySnapshot.documents.firstOrNull()
                                            if (document != null) {
                                                // Atualiza os campos no Firestore
                                                document.reference.update(
                                                    mapOf(
                                                        "nome" to user.nome,
                                                        "morada" to user.morada,
                                                        "codigoPostal" to user.codigoPostal,
                                                        "cidade" to user.cidade,
                                                        "nif" to user.nif,
                                                        "nss" to user.nss,
                                                        "telefone" to user.telefone,
                                                        "email" to user.email
                                                    )
                                                ).addOnSuccessListener {
                                                    // Feedback para o utilizador
                                                    Toast.makeText(
                                                        context,
                                                        "Dados atualizados com sucesso!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }.addOnFailureListener {
                                                    Toast.makeText(
                                                        context,
                                                        "Erro ao atualizar dados!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Erro: Documento do usuário não encontrado!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                context,
                                                "Erro ao acessar o Firestore!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                } else {
                                    Toast.makeText(context, "Usuário não logado!", Toast.LENGTH_SHORT).show()
                                }
                            }
                            Button(onClick = { isEditing = !isEditing }) {
                                Text(text = if (isEditing) "Cancelar" else "Editar")
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            if (isEditing) {
                                Button(onClick = { saveUserData(user) }) {
                                    Text(text = "Salvar")
                                }
                            }
                        }
                        val context = LocalContext.current
                        fun recoverPassword(email: String) {
                            val auth = FirebaseAuth.getInstance()

                            auth.sendPasswordResetEmail(email)
                                .addOnSuccessListener {
                                    // Mostra a mensagem de sucesso
                                    Toast.makeText(context, "Email de recuperação enviado", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    // Tratar falha
                                    Toast.makeText(context, "Erro ao enviar email", Toast.LENGTH_SHORT).show()
                                }
                        }
                        // Password Recovery Button
                        Button(
                            onClick = { recoverPassword(user.email) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Text(text = "Recuperar Senha")
                        }
                    }
                }
            }

            // Trash Icon Button at the Bottom
            if (showDeleteConfirmation) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirmation = false },
                    title = { Text("Confirmar exclusão") },
                    text = { Text("Você tem certeza de que deseja excluir sua conta?") },
                    confirmButton = {
                        TextButton(onClick = { deleteUser(navController) }) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirmation = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            // Trash Icon
            IconButton(
                onClick = { showDeleteConfirmation = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.mipmap.ic_trash),
                    contentDescription = "Delete User",
                    modifier = Modifier.size(50.dp),
                    tint = Color.Unspecified
                )
            }
        }
    }
}



fun deleteUser(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    user?.delete()?.addOnSuccessListener {
        // Delete from Firestore as well
        val db = FirebaseFirestore.getInstance()
        val userEmail = user.email
        db.collection("users").whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.firstOrNull()?.reference?.delete()
            }
        // Sign out the user and navigate to the login page
        auth.signOut()
        navController.navigate("login") {
            // Clear the back stack to prevent returning to deleted user pages
            popUpTo(0)
        }
    }?.addOnFailureListener {
        // Handle failure case (optional feedback)
        println("Failed to delete user.")
    }
}

data class User(
    val nome: String = "",
    val email: String = "",
    val cidade: String = "",
    val codigoPostal: String = "",
    val morada: String = "",
    val nif: String = "",
    val nss: String = "",
    val telefone: String = "",
)
