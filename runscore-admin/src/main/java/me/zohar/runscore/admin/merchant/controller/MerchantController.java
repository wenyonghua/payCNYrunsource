package me.zohar.runscore.admin.merchant.controller;

import me.zohar.runscore.merchant.param.*;
import me.zohar.runscore.merchant.vo.MerchantVO;
import me.zohar.runscore.useraccount.param.SaveAccountReceiveOrderChannelParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.SecureUtil;
import me.zohar.runscore.common.vo.Result;
import me.zohar.runscore.merchant.service.MerchantService;

@Controller
@RequestMapping("/merchant")
public class MerchantController {

	@Autowired
	private MerchantService merchantService;


//	/**
//	 * 体现确定审核通过
//	 * @param id
//	 * @param note
//	 * @return
//	 */
//	@GetMapping("/settlementApproved")
//	@ResponseBody
//	public Result settlementApproved(String id, String note,String payCardNo,String serverCharge) {
	//	merchantService.settlementApproved(id, note,payCardNo,serverCharge);
//		return Result.success();
//	}
	/**
	 * 体现确定审核通过 String id, String note,String payCardNo,String serverCharge  这个时候就绑定银行卡号
	 * @param param
	 * @return
	 */
	@PostMapping("/settlementApproved")
	@ResponseBody
	public Result settlementApproved(@RequestBody MerchantBankSettlementParam param) {
		//merchantService.settlementApproved(id, note,payCardNo,serverCharge);

		merchantService.settlementPayoutApproved(param);//添加付款银行卡号

		return Result.success();
	}


	/**
	 * 商户提现审核不通过 拒绝 需要返回金额给商户
	 * @param id
	 * @param note
	 * @return
	 */
	@GetMapping("/settlementNotApproved")
	@ResponseBody
	public Result settlementNotApproved(String id, String note) {
		merchantService.settlementNotApproved(id, note);
		return Result.success();
	}

	/**
	 * 商户提现确定到账 记录银行交易列表数据
	 * @param id
	 * @return
	 */
	@GetMapping("/settlementConfirmCredited")
	@ResponseBody
	public Result settlementConfirmCredited(String id) {
		merchantService.settlementConfirmCredited(id);
		return Result.success();
	}

	/**
	 * 进行审核提现查看
	 * @param id
	 * @return
	 */
	@GetMapping("/findByMerchantSettlementRecordId")
	@ResponseBody
	public Result findByMerchantSettlementRecordId(String id) {
		return Result.success().setData(merchantService.findByMerchantSettlementRecordId(id));
	}

	/**
	 * 查询商户结算记录列表
	 * @param param
	 * @return
	 */
	@GetMapping("/findMerchantSettlementRecordByPage")
	@ResponseBody
	public Result findMerchantSettlementRecordByPage(MerchantSettlementRecordQueryCondParam param) {
		return Result.success().setData(merchantService.findMerchantSettlementRecordByPage(param));
	}

	/**
	 * 查询商户列表数据
	 * @return
	 */
	@GetMapping("/findAllMerchant")
	@ResponseBody
	public Result findAllMerchant() {
		return Result.success().setData(merchantService.findAllMerchant());
	}

	/**
	 * 修改商户登录密码
	 * @param id
	 * @param newLoginPwd
	 * @return
	 */
	@PostMapping("/modifyLoginPwd")
	@ResponseBody
	public Result modifyLoginPwd(String id, String newLoginPwd,String moneyPwd) {
		merchantService.modifyNewLoginPwd(id, newLoginPwd,moneyPwd);
		return Result.success();
	}

	/**
	 * 修改商户资金密码
	 * @return
	 */
	@GetMapping("/generateSecretKey")
	@ResponseBody
	public Result generateSecretKey() {
		return Result.success().setData(SecureUtil.md5(UUID.fastUUID().toString()));
	}

	@PostMapping("/addMerchant")
	@ResponseBody
	public Result addMerchant(AddMerchantParam param) {
		merchantService.addMerchant(param);
		return Result.success();
	}

	/**
	 * 更新商户信息
	 * @param param
	 * @return
	 */
	@PostMapping("/updateMerchant")
	@ResponseBody
	public Result updateMerchant(MerchantEditParam param) {
		merchantService.updateMerchant(param);
		return Result.success();
	}

	/**
	 * 查询商户信息
	 * @param id
	 * @return
	 */
	@GetMapping("/findMerchantById")
	@ResponseBody
	public Result findMerchantById(String id) {
		MerchantVO vo=merchantService.findMerchantById(id);
		return Result.success().setData(merchantService.findMerchantById(id));
	}

	@GetMapping("/delMerchantById")
	@ResponseBody
	public Result delMerchantById(String id) {
		merchantService.delMerchantById(id);
		return Result.success();
	}

	@GetMapping("/findMerchantByPage")
	@ResponseBody
	public Result findPlatformOrderByPage(MerchantQueryCondParam param) {
		return Result.success().setData(merchantService.findMerchantByPage(param));
	}

}
