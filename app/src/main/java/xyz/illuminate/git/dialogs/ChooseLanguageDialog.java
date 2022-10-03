package xyz.illuminate.git.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.List;

import xyz.illuminate.git.R;
import xyz.illuminate.git.activities.ViewFileActivity;
import xyz.illuminate.git.utils.CodeGuesser;
import xyz.illuminate.git.views.SheimiDialogFragment;

/**
 * Created by sheimi on 8/16/13.
 */import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ChooseLanguageDialog extends SheimiDialogFragment {

    private ViewFileActivity mActivity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        mActivity = (ViewFileActivity) getActivity();

        final List<String> langs = CodeGuesser.getLanguageList();
        com.google.android.material.dialog.MaterialAlertDialogBuilder builder = new com.google.android.material.dialog.MaterialAlertDialogBuilder(mActivity);

        builder.setTitle(R.string.dialog_choose_language_title);
        builder.setItems(langs.toArray(new String[0]),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface,
                                        int position) {
                        String lang = langs.get(position);
                        String tag = CodeGuesser.getLanguageTag(lang);
                        mActivity.setLanguage(tag);
                    }
                });

        return builder.create();
    }

}
