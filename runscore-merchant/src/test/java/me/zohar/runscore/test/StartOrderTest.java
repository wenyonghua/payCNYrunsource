package me.zohar.runscore.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.zohar.runscore.common.utils.IdUtils;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;
import me.zohar.runscore.gatheringcode.repo.GatheringCodeRepo;
import me.zohar.runscore.merchant.domain.ActualIncomeRecord;
import me.zohar.runscore.merchant.domain.MerchantAutoOrder;
import me.zohar.runscore.merchant.domain.MerchantSettlementPayOutRecord;
import me.zohar.runscore.merchant.repo.ActualIncomeRecordRepo;
import me.zohar.runscore.merchant.repo.MerchantSettlementPayoutRecordRepo;
import me.zohar.runscore.merchant.service.MerchantAutoOrderService;
import org.junit.Test;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.http.HttpUtil;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StartOrderTest {

	@Autowired
	private MerchantSettlementPayoutRecordRepo settlementPayoutRecordRepo;//付款银行卡列表数据

	@Autowired
	private ActualIncomeRecordRepo actualIncomeRecordRepo;

	@Autowired
	private MerchantAutoOrderService merchantAutoOrderService;
	@Autowired
	private GatheringCodeRepo gatheringCodeRepo;//收款码表
	/**
	 * 发起订单接口生成签名例子
	 * 
	 * @param 
	 */
	@Test
	public void test1() {

		try {



			//MerchantAutoOrder merchantAutoOrder=	merchantAutoOrderService.findByBankFlowEquals("9897897897987896");//保存自动机交易银行数据记录

			String card="0511000467935";
			GatheringCode gatheringBank=gatheringCodeRepo.findByBankCode(card);//原银行卡号
			System.out.println(gatheringBank.getBankAddress());



//			List<ActualIncomeRecord> list=	actualIncomeRecordRepo.findByMerchantOrderNoEquals("A02222");
//
//			if(list.size()>0){
//				System.out.println("dfd");
//			}else{
//				System.out.println("错误");
//			}

			//List<MerchantSettlementPayOutRecord> merchantSettlementPayOutRecordList = settlementPayoutRecordRepo.findByMerchantSettlId("1512078526486937600");
		}catch (Exception ex){
			ex.toString();
		}

	/*	String merchantNum = "1001";// 商户号
		String orderNo = "20190629023134U936283877";// 商户订单号
		String amount = "10.00";// 支付金额
		String notifyUrl = "localhost:8080/paySuccessNotice";// 异步通知地址
		String returnUrl = "localhost:8080/paySuccessNotice";// 同步通知地址
		String payType = "wechat";// 请求支付类型
		String attch = ""; // 附加参数
		String secretKey = "fd45643dkfjdka";//商户密钥
		String sign = merchantNum + orderNo + amount + notifyUrl + secretKey;
		sign = new Digester(DigestAlgorithm.MD5).digestHex(sign);// md5pi签名

		String url = "http://localhost:8080/api/startOrder";// 发起订单地址
		Map<String, Object> paramMap = new HashMap<>();// post请求的参数
		paramMap.put("merchantNum", merchantNum);
		paramMap.put("orderNo", orderNo);
		paramMap.put("amount", amount);
		paramMap.put("notifyUrl", notifyUrl);
		paramMap.put("returnUrl", returnUrl);
		paramMap.put("payType", payType);
		paramMap.put("attch", attch);
		paramMap.put("sign", sign);
		String result = HttpUtil.post(url, paramMap);
		System.out.println(result);*/



	}

}
