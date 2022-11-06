package me.zohar.runscore.admin.statisticalanalysis.controller;

import me.zohar.runscore.common.vo.Result;
import me.zohar.runscore.statisticalanalysis.service.MerchantStatisticalAnalysisService;
import me.zohar.runscore.statisticalanalysis.service.StatisticalAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 付款统计
 */
@Controller
@RequestMapping("/statisticalPayoutAnalysis")
public class StatisticalPayoutAnalysisController {

	@Autowired
	private StatisticalAnalysisService statisticalAnalysisService;

	@Autowired
	private MerchantStatisticalAnalysisService merchantStatisticalAnalysisService;



	/**
	 * 各商户交易情况
	 * @return
	 */
	@GetMapping("/findMerchantPayoutTradeSituation")
	@ResponseBody
	public Result findMerchantPayoutTradeSituation() {
		return Result.success().setData(merchantStatisticalAnalysisService.findMerchantPayoutTradeSituation());
	}





	/**
	 * 付款统计总数据
	 * @return
	 */
	@GetMapping("/findPayoutTradeSituation")
	@ResponseBody
	public Result findTradeSituation() {
		return Result.success().setData(statisticalAnalysisService.findPayoutTradeSituation());
	}

}
