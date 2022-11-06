package me.zohar.runscore.merchantorder.controller;

import me.zohar.runscore.common.ApiLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import me.zohar.runscore.common.vo.Result;
import me.zohar.runscore.merchant.param.LowerLevelAccountReceiveOrderQueryCondParam;
import me.zohar.runscore.merchant.param.MyReceiveOrderRecordQueryCondParam;
import me.zohar.runscore.merchant.service.MerchantOrderService;
import me.zohar.runscore.useraccount.vo.UserAccountDetails;

@Controller
@RequestMapping("/merchantOrder")
public class MerchantOrderController {

	@Autowired
	private MerchantOrderService platformOrderService;

	/**
	 * 确认支付
	 * @param orderId
	 * @return
	 */
	@ApiLog
	@GetMapping("/userConfirmToPaid")
	@ResponseBody
	public Result userConfirmToPaid(String orderId,String textValuedesk) {
		if(orderId!=null && textValuedesk!=null) {
			UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();//登录用户ID号
			platformOrderService.userConfirmToPaid(user.getUserAccountId(), orderId,textValuedesk);
		}else{
			return Result.fail("请输入描述备注信息!");
		}
		return Result.success();
	}

	/**
	 * 查询审核订单数据
	 * @return
	 */
	@ApiLog
	@GetMapping("/findMyWaitConfirmOrder")
	@ResponseBody
	public Result findMyWaitConfirmOrder() {
		UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return Result.success().setData(platformOrderService.findMyWaitConfirmOrder(user.getUserAccountId()));
	}

	/**
	 * 查询接单列表数据
	 * @return
	 */
	@ApiLog
	@GetMapping("/findMyWaitReceivingOrder")
	@ResponseBody
	public Result findMyWaitReceivingOrder() {
		UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return Result.success().setData(platformOrderService.findMyWaitReceivingOrder(user.getUserAccountId()));
	}

	/**
	 * 自动接单和立即接单
	 * @param orderId 订单号
	 * @return
	 */
	@ApiLog
	@GetMapping("/receiveOrder")
	@ResponseBody
	public Result receiveOrder(String orderId) {
		UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		platformOrderService.receiveOrder(user.getUserAccountId(), orderId);//接单
		return Result.success();
	}

	/**
	 * 接单记录数据
	 * @param param
	 * @return
	 */
	@ApiLog
	@GetMapping("/findMyReceiveOrderRecordByPage")
	@ResponseBody
	public Result findMyReceiveOrderRecordByPage(MyReceiveOrderRecordQueryCondParam param) {
		UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		param.setReceiverUserName(user.getUsername());
		return Result.success().setData(platformOrderService.findMyReceiveOrderRecordByPage(param));
	}

	@GetMapping("/findLowerLevelAccountReceiveOrderRecordByPage")
	@ResponseBody
	@ApiLog
	public Result findLowerLevelAccountReceiveOrderRecordByPage(LowerLevelAccountReceiveOrderQueryCondParam param) {
		UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		param.setCurrentAccountId(user.getUserAccountId());
		return Result.success().setData(platformOrderService.findLowerLevelAccountReceiveOrderRecordByPage(param));
	}


}
