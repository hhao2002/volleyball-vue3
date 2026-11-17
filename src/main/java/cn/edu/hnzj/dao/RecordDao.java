package cn.edu.hnzj.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.edu.hnzj.pojo.Record;

/**
 * @ClassName: RecordDao
 * @Description: volleyball.record表 的 dao层
 * @author: HhaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-03 04:45:19
 */
@Repository
public interface RecordDao {
	
	/**
	 * @MethodName: selectLastRecordByMatchId
	 * @Description: 查询比赛的最后一场记录
	 * @param matchId
	 * @return Record
	 * @date: 2022-05-20 06:15:40
	 */
	public Record selectLastRecordByMatchId(int matchId);
	
	/**
	 * @MethodName: selectRecordsByMatchId
	 * @Description: 查询莫比赛的所有记录
	 * @param matchId
	 * @return List<Record>
	 * @date: 2023-05-05 10:16:25
	 */
	public List<Record> selectRecordsByMatchId(int matchId);

	/**
	 * @MethodName: insertRecordByObject
	 * @Description: 通过Record对象 向 volleyball.record表 添加一条记录
	 * @param record 记录对象
	 * @return int 影响行数
	 * @date: 2022-05-03 04:46:06
	 */	
	public int insertRecordByObject(Record record);

	/**
	 * @MethodName: updateRecordByMatIdSinIn
	 * @Description: 通过 比赛id、场数、局数 修改 得分者
	 * @param record
	 * @return int 是否更新成功
	 * @date: 2022-06-25 05:43:55
	 */
	public int updateRecordByMatIdSinIn(Record record);
	
	/**
	 * @MethodName: selectRecordByMatIdSinIn
	 * @Description: 通过 比赛id、场数、局数 获取 得分的记录
	 * @param record
	 * @return Record 得分
	 * @date: 2022-06-25 06:33:53
	 */
	public Record selectRecordByMatIdSinIn(Record record);
	
	/**
	 * @MethodName: deleteRecordById
	 * @Description: 通过id 向 volleyball.record表 删除一条记录
	 * @param record
	 * @return int
	 * @date: 2022-05-13 04:12:58
	 */
	public int deleteRecordById(int id);

	/**
	 * @MethodName: deleteRecordByMatchId
	 * @Description: 通过比赛id 向 volleyball.record表 删除一个比赛的所有记录
	 * @param MatchId
	 * @return int
	 * @date: 2022-05-13 04:14:59
	 */
	public int deleteRecordByMatchId(int MatchId);
}
