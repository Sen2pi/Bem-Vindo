package pt.karimp.bem_vindo.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.karimp.bem_vindo.API.LanguageSelector
import pt.karimp.bem_vindo.API.getTranslations
import pt.karimp.bem_vindo.R

@Composable
fun topNavBar(navController: NavController, selectedLanguage: String) {
    Row(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        // Language Selector
        LanguageSelector(
            selectedLanguage = selectedLanguage.toString(),
            onLanguageSelected = { selectedLanguage  }
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
}