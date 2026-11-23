package cn.edu.hnzj.service;

import org.apache.ibatis.annotations.Param;

import cn.edu.hnzj.pojo.Member;

public interface MemberService {
	/**
	 * @MethodName: selectScoreByMemberAndMatch
	 * @Description: 获得某人在某次比赛中获得的总分
	 * @param memberId 队员id
	 * @param matchId 比赛id
	 * @return int
	 * @date: 2022-06-22 09:53:06
	 */
	public int selectScoreByMemberAndMatch(@Param("memberId") int memberId,@Param("matchId") int matchId);
	
	/**
	 * @MethodName: coiledScoreNum
	 * @Description: 获得某人连续得分的次数
	 * @param scoreList
	 * @return int 次数
	 * @date: 2022-06-22 11:31:55
	 */
	public int coiledScoreNum(int memberId,int matchId);
	
	/**
	 * @MethodName: coiledScoreSum
	 * @Description: 某人某次比赛获得的连续得分的总分
	 * @param memberId
	 * @param matchId
	 * @return int
	 * @date: 2022-06-22 01:30:33
	 */
	public int coiledScoreSum(int memberId, int matchId);
	
	/**
	 * @MethodName: getScoreMatch
	 * @Description: 计算某人在某场比赛的评分，好判断mvp
	 * @param memberId
	 * @param matchId
	 * @return double
	 * @date: 2022-06-22 03:39:26
	 */
	public double getScoreMatch(int memberId, int matchId);
	
	/**
	 * @MethodName: selectMemberById
	 * @Description: 根据id查member
	 * @param id
	 * @return Member
	 * @date: 2022-06-22 04:46:13
	 */
	public Member selectMemberById(int id);
}