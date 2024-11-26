package pt.karimp.bem_vindo.paginas

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Lock
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pt.karimp.bem_vindo.R
import pt.karimp.bem_vindo.auth.AuthResponse
import pt.karimp.bem_vindo.auth.AuthenticationManager

@Composable
fun PaginaDeRegistro(navController: NavController) {
    val campos = listOf(
        "Nom Complet",
        "Numéro de Sécurité Social",
        "NIF",
        "Adresse",
        "Ville",
        "Code Postal",
        "Téléphone",
        "Email",
        "Mot de Pass"
    )

    val valores = remember { mutableStateListOf(*Array(campos.size) { "" }) }
    val errorMessages = remember { mutableStateListOf(*Array(campos.size) { "" }) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val authenticationManager = remember { AuthenticationManager() }
    val coroutineScope = rememberCoroutineScope()

    // Função de validação similar ao código original
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


        // Validação da Senha (mínimo de 8 caracteres, uma letra minúscula e uma maiúscula)
        val password = valores[8]
        if (password.length < 8 || !password.any { it.isLowerCase() } || !password.any { it.isUpperCase() }) {
            errors[8] = "Le Mot de Passe doit comporter au moins 8 caractères, incluant une lettre majuscule et une lettre minuscule."
        }

        // Verifique se há outros campos vazios (que precisam ser preenchidos)
        campos.forEachIndexed { index, label ->
            if (label != "Nom Complet" && label != "Adresse" && label != "Ville" && label != "Téléphone" && valores[index].isEmpty()) {
                errors[index] = "$label ne peut pas être vide."
            }
        }

        // Atualizar mensagens de erro
        errorMessages.forEachIndexed { index, _ ->
            errorMessages[index] = errors[index] ?: ""
        }

        // Retorna true se o formulário for válido (sem erros)
        return errors.isEmpty()
    }

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
            painter = painterResource(id = R.mipmap.logo2),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Campos de entrada de formulário
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
                            focusedContainerColor = Color(0xFF00405A),
                            unfocusedContainerColor = Color(0xFF00405A),
                            focusedBorderColor = Color.DarkGray,
                            unfocusedBorderColor = Color.LightGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White
                        )
                    )


                    if (errorMessages.isNotEmpty() && errorMessages.size > index && errorMessages[index].isNotEmpty()) {
                        Text(
                            text = errorMessages[index],
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão de registro
            Button(
                onClick = {
                    if (validateForm()) {
                        // Passando os valores para o método criarContaComEmail
                        authenticationManager.criarContaComEmail(
                            nome = valores[0],
                            nss = valores[1],
                            nif = valores[2],
                            morada = valores[3],
                            cidade = valores[4],
                            codigoPostal = valores[5],
                            telemovel = valores[6],
                            email = valores[7],
                            password = valores[8]
                        ).onEach { response ->
                            if (response is AuthResponse.Success) {
                                navController.navigate("home") {
                                    popUpTo("register") { inclusive = true }
                                }
                            } else {
                                errorMessage = "Erro ao criar conta. Tente novamente."
                            }
                        }.launchIn(coroutineScope)
                    }
                },
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                enabled = validateForm(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00405A),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "S'enregistrer",
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { navController.navigate("login") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00405A),
                    contentColor = Color.White
            )
            ) {
                // Ícone de retorno
                Icon(
                    imageVector = Icons.Rounded.Close, // Ícone de fechamento
                    contentDescription = "Retourner à la page de connexion",
                    modifier = Modifier.padding(end = 8.dp) // Espaço entre o ícone e o texto
                )
                // Texto do botão com a cor especificada
                Text(
                    text = "Retourner",
                    fontWeight = FontWeight.Bold,
                    color = Color.White, // Cor personalizada

                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mensagem de erro
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
}
