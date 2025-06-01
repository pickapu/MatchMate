package com.picka.matchmate.repository

sealed class RefreshResult {
    object Loading : RefreshResult()
    object Success : RefreshResult()
    data class Error(val message: String) : RefreshResult()
}
