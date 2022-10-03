package xyz.illuminate.git.activities.delegate.actions;

import xyz.illuminate.git.R;
import xyz.illuminate.git.activities.RepoDetailActivity;
import xyz.illuminate.git.activities.SheimiFragmentActivity.OnEditTextDialogClicked;
import xyz.illuminate.git.database.models.Repo;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class NewDirAction extends RepoAction {

    public NewDirAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        mActivity.showEditTextDialog(R.string.dialog_create_dir_title,
                R.string.dialog_create_dir_hint, R.string.label_create,
                new OnEditTextDialogClicked() {
                    @Override
                    public void onClicked(String text) {
                        mActivity.getFilesFragment().newDir(text);
                    }
                });
        mActivity.closeOperationDrawer();
    }
}
