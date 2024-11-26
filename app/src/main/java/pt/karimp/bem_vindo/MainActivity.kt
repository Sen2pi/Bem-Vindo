package pt.karimp.bem_vindo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pt.karimp.bem_vindo.paginas.Navegacao
import pt.karimp.bem_vindo.ui.theme.BemvindoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BemvindoTheme {
                Navegacao()
            }
        }
    }
}
