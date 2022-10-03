package xyz.illuminate.git.fragments;

import xyz.illuminate.git.activities.RepoDetailActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public abstract class RepoDetailFragment extends BaseFragment {

    public RepoDetailActivity getRawActivity() {
        return (RepoDetailActivity) super.getRawActivity();
    }

}
