package xyz.illuminate.git.activities.delegate.actions;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.Set;

import timber.log.Timber;
import xyz.illuminate.git.R;
import xyz.illuminate.git.activities.RepoDetailActivity;
import xyz.illuminate.git.database.models.Repo;
import xyz.illuminate.git.dialogs.DummyDialogListener;
import xyz.illuminate.git.views.SheimiDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class RemoveRemoteAction extends RepoAction {

    public RemoveRemoteAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    public static void removeRemote(Repo repo, RepoDetailActivity activity, String remote) throws IOException {
        repo.removeRemote(remote);
        activity.showToastMessage(R.string.success_remote_removed);
    }

    @Override
    public void execute() {
        Set<String> remotes = mRepo.getRemotes();
        if (remotes == null || remotes.isEmpty()) {
            mActivity.showToastMessage(R.string.alert_please_add_a_remote);
            return;
        }

        RemoveRemoteDialog dialog = new RemoveRemoteDialog();
        dialog.setArguments(mRepo.getBundle());
        dialog.show(mActivity.getSupportFragmentManager(), "remove-remote-dialog");
        mActivity.closeOperationDrawer();
    }

    public static class RemoveRemoteDialog extends SheimiDialogFragment {
        private Repo mRepo;
        private RepoDetailActivity mActivity;
        private ListView mRemoteList;
        private ArrayAdapter<String> mAdapter;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);
            Bundle args = getArguments();
            if (args != null && args.containsKey(Repo.TAG)) {
                mRepo = (Repo) args.getSerializable(Repo.TAG);
            }

            mActivity = (RepoDetailActivity) getActivity();
            com.google.android.material.dialog.MaterialAlertDialogBuilder builder = new com.google.android.material.dialog.MaterialAlertDialogBuilder(mActivity);
            LayoutInflater inflater = mActivity.getLayoutInflater();

            View layout = inflater.inflate(R.layout.dialog_remove_remote, null);
            mRemoteList = (ListView) layout.findViewById(R.id.remoteList);

            mAdapter = new ArrayAdapter<String>(mActivity,
                    android.R.layout.simple_list_item_1);
            Set<String> remotes = mRepo.getRemotes();
            mAdapter.addAll(remotes);
            mRemoteList.setAdapter(mAdapter);

            mRemoteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    String remote = mAdapter.getItem(position);
                    try {
                        removeRemote(mRepo, mActivity, remote);
                    } catch (IOException e) {
                        Timber.e(e);
                        mActivity.showMessageDialog(R.string.dialog_error_title, getString(R.string.error_something_wrong));
                    }
                    dismiss();
                }
            });

            builder.setTitle(R.string.dialog_remove_remote_title)
                    .setView(layout)
                    .setNegativeButton(R.string.label_cancel, new DummyDialogListener());
            return builder.create();
        }
    }

}
