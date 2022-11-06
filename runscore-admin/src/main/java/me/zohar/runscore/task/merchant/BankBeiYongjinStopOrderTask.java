package me.zohar.runscore.task.merchant;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import me.zohar.runscore.admin.merchant.controller.MerchantOrderController;
import me.zohar.runscore.common.vo.PageResult1;
import me.zohar.runscore.common.vo.Result;
import me.zohar.runscore.gatheringcode.param.GatheringCodeQueryCondParam;
import me.zohar.runscore.gatheringcode.service.GatheringCodeService;
import me.zohar.runscore.gatheringcode.vo.GatheringCodeUsageVO;
import me.zohar.runscore.merchant.repo.BankcardRepo;
import me.zohar.runscore.merchant.service.MerchantOrderService;
import me.zohar.runscore.merchant.vo.BankCardListReportVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 备用金停用银行卡 任务关闭此功能
 */
//@Component
@Slf4j
public class BankBeiYongjinStopOrderTask {
	

	@Autowired
	private GatheringCodeService gatheringCodeService;
	@Autowired
	private BankcardRepo bankcardRepo;//银行卡交易记录数据
	private final Logger logger = LoggerFactory.getLogger(BankBeiYongjinStopOrderTask.class);

	/**
	 * 定时任务
	 */
	//@Scheduled(fixedRate = 1900000)//60秒表示1分钟执行一次 15分钟*60表示15分钟执行一次
	public void execute() {
		try {
			logger.info("备用金停用银行卡任务start");
//			GatheringCodeQueryCondParam param=new GatheringCodeQueryCondParam();
//			param.setPageNum(1);
//			param.setPageSize(5);
//			param.setCardUse("3");//卡用途 1是存款卡，2是付款卡,3备用金
//			param.setInUse("1");//银行卡状态 1是使用 查询备用金使用中的银行卡
//			List<GatheringCodeUsageVO> gatheringCodeUsageVOList=gatheringCodeService.findBankUserStateByPage(param);
//
//			for(GatheringCodeUsageVO gatheringCodeUsageVO:gatheringCodeUsageVOList){
//			    String id=gatheringCodeUsageVO.getId();//获取ID号
//				String dailyQuota=null;
//			    if(id!=null) {
//				GatheringCodeUsageVO gatheringCodeUsageVO1 = gatheringCodeService.findGatheringCodeUsageById(id);//查询银行卡
//				dailyQuota=gatheringCodeUsageVO1.getDailyQuota();//银行卡单日额度限制;
//
//				String bankCode=gatheringCodeUsageVO.getBankCode();//银行卡号
//				//logger.info("银行卡单日限额={},银行卡号={}",dailyQuota,bankCode);
//				String startTime= DateUtil.beginOfDay(new Date()).toString();//开始时间当日时间
//				String endTime=DateUtil.endOfDay(new Date()).toString();//结束时间
//				Double 	dayDepositTotal = bankcardRepo.findDepositByBanKCodeAndStartTimeAndEneTime(bankCode,startTime,endTime);//查询存款银行卡当日收款金额
//				//logger.info("银行卡单日限额={},银行卡号={},银行卡今日总存款额度={}",dailyQuota,bankCode,dayDepositTotal);
//				if(!(dayDepositTotal==null)) {
//					if (dayDepositTotal.compareTo(new Double(dailyQuota))>=0) {//存款银行卡单日收款金额>银行卡单日额度限制
//						logger.info("银行卡单日限额={},银行卡号={},银行卡今日总存款额度={},停用银行卡开始", dailyQuota, bankCode, dayDepositTotal);
//						gatheringCodeService.updateToNormalState(id, false);//停用收款银行卡
//						logger.info("银行卡单日限额={},银行卡号={},银行卡今日总存款额度={},停用银行卡结束", dailyQuota, bankCode, dayDepositTotal);
//					}
//				}
//			}
//			}
			log.info("备用金停用银行卡定时任务end");
		} catch (Exception e) {
			log.error("定时发布订单定时任务", e);
		}
	}

}
