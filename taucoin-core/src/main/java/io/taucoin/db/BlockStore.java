package io.taucoin.db;

import io.taucoin.core.BlockContainer;
import io.taucoin.types.Block;

import java.util.List;
import java.util.Set;

public interface BlockStore {

    /**
     * Open database.
     *
     * @param path database path which can be accessed
     * @throws Exception
     */
    void open(String path) throws Exception;

    /**
     * Close database.
     */
    void close();

    /**
     * get block by hash
     * @param chainID
     * @param hash
     * @return
     * @throws Exception
     */
    Block getBlockByHash(byte[] chainID, byte[] hash) throws Exception;

    /**
     * get block container by hash
     * @param chainID chain ID
     * @param hash block hash
     * @return block container
     * @throws Exception
     */
    BlockContainer getBlockContainerByHash(byte[] chainID, byte[] hash) throws Exception;

    /**
     * get block info by hash
     * @param chainID
     * @param hash
     * @return
     * @throws Exception
     */
    BlockInfo getBlockInfoByHash(byte[] chainID, byte[] hash) throws Exception;

    /**
     * get main chain block by number
     * @param chainID chain id
     * @param number block number
     * @return block or null
     * @throws Exception
     */
    Block getMainChainBlockByNumber(byte[] chainID, long number) throws Exception;

    /**
     * get main chain block container by number
     * @param chainID chain id
     * @param number block number
     * @return block container or null
     * @throws Exception
     */
    BlockContainer getMainChainBlockContainerByNumber(byte[] chainID, long number) throws Exception;

    /**
     * get main chain block hash by number
     * @param chainID chain ID
     * @param number block number
     * @return block hash
     * @throws Exception
     */
    byte[] getMainChainBlockHashByNumber(byte[] chainID, long number) throws Exception;

    /**
     * save block
     * @param chainID chain ID
     * @param block block to save
     * @throws Exception
     */
    void saveBlock(byte[] chainID, Block block, boolean isMainChain) throws Exception;

    /**
     * save block container
     * @param chainID chain ID
     * @param blockContainer block container to save
     * @throws Exception
     */
    void saveBlockContainer(byte[] chainID, BlockContainer blockContainer, boolean isMainChain) throws Exception;

    /**
     * get all blocks of a chain, whether it is a block on the main chain or not
     * @param chainID
     * @return
     * @throws Exception
     */
    Set<Block> getChainAllBlocks(byte[] chainID) throws Exception;

    /**
     * remove all blocks and info of a chain
     * @param chainID
     * @throws Exception
     */
    void removeChain(byte[] chainID) throws Exception;

    /**
     * get fork point block between main chain and fork chain
     * @param chainID chain ID
     * @param block block on chain
     * @return
     * @throws Exception
     */
    Block getForkPointBlock(byte[] chainID, Block block) throws Exception;

    /**
     * get fork point block between chain 1 and chain 2
     * @param chainID chain ID
     * @param chain1Block block on chain 1
     * @param chain2Block block on chain 2
     * @return
     * @throws Exception
     */
    Block getForkPointBlock(byte[] chainID, Block chain1Block, Block chain2Block) throws Exception;

    /**
     * get fork info
     * @param chainID chain ID
     * @param forkBlock fork point block
     * @param bestBlock current chain best block
     * @param undoBlocks blocks to roll back from high to low
     * @param newBlocks blocks to connect from high to low
     * @return
     */
    boolean getForkBlocksInfo(byte[] chainID,
                              Block forkBlock,
                              Block bestBlock,
                              List<Block> undoBlocks,
                              List<Block> newBlocks) throws Exception;

    /**
     * get fork info
     * @param chainID chain ID
     * @param forkBlockContainer fork point block container
     * @param bestBlockContainer current chain best block container
     * @param undoBlockContainers block containers to roll back from high to low
     * @param newBlockContainers block containers to connect from high to low
     * @return true/false
     */
    boolean getForkBlockContainersInfo(byte[] chainID,
                                       BlockContainer forkBlockContainer,
                                       BlockContainer bestBlockContainer,
                                       List<BlockContainer> undoBlockContainers,
                                       List<BlockContainer> newBlockContainers) throws Exception;

    /**
     * re-branch blocks
     * @param chainID chain ID
     * @param undoBlocks move to non-main chain
     * @param newBlocks move to main chain
     */
    void reBranchBlocks(byte[] chainID, List<Block> undoBlocks, List<Block> newBlocks) throws Exception;

    /**
     * re-branch blocks with block containers
     * @param chainID chain ID
     * @param undoBlockContainers move to non-main chain
     * @param newBlockContainers move to main chain
     */
    void reBranchBlocksWithContainers(byte[] chainID,
                                 List<BlockContainer> undoBlockContainers,
                                 List<BlockContainer> newBlockContainers) throws Exception;
}
