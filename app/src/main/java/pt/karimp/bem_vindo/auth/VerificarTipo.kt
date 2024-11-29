package pt.karimp.bem_vindo.auth

import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

fun verificaTipoUsuario(navController: NavController, email: String) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users")
        .whereEqualTo("email", email)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents[0]
                val tipo = document.getString("tipo") ?: "Desconhecido"

                when (tipo) {
                    "Aluno" -> {
                        navController.navigate("homeAluno") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                    "Professor" -> {
                        navController.navigate("homeProfessor") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                    else -> {
                        // Tipo desconhecido
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            } else {
                // Nenhum usuário encontrado
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
        .addOnFailureListener {
            // Falha ao acessar o Firestore
            navController.navigate("login") {
                popUpTo("login") { inclusive = true }
            }
        }
}

