package xyz.illuminate.git.activities.delegate.actions;

import xyz.illuminate.git.R;
import xyz.illuminate.git.activities.RepoDetailActivity;
import xyz.illuminate.git.activities.SheimiFragmentActivity.OnEditTextDialogClicked;
import xyz.illuminate.git.database.models.Repo;
import xyz.illuminate.git.repo.tasks.SheimiAsyncTask.AsyncTaskPostCallback;
import xyz.illuminate.git.repo.tasks.repo.CherryPickTask;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CherryPickAction extends RepoAction {

    public CherryPickAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        mActivity.showEditTextDialog(R.string.dialog_cherrypick_title,
                R.string.dialog_cherrypick_msg_hint,
                R.string.dialog_label_cherrypick,
                new OnEditTextDialogClicked() {
                    @Override
                    public void onClicked(String text) {
                        cherrypick(text);
                    }
                });
        mActivity.closeOperationDrawer();
    }

    public void cherrypick(String commit) {
        CherryPickTask task = new CherryPickTask(mRepo, commit, new AsyncTaskPostCallback() {
            @Override
            public void onPostExecute(Boolean isSuccess) {
                mActivity.reset();
            }
        });
        task.executeTask();
    }

}
