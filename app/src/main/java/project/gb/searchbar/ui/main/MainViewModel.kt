package project.gb.searchbar.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: MainRepository
) : ViewModel() {
    private val _state = MutableStateFlow<State>(State.Success)

    // MutableStateFlow для отслеживания текста поиска
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _searchString2 = MutableStateFlow("")
    val searchString2 = _searchString2.asStateFlow()

    // Состояние прогресса анимации
    private val _animationProgress = MutableStateFlow(0)
    val animationProgress: StateFlow<Int> = _animationProgress


    fun updateSearchText(text: String) {

        _searchString2.value = text

        viewModelScope.launch {
            _state.value = State.Loading
            _searchText.value = repository.getData(text)
            _state.value = State.Success
        }
    }

    /**
     * Функция для обновления прогресса анимации
     */
    fun updateAnimationProgress(progress: Int) {
        _animationProgress.value = progress
    }
}