package xyz.illuminate.git.utils;

/**
 * Exception in SecurePrefs processing.
 */
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SecurePrefsException extends Exception {

    public SecurePrefsException(String s) {
        super(s);
    }

    public SecurePrefsException(Exception e) {
        super(e);
    }
}
