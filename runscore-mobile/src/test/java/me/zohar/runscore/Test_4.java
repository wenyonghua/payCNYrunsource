package me.zohar.runscore;


import me.zohar.runscore.common.utils.IdUtils;
import me.zohar.runscore.common.vo.PageResult;
import me.zohar.runscore.constants.Constant;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;
import me.zohar.runscore.gatheringcode.domain.GatheringCodeUsage;
import me.zohar.runscore.gatheringcode.param.GatheringCodeQueryCondParam;
import me.zohar.runscore.gatheringcode.repo.GatheringCodeUsageRepo;
import me.zohar.runscore.merchant.service.AppealService;
import me.zohar.runscore.merchant.service.MerchantOrderService;
import me.zohar.runscore.merchant.vo.AppealVO;
import me.zohar.runscore.useraccount.domain.LoginLog;
import me.zohar.runscore.useraccount.repo.LoginLogRepo;
import me.zohar.runscore.useraccount.service.LoginLogService;
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
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test_4 {
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
    private AppealService appealService;
    @Test
    @Transactional
    public void set(){

        String sessionId="507d6c40-ba80-43a4-8824-ebbc4a545e22";
        loginLogService.updateLastAccessTime(sessionId);

String merchnetid="1459150791334952960";
        PageResult<AppealVO> appealVOPageResult= appealService.findTop5TodoAppealByPage(merchnetid);
        List<AppealVO>  appealVO=  appealVOPageResult.getContent();


//        LoginLog loginLog = new LoginLog();
//        loginLog.setId(IdUtils.getId());
//        loginLog.setSessionId("111");
//        loginLog.setUserName("张顺");
//        loginLog.setSystemInfo("沙发斯蒂芬");
//        loginLog.setState(Constant.登录状态_成功);
//        loginLog.setMsg(Constant.登录提示_登录成功);
//        loginLog.setIpAddr("127.0.0.1");
//        loginLog.setLoginLocation("北京");
//        loginLog.setLoginTime(new Date());
//        loginLog.setBrowser("Chrome");
//        loginLog.setOs("苹果系统");
//        try {
//            loginLogRepo.saveAndFlush(loginLog);
//           // loginLogRepo.flush();
//
//            LoginLog ls= loginLogRepo.getOne(loginLog.getId());
//            System.out.println(ls.getIpAddr());
//
//        }catch (Exception ex){
//            ex.toString();
//        }



//String receivedAccountId, String gatheringChannelCode,
//											Double gatheringAmount
//        String receivedAccountId="1143183939259596800";
//        Double gatheringAmount=100.00;
//        String gatheringChannelCode="bankcard";
//
//        GatheringCode gatheringCode= platformOrderService.getGatheringCodeStorage(receivedAccountId,gatheringChannelCode,gatheringAmount);
//System.out.println(gatheringCode.getBankCode());
//
//
//
//        GatheringCodeUsage gatheringCodeUsageObject= platformOrderService.getGatheringCodeUsage(receivedAccountId,gatheringChannelCode,gatheringAmount);
//        System.out.println(gatheringCodeUsageObject.getBankCode());
//
//
//
//
//
//
//
//        GatheringCodeQueryCondParam param=new GatheringCodeQueryCondParam();
//        param.setPageNum(1);
//        param.setPageSize(10);
//        Specification<GatheringCodeUsage> spec = new Specification<GatheringCodeUsage>() {
//            @Override
//            public Predicate toPredicate(Root<GatheringCodeUsage> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
//                List<Predicate> predicates = new ArrayList<Predicate>();
//                return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
//
//            }
//        };
//
//        try {
//            Page<GatheringCodeUsage> result = gatheringCodeUsageRepo.findAll(spec,
//                    PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
//
//            for (GatheringCodeUsage gatheringCodeUsage : result) {
//
//                System.out.println("获取序号:"+gatheringCodeUsage.getId());
//                System.out.println("获取金额:"+gatheringCodeUsage.getTodayOrderNum());
//
//
//                gatheringCodeUsage.setTodayTradeAmount(new Double(100));
//                gatheringCodeUsage.setTotalPaidOrderNum(10l);
//                gatheringCodeUsage.setTotalOrderNum(20l);
//                gatheringCodeUsage.setTodayOrderNum(100l);
//                gatheringCodeUsageRepo.save(gatheringCodeUsage);
//            }
//        }catch (Exception ex){
//            ex.toString();
//        }



//        GatheringCodeUsage gatheringCodeUsage=new GatheringCodeUsage();
//        gatheringCodeUsage.setId("1455851390306877440");
      //  String id= "1455851390306877440";

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
