package io.taucoin.db;

import io.taucoin.param.ChainParam;
import io.taucoin.types.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockDB implements BlockStore{
    private static final Logger logger = LoggerFactory.getLogger("BlockDB");

    private KeyValueDataBase db;

    public BlockDB(KeyValueDataBase db) {
        this.db = db;
    }

    /**
     * open db
     * @param path database path which can be accessed
     * @throws Exception
     */
    public void open(String path) throws Exception {
        db.open(path);
    }

    /**
     * close db
     */
    public void close() {
        db.close();
    }

    /**
     * get block by hash
     * @param chainID
     * @param hash
     * @return
     * @throws Exception
     */
    @Override
    public Block getBlockByHash(byte[] chainID, byte[] hash) throws Exception {
        byte[] rlp = db.get(PrefixKey.blockKey(chainID, hash));
        if (null != rlp) {
            return new Block(rlp);
        }
        return null;
    }

    /**
     * save block info in db
     * @param block
     * @param isMainChain
     * @throws Exception
     */
    public void saveBlockInfo(Block block, boolean isMainChain) throws Exception {
        BlockInfos blockInfos;
        // if saved in the same height
        byte[] rlp = db.get(PrefixKey.blockInfoKey(block.getChainID(), block.getBlockNum()));
        if (null == rlp) {
            blockInfos = new BlockInfos();
        } else {
            blockInfos = new BlockInfos(rlp);
        }
        blockInfos.putBlock(block, isMainChain);

        db.put(PrefixKey.blockInfoKey(block.getChainID(), block.getBlockNum()), blockInfos.getEncoded());
    }

    /**
     * save block
     * @param block
     * @throws Exception
     */
    @Override
    public void saveBlock(Block block, boolean isMainChain) throws Exception {
        // save block
        db.put(PrefixKey.blockKey(block.getChainID(), block.getBlockHash()), block.getEncoded());
        // save block info
        saveBlockInfo(block, isMainChain);
        // delete blocks out of 3 * mutable range, when save main chain block
        if (isMainChain && block.getBlockNum() > ChainParam.WARNING_RANGE) {
            delNonChainBlockByNumber(block.getChainID(), block.getBlockNum() - ChainParam.WARNING_RANGE);
        }
    }

    /**
     * delete non-main chain block and block info
     * @param chainID
     * @param number
     * @throws Exception
     */
    public void delNonChainBlockByNumber(byte[] chainID, long number) throws Exception {
        byte[] rlp = db.get(PrefixKey.blockInfoKey(chainID, number));
        if (null == rlp) {
            return;
        }
        BlockInfos blockInfos = new BlockInfos(rlp);
        List<BlockInfo> list = blockInfos.getBlockInfoList();
        for (int i = list.size() - 1; i >= 0; i--) {
            if (!list.get(i).isMainChain()) {
                // delete non-main chain block
                db.delete(PrefixKey.blockKey(chainID, list.get(i).getHash()));
                list.remove(i);
            }
        }
        // save main chain block info
        db.put(PrefixKey.blockInfoKey(chainID, number), blockInfos.getEncoded());
    }

    /**
     * get all blocks of a chain, whether it is a block on the main chain or not
     * @param chainID
     * @return
     * @throws Exception
     */
    @Override
    public Set<Block> getChainAllBlocks(byte[] chainID) throws Exception {
        Set<byte[]> blocks = db.retrieveKeysWithPrefix(PrefixKey.blockPrefix(chainID));
        if (null == blocks) {
            logger.info("Cannot find any block with this chainID:{}", chainID.toString());
            return null;
        }
        Set<Block> set = new HashSet<>();
        for (byte[] rlp: blocks) {
            set.add(new Block(rlp));
        }
        return set;
    }

    /**
     * remove all blocks and info of a chain
     * @param chainID
     * @throws Exception
     */
    @Override
    public void removeChain(byte[] chainID) throws Exception {
        db.removeWithKeyPrefix(chainID);
    }

    /**
     * get fork point block
     * @param block
     * @return
     * @throws Exception
     */
    @Override
    public Block getForkPointBlock(Block block) throws Exception {
        // if on main chain
        byte[] infoRLP = db.get(PrefixKey.blockInfoKey(block.getChainID(), block.getBlockNum()));
        if (null != infoRLP) {
            BlockInfos blockInfos = new BlockInfos(infoRLP);
            List<BlockInfo> list = blockInfos.getBlockInfoList();

            for (BlockInfo blockInfo : list) {
                if (Arrays.equals(blockInfo.getHash(), block.getBlockHash()) && blockInfo.isMainChain()) {
                    // return itself
                    return block;
                }
            }
        } else {
            logger.info("Cannot find block info with current block.");
        }

        byte[] chainID = block.getChainID();
        byte[] rlp = db.get(PrefixKey.blockKey(chainID, block.getPreviousBlockHash()));
        // if previous is null, return
        if (null == rlp) {
            logger.error("Cannot find previous block, hash[{}]",
                    Hex.toHexString(block.getPreviousBlockHash()));
            return null;
        }
        Block previousBlock = new Block(rlp);

        while (true) {
            infoRLP = db.get(PrefixKey.blockInfoKey(chainID, previousBlock.getBlockNum()));
            if (null == infoRLP) {
                logger.error("Cannot find block info with this block number:{}", previousBlock.getBlockNum());
                return null;
            }
            BlockInfos blockInfos = new BlockInfos(infoRLP);
            List<BlockInfo> list = blockInfos.getBlockInfoList();
            boolean found = false;
            for (BlockInfo blockInfo : list) {
                if (Arrays.equals(blockInfo.getHash(), previousBlock.getBlockHash())) {
                    // found block
                    found = true;
                    if (blockInfo.isMainChain()) {
                        // if this block is on main chain, got it
                        return previousBlock;
                    } else {
                        // if this block is not on main chain, look ahead
                        rlp = db.get(PrefixKey.blockKey(chainID, previousBlock.getPreviousBlockHash()));
                        // if previous is null, return
                        if (null == rlp) {
                            logger.error("Cannot find previous block, hash[{}].",
                                    Hex.toHexString(previousBlock.getPreviousBlockHash()));
                            return null;
                        }
                        logger.info("Seek previous block hash[{}].",
                                Hex.toHexString(previousBlock.getPreviousBlockHash()));
                        previousBlock = new Block(rlp);
                        break;
                    }
                }
            }
            // if not found this block info in this height
            if (!found) {
                logger.error("Cannot find block info, hash[{}]", Hex.toHexString(previousBlock.getBlockHash()));
                return null;
            }
        }
    }

}

