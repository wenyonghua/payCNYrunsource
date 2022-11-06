package me.zohar.runscore;


import me.zohar.runscore.common.utils.IdUtils;
import me.zohar.runscore.common.vo.PageResult;
import me.zohar.runscore.constants.Constant;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;
import me.zohar.runscore.gatheringcode.repo.GatheringCodeRepo;
import me.zohar.runscore.gatheringcode.repo.GatheringCodeUsageRepo;
import me.zohar.runscore.gatheringcode.service.GatheringCodeService;
import me.zohar.runscore.merchant.domain.MerchantOrder;
import me.zohar.runscore.merchant.param.MerchantPaylistParam;
import me.zohar.runscore.merchant.repo.MerchantOrderRepo;
import me.zohar.runscore.merchant.service.AppealService;
import me.zohar.runscore.merchant.service.MerchantOrderService;
import me.zohar.runscore.merchant.vo.AppealVO;
import me.zohar.runscore.rechargewithdraw.domain.InsidewithdrawRecord;
import me.zohar.runscore.rechargewithdraw.service.InsideWithdrawService;
import me.zohar.runscore.useraccount.repo.LoginLogRepo;
import me.zohar.runscore.useraccount.service.LoginLogService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test_5 {
   // @Autowired
   // private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GatheringCodeUsageRepo gatheringCodeUsageRepo;
    @Autowired
    private MerchantOrderService platformOrderService;

    @Autowired
    private LoginLogRepo loginLogRepo;
    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private MerchantOrderRepo merchantOrderRepo;

    @Autowired
    private AppealService appealService;

    @Autowired
    private InsideWithdrawService insideWithdrawService;
//    @Autowired
//    private GatheringCodeService gatheringCodeService;

 @Autowired
 private GatheringCodeRepo gatheringCodeRepo;
    @Test
    @Transactional
    public void set(){
       // MerchantOrder merchantOrder=merchantOrderRepo.getOne("1460273338554253312");

     GatheringCode gatheringCode=gatheringCodeRepo.getOne("1461368114288525312");


     GatheringCode gatheringCodevv=gatheringCodeRepo.findByBankCode("1111");

     System.out.println(gatheringCodevv.getBankCode());//获取银行卡号

       // platformOrderService.generateCardBankIncomeRecord(merchantOrder);




//     InsidewithdrawRecord withdrawRecord=new InsidewithdrawRecord();
//     withdrawRecord.setId(IdUtils.getId());//订单号
//     withdrawRecord.setWithdrawAmount(new Double(100000000));//提现金额
//     withdrawRecord.setSubmitTime(new Date());//提交时间
//     withdrawRecord.setOrderNo(withdrawRecord.getOrderNo());//订单号
//     withdrawRecord.setWithdrawWay(Constant.提现方式_银行卡);//提现方式
//     withdrawRecord.setState(Constant.提现记录状态_发起提现);
//     withdrawRecord.setGatheringCode(gatheringCode);
//
//     insideWithdrawService.startInsideWithdrawWithVirtualWallet(withdrawRecord);


//     MerchantPaylistParam param=new MerchantPaylistParam();
//     param.setPageSize(10);
//     param.setPageNum(1);
//     param.setOrderState("4");
//     platformOrderService.merchantBankListPaylist(param);
    }
}
