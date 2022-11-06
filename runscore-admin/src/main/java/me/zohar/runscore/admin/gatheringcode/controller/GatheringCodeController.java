package me.zohar.runscore.admin.gatheringcode.controller;

import me.zohar.runscore.dictconfig.vo.DictItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import me.zohar.runscore.common.vo.Result;
import me.zohar.runscore.gatheringcode.param.GatheringCodeParam;
import me.zohar.runscore.gatheringcode.param.GatheringCodeQueryCondParam;
import me.zohar.runscore.gatheringcode.service.GatheringCodeService;

import java.util.List;

@Controller
@RequestMapping("/gatheringCode")
public class GatheringCodeController {

	@Autowired
	private GatheringCodeService gatheringCodeService;

	/**
	 * 删除银行卡
	 * @param id
	 * @param titleBankCode
	 * @return
	 */
	@GetMapping("/delGatheringCodeById")
	@ResponseBody
	public Result delGatheringCodeById(String id,String  titleBankCode) {

		gatheringCodeService.delGatheringCodeById(id,titleBankCode);
		return Result.success();
	}

	/**
	 * 查看单个收款卡详情
	 * @param id
	 * @return
	 */
	@GetMapping("/findGatheringCodeById")
	@ResponseBody
	public Result findGatheringCodeUsageById(String id) {
		return Result.success().setData(gatheringCodeService.findGatheringCodeUsageById(id));
	}

	/**
	 * 新方法查看单个收款卡详情
	 * @param id
	 * @return
	 */
	@GetMapping("/findNewGatheringCodeById")
	@ResponseBody
	public Result findNewGatheringCodeById(String id) {
		return Result.success().setData(gatheringCodeService.findNewGatheringCodeUsageById(id));
	}

	/**
	 * 添加收款码
	 * @param param
	 * @return
	 */
	@PostMapping("/addOrUpdateGatheringCode")
	@ResponseBody
	public Result addOrUpdateGatheringCode(@RequestBody GatheringCodeParam param) {
		gatheringCodeService.addOrUpdateGatheringCode(param);
		return Result.success();
	}

	/**
	 * 修改收款码
	 * @param id
	 * @return
	 */
	@GetMapping("/updateToNormalState")
	@ResponseBody
	public Result updateToNormalState(String id) {
		gatheringCodeService.updateToNormalState(id);
		return Result.success();
	}

	/**
	 * 收款码列表数据
	 * @param param
	 * @return
	 */
	@GetMapping("/findGatheringCodeByPage")
	@ResponseBody
	public Result findGatheringCodeByPage(GatheringCodeQueryCondParam param) {
		return Result.success().setData(gatheringCodeService.findGatheringCodeUsageByPage(param));
	}


	/**
	 * 新接口查询收款码列表数据
	 * @param param
	 * @return
	 */
	@GetMapping("/findNewGatheringCodeByPage")
	@ResponseBody
	public Result findNewGatheringCodeByPage(GatheringCodeQueryCondParam param) {
		if(param.getBankCardAccount()!=null && "All".equals(param.getBankCardAccount())){//全部
			param.setBankCardAccount("");//如果是全部就查询所以数据
		}
		return Result.success().setData(gatheringCodeService.findGatheringCodeByPage(param));
	}

	/**
	 * 获取存款卡使用中银行卡号列表
	 * @return
	 */
	@GetMapping("/getUseBankList")
	@ResponseBody
	public Result getSystemSetting() {
		GatheringCodeQueryCondParam param=new GatheringCodeQueryCondParam();
		param.setPageNum(1);
		param.setPageSize(99999999);
		//param.setInUse("1");//使用中 1是使用中
		param.setCardUse("1");//卡用途 1是存款卡，2备用金
		return Result.success().setData(gatheringCodeService.findBankUserStateByPage(param));
	}

	/**
	 * 启用收款码
	 * @param id
	 * @return
	 */
	@GetMapping("/updateUseInuse")
	@ResponseBody
	public Result updateUseInuse(String id) {
		gatheringCodeService.updateToNormalState(id,true);
		return Result.success();
	}

	/**
	 * 停用收款码
	 * @param id
	 * @return
	 */
	@GetMapping("/updateStopInuse")
	@ResponseBody
	public Result updateStopInuse(String id) {
		gatheringCodeService.updateToNormalState(id,false);
		return Result.success();
	}

	/**
	 * 通过银行卡号查询银行余额
	 * @return
	 */
	@GetMapping("/getBankBalance")
	@ResponseBody
	public Result getBankBalance(String bankCard) {
		return Result.success().setData(gatheringCodeService.getBankBalance(bankCard));
	}

	/**
	 * 启用自动机
	 * @param id
	 * @return
	 */
	@GetMapping("/startRun")
	@ResponseBody
	public Result startRun(String id) {
		gatheringCodeService.startRun(id);
		return Result.success();
	}

	/**
	 * 停用自动机
	 * @param id
	 * @return
	 */
	@GetMapping("/stopRun")
	@ResponseBody
	public Result stopRun(String id) {
		gatheringCodeService.stopRun(id);
		return Result.success();
	}


	/**
	 * 测试自动机链接
	 * @param id
	 * @return
	 */
	@GetMapping("/testAutoUse")
	@ResponseBody
	public Result testAutoUse(String id) {
		gatheringCodeService.testAutoUse(id);
		return Result.success();
	}
}
