package io.taucoin.torrent.publishing.core.storage.sqlite.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 数据库存储社区Member实体类
 */
@Entity(tableName = "Members")
public class Member implements Parcelable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public long id;                     // 自增id
    @NonNull
    public String chainID;              // 成员所属社区的chainID
    @NonNull
    public String publicKey;            // 成员的公钥
    public long balance;                // 成员的balance
    public long power;                  // 成员的power

    public Member(@NonNull String chainID, @NonNull String publicKey){
        this.chainID = chainID;
        this.publicKey = publicKey;
    }

    public Member(@NonNull String chainID, @NonNull String publicKey, long balance, long power){
        this.chainID = chainID;
        this.publicKey = publicKey;
        this.balance = balance;
        this.power = power;
    }

    protected Member(Parcel in) {
        id = in.readLong();
        chainID = in.readString();
        balance = in.readLong();
        power = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(chainID);
        dest.writeString(publicKey);
        dest.writeLong(balance);
        dest.writeLong(power);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    @Override
    public int hashCode() {
        return (int)(id ^ (id >>> 32));
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Member && (o == this || id == (((Member)o).id));
    }
}
