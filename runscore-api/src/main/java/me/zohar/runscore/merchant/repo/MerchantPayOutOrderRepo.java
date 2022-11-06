package me.zohar.runscore.merchant.repo;

import me.zohar.runscore.merchant.domain.MerchantOrder;
import me.zohar.runscore.merchant.domain.MerchantPayOutOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

/**
 * 付款对象
 */
public interface MerchantPayOutOrderRepo
		extends JpaRepository<MerchantPayOutOrder, String>, JpaSpecificationExecutor<MerchantPayOutOrder> {

	long deleteBySubmitTimeGreaterThanEqualAndSubmitTimeLessThanEqual(Date startTime, Date endTime);

	List<MerchantPayOutOrder> findByOrderStateAndSubmitTimeLessThan(String orderState, Date submitTime);

	List<MerchantPayOutOrder> findTop10ByOrderStateAndSpecifiedReceivedAccountId(String orderState,
			String specifiedReceivedAccountId);

	List<MerchantPayOutOrder> findTop10ByOrderStateAndSpecifiedReceivedAccountIdIsNull(String orderState);

	List<MerchantPayOutOrder> findTop10ByOrderStateAndGatheringAmountIsLessThanEqualAndGatheringChannelChannelCodeInAndAndSpecifiedReceivedAccountIdIsNullOrderBySubmitTimeDesc(
			String orderState, Double gatheringAmount, List<String> gatheringChannelCodes);

	List<MerchantPayOutOrder> findByOrderStateInAndReceivedAccountIdOrderBySubmitTimeDesc(List<String> orderStates,
			String receivedAccountId);

	MerchantPayOutOrder findByIdAndReceivedAccountId(String id, String receivedAccountId);

	List<MerchantPayOutOrder> findTop10ByOrderStateAndGatheringAmountInAndGatheringChannelChannelCodeAndSpecifiedReceivedAccountIdIsNullOrderBySubmitTimeDesc(
			String orderState, List<Double> gatheringAmounts, String gatheringChannelCode);

	MerchantPayOutOrder findByIdAndMerchantId(String id, String merchantId);

	MerchantPayOutOrder findByOrderNo(String orderNo);

	List<MerchantPayOutOrder> findByOrderStateAndUsefulTimeLessThan(String orderState, Date usefulTime);

	/**
	 * 通过商户订单号查询
	 * @param orderNo
	 * @return
	 */
	List<MerchantPayOutOrder> findByOutOrderNo(String orderNo);



}
