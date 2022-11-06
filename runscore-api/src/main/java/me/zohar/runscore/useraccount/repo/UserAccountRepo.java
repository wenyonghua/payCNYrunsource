package me.zohar.runscore.useraccount.repo;

import me.zohar.runscore.merchant.domain.MerchantOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import me.zohar.runscore.useraccount.domain.UserAccount;

import java.util.List;


public interface UserAccountRepo extends JpaRepository<UserAccount, String>, JpaSpecificationExecutor<UserAccount> {
	
	UserAccount findByUserNameAndDeletedFlagIsFalse(String userName);

	/**
	 * 查询付款人员列表
	 * @param payerMark  必须是付款人员
	 * @param receiveOrderState 必须是接单状态是启用状态 1是启用状态
	 * @return
	 */
	List<UserAccount> findByPayerMarkEqualsAndReceiveOrderState(String payerMark,String receiveOrderState);

}
