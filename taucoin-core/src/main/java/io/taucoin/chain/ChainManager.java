package io.taucoin.chain;

import io.taucoin.config.ChainConfig;
import io.taucoin.core.AccountState;
import io.taucoin.db.BlockDB;
import io.taucoin.db.KeyValueDataBaseFactory;
import io.taucoin.db.StateDBImpl;
import io.taucoin.listener.TauListener;
import io.taucoin.types.Transaction;
import io.taucoin.util.ByteArrayWrapper;
import io.taucoin.util.Repo;
import io.taucoin.types.Block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.*;

/**
 * ChainManager manages all blockchains for tau multi-chain system.
 * It's responsible for creating chain, following chain, unfollow chain,
 * pause chain, resume chain and so on.
 */
public class ChainManager {

    private static final Logger logger = LoggerFactory.getLogger("ChainManager");

    // Chain maps: chainID -> Chain.
    private Map<ByteArrayWrapper, Chain> chains
            = Collections.synchronizedMap(new HashMap<>());

    private TauListener listener;

    // state database
    private StateDBImpl repositoryImpl;

    // state path
    private static final String STATE_PATH = "state";

    // block database
    private BlockDB blockDB;

    // block store path
    private static final String BLOCK_PATH = "block";

    /**
     * ChainManager constructor.
     *
     * @param listener CompositeTauListener
     */
    public ChainManager(TauListener listener, KeyValueDataBaseFactory dbFactory) {
        this.listener = listener;

        // create state and block database.
        // If database does not exist, directly load.
        // If not exist, create log
        this.repositoryImpl = new StateDBImpl(dbFactory.newDatabase());
        this.blockDB = new BlockDB(dbFactory.newDatabase());
    }

    public void openChainDB() throws Exception {
        try {
            this.repositoryImpl.open(Repo.getRepoPath() + "/" + STATE_PATH);
            this.blockDB.open(Repo.getRepoPath() + "/" + BLOCK_PATH);
        } catch (Exception e) {
            throw e;
        }
    }

    public void initTauChain() throws Exception {
		// New TauConfig
		ChainConfig tauConfig= ChainConfig.NewTauChainConfig();
		byte[] chainid= tauConfig.getChainid();
		if(!this.repositoryImpl.isChainFollowed(chainid)) {
			// New TauChain
    		chainid= createNewCommunity(tauConfig);
		}
    }

    public void closeChainDB() {
        this.repositoryImpl.close();
        this.blockDB.close();
    }

    /**
     * Start all followed and mined blockchains.
     * This method is called by TauController.
     */
    public void start() {

		// Open the db for repo and block
        try {

            openChainDB();
			
			initTauChain();

            Set<byte[]> followedChains = new HashSet<byte[]>();
            followedChains= this.repositoryImpl.getAllFollowedChains();

            Iterator<byte[]> chainsItor = followedChains.iterator();
            while (chainsItor.hasNext()) {

                // New single chain
                byte[] chainid = chainsItor.next();
                ByteArrayWrapper wrapperChainID = new ByteArrayWrapper(chainid);

                Chain chain = new Chain(chainid, this.blockDB, this.repositoryImpl, this.listener);

                // Add chain
                this.chains.put(wrapperChainID, chain);
                // start chain process
                chain.start();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // TODO: notify starting blockchains exception.
        }
    }

    /**
     * Stop all followed and mined blockchains.
     * This method is called by TauController.
     */
    public void stop() {
		// stop chains
        Iterator<ByteArrayWrapper> chainsItor = this.chains.keySet().iterator();
        while(chainsItor.hasNext()) {
			chains.get(chainsItor.next()).stop();	
		}

		// close db
		closeChainDB();
    }

    /**
     * Follow some chain specified by chain id.
     *
     * @param chainID
     * @return boolean successful or not.
     */
    public boolean followChain(byte[] chainID) {
		try{
			this.repositoryImpl.followChain(chainID);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
        return true;
    }

    /**
     * Unfollow some chain specified by chain id.
     *
     * @param chainID
     * @return boolean successful or not.
     */
    public boolean unfollowChain(byte[] chainID) {
        return false;
    }

    /**
     * get chain by chain ID
     * @param chainID chain ID
     * @return chain or null if not found
     */
    public Chain getChain(byte[] chainID) {
        return chains.get(new ByteArrayWrapper(chainID));
    }

    /**
     * create new community.
     * @param cf
     * @return chainid.
     */
    public byte[] createNewCommunity(ChainConfig cf){
        Block genesis = new Block(cf);
        boolean ret = followChain(genesis.getChainID());
		if(ret) {
        	try {
            	blockDB.saveBlock(genesis,true);
        	} catch (Exception e) {
            	logger.error(e.getMessage(), e);
				return null;
        	}
		}
		return genesis.getChainID();
    }

    /**
     * according to chainid select a chain to sendTransaction.
     * @param tx
     * @return
     * todo
     */
    public void sendTransaction(Transaction tx){
		// get chainid
		ByteArrayWrapper chainid= new ByteArrayWrapper(tx.getChainID());

		// get chain and then add tx into the txpool
		chains.get(chainid).getTransactionPool().addRemote(tx);
    }

    /**
     * get account state according to chainid and pubkey
     * @param chainid
     * @param pubkey
     * @return
     * todo
     */
    public AccountState getAccountState(byte[] chainid, byte[] pubkey) throws Exception {

		AccountState account= null;

		try{
			account= this.repositoryImpl.getAccount(chainid, pubkey);
        } catch (Exception e) {
            throw e;
        }

		return account;
    }

    /**
     * get best block
     * @return
     * todo
     */
    public ArrayList<Block> getAllBestBlocks(){

		ArrayList<Block> blocks= new ArrayList<Block>();

        Iterator<ByteArrayWrapper> chainsItor = this.chains.keySet().iterator();
        while(chainsItor.hasNext()) {
			blocks.add(chains.get(chainsItor.next()).getBestBlock());	
		}

		return blocks;
    }

    /**
     * make all followed chain start mining.
     * @return
     * todo
     */
    public byte[][] startMining(){
        return null;
    }
}
