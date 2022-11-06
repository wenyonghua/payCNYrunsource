package me.zohar.runscore;


import me.zohar.runscore.common.vo.Result;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;
import me.zohar.runscore.gatheringcode.domain.GatheringCodeUsage;
import me.zohar.runscore.gatheringcode.param.GatheringCodeQueryCondParam;
import me.zohar.runscore.gatheringcode.repo.GatheringCodeUsageRepo;
import me.zohar.runscore.gatheringcode.service.GatheringCodeService;
import me.zohar.runscore.gatheringcode.vo.GatheringCodeUsageVO;
import me.zohar.runscore.merchant.service.MerchantOrderService;
import me.zohar.runscore.storage.domain.Storage;
import me.zohar.runscore.storage.repo.StorageRepo;
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

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test_3 {
   // @Autowired
   // private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GatheringCodeUsageRepo gatheringCodeUsageRepo;
    @Autowired
    private MerchantOrderService platformOrderService;
    @Autowired
    private GatheringCodeService gatheringCodeService;

    @Autowired
    private StorageRepo storageRepo;

    @Test
    @Transactional
    public void set(){
     Storage vv=   storageRepo.findByAssociateId("1521388660153384960");
        System.out.println(vv.getId());
     storageRepo.delete(vv);



//
//
//        GatheringCodeQueryCondParam param2=new GatheringCodeQueryCondParam();
//        param2.setPageNum(1);
//        param2.setPageSize(99999999);
//        //param.setInUse("1");//使用中 1是使用中
//        param2.setCardUse("1");//卡用途 1是存款卡，2备用金
//       // return Result.success().setData(gatheringCodeService.findBankUserStateByPage(param2));
//
//        List<GatheringCodeUsageVO> gatheringCodeUsageVOList2=gatheringCodeService.findBankUserStateByPage(param2);
//
//        GatheringCodeQueryCondParam param1=new GatheringCodeQueryCondParam();
//        param1.setPageNum(1);
//        param1.setPageSize(99999);
//        param1.setCardUse("3");//卡用途卡用途 1是存款卡，2是付款卡,3备用金
//        param1.setInUse("1");//银行卡状态 1是使用
//        List<GatheringCodeUsageVO> gatheringCodeUsageVOList=gatheringCodeService.findBankUserStateByPage(param1);
//
//
//        for(GatheringCodeUsageVO gatheringCodeUsageVO:gatheringCodeUsageVOList) {
//            String id = gatheringCodeUsageVO.getId();//获取ID号
//            String dailyQuota = null;
//            if (id != null) {
//                GatheringCodeUsageVO gatheringCodeUsageVO1 = gatheringCodeService.findGatheringCodeUsageById(id);
//                dailyQuota = gatheringCodeUsageVO1.getDailyQuota();//银行卡单日额度限制;
//                System.out.println(dailyQuota);
//            }
//        }



    }
}
