package me.zohar.runscore;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

	/**
	 * 统计分析界面
	 * @return
	 */
	@GetMapping("/")
	public String index() {
		return "statistical-analysis";
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
	 * 商户订单
	 * 
	 * @return
	 */
	@GetMapping("/merchant-order")
	public String merchantOrder() {
		return "merchant-order";
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

	@GetMapping("/merchant-info")
	public String merchantInfo() {
		return "merchant-info";
	}

	@GetMapping("/rate-details")
	public String rateDetails() {
		return "rate-details";
	}

	@GetMapping("/apply-settlement")
	public String applySettlement() {
		return "apply-settlement";
	}

	/**
	 * 商户交易列表
	 * @return
	 */
	@GetMapping("/merchant-paylist")
	public String merchantpaylist() {
		return "merchant-paylist";
	}
	/**
	 * FastPay界面
	 * @return
	 */
	@GetMapping("/FastPay")
	public String FastPay() {
		return "FastPay";
	}
	/**
	 * BankQr界面
	 * @return
	 */
	@GetMapping("/BankQr")
	public String BankQr() {
		return "BankQr";
	}

	/**
	 * Pay247界面
	 * @return
	 */
	@GetMapping("/Pay247")
	public String Pay247() {
		return "payyuenan";
	}

	/**
	 * 人民币的支付界面 微信和支付宝扫码使用
	 * @return
	 */
	@GetMapping("/pay")
	public String pay() {
		return "pay";
	}

	/**
	 * 付款商户订单
	 * @return
	 */
	@GetMapping("/payoutmerchant-order")
	public String payoutmerchant() {
		return "payoutmerchant-order";
	}
}
