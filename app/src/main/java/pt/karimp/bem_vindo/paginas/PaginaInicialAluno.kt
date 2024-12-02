package pt.karimp.bem_vindo.paginas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.karimp.bem_vindo.API.LanguageSelector
import pt.karimp.bem_vindo.API.getTranslations
import pt.karimp.bem_vindo.R
import pt.karimp.bem_vindo.ui.theme.BottomNavBar

@Composable
fun PaginaInicialAluno(navController: NavController) {
    var selectedLanguage by remember { mutableStateOf("fr") } // Idioma inicial em Francês
    val translations = getTranslations(selectedLanguage) // Obter traduções com base no idioma selecionado

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
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

            // Icons in the top right corner
            Row(
                modifier = Modifier
                    .padding(16.dp)
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

            // Content Section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(25.dp),
                verticalArrangement = Arrangement.spacedBy(25.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.size(50.dp))
                // Progress Section
                ProgressSection(progress = 50, title = translations["progress_title"]!!)

                // Tutor Section
                TutorSection(
                    title = translations["tutor_title"]!!,
                    tutorName = "Karim Santos",
                    city = "Braga",
                    email = "karim@exemplo.com"
                )

                // Daily Phrase Section
                DailyPhrase(
                    title = translations["daily_phrase_title"]!!,
                    phrase = translations["daily_phrase"]!!
                )
            }
        }
    }
}

@Composable
fun ProgressSection(progress: Int, title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LinearProgressIndicator(
            progress = progress / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
        Text(
            text = "$progress%",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun TutorSection(title: String, tutorName: String, city: String, email: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.logo2),
                    contentDescription = "Tutor Image",
                    modifier = Modifier.size(48.dp)
                )
                Column {
                    Text(text = "Nom: $tutorName", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Ville: $city", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Email: $email", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun DailyPhrase(title: String, phrase: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "\"$phrase\"",
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            )
        }
    }
}




