package me.zohar.runscore;


import me.zohar.runscore.common.utils.IdUtils;
import me.zohar.runscore.gatheringcode.domain.GatheringCodeUsage;
import me.zohar.runscore.gatheringcode.param.GatheringCodeQueryCondParam;
import me.zohar.runscore.gatheringcode.repo.GatheringCodeUsageRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test_1 {
   // @Autowired
   // private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GatheringCodeUsageRepo gatheringCodeUsageRepo;


    @Test
    @Transactional
    public void set(){
        GatheringCodeUsage ghc =new GatheringCodeUsage();
       // ghc.setId(IdUtils.getId());//id 号
        ghc.setState("1");//状态
        ghc.setPayee("王勇");//收款人
        ghc.setInUse(true);//使用中
        ghc.setAuditType("1");
        ghc.setUserAccountId("1143183939259596800");//账户ID

        ghc.setTodayTradeAmount(new Double(100));//今日总数据
        ghc.setTotalPaidOrderNum(10l);
        ghc.setTotalOrderNum(20l);
        ghc.setTodayOrderNum(100l);
        ghc.setBankCode("奔驰银行");
        ghc.setBankAddress("87897897987987987");//卡号
        ghc.setBankUsername("奔驰");//户主


        ghc.setGatheringChannelId("1149365394574671872");//渠道编号
        try {
            gatheringCodeUsageRepo.save(ghc);//添加数据
        }catch (Exception ex){
            ex.toString();
        }



        GatheringCodeQueryCondParam param=new GatheringCodeQueryCondParam();
        param.setPageNum(1);
        param.setPageSize(10);
        Specification<GatheringCodeUsage> spec = new Specification<GatheringCodeUsage>() {
            @Override
            public Predicate toPredicate(Root<GatheringCodeUsage> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;

            }
        };

        try {
            Page<GatheringCodeUsage> result = gatheringCodeUsageRepo.findAll(spec,
                    PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));

            for (GatheringCodeUsage gatheringCodeUsage : result) {

                System.out.println("获取序号:"+gatheringCodeUsage.getId());
                System.out.println("获取金额:"+gatheringCodeUsage.getTodayOrderNum());


                gatheringCodeUsage.setTodayTradeAmount(new Double(100));
                gatheringCodeUsage.setTotalPaidOrderNum(10l);
                gatheringCodeUsage.setTotalOrderNum(20l);
                gatheringCodeUsage.setTodayOrderNum(100l);
                gatheringCodeUsageRepo.save(gatheringCodeUsage);
            }
        }catch (Exception ex){
            ex.toString();
        }



//        GatheringCodeUsage gatheringCodeUsage=new GatheringCodeUsage();
//        gatheringCodeUsage.setId("1455851390306877440");
        String id= "1455851390306877440";

//GatheringCodeUsage gatheringCodeUsage = gatheringCodeUsageRepo.findById(id).orElse(null);
//       // GatheringCodeUsage gatheringCodeUsage = gatheringCodeUsageRepo.getOne(id);
//         System.out.println(gatheringCodeUsage.getPayee());
//        gatheringCodeUsage.setTodayOrderNum(100l);
//        gatheringCodeUsageRepo.save(gatheringCodeUsage);


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
