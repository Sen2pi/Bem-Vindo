package pt.karimp.bem_vindo.models
import com.google.firebase.Timestamp

data class Aula(
    val professor: String ="",
    val criacao: Timestamp = Timestamp.now(),
    val dataEHora: Timestamp = Timestamp.now(),
    val aluno: String = "",
    val presencaConfirmada: Boolean = false,
    val presente: Boolean = false,
    val avaliacao: Int = 0,
    val nivel: String = "",
    val sala: String = "",
)