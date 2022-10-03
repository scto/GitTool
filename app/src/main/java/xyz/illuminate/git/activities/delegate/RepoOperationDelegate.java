package xyz.illuminate.git.activities.delegate;

import static xyz.illuminate.git.repo.tasks.repo.DeleteFileFromRepoTask.DeleteOperationType;

import org.eclipse.jgit.lib.Ref;

import java.io.File;
import java.util.ArrayList;

import xyz.illuminate.git.activities.RepoDetailActivity;
import xyz.illuminate.git.activities.delegate.actions.AddAllAction;
import xyz.illuminate.git.activities.delegate.actions.AddRemoteAction;
import xyz.illuminate.git.activities.delegate.actions.CherryPickAction;
import xyz.illuminate.git.activities.delegate.actions.CommitAction;
import xyz.illuminate.git.activities.delegate.actions.ConfigAction;
import xyz.illuminate.git.activities.delegate.actions.DeleteAction;
import xyz.illuminate.git.activities.delegate.actions.DiffAction;
import xyz.illuminate.git.activities.delegate.actions.FetchAction;
import xyz.illuminate.git.activities.delegate.actions.MergeAction;
import xyz.illuminate.git.activities.delegate.actions.NewBranchAction;
import xyz.illuminate.git.activities.delegate.actions.NewDirAction;
import xyz.illuminate.git.activities.delegate.actions.NewFileAction;
import xyz.illuminate.git.activities.delegate.actions.PullAction;
import xyz.illuminate.git.activities.delegate.actions.PushAction;
import xyz.illuminate.git.activities.delegate.actions.QuickPushAction;
import xyz.illuminate.git.activities.delegate.actions.RawConfigAction;
import xyz.illuminate.git.activities.delegate.actions.RebaseAction;
import xyz.illuminate.git.activities.delegate.actions.RemoveRemoteAction;
import xyz.illuminate.git.activities.delegate.actions.RepoAction;
import xyz.illuminate.git.activities.delegate.actions.ResetAction;
import xyz.illuminate.git.activities.delegate.actions.UndoAction;
import xyz.illuminate.git.database.models.Repo;
import xyz.illuminate.git.repo.tasks.SheimiAsyncTask.AsyncTaskPostCallback;
import xyz.illuminate.git.repo.tasks.repo.AddToStageTask;
import xyz.illuminate.git.repo.tasks.repo.CheckoutFileTask;
import xyz.illuminate.git.repo.tasks.repo.CheckoutTask;
import xyz.illuminate.git.repo.tasks.repo.DeleteFileFromRepoTask;
import xyz.illuminate.git.repo.tasks.repo.MergeTask;
import xyz.illuminate.git.tasks.repo.UpdateIndexTask;
import xyz.illuminate.git.utils.FsUtils;

public class RepoOperationDelegate {
    private Repo mRepo;
    private RepoDetailActivity mActivity;
    private ArrayList<RepoAction> mActions = new ArrayList<>();

    public RepoOperationDelegate(Repo repo, RepoDetailActivity activity) {
        mRepo = repo;
        mActivity = activity;
        initActions();
    }

    private void initActions() {
        mActions.add(new NewBranchAction(mRepo, mActivity));
        mActions.add(new PullAction(mRepo, mActivity));
        mActions.add(new PushAction(mRepo, mActivity));
        mActions.add(new QuickPushAction(mRepo, mActivity));
        mActions.add(new AddAllAction(mRepo, mActivity));
        mActions.add(new CommitAction(mRepo, mActivity));
        mActions.add(new UndoAction(mRepo, mActivity));
        mActions.add(new ResetAction(mRepo, mActivity));
        mActions.add(new MergeAction(mRepo, mActivity));
        mActions.add(new FetchAction(mRepo, mActivity));
        mActions.add(new RebaseAction(mRepo, mActivity));
        mActions.add(new CherryPickAction(mRepo, mActivity));
        mActions.add(new DiffAction(mRepo, mActivity));
        mActions.add(new NewFileAction(mRepo, mActivity));
        mActions.add(new NewDirAction(mRepo, mActivity));
        mActions.add(new AddRemoteAction(mRepo, mActivity));
        mActions.add(new RemoveRemoteAction(mRepo, mActivity));
        mActions.add(new DeleteAction(mRepo, mActivity));
        mActions.add(new RawConfigAction(mRepo, mActivity));
        mActions.add(new ConfigAction(mRepo, mActivity));
    }

    public void executeAction(int key) {
        RepoAction action = mActions.get(key);
        if (action == null)
            return;
        action.execute();
    }

    public void checkoutCommit(final String commitName) {
        CheckoutTask checkoutTask = new CheckoutTask(mRepo, commitName,
                null, new AsyncTaskPostCallback() {
            @Override
            public void onPostExecute(Boolean isSuccess) {
                mActivity.reset(commitName);
            }
        });
        checkoutTask.executeTask();
    }

    public void checkoutCommit(final String commitName, final String branch) {
        CheckoutTask checkoutTask = new CheckoutTask(mRepo, commitName, branch,
                new AsyncTaskPostCallback() {
                    @Override
                    public void onPostExecute(Boolean isSuccess) {
                        mActivity.reset(branch);
                    }
                });
        checkoutTask.executeTask();
    }

    public void mergeBranch(final Ref commit, final String ffModeStr,
                            final boolean autoCommit) {
        MergeTask mergeTask = new MergeTask(mRepo, commit, ffModeStr,
                autoCommit, new AsyncTaskPostCallback() {
            @Override
            public void onPostExecute(Boolean isSuccess) {
                mActivity.reset();
            }
        });
        mergeTask.executeTask();
    }

    public void addToStage(String filepath) {
        String relative = getRelativePath(filepath);
        AddToStageTask addToStageTask = new AddToStageTask(mRepo, relative);
        addToStageTask.executeTask();
    }

    public void checkoutFile(String filepath) {
        String relative = getRelativePath(filepath);
        CheckoutFileTask task = new CheckoutFileTask(mRepo, relative, null);
        task.executeTask();
    }

    public void deleteFileFromRepo(String filepath, DeleteOperationType deleteOperationType) {
        String relative = getRelativePath(filepath);
        DeleteFileFromRepoTask task = new DeleteFileFromRepoTask(mRepo,
                relative, deleteOperationType, new AsyncTaskPostCallback() {
            @Override
            public void onPostExecute(Boolean isSuccess) {
                // TODO Auto-generated method stub
                mActivity.getFilesFragment().reset();
            }
        });
        task.executeTask();
    }

    private String getRelativePath(String filepath) {
        File base = mRepo.getDir();
        return FsUtils.getRelativePath(new File(filepath), base);
    }


    public void updateIndex(final String mFilePath, final int newMode) {
        String relative = getRelativePath(mFilePath);
        UpdateIndexTask task = new UpdateIndexTask(mRepo, relative, newMode);
        task.executeTask();
    }
}
