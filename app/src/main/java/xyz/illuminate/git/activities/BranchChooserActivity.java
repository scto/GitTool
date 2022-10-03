package xyz.illuminate.git.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.eclipse.jgit.api.errors.CannotDeleteCurrentBranchException;
import org.eclipse.jgit.api.errors.GitAPIException;

import xyz.illuminate.git.R;
import xyz.illuminate.git.database.models.Repo;
import xyz.illuminate.git.dialogs.RenameBranchDialog;
import xyz.illuminate.git.exception.StopTaskException;
import xyz.illuminate.git.repo.tasks.SheimiAsyncTask.AsyncTaskPostCallback;
import xyz.illuminate.git.repo.tasks.repo.CheckoutTask;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class BranchChooserActivity extends SheimiFragmentActivity implements ActionMode.Callback {
    private static final String LOGTAG = BranchChooserActivity.class.getSimpleName();

    private Repo mRepo;
    private ListView mBranchTagList;
    private ProgressBar mLoadding;
    private BranchTagListAdapter mAdapter;
    private boolean mInActionMode;
    private String mChosenCommit;

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mInActionMode = false;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_mode_rename_branch:
                Bundle pathArg = new Bundle();
                pathArg.putString(RenameBranchDialog.FROM_COMMIT,
                        mChosenCommit);
                pathArg.putSerializable(Repo.TAG,
                        mRepo);
                mode.finish();
                RenameBranchDialog rbd = new RenameBranchDialog();
                rbd.setArguments(pathArg);
                rbd.show(getFragmentManager(), "rename-dialog");

                return true;
            case R.id.action_mode_delete:
                com.google.android.material.dialog.MaterialAlertDialogBuilder builder = new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.dialog_branch_delete) + " " + mChosenCommit)
                        .setMessage(R.string.dialog_branch_delete_msg)
                        .setPositiveButton(R.string.label_delete, (dialog, which) -> {
                            int commitType = Repo.getCommitType(mChosenCommit);
                            try {
                                switch (commitType) {
                                    case Repo.COMMIT_TYPE_HEAD:
                                        mRepo.getGit().branchDelete()
                                                .setBranchNames(mChosenCommit)
                                                .setForce(true)
                                                .call();
                                        break;
                                    case Repo.COMMIT_TYPE_TAG:
                                        mRepo.getGit().tagDelete()
                                                .setTags(mChosenCommit)
                                                .call();
                                        break;
                                }
                            } catch (StopTaskException e) {
                                Log.e(LOGTAG, "can't delete " + mChosenCommit, e);
                                runOnUiThread(() -> Toast.makeText(BranchChooserActivity.this, getString(R.string.cannot_delete_branch, mChosenCommit), Toast.LENGTH_LONG).show());
                            } catch (CannotDeleteCurrentBranchException e) {
                                Log.e(LOGTAG, "can't delete " + mChosenCommit, e);
                                runOnUiThread(() -> Toast.makeText(BranchChooserActivity.this, getString(R.string.cannot_delete_current_branch, mChosenCommit), Toast.LENGTH_LONG).show());
                            } catch (GitAPIException e) {
                                Log.e(LOGTAG, "can't delete " + mChosenCommit, e);
                                runOnUiThread(() -> Toast.makeText(BranchChooserActivity.this, getString(R.string.cannot_delete_branch, mChosenCommit), Toast.LENGTH_LONG).show());
                            }
                            refreshList();
                        })
                        .setNegativeButton(R.string.label_cancel, null);
                mode.finish();
                builder.show();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.action_mode_branch, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    public void refreshList() {
        mAdapter = new BranchTagListAdapter(this);
        mBranchTagList.setAdapter(mAdapter);
        String[] branches = mRepo.getBranches();
        String[] tags = mRepo.getTags();
        mAdapter.addAll(branches);
        mAdapter.addAll(tags);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = getLayoutInflater().inflate(R.layout.fragment_branches, null);
        mRepo = (Repo) getIntent().getSerializableExtra(Repo.TAG);
        mBranchTagList = (ListView) v.findViewById(R.id.branches);
        mLoadding = (ProgressBar) v.findViewById(R.id.loading);
        mAdapter = new BranchTagListAdapter(this);
        mBranchTagList.setAdapter(mAdapter);
        mBranchTagList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        setTitle(R.string.dialog_choose_branch_title);

        refreshList();

        mBranchTagList
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView,
                                            View view, int position, long id) {
                        String commitName = mAdapter.getItem(position);
                        CheckoutTask checkoutTask = new CheckoutTask(mRepo, commitName, null,
                                new AsyncTaskPostCallback() {
                                    @Override
                                    public void onPostExecute(Boolean isSuccess) {
                                        finish();
                                    }
                                });
                        mLoadding.setVisibility(View.VISIBLE);
                        mBranchTagList.setVisibility(View.GONE);
                        checkoutTask.executeTask();
                    }
                });

        mBranchTagList
                .setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView,
                                                   View view, int position, long id) {

                        if (mInActionMode) {
                            return true;
                        }

                        mInActionMode = true;
                        mChosenCommit = mAdapter.getItem(position);
                        BranchChooserActivity.this.startActionMode(BranchChooserActivity.this);
                        view.setSelected(true);
                        mAdapter.notifyDataSetChanged();
                        return true;
                    }
                });

        setContentView(v);
    }

    private static class ListItemHolder {
        public TextView commitTitle;
        public ImageView commitIcon;
    }

    private class BranchTagListAdapter extends ArrayAdapter<String> {

        public BranchTagListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            ListItemHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(
                        R.layout.listitem_dialog_choose_commit, parent, false);
                holder = new ListItemHolder();
                holder.commitTitle = (TextView) convertView
                        .findViewById(R.id.commitTitle);
                holder.commitIcon = (ImageView) convertView
                        .findViewById(R.id.commitIcon);
                convertView.setTag(holder);
            } else {
                holder = (ListItemHolder) convertView.getTag();
            }
            String commitName = getItem(position);
            String displayName = Repo.getCommitDisplayName(commitName);
            int commitType = Repo.getCommitType(commitName);
            switch (commitType) {
                case Repo.COMMIT_TYPE_HEAD:
                    holder.commitIcon.setImageResource(R.drawable.ic_branch_d);
                    break;
                case Repo.COMMIT_TYPE_TAG:
                    holder.commitIcon.setImageResource(R.drawable.ic_tag_d);
                    break;
            }
            holder.commitTitle.setText(displayName);

            // set if selected
            if (convertView.isSelected()) {
                convertView.setBackgroundColor(convertView.getContext().getResources().getColor(R.color.pressed_sgit));
            } else {
                convertView.setBackgroundColor(convertView.getContext().getResources().getColor(android.R.color.transparent));
            }
            return convertView;
        }

    }
}
