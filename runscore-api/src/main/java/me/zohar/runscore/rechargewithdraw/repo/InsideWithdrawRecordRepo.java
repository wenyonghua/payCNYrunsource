package me.zohar.runscore.rechargewithdraw.repo;

import me.zohar.runscore.rechargewithdraw.domain.InsidewithdrawRecord;
import me.zohar.runscore.rechargewithdraw.domain.WithdrawRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

public interface InsideWithdrawRecordRepo
		extends JpaRepository<InsidewithdrawRecord, String>, JpaSpecificationExecutor<InsidewithdrawRecord> {
	
	long deleteBySubmitTimeGreaterThanEqualAndSubmitTimeLessThanEqual(Date startTime, Date endTime);

//	List<InsidewithdrawRecord> findByUserAccountIdAndSubmitTimeGreaterThanEqualAndSubmitTimeLessThanEqual(
//			String userAccountId, Date startTime, Date endTime);

	/**
	 * 通过银行卡号和发起提现状态=1来查询内部转账数据
	 * @param bankCardAccount 银行卡号
	 * @param state=1
	 * @return
	 */
	List<InsidewithdrawRecord> findByBankCardAccountAndState(String bankCardAccount,String state);
}
