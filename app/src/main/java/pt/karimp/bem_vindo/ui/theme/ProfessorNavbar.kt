package pt.karimp.bem_vindo.ui.theme

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.karimp.bem_vindo.R // Certifique-se de usar o caminho correto para o recurso
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import pt.karimp.bem_vindo.utils.sendNewMessageNotification
import pt.karimp.bem_vindo.utils.sendUnmarkedClassNotification
import pt.karimp.bem_vindo.utils.sendmarkedClassNotification

fun getmarkedProfessorClasses(userId: String, onClassesCountChanged: (Int) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()

    // Obtenha as mensagens não lidas para o usuário conectado
    firestore.collection("aulas")
        .whereEqualTo("professor", userId)
        .whereEqualTo("presencaConfirmada", true)
        .get()
        .addOnSuccessListener { documents ->
            // Contabiliza as mensagens não lidas
            val unreadCount = documents.size()
            onClassesCountChanged(unreadCount)
        }
        .addOnFailureListener { exception ->
            // Handle failure
            println("Error getting unrmarked classes: ${exception.message}")
        }
}

@Composable
fun ProfessorNavbar(navController: NavController, userId: String) {
    var unreadMessagesCount by remember { mutableStateOf(0)}
    var unmarkedClass by remember { mutableStateOf(0)}
    val context = LocalContext.current
    // Chama a função para buscar mensagens não lidas sempre que o userId mudar
    LaunchedEffect(userId) {
        getmarkedProfessorClasses(userId) { count ->
            unmarkedClass = count
            if (unmarkedClass > 0) {
                // Dispara a notificação se houver novas aulas com presença nao marcada
                sendmarkedClassNotification(context)
            }
        }
    }
    NavigationBar(
        containerColor = Color(0xFFA1B8CC), // Azul inspirado nos azulejos portugueses
    ) {

        // Search
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.mipmap.ic_alunos),
                    contentDescription = "Escolha Alunos",
                    modifier = Modifier.size(50.dp), // Tamanho do ícone ajustado
                    tint = Color.Unspecified // Desativa a tintagem para preservar a cor original da imagem
                )
                if (unmarkedClass > 0) {
                    Badge {
                        Text(text = unmarkedClass.toString())
                    }
                }
            },
            selected = false, // Atualize a lógica para seleção dinâmica
            onClick = { navController.navigate("escolhaAlunos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                indicatorColor = Color(0xFF006377),
                unselectedIconColor = Color.LightGray,
                unselectedTextColor = Color.LightGray
            )
        )
        // Notifications
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.mipmap.ic_darnota),
                    contentDescription = "Dar notas",
                    modifier = Modifier.size(50.dp), // Tamanho do ícone ajustado
                    tint = Color.Unspecified // Desativa a tintagem para preservar a cor original da imagem
                )
            },
            selected = false, // Atualize a lógica para seleção dinâmica
            onClick = { navController.navigate("darNotas") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                indicatorColor = Color(0xFF006377),
                unselectedIconColor = Color.LightGray,
                unselectedTextColor = Color.LightGray
            )
        )
        // Custom Home Button
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.mipmap.homebutton_bac),
                    contentDescription = "Home Button",
                    modifier = Modifier.size(50.dp), // Tamanho do ícone ajustado
                    tint = Color.Unspecified // Desativa a tintagem para preservar a cor original da imagem
                )
            },
            selected = false, // Atualize a lógica para seleção dinâmica
            onClick = { navController.navigate("homeProfessor") }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.mipmap.agenda_for),
                    contentDescription = "Agenda",
                    modifier = Modifier.size(50.dp),
                    tint = Color.Unspecified
                )
                if (unreadMessagesCount > 0) {
                    Badge {
                        Text(text = unreadMessagesCount.toString())
                    }
                }
            },
            selected = false,
            onClick = { navController.navigate("agendaProfessor") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                indicatorColor = Color(0xFF006377),
                unselectedIconColor = Color.LightGray,
                unselectedTextColor = Color.LightGray
            )
        )
    }
}
