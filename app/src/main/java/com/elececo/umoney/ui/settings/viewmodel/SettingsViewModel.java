package com.elececo.umoney.ui.settings.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elececo.umoney.data.model.UserPreferences;
import com.elececo.umoney.data.repository.UserPreferencesRepository;

public class SettingsViewModel extends ViewModel {
    private final UserPreferencesRepository preferencesRepository;
    private final MutableLiveData<Boolean> saveResult;

    public SettingsViewModel() {
        preferencesRepository = new UserPreferencesRepository();
        saveResult = new MutableLiveData<>();
    }

    public LiveData<UserPreferences> getDistributionSettings() {
        return preferencesRepository.getUserPreferences();
    }

    public LiveData<Boolean> getSaveResult() {
        return saveResult;
    }

    public void saveDistributionSettings(int needs, int wants, int savings) {
        preferencesRepository.saveUserPreferences(needs, wants, savings);
        saveResult.setValue(true);
    }
} 