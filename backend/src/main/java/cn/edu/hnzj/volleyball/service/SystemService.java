package cn.edu.hnzj.volleyball.service;

import cn.edu.hnzj.volleyball.common.Roles;
import cn.edu.hnzj.volleyball.dto.SystemInfoVO;
import cn.edu.hnzj.volleyball.entity.Match;
import cn.edu.hnzj.volleyball.mapper.AnnouncementMapper;
import cn.edu.hnzj.volleyball.mapper.ApplicationMapper;
import cn.edu.hnzj.volleyball.mapper.LoginLogMapper;
import cn.edu.hnzj.volleyball.mapper.MatchMapper;
import cn.edu.hnzj.volleyball.mapper.MemberMapper;
import cn.edu.hnzj.volleyball.mapper.TeamMapper;
import cn.edu.hnzj.volleyball.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class SystemService {

    private final UserMapper userMapper;
    private final TeamMapper teamMapper;
    private final MemberMapper memberMapper;
    private final MatchMapper matchMapper;
    private final ApplicationMapper applicationMapper;
    private final LoginLogMapper loginLogMapper;
    private final AnnouncementMapper announcementMapper;

    public SystemService(UserMapper userMapper, TeamMapper teamMapper, MemberMapper memberMapper,
                         MatchMapper matchMapper, ApplicationMapper applicationMapper,
                         LoginLogMapper loginLogMapper, AnnouncementMapper announcementMapper) {
        this.userMapper = userMapper;
        this.teamMapper = teamMapper;
        this.memberMapper = memberMapper;
        this.matchMapper = matchMapper;
        this.applicationMapper = applicationMapper;
        this.loginLogMapper = loginLogMapper;
        this.announcementMapper = announcementMapper;
    }

    public SystemInfoVO info() {
        SystemInfoVO vo = new SystemInfoVO();
        vo.setUserCount(userMapper.countAll());
        vo.setActiveUserCount(userMapper.countActive());
        vo.setAdminCount(userMapper.countByRole(Roles.ADMIN));
        vo.setScorerCount(userMapper.countByRole(Roles.SCORER));
        vo.setTeamCount(teamMapper.findAll().size());
        vo.setMemberCount(memberMapper.countAll());
        vo.setMatchCount(matchMapper.countAll());
        vo.setOngoingMatchCount(matchMapper.countByState(Match.ONGOING));
        vo.setFinishedMatchCount(matchMapper.countByState(Match.FINISHED));
        vo.setPendingApplicationCount(applicationMapper.countPendingAll());
        vo.setAnnouncementCount(announcementMapper.countAll());
        vo.setTotalLoginCount(loginLogMapper.countAll());

        LocalDateTime start = LocalDate.now().atStartOfDay();
        vo.setTodayLoginCount(loginLogMapper.countSuccessBetween(start, start.plusDays(1)));

        Runtime runtime = Runtime.getRuntime();
        long heapUsed = runtime.totalMemory() - runtime.freeMemory();
        long heapMax = runtime.maxMemory();
        vo.setHeapUsedMb(round(heapUsed / 1024.0 / 1024.0));
        vo.setHeapMaxMb(round(heapMax / 1024.0 / 1024.0));
        vo.setHeapUsagePercent(round(heapMax == 0 ? 0 : heapUsed * 100.0 / heapMax));

        fillSystemUsage(vo);

        vo.setCpuCores(runtime.availableProcessors());
        vo.setOsName(System.getProperty("os.name"));
        vo.setOsArch(System.getProperty("os.arch"));
        vo.setJvmVersion(System.getProperty("java.version"));
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        vo.setUptimeMinutes(runtimeMXBean.getUptime() / 60000);
        return vo;
    }

    private void fillSystemUsage(SystemInfoVO vo) {
        try {
            java.lang.management.OperatingSystemMXBean base = ManagementFactory.getOperatingSystemMXBean();
            if (base instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) base;
                long total = os.getTotalPhysicalMemorySize();
                long free = os.getFreePhysicalMemorySize();
                vo.setSystemTotalMb(round(total / 1024.0 / 1024.0));
                vo.setSystemFreeMb(round(free / 1024.0 / 1024.0));
                vo.setSystemUsagePercent(round(total == 0 ? 0 : (total - free) * 100.0 / total));
                double load = os.getSystemCpuLoad();
                vo.setCpuLoad(Double.isNaN(load) || load < 0 ? -1 : round(load * 100));
            }
        } catch (Exception ignored) {
            // 某些 JVM 不暴露系统内存与 CPU 信息，页面会显示为 0
        }

        long diskTotal = 0;
        long diskFree = 0;
        File[] roots = File.listRoots();
        if (roots != null) {
            for (File root : roots) {
                try {
                    long total = root.getTotalSpace();
                    if (total > 0) {
                        diskTotal += total;
                        diskFree += root.getUsableSpace();
                    }
                } catch (Exception ignored) {
                    // 忽略不可访问的磁盘分区
                }
            }
        }
        vo.setDiskTotalGb(round(diskTotal / 1024.0 / 1024.0 / 1024.0));
        vo.setDiskFreeGb(round(diskFree / 1024.0 / 1024.0 / 1024.0));
        vo.setDiskUsagePercent(round(diskTotal == 0 ? 0 : (diskTotal - diskFree) * 100.0 / diskTotal));
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
