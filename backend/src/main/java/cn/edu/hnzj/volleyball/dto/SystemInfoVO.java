package cn.edu.hnzj.volleyball.dto;

import lombok.Data;

@Data
public class SystemInfoVO {

    private long userCount;
    private long activeUserCount;
    private long adminCount;
    private long scorerCount;
    private long teamCount;
    private long memberCount;
    private long matchCount;
    private long ongoingMatchCount;
    private long finishedMatchCount;
    private long pendingApplicationCount;
    private long todayLoginCount;
    private long totalLoginCount;
    private long announcementCount;

    private double heapUsedMb;
    private double heapMaxMb;
    private double heapUsagePercent;
    private double systemTotalMb;
    private double systemFreeMb;
    private double systemUsagePercent;
    private double diskTotalGb;
    private double diskFreeGb;
    private double diskUsagePercent;
    private double cpuLoad;
    private int cpuCores;
    private String osName;
    private String osArch;
    private String jvmVersion;
    private long uptimeMinutes;
}
