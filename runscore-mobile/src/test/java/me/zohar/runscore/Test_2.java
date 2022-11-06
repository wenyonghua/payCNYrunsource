package me.zohar.runscore;


import me.zohar.runscore.merchant.domain.ActualIncomeRecord;
import me.zohar.runscore.merchant.repo.ActualIncomeRecordRepo;
import me.zohar.runscore.merchant.service.MerchantOrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test_2 {

   @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MerchantOrderService platformOrderService;

   @Autowired
    private ActualIncomeRecordRepo actualIncomeRecordRepo;

    @Test
    public void set(){
        List<ActualIncomeRecord> list =actualIncomeRecordRepo.findByMerchantId("1455913510121766912");

        for(ActualIncomeRecord actualIncomeRecord:list){
          System.out.println(actualIncomeRecord.getMerchantOrderNo());

        }
        // ActualIncomeRecord actualIncomeRecord= actualIncomeRecordRepo.findTopByMerchantOrderIdAndAvailableFlagTrue("1455913510121766912");

//        redisTemplate.opsForValue().set("myKey2","myValue122222222222222222");
//        System.out.println(redisTemplate.opsForValue().get("myKey2"));


       // ThreadPoolUtils.getPaidMerchantOrderPool().schedule(() -> {
          //  System.out.println("设置订单号》》》》》="+merchantOrder.getId());
//        redisTemplate.opsForList().leftPush("list","a");
//        redisTemplate.opsForList().leftPush("list","b");
//        redisTemplate.opsForList().leftPush("list","c");
//        redisTemplate.opsForList().leftPush("list","d");
           //redisTemplate.opsForList().leftPush(Constant.商户订单ID, merchantOrder.getId());
      // redisTemplate.opsForList().leftPush("AAAAAAAAA", "123423525");//存放数据
       //}, 1, TimeUnit.SECONDS);

       // String orderNo = redisTemplate.opsForList().rightPop(Constant.商户订单ID, 5L, TimeUnit.SECONDS);
       // System.out.println("获取值redis的值》》》"+orderNo);

       // String orderNoLlist = redisTemplate.opsForList().rightPop("list", 5L, TimeUnit.SECONDS);
      //  System.out.println("获取值redis的值》》》orderNoLlist="+orderNoLlist);

    }
}
