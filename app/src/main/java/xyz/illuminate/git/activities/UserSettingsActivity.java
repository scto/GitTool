package xyz.illuminate.git.activities;

import android.os.Bundle;

import xyz.illuminate.git.fragments.SettingsFragment;

/**
 * Activity for user settings
 */import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class UserSettingsActivity extends SheimiFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
