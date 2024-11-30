package pt.karimp.bem_vindo.ui.theme

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.karimp.bem_vindo.R // Certifique-se de usar o caminho correto para o recurso

@Composable
fun BottomNavBar(navController: NavController) {
    NavigationBar(
        containerColor = Color(0xFF005B7F), // Azul inspirado nos azulejos portugueses
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

        // Settings
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.mipmap.chat_for),
                    contentDescription = "Chat",
                    modifier = Modifier.size(50.dp), // Tamanho do ícone ajustado
                    tint = Color.Unspecified // Desativa a tintagem para preservar a cor original da imagem
                )
            },
            selected = false, // Atualize a lógica para seleção dinâmica
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
