package pt.karimp.bem_vindo.API

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pt.karimp.bem_vindo.R

@Composable
fun LanguageSelector(selectedLanguage: String, onLanguageSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) } // Estado para controlar a visibilidade do menu suspenso
    val flagResource = when (selectedLanguage) {
        "pt" -> R.mipmap.flag_pt
        "fr" -> R.mipmap.flag_fr
        "en" -> R.mipmap.flag_en
        "es" -> R.mipmap.flag_es
        else -> R.mipmap.flag_fr // Usar a bandeira padrão (Inglês) se não houver seleção
    }

    Box {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                painter = painterResource(id = flagResource), // Ícone do botão de idioma
                contentDescription = "Change Language",
                modifier = Modifier.size(50.dp),
                tint = Color.Unspecified
            )
        }

        // DropdownMenu para mostrar as opções de idioma
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    onLanguageSelected("pt") // Atualiza o idioma para Português
                    expanded = false // Fecha o menu
                },
                text = { Text("Português") }
            )
            DropdownMenuItem(
                onClick = {
                    onLanguageSelected("fr") // Atualiza o idioma para Francês
                    expanded = false // Fecha o menu
                },
                text = { Text("Français") }
            )
            DropdownMenuItem(
                onClick = {
                    onLanguageSelected("en") // Atualiza o idioma para Inglês
                    expanded = false // Fecha o menu
                },
                text = { Text("English") }
            )
            DropdownMenuItem(
                onClick = {
                    onLanguageSelected("es") // Atualiza o idioma para Espanhol
                    expanded = false // Fecha o menu
                },
                text = { Text("Español") }
            )
        }
    }
}
