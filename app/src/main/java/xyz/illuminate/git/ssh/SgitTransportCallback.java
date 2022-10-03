package xyz.illuminate.git.ssh;

import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;

/**
 * Created by sheimi on 8/22/13.
 */import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SgitTransportCallback implements TransportConfigCallback {

    @Override
    public void configure(Transport tn) {
        if (tn instanceof SshTransport) {
            ((SshTransport) tn).setSshSessionFactory(SshSessionFactory.getInstance());
        }
    }
}
