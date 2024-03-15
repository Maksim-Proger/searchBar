package project.gb.searchbar.ui.main

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import project.gb.searchbar.R
import project.gb.searchbar.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var constraintSetStart: ConstraintSet
    private lateinit var constraintSetEnd: ConstraintSet

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        constraintLayout = binding.mainConstraintLayout

        // Инициализируем ConstraintSets
        constraintSetStart = ConstraintSet()
        constraintSetStart.clone(constraintLayout)
        constraintSetEnd = ConstraintSet()
        constraintSetEnd.clone(requireContext(), R.layout.fragment_main_expanded)

        binding.textInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                expandEditText()
            }
        }
        binding.mainConstraintLayout.setOnClickListener {
            onEmptyAreaClicked()
        }

        observedForChangeTextSearch()
        observedForChangeExtInFieldSearch()
    }

    /**
     * Наблюдаем за изменениями текста поиска
     */
    private fun observedForChangeTextSearch() {
        lifecycleScope.launchWhenStarted {
            viewModel.searchText.collect { searchText ->
                // Обновляем состояние кнопки поиска на основе длины текста
                updateSearchButtonState(searchText)
            }
        }
    }

    /**
     * Слушатель изменения текста в поле ввода поиска.
     * Перенаправляет введенный пользователем текст во ViewModel
     */
    private fun observedForChangeExtInFieldSearch() {
        binding.textInputEditText.addTextChangedListener { editable ->
            viewModel.updateSearchText(editable.toString())
        }
    }

    /**
     * Добавляет иконку поиска к TextInputLayout.
     * Иконка поиска будет располагаться в конце поля ввода.
     * И проверяем какое кол-во символов введено.
     */
    private fun updateSearchButtonState(searchText: String) {
        // Проверяем, что длина текста поиска больше или равна 3 символам
        val isSearchTextValid = searchText.length >= 3

        // Получаем ссылку на ресурс иконки поиска
        val searchIcon = ContextCompat.getDrawable(requireContext(), R.drawable.searched_icon)

        // Устанавливаем иконку поиска в качестве конечной иконки, если длина текста поиска достаточна
        binding.textInputLayout.endIconMode =
            if (isSearchTextValid) TextInputLayout
                .END_ICON_CUSTOM else TextInputLayout.END_ICON_NONE
        binding.textInputLayout.endIconDrawable = searchIcon
    }

    /**
     * Метод для анимации раскрытия поля ввода
     */
    private fun expandEditText() {
        TransitionManager.beginDelayedTransition(constraintLayout, ChangeBounds().apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
        })
        constraintSetEnd.applyTo(constraintLayout)
    }

    /**
     * Метод для анимации сворачивания поля ввода
     */
    private fun collapseEditText() {
        TransitionManager.beginDelayedTransition(constraintLayout, ChangeBounds().apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
        })
        constraintSetStart.applyTo(constraintLayout)
        binding.textInputEditText.clearFocus() // Сброс фокуса, чтобы клавиатура скрылась
    }

    // Обработчик нажатия на пустую область экрана
    private fun onEmptyAreaClicked() {
        collapseEditText()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
