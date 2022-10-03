package xyz.illuminate.git.activities.delegate.actions;

import xyz.illuminate.git.activities.RepoDetailActivity;
import xyz.illuminate.git.database.models.Repo;
import xyz.illuminate.git.repo.tasks.repo.AddToStageTask;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AddAllAction extends RepoAction {

    public AddAllAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        AddToStageTask addTask = new AddToStageTask(mRepo, ".");
        addTask.executeTask();
        mActivity.closeOperationDrawer();
    }

}
