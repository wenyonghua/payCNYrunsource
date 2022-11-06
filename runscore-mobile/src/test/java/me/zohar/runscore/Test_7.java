package me.zohar.runscore;


import me.zohar.runscore.merchant.domain.ActualIncomeRecord;
import me.zohar.runscore.merchant.repo.ActualIncomeRecordRepo;
import me.zohar.runscore.merchant.service.MerchantOrderService;
import me.zohar.runscore.useraccount.domain.UserAccount;
import me.zohar.runscore.useraccount.repo.UserAccountRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test_7<a33> {

   @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MerchantOrderService platformOrderService;

   @Autowired
    private ActualIncomeRecordRepo actualIncomeRecordRepo;

    @Autowired
    private UserAccountRepo userAccountRepo;

    @Test
    public void set(){
        String id="1523980645963923456";
        String shouku="王大勇";
//        List<UserAccount> userAccounts=  userAccountRepo.findByPayerMarkEquals("1");
//        System.out.println(userAccounts.size());

        String aa="0_100";
        String value="101";
      String vv[]=  aa.split("_");
     // if(vv.length>0){
       String first= vv[0];;//第一个
          String seconed= vv[1];//第2个

         // int flag = new BigDecimal(first).compareTo(new BigDecimal(value));
        //  a<b(-1)i
          //  f(a.compareTo(b) == -1){
          //    System.out.println("a小于b");
          //}
          if(new BigDecimal(first).compareTo(new BigDecimal(value)) == -1 && (new BigDecimal(value).compareTo(new BigDecimal(seconed)) == -1 || new BigDecimal(value).compareTo(new BigDecimal(seconed))== 0)){//a小于=b   0<value<=100
              System.out.println("进入这个范围以内");
          }else{
System.out.println("不在范围以内");
          }

      }









//    if((x>100)&&(x<=200))
//    {
//        system.out.println("在a区间");
//    }else if((x>200)&&(x<=300)))
//    {
//        system.out.println("在b区间");
//    }else if((x>300)&&(x<=400)))

    public static boolean rangeInDefined(int current, int min, int max)
    {
        return Math.max(min, current) == Math.min(current, max);
    }
//    public static void main(String[] args) {
//        int current = 7;
//        if(rangeInDefined(current, 1, 99999999))
//            System.out.println(current + "在1——10之间");
//
//    }

}
