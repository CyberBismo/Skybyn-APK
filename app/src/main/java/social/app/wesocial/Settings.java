package social.app.wesocial;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

public class Settings extends PreferenceFragmentCompat {

SwitchPreference switchPreference ;
SharedPreferences sharedPreferences;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        sharedPreferences= getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        Boolean biometricPrompt = sharedPreferences.getBoolean(getString(R.string.biometric_prompt_key),false);
        switchPreference = (SwitchPreference) findPreference(getString(R.string.biometric_prompt_key));
        if (biometricPrompt) {
            switchPreference.setChecked(true);
        }else{
            switchPreference.setChecked(false);
        }

        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (switchPreference.isChecked()) {
                    switchPreference.setChecked(false);
                    editor.putBoolean(getString(R.string.biometric_prompt_key), false);
                    Toast.makeText(getContext(),"Checked",Toast.LENGTH_SHORT).show();
                }else{
                    switchPreference.setChecked(true);
                    editor.putBoolean(getString(R.string.biometric_prompt_key), true);
                    Toast.makeText(getContext(),"unChecked",Toast.LENGTH_SHORT).show();
                }
                editor.commit();
                Toast.makeText(getContext(),"Saved",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }




}