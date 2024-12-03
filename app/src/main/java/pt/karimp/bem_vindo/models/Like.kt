package pt.karimp.bem_vindo.models

data class Like(
    val like: Boolean = false,
    val documento: String = "",
    val user: String = "",
)