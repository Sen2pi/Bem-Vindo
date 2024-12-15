package pt.karimp.bem_vindo.paginas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pt.karimp.bem_vindo.API.LanguageSelector
import pt.karimp.bem_vindo.API.getTranslations
import pt.karimp.bem_vindo.R
import pt.karimp.bem_vindo.auth.AuthResponse
import pt.karimp.bem_vindo.auth.AuthenticationManager


@Composable
fun PaginaDeRedefinicaoSenha(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val authenticationManager = remember { AuthenticationManager() }
    val coroutineScope = rememberCoroutineScope()
    var selectedLanguage by remember { mutableStateOf("fr") } // Idioma inicial em Francês
    val translations =
        getTranslations(selectedLanguage) // Obter traduções com base no idioma selecionado

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.mipmap.azulejo1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.60f)
        )
        Image(
            painter = painterResource(id = R.mipmap.logo_final1),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp).size(150.dp)
        )

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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = {
                    Icon(imageVector = Icons.Rounded.Email, contentDescription = null)
                },
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

            Spacer(modifier = Modifier.height(25.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        authenticationManager.redefinirSenha(email).collect { response ->
                            when (response) {
                                is AuthResponse.Success -> {
                                    successMessage = "${translations["password_mail"]}"
                                    errorMessage = null
                                    navController.navigate("login")
                                }
                                is AuthResponse.Error -> {
                                    errorMessage = response.message
                                    successMessage = null
                                }
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00405A),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "${translations["password_recovery_button"]}",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mensagem de erro ou sucesso
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
            successMessage?.let {
                Text(
                    text = it,
                    color = Color.Green,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}

