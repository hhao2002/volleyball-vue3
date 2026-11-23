package cn.edu.hnzj.volleyball.service;

import cn.edu.hnzj.volleyball.entity.Member;
import cn.edu.hnzj.volleyball.entity.ScoreRecord;
import cn.edu.hnzj.volleyball.entity.Vp;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class VpCalculatorTest {

    private final VpCalculator calculator = new VpCalculator();

    private Member member(long id, long teamId, String name) {
        Member member = new Member();
        member.setId(id);
        member.setTeamId(teamId);
        member.setName(name);
        return member;
    }

    private ScoreRecord event(int seq, long teamId, Long memberId) {
        ScoreRecord record = new ScoreRecord();
        record.setSeq(seq);
        record.setTeamId(teamId);
        record.setMemberId(memberId);
        record.setType(memberId == null ? ScoreRecord.TYPE_OUT : ScoreRecord.TYPE_ATTACK);
        return record;
    }

    private Vp find(List<Vp> vps, String type) {
        for (Vp vp : vps) {
            if (type.equals(vp.getType())) {
                return vp;
            }
        }
        return null;
    }

    private List<Member> left() {
        return Arrays.asList(member(1, 1, "A"), member(2, 1, "B"));
    }

    private List<Member> right() {
        return Arrays.asList(member(3, 2, "C"), member(4, 2, "D"));
    }

    @Test
    void pointsArePriority() {
        List<ScoreRecord> events = new ArrayList<>();
        events.add(event(1, 1, 1L));
        events.add(event(2, 1, 2L));
        events.add(event(3, 1, 2L));
        List<Vp> vps = calculator.calculate(left(), right(), events, 1L, 1L);
        assertEquals("B", find(vps, Vp.MVP).getMemberName());
        assertEquals(2, find(vps, Vp.MVP).getPoints());
        assertNotNull(find(vps, Vp.SVP), "失利队伍即使无人得分也必须产生 SVP");
        assertEquals(0, find(vps, Vp.SVP).getPoints());
    }

    @Test
    void maxStreakBreaksTieOnPoints() {
        List<ScoreRecord> events = new ArrayList<>();
        // A: 4 分，最大连续 2；B: 4 分，最大连续 4 => B 当选 MVP
        events.add(event(1, 1, 1L));
        events.add(event(2, 1, 1L));
        events.add(event(3, 1, 2L));
        events.add(event(4, 1, 2L));
        events.add(event(5, 1, 2L));
        events.add(event(6, 1, 2L));
        events.add(event(7, 1, 1L));
        events.add(event(8, 1, 1L));
        events.add(event(9, 2, 3L));
        events.add(event(10, 2, 3L));
        events.add(event(11, 2, 3L));

        List<Vp> vps = calculator.calculate(left(), right(), events, 1L, 1L);
        Vp mvp = find(vps, Vp.MVP);
        assertEquals("B", mvp.getMemberName());
        assertEquals(4, mvp.getPoints());
        assertEquals(4, mvp.getMaxStreak());
        assertEquals("C", find(vps, Vp.SVP).getMemberName());
    }

    @Test
    void streakCountBreaksTieOnPointsAndMaxStreak() {
        List<ScoreRecord> events = new ArrayList<>();
        // A: 4 分，最大连续 2，连续段 2 次；B: 4 分，最大连续 2，连续段 1 次 => A 当选
        events.add(event(1, 1, 1L));
        events.add(event(2, 1, 1L));
        events.add(event(3, 2, 3L));
        events.add(event(4, 1, 1L));
        events.add(event(5, 1, 1L));
        events.add(event(6, 2, 3L));
        events.add(event(7, 1, 2L));
        events.add(event(8, 1, 2L));
        events.add(event(9, 2, 3L));
        events.add(event(10, 1, 2L));
        events.add(event(11, 1, 2L));

        List<Vp> vps = calculator.calculate(left(), right(), events, 1L, 1L);
        Vp mvp = find(vps, Vp.MVP);
        assertEquals("A", mvp.getMemberName());
        assertEquals(4, mvp.getPoints());
        assertEquals(2, mvp.getMaxStreak());
        assertEquals(2, mvp.getStreakCount());
        assertEquals("C", find(vps, Vp.SVP).getMemberName());
    }

    @Test
    void outOfBoundsBreaksStreakAndIsNotAttributed() {
        List<ScoreRecord> events = new ArrayList<>();
        // OUT 中断 A 的连续得分：A 得 4 分，最大连续 2（而非 4），连续段 2 次
        events.add(event(1, 1, 1L));
        events.add(event(2, 1, 1L));
        events.add(event(3, 1, null));
        events.add(event(4, 1, 1L));
        events.add(event(5, 1, 1L));

        List<Vp> vps = calculator.calculate(left(), right(), events, 1L, 1L);
        Vp mvp = find(vps, Vp.MVP);
        assertNotNull(mvp);
        assertEquals("A", mvp.getMemberName());
        assertEquals(4, mvp.getPoints());
        assertEquals(2, mvp.getMaxStreak());
        assertEquals(2, mvp.getStreakCount());
    }

    @Test
    void svpComesFromLosingTeamOnly() {
        List<ScoreRecord> events = new ArrayList<>();
        events.add(event(1, 1, 1L));
        events.add(event(2, 1, 1L));
        events.add(event(3, 1, 1L));
        events.add(event(4, 2, 3L));
        events.add(event(5, 2, 3L));

        List<Vp> vps = calculator.calculate(left(), right(), events, 2L, 1L);
        assertEquals("C", find(vps, Vp.MVP).getMemberName());
        assertEquals("A", find(vps, Vp.SVP).getMemberName());
        assertEquals(Vp.SVP, find(vps, Vp.SVP).getType());
    }
}
