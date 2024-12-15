package pt.karimp.bem_vindo.paginas

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navegacao() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "homeAluno") {
        composable("login") { PaginaDeLogin(navController) }
        composable("register") { PaginaDeRegistro(navController) }
        composable("homeProfessor") { PaginaInicialProfessor(navController) }
        composable("homeAluno") { PaginaInicialAluno(navController) }
        composable("agenda") { Agenda(navController) }
        composable("notas") { Notas(navController) }
        composable("chat") { Chat(navController) }
        composable("aprender") { Aprender(navController) }
        composable("profile") { Profile(navController) }
        composable("resetPassword") {PaginaDeRedefinicaoSenha(navController)}
        composable("semprof") { SemProf(navController) }
    }
}

