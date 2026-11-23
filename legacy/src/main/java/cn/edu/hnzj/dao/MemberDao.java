package cn.edu.hnzj.dao;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.edu.hnzj.pojo.Member;

/**
 * @ClassName: MemberDao
 * @Description: volleyball.member表 的 dao层
 * @author: aJun
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-15 10:22:24
*/

@Repository
public interface MemberDao {
	
	/**
	 * @MethodName: insertMemberByObject
	 * @Description: 通过Member对象 向 volleyball.member表 添加一条记录
	 * @param member 添加成员的信息
	 * @return int 添加成员的数量
	 * @date: 2022-05-03 04:46:06
	 */
	public int insertMemberByObject(Member member);
	
	/**
	 * @MethodName: deleteMemberById
	 * @Description: 通过Member对象 向 volleyball.member表 删除一条记录
	 * @param id 要删除成员的id
	 * @return int 添加成员的数量
	 * @date: 2022-05-03 04:46:06
	 */
	public int deleteMemberById(int id);
	
	/**
	 * @MethodName: updateMemberById
	 * @Description: 通过Member对象 向 volleyball.member表 更新一条记录
	 * @param Member 
	 * @return int 修改成员的数量
	 * @date: 2022-05-03 04:46:06
	 */
	public int updateMemberById(Member member);
	
	/**
	 * @MethodName: selectMemberById
	 * @Description: 通过Member对象 向 volleyball.member表 查询一条记录
	 * @param id 要查询的成员
	 * @return Member 成员的信息
	 * @date: 2022-05-03 04:46:06
	 */
	public Member selectMemberById(int id);
	
	/**
	 * @MethodName: selectMemberAll
	 * @Description: 通过Member对象 向 volleyball.member表 查询所有记录
	 * @param 
	 * @return ArrayList<Member> 成员的信息
	 * @date: 2022-05-03 04:46:06
	 */
	public ArrayList<Member> selectMemberAll();
	
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
	 * @MethodName: getScoreIdByMemberAndMatch
	 * @Description: 获得某人某次比赛获得分数记录的id
	 * @param memberId
	 * @param matchId
	 * @return int[]
	 * @date: 2022-06-22 11:12:19
	 */
	public int[] getScoreIdByMemberAndMatch(@Param("memberId") int memberId,@Param("matchId") int matchId);
}
