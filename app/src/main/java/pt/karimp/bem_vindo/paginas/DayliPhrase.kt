package pt.karimp.bem_vindo.paginas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.karimp.bem_vindo.models.Phrase
import kotlin.random.Random

@Composable
fun DailyPhrase(title: String, chosenLanguage: String) {
    var phrases by remember { mutableStateOf(listOf<Phrase>()) }
    val db = FirebaseFirestore.getInstance()
    var fraseDoDia by remember { mutableStateOf<Phrase?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    suspend fun loadPhrases(db: FirebaseFirestore) {
        try {
            // Buscar as mensagens enviadas pelo usuário atual
            val phrasesQuery = db.collection("proverbios")
                .get().await()
            // Combinar as duas listas de mensagens
            val allMessages = mutableListOf<Phrase>()

            // Mapear as mensagens recebidas
            allMessages.addAll(phrasesQuery.documents.map { document ->
                document.toObject(Phrase::class.java) ?: Phrase()
            })
            // Ordenar as mensagens por timestamp
            phrases = allMessages

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    LaunchedEffect(Unit) {
        loading = true
        try {
            loadPhrases(db)
            if (phrases.isNotEmpty()) {
                // Calcula um índice baseado na data atual
                val randomIndex = Random(0).nextInt(phrases.size)
                // Seleciona a frase do dia
                fraseDoDia = phrases.get(randomIndex)

            } else {
                error = "Nenhuma frase encontrada."
            }
        } catch (e: Exception) {
            error = "Erro ao carregar dados: ${e.message}"
        } finally {
            loading = false
        }
    }

    if (loading) {
        // Exibir carregamento
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else if (error != null) {
        // Exibir erro
        Text("Erro: $error", color = Color.Red, modifier = Modifier.fillMaxSize())
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF005B7F))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
            )
            Text(
                text = "${fraseDoDia?.frase}",
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                color = Color.White
            )
            Text(
                text =
                if(chosenLanguage =="fr"){"${fraseDoDia?.definicaoFr}"}
                else if(chosenLanguage =="pt"){ "${fraseDoDia?.definicaoPt}"}
                else if(chosenLanguage =="en"){ "${fraseDoDia?.definicaoEn}"}
                else{"${fraseDoDia?.definicaoEs}"},
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                color = Color.White
            )
        }
    }
}

