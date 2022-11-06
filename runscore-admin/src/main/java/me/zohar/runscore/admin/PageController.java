package me.zohar.runscore.admin;

import me.zohar.runscore.useraccount.vo.UserAccountDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

	@GetMapping("/")
	public String index() {

		UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		System.out.println("用户登录名称="+user.getUsername());
//admin 和admin1还有admin2和system 都是有统计分析
		if(user.getUsername().equals("admin") || user.getUsername().equals("admin1")|| user.getUsername().equals("admin2") || user.getUsername().equals("system") || user.getUsername().equals("zohar001")){
			return "statistical-analysis";//统计分析界面
		}
		return "merchant-order";//解决运营环境不跳转界面问题
	}

	/**
	 * 登录页面
	 * 
	 * @return
	 */
	@GetMapping("/login")
	public String login() {
		return "login";
	}

	/**
	 * 投注记录
	 * 
	 * @return
	 */
	@GetMapping("/betting-record")
	public String bettingRecord() {
		return "betting-record";
	}

	/**
	 * 账号管理
	 * 
	 * @return
	 */
	@GetMapping("/account-manage")
	public String accountManage() {
		return "account-manage";
	}

	/**
	 * 帐变日志
	 * 
	 * @return
	 */
	@GetMapping("/account-change-log")
	public String accountChangeLog() {
		return "account-change-log";
	}

	/**
	 * 充值订单
	 * 
	 * @return
	 */
	@GetMapping("/recharge-order")
	public String rechargeOrder() {
		return "recharge-order";
	}

	/**
	 * 提现记录
	 * 
	 * @return
	 */
	@GetMapping("/withdraw-record")
	public String withdrawRecord() {
		return "withdraw-record";
	}

	/**
	 * 配置项管理
	 * 
	 * @return
	 */
	@GetMapping("/config-manage")
	public String configManage() {
		return "config-manage";
	}

	/**
	 * 字典管理
	 * 
	 * @return
	 */
	@GetMapping("/dict-manage")
	public String dictManage() {
		return "dict-manage";
	}

	/**
	 * 总控室
	 * 
	 * @return
	 */
	@GetMapping("/master-control-room")
	public String masterControlRoom() {
		return "master-control-room";
	}

	/**
	 * 商户订单
	 * 
	 * @return
	 */
	@GetMapping("/merchant-order")
	public String merchantOrder() {
		return "merchant-order";
	}

	/**
	 * 自动机查询商户订单记录数据
	 *
	 * @return
	 */
	@GetMapping("/merchant-auto-order")
	public String merchantAutoOrder() {
		return "merchant-auto-order";
	}

	/**
	 * 付款订单列表
	 *
	 * @return
	 */
	@GetMapping("/merchant-payout_order")
	public String merchantPayoutOrder() {
		return "merchant-payout_order";
	}
	/**
	 * 商户管理
	 * 
	 * @return
	 */
	@GetMapping("/merchant")
	public String merchant() {
		return "merchant";
	}

	/**
	 * 统计分析
	 * 
	 * @return
	 */
	@GetMapping("/statistical-analysis")
	public String statisticalAnalysis() {
		return "statistical-analysis";
	}

	/**
	 * 收款码列表
	 * 
	 * @return
	 */
	@GetMapping("/gathering-code")
	public String gatheringCode() {
		return "gathering-code";
	}

	/**
	 * 新的收款码列表
	 *
	 * @return
	 */
	@GetMapping("/gathering-newcode")
	public String gatheringNewCode() {
		return "gathering-newcode";
	}
	/**
	 * 银行卡结余列表
	 *
	 * @return
	 */
	@GetMapping("/bankcardBalance")
	public String bankcardBalance() {
		return "bankcardBalance";
	}

	/**
	 * 自动机payout使用列表
	 *
	 * @return
	 */
	@GetMapping("/gatheringautocode")
	public String gatheringautocode() {
		return "gathering-autocode";
	}

	/**
	 * 银行卡交易列表数据收款码
	 *
	 * @return
	 */
	@GetMapping("/bankcardorder-list")
	public String gatheringCodeList() {
		return "bankcardorder-list";
	}
	/**
	 * 申诉记录
	 * 
	 * @return
	 */
	@GetMapping("/appeal-record")
	public String appealRecord() {
		return "appeal-record";
	}

	@GetMapping("/appeal-details")
	public String appealDetails() {
		return "appeal-details";
	}

	@GetMapping("/recharge-channel")
	public String rechargeChannel() {
		return "recharge-channel";
	}

	@GetMapping("/login-log")
	public String loginLog() {
		return "login-log";
	}

	/**
	 * 返点表
	 * 
	 * @return
	 */
	@GetMapping("/rebate")
	public String rebate() {
		return "rebate";
	}

	@GetMapping("/gathering-channel")
	public String gatheringChannel() {
		return "gathering-channel";
	}

	@GetMapping("/gathering-channel-rate")
	public String gatheringChannelRate() {
		return "gathering-channel-rate";
	}

	@GetMapping("/gathering-channel-rebate")
	public String gatheringChannelRebate() {
		return "gathering-channel-rebate";
	}

	@GetMapping("/online-account")
	public String onlineAccount() {
		return "online-account";
	}

	@GetMapping("/merchant-settlement-record")
	public String merchantSettlementRecord() {
		return "merchant-settlement-record";
	}
	
	@GetMapping("/data-clean")
	public String dataClean() {
		return "data-clean";
	}
	
	@GetMapping("/system-notice")
	public String systemNotice() {
		return "system-notice";
	}

	/**
	 * 内部转账提现记录
	 *
	 * @return
	 */
	@GetMapping("/insideWithdraw-record")
	public String insideWithdraw() {
		return "insidewithdraw-record";
	}
	/**
	 * 内部转账提现记录
	 *
	 * @return
	 */
	@GetMapping("monitor")
	public String monitor() {
		return "monitor";
	}
	/**
	 * 商户交易列表数据
	 * @return
	 */
	@GetMapping("merchantList")
	public String merchantList() {
		return "merchantList";
	}

	/**
	 * 付款统计总数据
	 * @return
	 */
	@GetMapping("payoutRecord")
	public String payoutRecordList() {
		return "statistical-payout-analysis";
	}

}
