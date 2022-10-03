package xyz.illuminate.git.activities.delegate.actions;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.Set;

import xyz.illuminate.git.R;
import xyz.illuminate.git.activities.RepoDetailActivity;
import xyz.illuminate.git.database.models.Repo;
import xyz.illuminate.git.dialogs.DummyDialogListener;
import xyz.illuminate.git.repo.tasks.repo.PushTask;
import xyz.illuminate.git.views.SheimiDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class PushAction extends RepoAction {

    public PushAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    public static void push(Repo repo, RepoDetailActivity activity,
                            String remote, boolean pushAll, boolean forcePush) {
        PushTask pushTask = new PushTask(repo, remote, pushAll, forcePush,
                activity.new ProgressCallback(R.string.push_msg_init));
        pushTask.executeTask();
    }

    @Override
    public void execute() {
        Set<String> remotes = mRepo.getRemotes();
        if (remotes == null || remotes.isEmpty()) {
            mActivity.showToastMessage(R.string.alert_please_add_a_remote);
            return;
        }
        PushDialog pd = new PushDialog();
        pd.setArguments(mRepo.getBundle());
        pd.show(mActivity.getSupportFragmentManager(), "push-repo-dialog");
        mActivity.closeOperationDrawer();
    }

    public static class PushDialog extends SheimiDialogFragment {

        private Repo mRepo;
        private RepoDetailActivity mActivity;
        private CheckBox mPushAll;
        private CheckBox mForcePush;
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

            View layout = inflater.inflate(R.layout.dialog_push, null);
            mPushAll = (CheckBox) layout.findViewById(R.id.pushAll);
            mForcePush = (CheckBox) layout.findViewById(R.id.forcePush);
            mRemoteList = (ListView) layout.findViewById(R.id.remoteList);

            mAdapter = new ArrayAdapter<String>(mActivity,
                    android.R.layout.simple_list_item_1);
            Set<String> remotes = mRepo.getRemotes();
            mAdapter.addAll(remotes);
            mRemoteList.setAdapter(mAdapter);

            mRemoteList.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    String remote = mAdapter.getItem(position);
                    boolean isPushAll = mPushAll.isChecked();
                    boolean isForcePush = mForcePush.isChecked();
                    push(mRepo, mActivity, remote, isPushAll, isForcePush);
                    dismiss();
                }
            });

            builder.setTitle(R.string.dialog_push_repo_title)
                    .setView(layout)
                    .setNegativeButton(R.string.label_cancel,
                            new DummyDialogListener());
            return builder.create();
        }
    }

}
