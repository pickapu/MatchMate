package com.picka.matchmate.viewmodel

import androidx.lifecycle.*
import com.picka.matchmate.local.ProfileStatus
import com.picka.matchmate.local.UserProfile
import com.picka.matchmate.repository.MatchRepository
import com.picka.matchmate.repository.RefreshResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val repository: MatchRepository
) : ViewModel() {

    private val _allProfiles = repository.getAllStoredProfiles()
        .asLiveData(context = viewModelScope.coroutineContext + Dispatchers.IO)
    val allProfiles: LiveData<List<UserProfile>> = _allProfiles

    private val _refreshState = MutableLiveData<RefreshResult>()
    val refreshState: LiveData<RefreshResult> = _refreshState

    init {
        fetchProfilesFromNetwork()
    }

    fun fetchProfilesFromNetwork() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.refreshProfiles().collect { result ->
                _refreshState.postValue(result)
            }
        }
    }

    fun setProfileStatus(email: String, status: ProfileStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProfileStatus(email, status)
        }
    }
}
