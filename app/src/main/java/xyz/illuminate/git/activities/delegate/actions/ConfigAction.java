package xyz.illuminate.git.activities.delegate.actions;

import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import timber.log.Timber;
import xyz.illuminate.git.R;
import xyz.illuminate.git.activities.RepoDetailActivity;
import xyz.illuminate.git.database.models.GitConfig;
import xyz.illuminate.git.database.models.Repo;
import xyz.illuminate.git.databinding.DialogRepoConfigBinding;
import xyz.illuminate.git.exception.StopTaskException;

/**
 * Action to display configuration for a Repo
 */import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ConfigAction extends RepoAction {


    public ConfigAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {

        try {
            DialogRepoConfigBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.dialog_repo_config, null, false);
            GitConfig gitConfig = new GitConfig(mRepo);
            binding.setViewModel(gitConfig);

            com.google.android.material.dialog.MaterialAlertDialogBuilder builder = new com.google.android.material.dialog.MaterialAlertDialogBuilder(mActivity);
            builder.setView(binding.getRoot())
                    .setNeutralButton(R.string.label_done, null)
                    .create().show();

        } catch (StopTaskException e) {
            //FIXME: show error to user
            Timber.e(e);
        }
    }

}
