package nicolasmachado777.com.github.cryptomonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import nicolasmachado777.com.github.cryptomonitor.ui.screens.QuoteScreen
import nicolasmachado777.com.github.cryptomonitor.ui.theme.CryptoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CryptoTheme {
                QuoteScreen()
            }
        }
    }
}