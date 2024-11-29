package pt.karimp.bem_vindo.paginas

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navegacao() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { PaginaDeLogin(navController) }
        composable("register") { PaginaDeRegistro(navController) }
        composable("homeProfessor") { PaginaInicialProfessor(navController) }
        composable("homeAluno") { PaginaInicialAluno(navController) }
    }
}

