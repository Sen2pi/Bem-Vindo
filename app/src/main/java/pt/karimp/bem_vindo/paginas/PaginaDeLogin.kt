package pt.karimp.bem_vindo.paginas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
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
fun PaginaDeLogin(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val authenticationManager = remember { AuthenticationManager() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagem de background
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

        // Conteúdo da página
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
                    focusedContainerColor = Color(0xFF00405A), // Background when focused
                    unfocusedContainerColor = Color(0xFF00405A),  // Background when not focused
                    focusedBorderColor = Color.DarkGray,    // Border color when focused
                    unfocusedBorderColor = Color.LightGray, // Border color when not focused
                    focusedTextColor = Color.White,         // Text color when focused
                    unfocusedTextColor = Color.White,        // Text color when not focuse
                    focusedLeadingIconColor = Color.White,    // Leading icon color when focused
                    unfocusedLeadingIconColor = Color.White,  // Leading icon color when not focused
                    focusedTrailingIconColor = Color.White,   // Optional: Customize trailing icon color
                    unfocusedTrailingIconColor = Color.White, // Optional: Customize trailing icon color
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,      // Text color when not focused
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text(
                    text = "Mot de Pass",

                    ) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(imageVector = Icons.Rounded.Lock, contentDescription = null)
                },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF00405A), // Background when focused
                    unfocusedContainerColor = Color(0xFF00405A),  // Background when not focused
                    focusedBorderColor = Color.DarkGray,    // Border color when focused
                    unfocusedBorderColor = Color.LightGray, // Border color when not focused
                    focusedTextColor = Color.White,         // Text color when focused
                    unfocusedTextColor = Color.White,        // Text color when not focuse
                    focusedLeadingIconColor = Color.White,    // Leading icon color when focused
                    unfocusedLeadingIconColor = Color.White,  // Leading icon color when not focused
                    focusedTrailingIconColor = Color.White,   // Optional: Customize trailing icon color
                    unfocusedTrailingIconColor = Color.White, // Optional: Customize trailing icon color
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                )
            )

            Spacer(modifier = Modifier.height(25.dp))

            Button(
                onClick = {

                        authenticationManager.loginComEmail(email, senha).onEach { response ->
                            if (response is AuthResponse.Success) {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                errorMessage = "Email ou mot de pass erronés! Inserez des identifiants valides."
                            }
                        }.launchIn(coroutineScope)

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00405A), // Background color of the button
                    contentColor = Color.White          // Text/Icon color inside the button
                ),
                modifier = Modifier
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),

            ) {
                Text(
                    text = "Se Connecter",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("register") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00405A), // Background color of the button
                    contentColor = Color.White          // Text/Icon color inside the button
                ),
                modifier = Modifier
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "S'enregistrer",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
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
