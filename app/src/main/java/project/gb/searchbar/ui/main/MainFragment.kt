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
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import project.gb.searchbar.R
import project.gb.searchbar.databinding.FragmentMainBinding
import kotlinx.coroutines.*


class MainFragment : Fragment() {

    // region init
    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var constraintSetStart: ConstraintSet
    private lateinit var constraintSetEnd: ConstraintSet

    private var job: Job? = null
    // endregion

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

        // region Инициализируем ConstraintSets
        constraintSetStart = ConstraintSet()
        constraintSetStart.clone(constraintLayout)
        constraintSetEnd = ConstraintSet()
        constraintSetEnd.clone(requireContext(), R.layout.fragment_main_expanded)
        // endregion

        binding.textInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                expandEditText()
            }
        }
        binding.mainConstraintLayout.setOnClickListener {
            onEmptyAreaClicked()
        }
        // Подписываемся на StateFlow для отслеживания прогресса анимации
        lifecycleScope.launchWhenStarted {
            viewModel.animationProgress.collect { progress ->
                binding.progressBar.progress = progress
            }
        }

        checkLengthText()
        observedForChangeTextSearch()
        subscribeOnChangeState()
        observedForChangeExtInFieldSearch()
    }

    /**
     * Наблюдаем за изменениями текста поиска.
     * Получаем его из ViewModel
     */
    private fun observedForChangeTextSearch() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchText.collect {
                binding.resultTextView.text = it
            }
        }
    }

    /**
     * Слушатель кнопки поиска
     */
    private fun listenerButtonsSend() {
        binding.textInputLayout.setEndIconOnClickListener {
            viewModel.updateSearchText(binding.textInputEditText.text.toString())
            animationProgressBar()
        }
    }

    /**
     * Подписка на изменения состояния
     */
    private fun subscribeOnChangeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    State.Loading -> {
                        binding.progressBar.isVisible = true
                    }

                    is State.Success -> {
                        binding.progressBar.isVisible = false
                    }
                }
            }
        }
    }

    /**
     * Слушатель изменения текста в поле ввода поиска.
     * Перенаправляет введенный пользователем текст во ViewModel
     */
    private fun observedForChangeExtInFieldSearch() {
        binding.textInputEditText.addTextChangedListener { editable ->
            viewModel.lengthText(editable.toString())
        }
    }

    /**
     * Вспомогательный метод для проверки длинны введенного запроса
     */
    private fun checkLengthText() {
        lifecycleScope.launchWhenStarted {
            viewModel.searchString2.collect { searchString2 ->
                updateSearchButtonState(searchString2)
            }
        }
    }

    /**
     * Метод добавляет простую анимацию прогресс бару
     */
    private fun animationProgressBar() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            for (i in 0..100) {

                // Обновляем прогресс анимации в ViewModel
                viewModel.updateAnimationProgress(i)
                delay(30)
            }
            // Обнуляем прогресс анимации после завершения
            viewModel.updateAnimationProgress(0)
        }
    }

    /**
     * Метод добавляет иконку поиска к TextInputLayout.
     * Иконка поиска будет располагаться в конце поля ввода.
     * И проверяем какое кол-во символов введено.
     */
    private fun updateSearchButtonState(searchText: String) {
        val isSearchTextValid = searchText.length >= 3
        val searchIcon = ContextCompat.getDrawable(requireContext(), R.drawable.searched_icon)

        // Устанавливаем иконку
        binding.textInputLayout.endIconMode =
            if (isSearchTextValid) TextInputLayout
                .END_ICON_CUSTOM else TextInputLayout.END_ICON_NONE
        binding.textInputLayout.endIconDrawable = searchIcon
        listenerButtonsSend()
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

    /**
     * Обработчик нажатия на пустую область экрана
     */
    private fun onEmptyAreaClicked() {
        collapseEditText()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
