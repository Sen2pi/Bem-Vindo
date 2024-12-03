package pt.karimp.bem_vindo.models

data class User(
    val nome: String = "",
    val email: String = "",
    val cidade: String = "",
    val codigoPostal: String = "",
    val morada: String = "",
    val nif: String = "",
    val nss: String = "",
    val telefone: String = "",
    val professor: String = "",
    val progresso: Int = 0,
    val nivel: String = "Básico",
    val tipo: String = ""
)
