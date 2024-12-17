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
    var progresso: Int = 0,
    var nivel: String = "Básico",
    val tipo: String = "",
    val preferenciaHorario: String = "",
    val Pontuacao: Int = 0,
    val aprender: Int = 0,
    val lingua: String = "",

    )
