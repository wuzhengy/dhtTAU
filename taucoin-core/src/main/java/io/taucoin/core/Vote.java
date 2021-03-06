package io.taucoin.core;

import java.util.Arrays;
import java.util.Objects;

public class Vote implements Comparable<Vote> {
    private byte[] blockHash;
    private int blockNumber;
    private int count;

    public Vote(byte[] blockHash, int blockNumber) {
        this.blockHash = blockHash;
        this.blockNumber = blockNumber;
        this.count = 0;
    }

    public Vote(byte[] blockHash, int blockNumber, int count) {
        this.blockHash = blockHash;
        this.blockNumber = blockNumber;
        this.count = count;
    }

    public byte[] getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(byte[] blockHash) {
        this.blockHash = blockHash;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(int blockNumber) {
        this.blockNumber = blockNumber;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /**
     * 投一票
     * @return
     */
    public int voteUp() {
        this.count++;
        return this.count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return blockNumber == vote.blockNumber &&
                count == vote.count &&
                Arrays.equals(blockHash, vote.blockHash);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(blockNumber, count);
        result = 31 * result + Arrays.hashCode(blockHash);
        return result;
    }

    @Override
    // 比较策略：票数优先；票数相同的情况下，高度优先
    public int compareTo(Vote o) {
        // from high to low
        // 先比较票数, 如果比较不出来，再比较高度，高度更大的优先
        if (this.count < o.count) {
            return 1;
        } else if (this.count == o.count) {
            if (this.blockNumber < o.blockNumber) {
                return 1;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "blockHash=" + new String(blockHash) +
                ", blockNumber=" + blockNumber +
                ", count=" + count +
                '}';
    }
}

