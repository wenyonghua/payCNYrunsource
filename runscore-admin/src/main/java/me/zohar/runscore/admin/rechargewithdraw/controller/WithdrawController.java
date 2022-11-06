package me.zohar.runscore.admin.rechargewithdraw.controller;

import me.zohar.runscore.merchant.param.MerchantSettlementRecordQueryCondParam;
import me.zohar.runscore.rechargewithdraw.param.InsideWithdrawRecordQueryCondParam;
import me.zohar.runscore.rechargewithdraw.service.InsideWithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import me.zohar.runscore.common.vo.Result;
import me.zohar.runscore.rechargewithdraw.param.WithdrawRecordQueryCondParam;
import me.zohar.runscore.rechargewithdraw.service.WithdrawService;


/**
 * 内部转折记录
 */
@Controller
@RequestMapping("/withdraw")
public class WithdrawController {

	@Autowired
	private WithdrawService withdrawService;

	@Autowired
	private InsideWithdrawService insideWithdrawService;

	@GetMapping("/findWithdrawRecordById")
	@ResponseBody
	public Result findWithdrawRecordById(String id) {
		return Result.success().setData(withdrawService.findWithdrawRecordById(id));
	}

	@GetMapping("/approved")
	@ResponseBody
	public Result approved(String id, String note) {
		withdrawService.approved(id, note);
		return Result.success();
	}

	@GetMapping("/notApproved")
	@ResponseBody
	public Result notApproved(String id, String note) {
		withdrawService.notApproved(id, note);
		return Result.success();
	}

	@GetMapping("/confirmCredited")
	@ResponseBody
	public Result confirmCredited(String id) {
		withdrawService.confirmCredited(id);
		return Result.success();
	}

	/**
	 * 查询体现记录数据
	 * @param param
	 * @return
	 */
	@GetMapping("/findWithdrawRecordByPage")
	@ResponseBody
	public Result findWithdrawRecordByPage(WithdrawRecordQueryCondParam param) {
		return Result.success().setData(withdrawService.findWithdrawRecordByPage(param));
	}








//	/**
//	 * 查询自己的结算记录列表
//	 * @param param
//	 * @return
//	 */
//	@GetMapping("/findMerchantSettlementRecordByPage")
//	@ResponseBody
//	public Result findMerchantSettlementRecordByPage(MerchantSettlementRecordQueryCondParam param) {
//		return Result.success().setData(merchantService.findMerchantSettlementRecordByPage(param));
//	}


}
