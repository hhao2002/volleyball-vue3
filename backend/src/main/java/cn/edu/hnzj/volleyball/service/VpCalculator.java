package cn.edu.hnzj.volleyball.service;

import cn.edu.hnzj.volleyball.entity.Member;
import cn.edu.hnzj.volleyball.entity.ScoreRecord;
import cn.edu.hnzj.volleyball.entity.Vp;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * MVP/SVP 评选规则：
 * 1. 得分多者优先；
 * 2. 得分相同，最大连续得分高者优先；
 * 3. 仍相同，连续得分（长度 >= 2 的连续段）发生次数多者优先。
 * 出界得分不计入个人，且会中断个人的连续得分段。
 */
@Component
public class VpCalculator {

    public List<Vp> calculate(List<Member> leftMembers, List<Member> rightMembers,
                              List<ScoreRecord> events, Long winnerTeamId, Long leftTeamId) {
        Map<Long, Vp> stats = new LinkedHashMap<>();
        for (Member member : concat(leftMembers, rightMembers)) {
            Vp stat = new Vp();
            stat.setMemberId(member.getId());
            stat.setMemberName(member.getName());
            stat.setTeamId(member.getTeamId());
            stat.setPoints(0);
            stat.setMaxStreak(0);
            stat.setStreakCount(0);
            stats.put(member.getId(), stat);
        }

        Long previous = null;
        int run = 0;
        for (ScoreRecord event : events) {
            Long memberId = event.getMemberId();
            if (memberId == null || !stats.containsKey(memberId)) {
                if (previous != null) {
                    closeRun(stats.get(previous), run);
                }
                previous = null;
                run = 0;
                continue;
            }
            Vp stat = stats.get(memberId);
            stat.setPoints(stat.getPoints() + 1);
            if (memberId.equals(previous)) {
                run++;
            } else {
                if (previous != null) {
                    closeRun(stats.get(previous), run);
                }
                previous = memberId;
                run = 1;
            }
        }
        if (previous != null) {
            closeRun(stats.get(previous), run);
        }

        Long loserTeamId = winnerTeamId.equals(leftTeamId)
                ? findOtherTeamId(stats, leftTeamId) : leftTeamId;
        List<Vp> result = new ArrayList<>();
        Vp mvp = pickBest(stats, winnerTeamId);
        if (mvp != null) {
            mvp.setType(Vp.MVP);
            mvp.setEvaluate(evaluate(mvp));
            result.add(mvp);
        }
        Vp svp = pickBest(stats, loserTeamId);
        if (svp != null) {
            svp.setType(Vp.SVP);
            svp.setEvaluate(evaluate(svp));
            result.add(svp);
        }
        return result;
    }

    private Long findOtherTeamId(Map<Long, Vp> stats, Long leftTeamId) {
        for (Vp stat : stats.values()) {
            if (!leftTeamId.equals(stat.getTeamId())) {
                return stat.getTeamId();
            }
        }
        return null;
    }

    private Vp pickBest(Map<Long, Vp> stats, Long teamId) {
        List<Vp> candidates = new ArrayList<>();
        for (Vp stat : stats.values()) {
            if (teamId != null && teamId.equals(stat.getTeamId())) {
                candidates.add(stat);
            }
        }
        candidates.sort(new Comparator<Vp>() {
            @Override
            public int compare(Vp a, Vp b) {
                int byPoints = Integer.compare(b.getPoints(), a.getPoints());
                if (byPoints != 0) {
                    return byPoints;
                }
                int byStreak = Integer.compare(b.getMaxStreak(), a.getMaxStreak());
                if (byStreak != 0) {
                    return byStreak;
                }
                int byCount = Integer.compare(b.getStreakCount(), a.getStreakCount());
                if (byCount != 0) {
                    return byCount;
                }
                return Long.compare(a.getMemberId(), b.getMemberId());
            }
        });
        return candidates.isEmpty() ? null : candidates.get(0);
    }

    private void closeRun(Vp stat, int run) {
        if (stat == null || run <= 0) {
            return;
        }
        stat.setMaxStreak(Math.max(stat.getMaxStreak(), run));
        if (run >= 2) {
            stat.setStreakCount(stat.getStreakCount() + 1);
        }
    }

    private String evaluate(Vp vp) {
        return "得分" + vp.getPoints() + " · 最高连续" + vp.getMaxStreak()
                + "分 · 连续得分" + vp.getStreakCount() + "次";
    }

    private List<Member> concat(List<Member> left, List<Member> right) {
        List<Member> all = new ArrayList<>();
        if (left != null) {
            all.addAll(left);
        }
        if (right != null) {
            all.addAll(right);
        }
        return all;
    }
}
