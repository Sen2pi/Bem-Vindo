package pt.karimp.bem_vindo.paginas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import pt.karimp.bem_vindo.API.LanguageSelector
import pt.karimp.bem_vindo.API.getTranslations
import pt.karimp.bem_vindo.R
import pt.karimp.bem_vindo.auth.AuthResponse
import pt.karimp.bem_vindo.auth.AuthenticationManager
import pt.karimp.bem_vindo.utils.ConfettiRain

@Composable
fun PaginaDeRegistro(navController: NavController) {

    val context = LocalContext.current
    var mostrarPopup by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("fr") } // Idioma inicial em Francês
    val translations =
        getTranslations(selectedLanguage) // Obter traduções com base no idioma selecionado
    val firestore = FirebaseFirestore.getInstance() // Inicializa o Firestore
    val campos = listOf(
        "${translations["full_name_label"]}",
        "${translations["nss_label"]}",
        "${translations["nif_label"]}",
        "${translations["address_label"]}",
        "${translations["city_label"]}",
        "${translations["postal_code_label"]}",
        "${translations["phone_number_label"]}",
        "${translations["email_label"]}",
        "${translations["password"]}"
    )
    val valores = remember { mutableStateListOf(*Array(campos.size) { "" }) }
    val errorMessages = remember { mutableStateListOf(*Array(campos.size) { "" }) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val authenticationManager = remember { AuthenticationManager() }
    val coroutineScope = rememberCoroutineScope()
    // Função de validação
    fun validateForm(): Boolean {
        val errors = mutableMapOf<Int, String>()

        // Validação do NIF
        if (valores[2].length != 9 || !valores[2].all { it.isDigit() }) {
            errors[2] = "Le NIF doit contenir 9 chiffres."
        }

        // Validação do Número de Segurança Social
        if (valores[1].length != 11 || !valores[1].all { it.isDigit() }) {
            errors[1] = "Le Numéro de Sécurité Social doit contenir 11 chiffres."
        }

        // Validação do Código Postal (formato 4 dígitos - 3 dígitos)
        if (!valores[5].matches(Regex("^\\d{4}-\\d{3}$"))) {
            errors[5] = "Le Code Postal doit avoir le format XXXX-XXX."
        }

        // Validação da Senha
        val password = valores[8]
        if (password.length < 8 || !password.any { it.isLowerCase() } || !password.any { it.isUpperCase() }) {
            errors[8] = "Le Mot de Passe doit comporter au moins 8 caractères, incluant une lettre majuscule et une lettre minuscule."
        }

        // Atualizar mensagens de erro
        errorMessages.forEachIndexed { index, _ ->
            errorMessages[index] = errors[index] ?: ""
        }

        return errors.isEmpty()
    }

    fun checkUserExists(onResult: (Boolean) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("email", valores[7])
            .whereEqualTo("nif", valores[2])
            .whereEqualTo("telefone", valores[6])
            .whereEqualTo("nss", valores[1])
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    onResult(true) // Usuário já existe
                } else {
                    onResult(false) // Usuário não existe
                }
            }
            .addOnFailureListener {
                errorMessage = "Erro ao verificar usuário: ${it.message}"
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.mipmap.azulejo1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.60f)
        )
        Row (modifier = Modifier
            .padding(top = 30.dp)
            .fillMaxWidth(),
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
                .padding(top = 50.dp, end = 15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            // Language Selector
            LanguageSelector(
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { selectedLanguage = it }
            )
            IconButton(onClick = { navController.navigate("login") }) {
                Icon(
                    painter = painterResource(id = R.mipmap.ic_logout),
                    contentDescription = "Logoff",
                    modifier = Modifier.size(50.dp),
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = { mostrarPopup = true }) {
                Icon(
                    painter = painterResource(id = R.mipmap.ic_faq),
                    contentDescription = "Logoff",
                    modifier = Modifier.size(50.dp),
                    tint = Color.Unspecified
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 75.dp).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            campos.forEachIndexed { index, label ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = valores[index],
                        onValueChange = { valores[index] = it },
                        label = { Text(label) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = when (label) {
                                "Email" -> KeyboardType.Email
                                "Mot de Pass" -> KeyboardType.Password
                                else -> KeyboardType.Text
                            }
                        ),
                        visualTransformation = if (label == "Mot de Pass") PasswordVisualTransformation() else VisualTransformation.None,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFA1B8CC),    // Background when focused
                            unfocusedContainerColor = Color(0xFFA1B8CC),  // Background when not focused
                            focusedBorderColor = Color.DarkGray,    // Border color when focused
                            unfocusedBorderColor = Color.LightGray, // Border color when not focused
                            focusedTextColor = Color(0xFF00405A),   // Text color when focused
                            unfocusedTextColor = Color(0xFF00405A), // Text color when not focuse
                            focusedLeadingIconColor = Color(0xFF00405A),    // Leading icon color when focused
                            unfocusedLeadingIconColor = Color.White,  // Leading icon color when not focused
                            focusedTrailingIconColor = Color(0xFF00405A),   // Optional: Customize trailing icon color
                            unfocusedTrailingIconColor = Color.White, // Optional: Customize trailing icon color
                            focusedLabelColor = Color(0xFF00405A),
                            unfocusedLabelColor = Color.White,
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (validateForm()) {
                        checkUserExists { exists ->
                            if (exists) {
                                errorMessage = "Usuário já existe. Verifique seus dados."
                            } else {
                                authenticationManager.criarContaComEmail(
                                    nome = valores[0],
                                    nss = valores[1],
                                    nif = valores[2],
                                    morada = valores[3],
                                    cidade = valores[4],
                                    codigoPostal = valores[5],
                                    telemovel = valores[6],
                                    email = valores[7],
                                    password = valores[8],
                                    progresso = 0,
                                ).onEach { response ->
                                    if (response is AuthResponse.Success) {
                                        firestore.collection("users")
                                            .add(
                                                hashMapOf(
                                                    "nome" to valores[0],
                                                    "nss" to valores[1],
                                                    "nif" to valores[2],
                                                    "morada" to valores[3],
                                                    "cidade" to valores[4],
                                                    "codigoPostal" to valores[5],
                                                    "telefone" to valores[6],
                                                    "email" to valores[7],
                                                    "tipo" to "Aluno", // Sempre "Aluno"
                                                    "progresso" to 0,
                                                    "nivel" to "1",
                                                    "professor" to "aSyWDvIyEM8zUlUWaXAF",
                                                    "preferenciaHorario" to "",
                                                    "pontuacao" to 0,
                                                    "aprender" to 0,
                                                    "lingua" to selectedLanguage
                                                )
                                            )
                                            .addOnSuccessListener {
                                                // Dados salvos com sucesso
                                                navController.navigate("login") {
                                                    popUpTo("register") { inclusive = true }
                                                }
                                            }
                                            .addOnFailureListener { exception ->
                                                // Erro ao salvar dados
                                                errorMessage = "Erro ao salvar dados no Firestore: ${exception.message}"
                                            }

                                    } else {
                                        errorMessage = "Erro ao criar conta. Tente novamente."
                                    }
                                }.launchIn(coroutineScope)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                enabled = validateForm(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00405A),
                    disabledContainerColor = Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "${translations["register_button"]}",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    if (mostrarPopup) {
        AlertDialog(
            onDismissRequest = { mostrarPopup = false },
            containerColor = Color(0xFFA1B8CC),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.mipmap.ic_regras), // Substitua pelo ID real do recurso do ícone
                        contentDescription = "Ícone de regras",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${translations["regras"]}", color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = R.mipmap.ic_regras), // Substitua pelo ID real do recurso do ícone
                        contentDescription = "Ícone de regras",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(50.dp)
                    )
                }
            },
            text = {
                Text(
                    "${translations["regulamento_registro"]}",
                    color = Color.White,
                    fontSize = 20.sp
                )
            },
            confirmButton = {
            },
            dismissButton = {
                Button(
                    onClick = { mostrarPopup = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8E1213),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("${translations["cancel_button"]}")
                }
            }
        )
    }
}
