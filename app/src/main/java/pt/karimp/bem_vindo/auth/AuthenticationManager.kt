package pt.karimp.bem_vindo.auth

import androidx.compose.runtime.snapshots.SnapshotApplyResult
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

public class AuthenticationManager {
    private val auth = Firebase.auth

    fun criarContaComEmail(
        nome: String,
        nss: String,
        nif: String,
        morada: String,
        cidade: String,
        codigoPostal: String,
        telemovel: String,
        email: String,
        password: String
    ): Flow<AuthResponse> =
        callbackFlow {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        trySend(AuthResponse.Success)
                    } else {
                        trySend(AuthResponse.Error(message = task.exception?.message ?: ""))
                    }
                }
            awaitClose()
        }

    fun loginComEmail(email: String, password: String):Flow<AuthResponse> =
    callbackFlow{
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                trySend(AuthResponse.Success)
            } else {
                trySend(AuthResponse.Error(message = task.exception?.message ?: ""))
            }
        }
        awaitClose()
    }

}
interface AuthResponse{
    data object Success : AuthResponse
    data class Error(val message: String): AuthResponse
}