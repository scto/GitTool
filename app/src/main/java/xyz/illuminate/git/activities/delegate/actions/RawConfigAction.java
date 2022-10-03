package xyz.illuminate.git.activities.delegate.actions;

import android.content.Intent;

import xyz.illuminate.git.activities.RepoDetailActivity;
import xyz.illuminate.git.activities.ViewFileActivity;
import xyz.illuminate.git.database.models.Repo;

/**
 * Created by phcoder on 05.12.15.
 */import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class RawConfigAction extends RepoAction {

    public RawConfigAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        Intent intent = new Intent(mActivity, ViewFileActivity.class);
        intent.putExtra(ViewFileActivity.TAG_FILE_NAME,
                mRepo.getDir().getAbsoluteFile() + "/.git/config");
        mActivity.startActivity(intent);
    }
}
