package gr.zimagegen.model

sealed interface CreditsUiState {
    data object NotSupported : CreditsUiState
    data object NotFetched : CreditsUiState
    data object Loading : CreditsUiState
    data class Available(
        val amount: Double,
        val unit: String = "credits"
    ) : CreditsUiState
    data object Unavailable : CreditsUiState
}