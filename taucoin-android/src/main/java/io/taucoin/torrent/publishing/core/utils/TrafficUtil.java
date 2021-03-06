package io.taucoin.torrent.publishing.core.utils;

import android.content.Context;

import java.util.Date;

import androidx.annotation.NonNull;
import io.taucoin.torrent.publishing.MainApplication;
import io.taucoin.torrent.publishing.core.settings.SettingsRepository;
import io.taucoin.torrent.publishing.core.storage.sqlite.RepositoryHelper;

/**
 * 流量统计工具类
 */
public class TrafficUtil {
    private static final String TRAFFIC_DOWN = "download";
    private static final String TRAFFIC_UP = "upload";
    private static final String TRAFFIC_METERED = "metered";

    private static final String TRAFFIC_VALUE_OLD = "pref_key_traffic_old_";
    private static final String TRAFFIC_VALUE = "pref_key_traffic_";
    private static final String TRAFFIC_TIME = "pref_key_traffic_time";

    private static SettingsRepository settingsRepo;
    static {
        Context context = MainApplication.getInstance();
        settingsRepo = RepositoryHelper.getSettingsRepository(context);
    }

    /**
     * 保存当前流量总量（统计入口）
     * @param statistics 当前网络数据总量
     */
    public static void saveTrafficTotal(@NonNull NetworkStatistics statistics) {
        saveTrafficTotal(TRAFFIC_DOWN, statistics.getRxBytes());
        saveTrafficTotal(TRAFFIC_UP, statistics.getTxBytes());
        // 如果是计费网络，统计当天计费网络使用总量
        long total = statistics.getRxBytes() + statistics.getTxBytes();
        if (NetworkSetting.isMeteredNetwork()) {
            saveTrafficTotal(TRAFFIC_METERED, total);
        } else {
            String trafficValueOld = TRAFFIC_VALUE_OLD + TRAFFIC_METERED;
            settingsRepo.setLongValue(trafficValueOld, total);
        }
    }

    /**
     * 根据流量类型，计算增量流量，更新流量统计值
     * @param trafficType
     * @param byteSize
     */
    private static void saveTrafficTotal(String trafficType, long byteSize) {
        resetTrafficInfo();
        String trafficValueOld = TRAFFIC_VALUE_OLD + trafficType;
        long incrementalSize = calculateIncrementalSize(trafficType, byteSize);
        settingsRepo.setLongValue(trafficValueOld, byteSize);
        String trafficValue = TRAFFIC_VALUE + trafficType;
        long trafficTotal = incrementalSize + settingsRepo.getLongValue(trafficValue);
        settingsRepo.setLongValue(trafficValue, trafficTotal);
    }

    /**
     * 计算增量大小
     * @param trafficType
     * @param byteSize
     * @return
     */
    static long calculateIncrementalSize(String trafficType, long byteSize) {
        resetTrafficInfo();
        String trafficValueOld = TRAFFIC_VALUE_OLD + trafficType;
        long oldTraffic = settingsRepo.getLongValue(trafficValueOld, -1);
        if (oldTraffic >= 0 && byteSize > oldTraffic) {
            byteSize = byteSize - oldTraffic;
        } else {
            byteSize = 0;
        }
        return byteSize;
    }

    /**
     * 重置上一次本地流量统计信息
     */
    private static void resetTrafficTotalOld() {
        settingsRepo.setLongValue(TRAFFIC_VALUE_OLD + TRAFFIC_DOWN, -1);
        settingsRepo.setLongValue(TRAFFIC_VALUE_OLD + TRAFFIC_UP, -1);
        settingsRepo.setLongValue(TRAFFIC_VALUE_OLD + TRAFFIC_METERED, -1);
    }

    /**
     * 重置本地流量统计信息
     */
    private synchronized static void resetTrafficInfo() {
        long currentTrafficTime = new Date().getTime();
        long oldTrafficTime = settingsRepo.getLongValue(TRAFFIC_TIME);
        if (oldTrafficTime == 0 || DateUtil.compareDay(oldTrafficTime, currentTrafficTime) > 0) {
            settingsRepo.setLongValue(TRAFFIC_TIME, currentTrafficTime);
            settingsRepo.setLongValue(TRAFFIC_VALUE + TRAFFIC_DOWN, 0);
            settingsRepo.setLongValue(TRAFFIC_VALUE + TRAFFIC_UP, 0);
            settingsRepo.setLongValue(TRAFFIC_VALUE + TRAFFIC_METERED, 0);
            resetTrafficTotalOld();
        }
    }

    /**
     * 获取当天计费网络流量值
     * @return
     */
    public static long getMeteredTrafficTotal() {
        resetTrafficInfo();
        return settingsRepo.getLongValue(TRAFFIC_VALUE + TRAFFIC_METERED);
    }

    /**
     * 获取计费网络类型
     * @return
     */
    public static String getMeteredType() {
        return TRAFFIC_METERED;
    }

    public static String getUpType() {
        return TRAFFIC_UP;
    }

    public static String getDownType() {
        return TRAFFIC_DOWN;
    }

    /**
     * 获取计费网络本地存储Key
     * @return
     */
    public static String getMeteredKey() {
        return TRAFFIC_VALUE + TRAFFIC_METERED;
    }

    /**
     * 获取当天下行网络流量值
     * @return
     */
    public static long getTrafficDownloadTotal() {
        resetTrafficInfo();
        return settingsRepo.getLongValue(TRAFFIC_VALUE + TRAFFIC_DOWN);
    }

    /**
     * 获取当天上行网络流量值
     * @return
     */
    public static long getTrafficUploadTotal() {
        resetTrafficInfo();
        return settingsRepo.getLongValue(TRAFFIC_VALUE + TRAFFIC_UP);
    }
}
