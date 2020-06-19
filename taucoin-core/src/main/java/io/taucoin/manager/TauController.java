package io.taucoin.manager;

import io.taucoin.account.AccountManager;
import io.taucoin.chain.ChainManager;
import io.taucoin.datasource.KeyValueDataSource;
import io.taucoin.db.BlockDB;
import io.taucoin.db.StateDB;
import io.taucoin.listener.CompositeTauListener;
import io.taucoin.listener.TauListener;
import io.taucoin.util.Repo;

import com.frostwire.jlibtorrent.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TauController is the core controller of tau blockchain.
 * It manages all the components including ChainManager, RpcServer,
 * TorrentEngine and so on.
 */
public class TauController {

    private static final Logger logger = LoggerFactory.getLogger(TauController.class);

    // Tau listeners registery.
    private CompositeTauListener compositeTauListener
            = new CompositeTauListener();

    // AccountManager manages the key pair for the miner.
    private AccountManager accountManager = AccountManager.getInstance();

    private ChainManager chainManager;

    // state database
    private StateDB stateDB;

    // block database
    private BlockDB blockDB;

    /**
     * TauController constructor.
     *
     * @param repoPath the directory where data is stored.
     * @param key the pair of public key and private key.
     * @param stateDS state key-value database implementation.
     * @param blockDS block key-value database implementation.
     */
    public TauController(String repoPath, Pair<byte[], byte[]> key,
            KeyValueDataSource stateDS, KeyValueDataSource blockDS) {

        // set the root directory.
        Repo.setRepoPath(repoPath);
	// store public key and private key.
	this.accountManager.updateKey(key);

	// create state and block database.
	// If database does not exist, directly load.
	// If not exist, create new database.
	this.stateDB = new StateDB(stateDS);
	this.blockDB = new BlockDB(blockDS);
    }

    /**
     * Register TauListener.
     *
     * @param listener TauListener implementation.
     */
    public void registerListener(TauListener listener) {
        this.compositeTauListener.addListener(listener);
    }

    /**
     * Unregister TauListener.
     *
     * @param listener TauListener implementation.
     */
    public void unregisterListener(TauListener listener) {
        this.compositeTauListener.removeListener(listener);
    }

    /**
     * Update key pair for the miner.
     *
     * @param key pair of public key and private key.
     */
    public void updateKey(Pair<byte[], byte[]> key) {
        this.accountManager.updateKey(key);
    }

    /**
     * Get ChainManager reference.
     *
     * @return ChainManager
     */
    public ChainManager getChainManager() {
        return this.chainManager;
    }
}
