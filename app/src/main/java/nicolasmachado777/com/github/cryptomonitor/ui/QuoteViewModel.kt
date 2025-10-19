package nicolasmachado777.com.github.cryptomonitor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import nicolasmachado777.com.github.cryptomonitor.service.MercadoBitcoinService
import nicolasmachado777.com.github.cryptomonitor.service.MercadoBitcoinServiceFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UiState(
    val loading: Boolean = false,
    val valueBRL: String? = null,
    val dateString: String? = null,
    val error: String? = null
)

class QuoteViewModel(
    private val service: MercadoBitcoinService = MercadoBitcoinServiceFactory().create()
) : ViewModel() {

    private val _ui = MutableStateFlow(UiState(loading = true))
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null) }
            try {
                val response = service.getTicker()
                if (response.isSuccessful) {
                    val body = response.body()
                    val lastValue = body?.ticker?.last?.toDoubleOrNull()

                    val valueBRL = lastValue?.let {
                        NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(it)
                    }

                    val date = body?.ticker?.date?.let { Date(it * 1000L) }
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    val dateStr = date?.let { sdf.format(it) }

                    _ui.value = UiState(
                        loading = false,
                        valueBRL = valueBRL,
                        dateString = dateStr
                    )
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Bad Request"
                        401 -> "Unauthorized"
                        403 -> "Forbidden"
                        404 -> "Not Found"
                        else -> "Unknown error"
                    }
                    _ui.value = UiState(loading = false, error = errorMessage)
                }
            } catch (e: Exception) {
                _ui.value = UiState(
                    loading = false,
                    error = "Falha na chamada: ${e.message}"
                )
            }
        }
    }
}