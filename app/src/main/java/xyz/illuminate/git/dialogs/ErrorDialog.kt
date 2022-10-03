package xyz.illuminate.git.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.dialog_error.view.*
import timber.log.Timber
import xyz.illuminate.git.BuildConfig
import xyz.illuminate.git.R
import xyz.illuminate.git.views.SheimiDialogFragment

class ErrorDialog : SheimiDialogFragment() {
    private var mThrowable: Throwable? = null

    @StringRes
    private var mErrorRes: Int = 0

    @StringRes
    var errorTitleRes: Int = 0
        get() = if (field != 0) field else R.string.dialog_error_title

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val builder = com.google.android.material.dialog.MaterialAlertDialogBuilder(rawActivity)
        val inflater = rawActivity.layoutInflater
        val layout = inflater.inflate(R.layout.dialog_error, null)
        val details = when (mThrowable) {
            is Exception -> {
                (mThrowable as Exception).message
            }
            else -> ""
        }
        layout.error_message.setText(getString(mErrorRes) + "\n" + details)

        builder.setView(layout)

        // set button listener
        builder.setTitle(errorTitleRes)
        builder.setPositiveButton(
            getString(R.string.label_ok),
            DummyDialogListener()
        )
        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as AlertDialog
        val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE) as Button
        positiveButton.setOnClickListener {
            if (BuildConfig.DEBUG) {
                // when debugging just log the exception
                if (mThrowable != null) {
                    Timber.e(mThrowable);
                } else {
                    Timber.e(if (mErrorRes != 0) getString(mErrorRes) else "")
                }
            }
            dismiss()
        }
    }

    fun setThrowable(throwable: Throwable?) {
        mThrowable = throwable
    }

    fun setErrorRes(@StringRes errorRes: Int) {
        mErrorRes = errorRes
    }
}
