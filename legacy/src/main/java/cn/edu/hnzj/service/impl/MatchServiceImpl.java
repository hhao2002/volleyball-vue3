package cn.edu.hnzj.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.edu.hnzj.dao.MatchDao;
import cn.edu.hnzj.pojo.Match;
import cn.edu.hnzj.pojo.Member;
import cn.edu.hnzj.service.MatchService;
import cn.edu.hnzj.service.MemberService;

@Service("matchService")
public class MatchServiceImpl implements MatchService {
	@Resource
	private MatchDao matchDao;

	@Resource
	private MemberService memberService;

	public void setMatchDao(MatchDao matchDao) {
		this.matchDao = matchDao;
	}

	@Override
	public boolean addMatchByObject(Match match) {
		// TODO 添加一个记录
		return matchDao.insertMatchByObject(match) == 1;
	}

	@Override
	public int deleteMatchById(int id) {
		// TODO 删除一条记录
		return matchDao.deleteMatchById(id);
	}
	
	@Override
	public Match getMatchById(int id) {
		// TODO 通过id 获取此比赛简略信息
		return matchDao.selectMatchById(id);
	}

	@Override
	public List<Match> getMatchAll() {
		// TODO Auto-generated method stub
		return matchDao.selectMatchAll();
	}

	@Override
	public Member getMvp(int matchId) {
		// TODO Auto-generated method stub
		System.out.println(memberService);
		int[] members = matchDao.getMembersBymatchId(matchId);
		Map<Integer, Double> s = new HashMap<Integer, Double>();
		for (int i = 0; i < members.length; i++) {
			s.put(members[i], memberService.getScoreMatch(members[i], matchId));
		}
		Set<Integer> keySet = s.keySet();
		Double maxScore = 0.0;
		Integer id = null;
		for (Integer integer : keySet) {
			if (s.get(integer) > maxScore) {
				maxScore = s.get(integer);
				id = integer;
			}
		}
		System.out.println(memberService);
		return memberService.selectMemberById(id);
	}

	@Override
	public int updateMatchStateById(int id, String state) {
		// TODO Auto-generated method stub
		return matchDao.updateMatchStateById(id, state);
	}
	
}
