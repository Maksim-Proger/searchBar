package project.gb.searchbar.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    // MutableStateFlow для отслеживания текста поиска
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    /**
     * Метод для обновления текста поиска
     */
    fun updateSearchText(text: String) {
        _searchText.value = text
    }


}