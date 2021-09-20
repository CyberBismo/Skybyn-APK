package social.app.wesocial;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

public class Settings extends PreferenceFragmentCompat {

    SwitchPreference switchPreference;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        boolean biometricPrompt = sharedPreferences.getBoolean(getString(R.string.biometric_prompt_key), false);
        switchPreference = (SwitchPreference) findPreference(getString(R.string.biometric_prompt_key));
        requireActivity().setTheme(R.style.PreferenceScreen);

        switchPreference.setChecked(biometricPrompt);

        switchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (switchPreference.isChecked()) {
                switchPreference.setChecked(false);
                editor.putBoolean(getString(R.string.biometric_prompt_key), false);
            } else {
                switchPreference.setChecked(true);
                editor.putBoolean(getString(R.string.biometric_prompt_key), true);
            }
            editor.apply();
            return false;
        });
    }


}