package cn.edu.hnzj.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.edu.hnzj.dao.RecordDao;
import cn.edu.hnzj.dao.ScoreDao;
import cn.edu.hnzj.pojo.Record;
import cn.edu.hnzj.service.RecordService;

@Service("recordService")
public class RecordServiceImpl implements RecordService {
	@Resource
	private RecordDao recordDao;
	@Resource
	private ScoreDao scoreDao;
	@Resource
	private Record defaultRecord;

	public void setRecordDao(RecordDao recordDao) {
		this.recordDao = recordDao;
	}

	public void setScoreDao(ScoreDao scoreDao) {
		this.scoreDao = scoreDao;
	}

	@Override
	public boolean addRecordByObject(Record record) {
		// TODO 添加记录
		int rowRecord = recordDao.insertRecordByObject(record);
		return rowRecord == 1;
	}

	@Override
	public int updateRecordByObject(Record record) {
		// TODO 修改得分的队员
		Integer oldTeamId = getRecordByMatIdSinIn(record).getTeamId();
		recordDao.updateRecordByMatIdSinIn(record);
		return oldTeamId;
	}

	@Override
	public Record getLastRecord(int matchId) {
		// TODO 查询出比赛的最后一条记录
		return recordDao.selectLastRecordByMatchId(matchId);
	}

	@Override
	public Record getRecordByMatIdSinIn(Record record) {
		// TODO Auto-generated method stub
		return recordDao.selectRecordByMatIdSinIn(record);
	}

	/**
	 * @MethodName: getRecordsByMatchId
	 * @Description: 获取某场比赛的所有记录
	 * @param matchId
	 * @return
	 * @see cn.edu.hnzj.service.RecordService#getRecordByMatchId(int)
	 * @date: 2023-05-05 10:17:35
	 */
	@Override
	public List<Record> getRecordsByMatchId(int matchId) {
		// TODO Auto-generated method stub
		return recordDao.selectRecordsByMatchId(matchId);
	}
}
