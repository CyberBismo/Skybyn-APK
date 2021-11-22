package social.app.wesocial;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

public class Settings extends PreferenceFragmentCompat {

    SwitchPreference biometricSwitchPreference, darkModePreference;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        boolean biometricPrompt = sharedPreferences.getBoolean(getString(R.string.biometric_prompt_key), false);
        boolean darkMode = sharedPreferences.getBoolean(getString(R.string.toggleDarkMode_key), false);


        biometricSwitchPreference = findPreference(getString(R.string.biometric_prompt_key));
        darkModePreference = findPreference(getString(R.string.toggleDarkMode_key));

        //requireActivity().setTheme(R.style.PreferenceScreen);

        biometricSwitchPreference.setChecked(biometricPrompt);
        darkModePreference.setChecked(darkMode);

        biometricSwitchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (biometricSwitchPreference.isChecked()) {
                biometricSwitchPreference.setChecked(false);
                editor.putBoolean(getString(R.string.biometric_prompt_key), false);
            } else {
                biometricSwitchPreference.setChecked(true);
                editor.putBoolean(getString(R.string.biometric_prompt_key), true);
            }
            editor.apply();
            return false;
        });


        darkModePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (darkModePreference.isChecked()) {
                darkModePreference.setChecked(false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean(getString(R.string.toggleDarkMode_key), false);
            } else {
                darkModePreference.setChecked(true);
                editor.putBoolean(getString(R.string.toggleDarkMode_key), true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            }
            editor.apply();
            return false;
        });
    }

}