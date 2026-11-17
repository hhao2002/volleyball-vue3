package cn.edu.hnzj.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.edu.hnzj.dao.MemberDao;
import cn.edu.hnzj.pojo.Member;
import cn.edu.hnzj.service.MemberService;

@Service("memberService")
public class MemberServiceImpl implements MemberService {

	@Resource
	private MemberDao memberDao;

	@Override
	public int selectScoreByMemberAndMatch(int memberId, int matchId) {
		// TODO Auto-generated method stub
		return memberDao.selectScoreByMemberAndMatch(memberId, matchId);
	}

	@Override
	public int coiledScoreNum(int memberId, int matchId) {
		// TODO Auto-generated method stub
		int[] scoreList = memberDao.getScoreIdByMemberAndMatch(memberId, matchId);
		int isCoiled = 1;
		int num = 0;
		for (int i = 1; i < scoreList.length; i++) {
			if (scoreList[i - 1] + 1 == scoreList[i]) {
				isCoiled++;
			} else {
				if (isCoiled >= 2) {
					num++;
					isCoiled = 1;
				}
			}
		}
		if (isCoiled >= 2) {
			return 1;
		}
		return num;
	}

	@Override
	public int coiledScoreSum(int memberId, int matchId) {
		// TODO Auto-generated method stub
		int[] scoreList = memberDao.getScoreIdByMemberAndMatch(memberId, matchId);
		int isCoiled = 1;
		int sum = 0;
		for (int i = 1; i < scoreList.length; i++) {
			if (scoreList[i - 1] + 1 == scoreList[i]) {
				isCoiled++;
			} else {
				if (isCoiled >= 2) {
					for (int j = 0; j < isCoiled; j++) {
						sum++;
					}
					isCoiled = 1;
				}
			}
		}
		if (scoreList.length >= 2) {
			if (scoreList[0] + 1 == scoreList[1]) {
				sum++;
			}
		}
		return sum;
	}

	@Override
	public double getScoreMatch(int memberId, int matchId) {
		Double sumScore = Double.valueOf(selectScoreByMemberAndMatch(memberId, matchId)) * 40 / 100;
		Double coiledScoreNum = Double.valueOf(coiledScoreNum(memberId, matchId)) * 30 / 100;
		Double coiledScoreSum = Double.valueOf(coiledScoreSum(memberId, matchId)) * 30 / 100;
		Double score = sumScore + coiledScoreNum + coiledScoreSum;
		return score;
	}

	@Override
	public Member selectMemberById(int id) {
		// TODO Auto-generated method stub
		System.out.println(memberDao);
		return memberDao.selectMemberById(id);
	}
}
