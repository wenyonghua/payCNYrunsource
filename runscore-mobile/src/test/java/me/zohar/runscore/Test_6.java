package me.zohar.runscore;


import cn.hutool.core.date.DateTime;

import cn.hutool.core.date.DateUtil;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;
import me.zohar.runscore.gatheringcode.repo.GatheringCodeRepo;
import me.zohar.runscore.gatheringcode.repo.GatheringCodeUsageRepo;
import me.zohar.runscore.merchant.domain.MerchantOrder;
import me.zohar.runscore.merchant.repo.BankcardRepo;
import me.zohar.runscore.merchant.repo.MerchantOrderRepo;
import me.zohar.runscore.merchant.service.AppealService;
import me.zohar.runscore.merchant.service.MerchantOrderService;
import me.zohar.runscore.rechargewithdraw.service.InsideWithdrawService;
import me.zohar.runscore.useraccount.repo.LoginLogRepo;
import me.zohar.runscore.useraccount.service.LoginLogService;
import me.zohar.runscore.util.DecimalFormatUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.math.BigDecimal;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test_6 {
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
 private BankcardRepo bankcardRepo;

 @Autowired
 private GatheringCodeRepo gatheringCodeRepo;
    @Test
    @Transactional
    public void set(){
       // MerchantOrder merchantOrder=merchantOrderRepo.getOne("1460273338554253312");

    // DateTime stardTime = DateUtil.beginOfDay(new Date("Fri Apr 08 00:00:00 CST 2022"));//开始时间
    // DateTime endTime= DateUtil.endOfDay(new Date("2022-04-08 23:59:59"));//结束时间


     Double vvAll= bankcardRepo.findDepositByAll();//查询所用存款
     System.out.println(vvAll);
     System.out.println(DecimalFormatUtil.formatString(new BigDecimal(vvAll), null));


     Double vvAll1= bankcardRepo.findPayoutByAll();//查询所用付款
     System.out.println(vvAll1);
     System.out.println(DecimalFormatUtil.formatString(new BigDecimal(vvAll1), null));



     Double vv3= bankcardRepo.findDepositByBanKCode("123456");//通过银行卡号查询存款
     System.out.println(vv3);
     System.out.println(DecimalFormatUtil.formatString(new BigDecimal(vv3), null));


     Double vv4= bankcardRepo.findPayoutByBanKCode("123456");//通过银行卡号查询存款付款
     System.out.println(vv4);
     System.out.println(DecimalFormatUtil.formatString(new BigDecimal(vv4), null));


     Double vv= bankcardRepo.findDepositByBanKCodeAndStartTimeAndEneTime("123456","2022-04-08 00:00:00","2022-04-09 23:59:59");//通过银行卡号和时间查询存款
      System.out.println(vv);
     System.out.println(DecimalFormatUtil.formatString(new BigDecimal(vv), null));

     Double vv2= bankcardRepo.findPayoutByBanKCodeAndStartTimeAndEneTime("123456","2022-04-08 00:00:00","2022-04-09 23:59:59");//通过银行卡号和时间查询付款
     System.out.println(vv2);
     System.out.println(DecimalFormatUtil.formatString(new BigDecimal(vv2), null));


     Double vv33=bankcardRepo.findDepositByStartTimeAndEneTime("2022-04-08 00:00:00","2022-04-09 23:59:59");
     System.out.println(vv33);
     System.out.println(DecimalFormatUtil.formatString(new BigDecimal(vv33), null));

     Double vv34=bankcardRepo.findDepositByStartTimeAndEneTime("2022-04-08 00:00:00","2022-04-09 23:59:59");
     System.out.println(vv34);
     System.out.println(DecimalFormatUtil.formatString(new BigDecimal(vv34), null));



     }



//     GatheringCode gatheringCode=gatheringCodeRepo.getOne("1461368114288525312");
//System.out.println(gatheringCode.getBankCode());


}
