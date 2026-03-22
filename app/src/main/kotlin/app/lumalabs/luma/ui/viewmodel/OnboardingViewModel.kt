package app.lumalabs.luma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lumalabs.luma.data.repository.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    val onboardingCompleted = preferenceRepository.onboardingCompleted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun completeOnboarding() {
        viewModelScope.launch {
            preferenceRepository.setOnboardingCompleted(true)
        }
    }
}
