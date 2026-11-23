package cn.edu.hnzj.volleyball.service;

import cn.edu.hnzj.volleyball.common.BusinessException;
import cn.edu.hnzj.volleyball.dto.AnnouncementRequest;
import cn.edu.hnzj.volleyball.entity.Announcement;
import cn.edu.hnzj.volleyball.entity.Match;
import cn.edu.hnzj.volleyball.entity.Team;
import cn.edu.hnzj.volleyball.entity.Vp;
import cn.edu.hnzj.volleyball.mapper.AnnouncementMapper;
import cn.edu.hnzj.volleyball.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    public AnnouncementService(AnnouncementMapper announcementMapper) {
        this.announcementMapper = announcementMapper;
    }

    public List<Announcement> latest(int limit) {
        return announcementMapper.findLatest(limit <= 0 ? 20 : limit);
    }

    public List<Announcement> findAll() {
        return announcementMapper.findAll();
    }

    @Transactional
    public Announcement create(AnnouncementRequest request) {
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle().trim());
        announcement.setContent(request.getContent());
        announcement.setType(Announcement.TYPE_MANUAL);
        announcement.setPinned(Boolean.TRUE.equals(request.getPinned()) ? 1 : 0);
        announcement.setCreatedBy(UserContext.id());
        announcementMapper.insert(announcement);
        return announcementMapper.findById(announcement.getId());
    }

    @Transactional
    public Announcement update(Long id, AnnouncementRequest request) {
        Announcement announcement = announcementMapper.findById(id);
        if (announcement == null) {
            throw BusinessException.notFound("公告不存在");
        }
        announcement.setTitle(request.getTitle().trim());
        announcement.setContent(request.getContent());
        announcement.setPinned(Boolean.TRUE.equals(request.getPinned()) ? 1 : 0);
        announcementMapper.update(announcement);
        return announcementMapper.findById(id);
    }

    @Transactional
    public void delete(Long id) {
        if (announcementMapper.findById(id) == null) {
            throw BusinessException.notFound("公告不存在");
        }
        announcementMapper.deleteById(id);
    }

    @Transactional
    public void announceMatchStart(Match match, Team left, Team right) {
        Announcement announcement = new Announcement();
        announcement.setTitle("比赛开始：" + match.getName());
        announcement.setContent(left.getName() + " VS " + right.getName()
                + " 的比赛已经开始，登录后即可实时观看比分。");
        announcement.setType(Announcement.TYPE_MATCH_START);
        announcement.setMatchId(match.getId());
        announcement.setPinned(0);
        announcement.setCreatedBy(match.getCreatorId());
        announcementMapper.insert(announcement);
    }

    @Transactional
    public void announceMatchEnd(Match match, Team winner, Team loser, Vp mvp, Vp svp,
                                 int leftSets, int rightSets) {
        StringBuilder content = new StringBuilder();
        content.append(winner.getName()).append(" 以 ")
                .append(leftSets).append(":").append(rightSets).append(" 战胜 ")
                .append(loser.getName()).append("。");
        content.append("MVP：").append(mvp == null ? "暂无" : mvp.getMemberName() + "（" + mvp.getEvaluate() + "）");
        content.append("，SVP：").append(svp == null ? "暂无" : svp.getMemberName() + "（" + svp.getEvaluate() + "）");
        content.append("。");

        Announcement announcement = new Announcement();
        announcement.setTitle("比赛结束：" + match.getName());
        announcement.setContent(content.toString());
        announcement.setType(Announcement.TYPE_MATCH_END);
        announcement.setMatchId(match.getId());
        announcement.setPinned(0);
        announcement.setCreatedBy(match.getCreatorId());
        announcementMapper.insert(announcement);
    }

    public String safeText(String text) {
        return StringUtils.hasText(text) ? text : "";
    }
}
