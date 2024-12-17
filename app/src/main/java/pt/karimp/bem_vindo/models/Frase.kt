package pt.karimp.bem_vindo.models

import android.R

data class Frase(
    val frase: List<String> = emptyList<String>(),
    val frase1Fr: String = "",
    val frase1: String = "",
    val frase1En: String = "",
    val frase1Es: String = "",
    val final: Boolean = false
)