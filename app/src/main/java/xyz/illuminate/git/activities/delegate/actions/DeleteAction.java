package xyz.illuminate.git.activities.delegate.actions;

import android.content.DialogInterface;

import xyz.illuminate.git.R;
import xyz.illuminate.git.activities.RepoDetailActivity;
import xyz.illuminate.git.database.models.Repo;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DeleteAction extends RepoAction {

    public DeleteAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        mActivity.showMessageDialog(R.string.dialog_delete_repo_title,
                R.string.dialog_delete_repo_msg, R.string.label_delete,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mRepo.deleteRepo();
                        mActivity.finish();
                    }
                });
        mActivity.closeOperationDrawer();
    }
}
