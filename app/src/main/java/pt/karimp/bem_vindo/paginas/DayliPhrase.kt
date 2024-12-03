package pt.karimp.bem_vindo.paginas

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.internal.isLiveLiteralsEnabled
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.play.integrity.internal.i
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pt.karimp.bem_vindo.models.Like
import pt.karimp.bem_vindo.models.Phrase
import kotlin.random.Random

@Composable
fun DailyPhrase(title: String, chosenLanguage: String, userDocumentID: String) {
    val db = FirebaseFirestore.getInstance()
    var phrases by remember { mutableStateOf<List<Phrase>>(emptyList()) }
    var fraseDoDia by remember { mutableStateOf<Phrase?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var phraseDocumentId by remember { mutableStateOf("") }
    var likeData by remember { mutableStateOf<Like?>(null) }
    var isLiked by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val currentUserId = rememberUpdatedState(userDocumentID)
    // Carregar frases e determinar a frase do dia
    LaunchedEffect(Unit) {
        loading = true
        try {
            val phrasesQuery = db.collection("proverbios").get().await()
            val allPhrases = phrasesQuery.documents.mapNotNull { it.toObject(Phrase::class.java) }
            phrases = allPhrases

            if (phrases.isNotEmpty()) {
                val randomIndex = Random(0).nextInt(phrases.size)
                fraseDoDia = phrases[randomIndex]

                fraseDoDia?.let {
                    val phraseQuery =
                        db.collection("proverbios").whereEqualTo("frase", it.frase).get().await()
                    if (!phraseQuery.isEmpty) {
                        phraseDocumentId = phraseQuery.documents.first().id
                    }
                }
            } else {
                error = "Nenhuma frase encontrada."
            }
            val currentId = currentUserId.value
            println("Consultando com user: $currentId e documento: $phraseDocumentId")
            val likeQuery = db.collection("likes")
                .whereEqualTo("documento", phraseDocumentId)
                .whereEqualTo("user", currentId)
                .get()
                .await()
            println("Número de documentos encontrados: ${likeQuery.size()}")
            if (likeQuery.documents.isNotEmpty()) {
                val likeDocument = likeQuery.documents.first()
                println("Documento encontrado: ${likeDocument.data}") // Verifique o documento

                try {
                    likeData = likeDocument.toObject(Like::class.java)
                    println("Objeto Like: $likeData")
                    isLiked = likeData?.like ?: false
                } catch (e: Exception) {
                    println("Erro ao mapear o documento para Like: ${e.message}")
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            error = "Erro ao carregar dados: ${e.message}"
        } finally {
            loading = false
        }
    }

    // UI
    if (loading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else if (error != null) {
        Text("Erro: $error", color = Color.Red, modifier = Modifier.fillMaxSize())
    } else {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFA1B8CC))
        ) {
            Column(
                modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "${fraseDoDia?.frase}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                    color = Color.White
                )
                Text(
                    text = when (chosenLanguage) {
                        "fr" -> fraseDoDia?.definicaoFr
                        "pt" -> fraseDoDia?.definicaoPt
                        "en" -> fraseDoDia?.definicaoEn
                        else -> fraseDoDia?.definicaoEs
                    } ?: "",
                    style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                    color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            try {
                                if (isLiked == true) {
                                    isLiked = false
                                    val documentsQuery = db.collection("likes")
                                        .whereEqualTo("documento", phraseDocumentId)
                                        .whereEqualTo("user", userDocumentID).get().await()
                                    val document = documentsQuery.documents.first()
                                    if (document != null) {
                                        // Atualiza os campos no Firestore
                                        document.reference.update(
                                            mapOf(
                                                "user" to likeData!!.user,
                                                "documento" to likeData!!.documento,
                                                "like" to isLiked
                                            )
                                        )
                                    }

                                } else if (isLiked == false) {
                                    isLiked = true
                                    val documentsQuery = db.collection("likes")
                                        .whereEqualTo("documento", phraseDocumentId)
                                        .whereEqualTo("user", userDocumentID).get().await()
                                    val document = documentsQuery.documents.first()
                                    if (document != null) {
                                        // Atualiza os campos no Firestore
                                        document.reference.update(
                                            mapOf(
                                                "user" to likeData!!.user,
                                                "documento" to likeData!!.documento,
                                                "like" to isLiked
                                            )
                                        )
                                    }
                                } else {
                                    db.collection("likes").add(
                                        mapOf(
                                            "user" to userDocumentID,
                                            "documento" to phraseDocumentId,
                                            "like" to true
                                        )
                                    ).await()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isLiked) "like" else "UnLike",
                            tint = if (isLiked) Color.Red else Color.White
                        )
                    }
                }
            }
        }
    }
}




