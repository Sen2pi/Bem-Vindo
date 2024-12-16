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


fun getUnreadMessages(userId: String, onMessagesCountChanged: (Int) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()

    // Obtenha as mensagens não lidas para o usuário conectado
    firestore.collection("messages")
        .whereEqualTo("toUserId", userId)
        .whereEqualTo("read", false)
        .get()
        .addOnSuccessListener { documents ->
            // Contabiliza as mensagens não lidas
            val unreadCount = documents.size()
            onMessagesCountChanged(unreadCount)
        }
        .addOnFailureListener { exception ->
            // Handle failure
            println("Error getting unread messages: ${exception.message}")
        }
}
fun getUnmarkedClasses(userId: String, onClassesCountChanged: (Int) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()

    // Obtenha as mensagens não lidas para o usuário conectado
    firestore.collection("aulas")
        .whereEqualTo("aluno", userId)
        .whereEqualTo("presencaConfirmada", false)
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
fun BottomNavBar(navController: NavController, userId: String) {
    var unreadMessagesCount by remember { mutableStateOf(0)}
    var unmarkedClass by remember { mutableStateOf(0)}
    val context = LocalContext.current
    // Chama a função para buscar mensagens não lidas sempre que o userId mudar
    LaunchedEffect(userId) {
        getUnreadMessages(userId) { count ->
            unreadMessagesCount = count
            if (unreadMessagesCount > 0) {
                // Dispara a notificação se houver novas mensagens não lidas
                sendNewMessageNotification(context)
            }
        }
        getUnmarkedClasses(userId) { count ->
            unmarkedClass = count
            if (unmarkedClass > 0) {
                // Dispara a notificação se houver novas aulas com presença nao marcada
                sendUnmarkedClassNotification(context)
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
                    painter = painterResource(id = R.mipmap.agenda_for),
                    contentDescription = "Agenda",
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
            onClick = { navController.navigate("agenda") },
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
                    painter = painterResource(id = R.mipmap.aprender_for),
                    contentDescription = "Aprender",
                    modifier = Modifier.size(50.dp), // Tamanho do ícone ajustado
                    tint = Color.Unspecified // Desativa a tintagem para preservar a cor original da imagem
                )
            },
            selected = false, // Atualize a lógica para seleção dinâmica
            onClick = { navController.navigate("aprender") },
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
            onClick = { navController.navigate("homeAluno") }
        )

        // Profile
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.mipmap.notas_for),
                    contentDescription = "Notas",
                    modifier = Modifier.size(50.dp), // Tamanho do ícone ajustado
                    tint = Color.Unspecified // Desativa a tintagem para preservar a cor original da imagem
                )
            },
            selected = false, // Atualize a lógica para seleção dinâmica
            onClick = { navController.navigate("notas") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                indicatorColor = Color(0xFF006377),
                unselectedIconColor = Color.LightGray,
                unselectedTextColor = Color.LightGray
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.mipmap.chat_for),
                    contentDescription = "Chat",
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
            onClick = { navController.navigate("chat") },
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
