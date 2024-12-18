package pt.karimp.bem_vindo.paginas

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pt.karimp.bem_vindo.API.LanguageSelector
import pt.karimp.bem_vindo.API.getTranslations
import pt.karimp.bem_vindo.models.User
import pt.karimp.bem_vindo.R
import pt.karimp.bem_vindo.ui.theme.BottomNavBar
import pt.karimp.bem_vindo.ui.theme.ProfessorNavbar

@Composable
fun Profile(navController: NavController) {
    var userData by remember { mutableStateOf<User?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("fr") } // Idioma inicial em Francês
    val translations = getTranslations(selectedLanguage) // Obter traduções com base no idioma selecionado
    var currentUserDocumentId by remember { mutableStateOf("") }
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
                .background(color = Color(0xFFA1B8CC), shape = RoundedCornerShape(12.dp)
                )
        ) {
            Text(
                text = translations["profile_title"] ?:"Profil",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF005B7F),
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 15.dp)
            )
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 50.dp)
                    .padding(5.dp)
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

                        TextField(
                            value = user.nome,
                            onValueChange = { userData = userData?.copy(nome = it) },
                            label = { Text(translations["full_name_label"] ?:"Nom Complet", color = Color.White) },
                            enabled = isEditing,
                            modifier = Modifier.fillMaxWidth(),
                            shape =  RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF00405A),
                                unfocusedContainerColor = Color(0xFF00405A),
                                disabledContainerColor = Color(0xFF00405A),
                                focusedBorderColor = Color.DarkGray,
                                unfocusedBorderColor = Color.LightGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = Color.LightGray,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White
                            )
                        )
                        TextField(
                            value = user.morada,
                            onValueChange = { userData = userData?.copy(morada = it) },
                            label = { Text(translations["address_label"] ?:"Adresse", color = Color.White) },
                            enabled = isEditing,
                            modifier = Modifier.fillMaxWidth(),
                            shape =  RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF00405A),
                                unfocusedContainerColor = Color(0xFF00405A),
                                disabledContainerColor = Color(0xFF00405A),
                                focusedBorderColor = Color.DarkGray,
                                unfocusedBorderColor = Color.LightGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = Color.LightGray,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White
                            )
                        )
                        TextField(
                            value = user.codigoPostal,
                            onValueChange = { userData = userData?.copy(codigoPostal = it) },
                            label = { Text(translations["postal_code_label"] ?:"Code Postale", color = Color.White) },
                            enabled = isEditing,
                            modifier = Modifier.fillMaxWidth(),
                            shape =  RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF00405A),
                                unfocusedContainerColor = Color(0xFF00405A),
                                disabledContainerColor = Color(0xFF00405A),
                                focusedBorderColor = Color.DarkGray,
                                unfocusedBorderColor = Color.LightGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = Color.LightGray,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White
                            )

                        )
                        TextField(
                            value = user.cidade,
                            onValueChange = { userData = userData?.copy(cidade = it) },
                            label = { Text(translations["city_label"] ?:"Ville", color = Color.White) },
                            enabled = isEditing,
                            modifier = Modifier.fillMaxWidth(),
                            shape =  RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF00405A),
                                unfocusedContainerColor = Color(0xFF00405A),
                                disabledContainerColor = Color(0xFF00405A),
                                focusedBorderColor = Color.DarkGray,
                                unfocusedBorderColor = Color.LightGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = Color.LightGray,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White
                            )
                        )
                        TextField(
                            value = user.nif,
                            onValueChange = { userData = userData?.copy(nif = it) },
                            label = { Text(translations["nif_label"] ?:"Número D'identification Fiscal", color = Color.White) },
                            enabled = isEditing,
                            modifier = Modifier.fillMaxWidth(),
                            shape =  RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF00405A),
                                unfocusedContainerColor = Color(0xFF00405A),
                                disabledContainerColor = Color(0xFF00405A),
                                focusedBorderColor = Color.DarkGray,
                                unfocusedBorderColor = Color.LightGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = Color.LightGray,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White
                            )
                        )
                        TextField(
                            value = user.nss,
                            onValueChange = { userData = userData?.copy(nss = it) },
                            label = { Text(translations["nss_label"] ?:"Número de Securité Social", color = Color.White) },
                            enabled = isEditing,
                            modifier = Modifier.fillMaxWidth(),
                            shape =  RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF00405A),
                                unfocusedContainerColor = Color(0xFF00405A),
                                disabledContainerColor = Color(0xFF00405A),
                                focusedBorderColor = Color.DarkGray,
                                unfocusedBorderColor = Color.LightGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = Color.LightGray,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White
                            )
                        )
                        TextField(
                            value = user.telefone,
                            onValueChange = { userData = userData?.copy(telefone = it) },
                            label = { Text(translations["phone_number_label"] ?:"Número de Telefone", color = Color.White) },
                            enabled = isEditing,
                            modifier = Modifier.fillMaxWidth(),
                            shape =  RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF00405A),
                                unfocusedContainerColor = Color(0xFF00405A),
                                disabledContainerColor = Color(0xFF00405A),
                                focusedBorderColor = Color.DarkGray,
                                unfocusedBorderColor = Color.LightGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = Color.LightGray,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White
                            )
                        )
                        TextField(
                            value = user.email,
                            onValueChange = { userData = userData?.copy(email = it) },
                            label = { Text(translations["email_label"] ?:"Email", color = Color.White) },
                            enabled = isEditing,
                            modifier = Modifier.fillMaxWidth(),
                            shape =  RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF00405A),
                                unfocusedContainerColor = Color(0xFF00405A),
                                disabledContainerColor = Color(0xFF00405A),
                                focusedBorderColor = Color.DarkGray,
                                unfocusedBorderColor = Color.LightGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = Color.LightGray,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White
                            )
                        )
                        // Edit and Save Button
                        Row {
                            val context = LocalContext.current
                            fun saveUserData(user: User) {
                                val db = FirebaseFirestore.getInstance()
                                val userEmail = FirebaseAuth.getInstance().currentUser?.email

                                if (userEmail != null) {
                                    db.collection("users")
                                        .whereEqualTo(
                                            "email",
                                            userEmail
                                        ) // Encontra o documento do usuário logado
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
                                    Toast.makeText(
                                        context,
                                        "Usuário não logado!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            Button(
                                onClick = { isEditing = !isEditing },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00405A),
                                    contentColor = Color.White
                                ),
                            ) {
                                Text(text = if (isEditing) translations["cancel_button"] ?:"Annuler" else translations["edit_button"] ?:"Editer")
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            if (isEditing) {
                                Button(
                                    onClick = { saveUserData(user) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00405A),
                                        contentColor = Color.White
                                    ),
                                ) {
                                    Text(text = translations["save_button"] ?:"Sauvegarder")
                                }
                            }
                        }
                        val context = LocalContext.current
                        fun recoverPassword(email: String) {
                            val auth = FirebaseAuth.getInstance()

                            auth.sendPasswordResetEmail(email)
                                .addOnSuccessListener {
                                    // Mostra a mensagem de sucesso
                                    Toast.makeText(
                                        context,
                                        "Email de Recuperation de Mot de Pass envoyé!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener {
                                    // Tratar falha
                                    Toast.makeText(
                                        context,
                                        "Erreur envoyant un email",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                        // Password Recovery Button
                        Button(
                            onClick = { recoverPassword(user.email) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00405A),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Text(text = translations["password_recovery_button"] ?:"Recuperer Mot de Passe")
                        }
                        // Trash Icon Button at the Bottom
                        if (showDeleteConfirmation) {
                            AlertDialog(
                                onDismissRequest = { showDeleteConfirmation = false },
                                title = { Text(translations["confirm_label"] ?:"Confirmer l'elimination") },
                                text = { Text(translations["confirm_deletion_phrase"] ?:"Êtes vous certain de vouloir eliminer votre compte?") },
                                confirmButton = {
                                    TextButton(onClick = { deleteUser(navController) }) {
                                        Text(translations["confirm_button"] ?:"Confirmer")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDeleteConfirmation = false }) {
                                        Text(translations["cancel_button"] ?:"Annuler")
                                    }
                                }
                            )
                        }

                        // Trash Icon
                        IconButton(
                            onClick = { showDeleteConfirmation = true },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row (
                                modifier = Modifier.align(Alignment.CenterHorizontally).background(Color.Red).fillMaxWidth().padding(10.dp),
                                horizontalArrangement =  Arrangement.Center,
                            ){ Icon(
                                painter = painterResource(id = R.mipmap.ic_trash),
                                contentDescription = "Delete User",
                                modifier = Modifier.size(35.dp),
                                tint = Color.Unspecified
                            )
                                Text(text = translations["apagar_user"]!!, color = Color.White)
                            }

                        }
                    }
                }
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

