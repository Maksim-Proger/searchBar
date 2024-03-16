package project.gb.searchbar.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val repository = MainRepository()

    private val _state = MutableStateFlow<State>(State.Success)
    val state = _state.asStateFlow()

    // MutableStateFlow для отслеживания текста поиска
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    // Данную переменную добавил как вспомогательную для проверки длины запроса
    private val _searchString2 = MutableStateFlow("")
    val searchString2 = _searchString2.asStateFlow()

    // Состояние прогресса анимации
    private val _animationProgress = MutableStateFlow(0)
    val animationProgress= _animationProgress.asStateFlow()

    fun updateSearchText(text: String) {
        viewModelScope.launch {
            _state.value = State.Loading
            delay(3000)
            _searchText.value = repository.getData(text)
            _state.value = State.Success
        }
    }

    fun lengthText(text: String) {
        _searchString2.value = text
    }

    /**
     * Функция для обновления прогресса анимации
     */
    fun updateAnimationProgress(progress: Int) {
        _animationProgress.value = progress
    }
}