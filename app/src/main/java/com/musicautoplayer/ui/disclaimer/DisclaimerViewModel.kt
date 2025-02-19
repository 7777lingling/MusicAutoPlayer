package com.musicautoplayer.ui.disclaimer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.musicautoplayer.data.PreferenceManager

class DisclaimerViewModel(application: Application) : AndroidViewModel(application) {
    private val preferenceManager = PreferenceManager(application)
    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    fun hasAcceptedDisclaimer(): Boolean {
        return preferenceManager.hasAcceptedDisclaimer()
    }

    fun onAcceptClick() {
        preferenceManager.setDisclaimerAccepted(true)
        _navigationEvent.value = NavigationEvent.NavigateToMain
    }

    fun onDeclineClick() {
        _navigationEvent.value = NavigationEvent.Finish
    }

    sealed class NavigationEvent {
        object NavigateToMain : NavigationEvent()
        object Finish : NavigationEvent()
    }
} 