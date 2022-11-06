package me.zohar.runscore.admin.rechargewithdraw.controller;

import me.zohar.runscore.common.vo.Result;
import me.zohar.runscore.rechargewithdraw.param.InsideWithdrawRecordQueryCondParam;
import me.zohar.runscore.rechargewithdraw.param.WithdrawRecordQueryCondParam;
import me.zohar.runscore.rechargewithdraw.service.InsideWithdrawService;
import me.zohar.runscore.rechargewithdraw.service.WithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 内部转折记录
 */
@Controller
@RequestMapping("/insidewithdraw")
public class InsideWithdrawController {



	@Autowired
	private InsideWithdrawService insideWithdrawService;

	/**
	 * 进行审核
	 * @param id
	 * @return
	 */
	@GetMapping("/findWithdrawRecordById")
	@ResponseBody
	public Result findWithdrawRecordById(String id) {
		return Result.success().setData(insideWithdrawService.findWithdrawRecordById(id));
	}


	/**
	 * 添加内部转账功能
	 * @param openAccountBank 开户银行 建设银行
	 * @param accountHolder 开户姓名
	 * @param bankCardAccount 银行卡账号
	 * @param userName 用户
	 * @param note 备注说明
	 * @serviceCharge 手续
	 * @shoukuanNumber 收款卡号
	 * @withdrawAmount 提现金额
	 * @return
	 */
	@GetMapping("/saveInsideWithdraw")
	@ResponseBody
	public Result saveInsideWithdraw(String openAccountBank,String accountHolder,String bankCardAccount,String userName,String note,String serviceCharge,String shoukuanNumber,String withdrawAmount) {
		insideWithdrawService.saveInsideWithdraw( bankCardAccount,note,serviceCharge,shoukuanNumber,withdrawAmount);
		return Result.success();
	}

	/**
	 * 确认审核通过
	 * @param id
	 * @param note
	 * @serviceCharge 手续
	 * @shoukuanNumber 收款卡号
	 * @withdrawAmount 提现金额
	 * @return
	 */
	@GetMapping("/approved")
	@ResponseBody
	public Result approved(String id, String note,String serviceCharge,String shoukuanNumber,String withdrawAmount) {
		insideWithdrawService.approved(id, note,serviceCharge,shoukuanNumber,withdrawAmount);
		return Result.success();
	}

	@GetMapping("/notApproved")
	@ResponseBody
	public Result notApproved(String id, String note) {
		insideWithdrawService.notApproved(id, note);
		return Result.success();
	}

	/**
	 * 确认到账才会扣除银行卡的记录
	 * @param id
	 * @return
	 */
	@GetMapping("/confirmCredited")
	@ResponseBody
	public Result confirmCredited(String id) {
		insideWithdrawService.confirmCredited(id);
		return Result.success();
	}

	/**
	 * 查询体现记录数据
	 * @param param
	 * @return
	 */
	@GetMapping("/findWithdrawRecordByPage")
	@ResponseBody
	public Result findWithdrawRecordByPage(InsideWithdrawRecordQueryCondParam param) {
		return Result.success().setData(insideWithdrawService.insideFindWithdrawRecordByPage(param));
	}



	/**
	 * 内部转折体现记录数据
	 * @param param
	 * @return
	 */
	@GetMapping("/insidewRecordByPage")
	@ResponseBody
	public Result insidewRecordByPage(InsideWithdrawRecordQueryCondParam param) {
		return Result.success().setData(insideWithdrawService.insideFindWithdrawRecordByPage(param));
	}



}
