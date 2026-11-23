package cn.edu.hnzj.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.edu.hnzj.pojo.Record;
import cn.edu.hnzj.service.RecordService;

/**
 * @ClassName: RecordController
 * @Description: 记录的控制层
 * @author: HHaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-03 05:50:49
 */
@Controller
@RequestMapping("/record")
public class RecordController {
	/**
	 * @Fields service : 服务层实现类
	 * @date: 2022-05-03 05:56:39
	 */
	@Resource
	private RecordService recordService;
	@Resource
	private Record record;

	/**
	 * @MethodName: addRecord
	 * @Description: 保存一次记录 并更新比分数据
	 * @param Record 记录对象
	 * @return Record 记录对象
	 * @date: 2022-06-24 02:30:13
	 */
	@RequestMapping(value = "/addRecord.do")
	@ResponseBody
	public Record addRecord(@RequestBody Record record) {
		// TODO 添加记录到数据库 成功返回对象
		System.out.println(record);
		if (recordService.addRecordByObject(record))
			return record;
		return null;
	}

	@RequestMapping(value = "/updateRecord.do")
	@ResponseBody
	public Record updateRecord(@RequestBody Record record) {
		// TODO 将队伍的id设置为修改之前的队伍id
		record.setTeamId(recordService.updateRecordByObject(record));
		return record;
	}

	@RequestMapping(value = "/getOneRecord.do")
	@ResponseBody
	public Record getRecordByMatIdSinIn(@RequestBody Record record) {
		return recordService.getRecordByMatIdSinIn(record);
	}

	/**
	 * @MethodName: getRecordByMatchId
	 * @Description: 通过比赛id获取所有记录
	 * @param id
	 * @return List<Record>
	 * @date: 2023-05-05 10:14:33
	 */
	@RequestMapping(value = "/getRecords.do")
	@ResponseBody
	public List<Record> getRecordsByMatchId(@RequestBody Integer matchId) {
		return recordService.getRecordsByMatchId(matchId);
	}

}
