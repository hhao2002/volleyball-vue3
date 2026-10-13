package cn.edu.hnzj.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.edu.hnzj.pojo.Match;
import cn.edu.hnzj.pojo.Team;

/**
 * @ClassName: TeamDao
 * @Description: volleyball.team表 的 dao层
 * @author: aJun
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-14 07:36:39
 */
@Repository
public interface TeamDao {

	/**
	 * @MethodName: updateTeamByid
	 * @Description: 通过name属性 修改 volleyball.team表 中的队伍信息
	 * @param Team 队伍对象
	 * @return int 修改队伍影响的数量
	 * @date: 2022-05-03 04:46:06
	 */
	public int updateTeamByName(Team team);
	
	/**
	 * @MethodName: selectTeamById
	 * @Description: 通过比赛id属性 查询 volleyball.team表 中的队伍信息
	 * @param matchId 比赛Id
	 * @return List<team> 某个比赛的连个队伍对象
	 * @date: 2022-05-03 04:46:06
	 */
	public List<Team> selectTeamByMatchId(int matchId);

	/**
	 * @MethodName: insertTeamByObject
	 * @Description: 通过Team对象 向 volleyball.team表 添加一个队伍
	 * @param team 记录添加比赛队伍信息
	 * @return int 添加的队伍数量
	 * @date: 2022-05-03 04:46:06
	 */
	public int insertTeamByObject(Team team);

	/**
	 * @MethodName: deleteTeamById
	 * @Description: 通过id属性 向 volleyball.team表 删除一个队伍
	 * @param id 删除的队员
	 * @return int 删除的队伍数量
	 * @date: 2022-05-03 04:46:06
	 */
	public int deleteTeamById(int id);

	/**
	 * @MethodName: selectTeamAll
	 * @Description: 通过比赛id属性 查询 volleyball.team表 中的队伍信息
	 * @param
	 * @return List<Team> 所有队伍的信息
	 * @date: 2022-05-03 04:46:06
	 */
	public List<Team> selectTeamAll();
	
	/**
	 * @MethodName: selectTeamById
	 * @Description: 通过队伍id属性 查询 volleyball.team表 中的队伍信息
	 * @param id
	 * @return Team
	 * @date: 2023-05-04 03:53:17
	 */
	public Team selectTeamById(int id);

	/**
	 * 
	 * @MethodName: selectTeamNameAll
	 * @Description: 获得所有team的名称
	 * @return List<String>
	 * @date: 2022-05-15 08:08:57
	 */
	public List<String> selectTeamNameAll();

	/**
	 * @MethodName: modifyTeamName
	 * @Description: TODO
	 * @param match
	 * @return boolean
	 * @date: 2022-05-17 10:25:17
	 */
	public boolean modifyTeamName(Match match);
	
	/**
	 * @MethodName: updateMatchIdOfTeamToOneByMatchId
	 * @Description: 修改队伍中指定参与比赛id为1
	 * @param matchId
	 * @return int
	 * @date: 2022-06-24 10:48:14
	 */
	public int updateMatchIdOfTeamToOneByMatchId(Integer matchId);
}
