package cn.edu.hnzj.service;

import java.util.List;

import cn.edu.hnzj.pojo.Record;

/**
 * @ClassName: RecordService
 * @Description: 记录的业务层
 * @author:
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-06-24 11:31:03
 */
public interface RecordService {
	/**
	 * @MethodName: getLastRecord
	 * @Description: 通过比赛id获取最后一个记录
	 * @param matchId 比赛id
	 * @return Record 最后议一个记录
	 * @date: 2022-06-29 09:58:38
	 */
	public Record getLastRecord(int matchId);
	
	/**
	 * @MethodName: addRecordScoreByObject
	 * @Description: 添加一条记录
	 * @param record 记录对象
	 * @return 是否添加成功
	 * @date: 2022-05-03 04:46:06
	 */
	public boolean addRecordByObject(Record record);

	/**
	 * @MethodName: updateRecordScoreByObject
	 * @Description: 修改得分
	 * @param record 记录对象
	 * @return int 返回老队伍的id
	 * @date: 2022-06-25 06:35:00
	 */
	public int updateRecordByObject(Record record);
	
	/**
	 * @MethodName: getRecordByMatIdSinIn
	 * @Description: 通过比赛id 场数 局数 获取记录对象
	 * @param record 记录对象
	 * @return Record 新的记录对象
	 * @date: 2022-06-29 01:59:44
	 */
	public Record getRecordByMatIdSinIn(Record record);

	/**
	 * @MethodName: getRecordByMatchId
	 * @Description: 通过比赛id获取所有记录
	 * @param id
	 * @return List<Record>
	 * @date: 2023-05-05 10:09:42
	 */
	public List<Record> getRecordsByMatchId(int matchId);
}
