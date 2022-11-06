package me.zohar.runscore.merchant.repo;

import cn.hutool.core.date.DateTime;
import me.zohar.runscore.merchant.domain.ActualIncomeRecord;
import me.zohar.runscore.merchant.domain.BankcardRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface BankcardRepo
		extends JpaRepository<BankcardRecord, String>, JpaSpecificationExecutor<BankcardRecord> {
	
//	long deleteByCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(Date startTime, Date endTime);
//
//	ActualIncomeRecord findTopByMerchantOrderIdAndAvailableFlagTrue(String merchantOrderId);
//
//	List<ActualIncomeRecord> findBySettlementTimeIsNullAndAvailableFlagTrue();
////findByMerchantId
//	List<ActualIncomeRecord> findByMerchantId(String merchantOrderId);


	/**
	 * 查询银行卡存款 通过银行卡号开始时间结束数据查询存款
	 * @return
	 */
	@Query(value = " SELECT SUM(actual_income) as 'depositTotal' FROM bank_card_record br RIGHT JOIN (select a.id from gathering_code a ) b on br.gathering_code_id = b.id and br.actual_income>0", nativeQuery = true)
	Double findDepositByAll();

	/**
	 * 查询银行卡付款 通过银行卡号开始时间结束数据查询存款
	 * @return
	 */
	@Query(value = " SELECT SUM(actual_income) as 'depositTotal' FROM bank_card_record br RIGHT JOIN (select a.id from gathering_code a ) b on br.gathering_code_id = b.id and br.actual_income<0", nativeQuery = true)
	Double findPayoutByAll();


	/**
	 * 查询银行卡存款 通过银行卡号查询存款
	 * @param bankCode
	 * @return
	 */
	@Query(value = "select SUM(actual_income) as 'depositTotal' from bank_card_record  br where br.gathering_code_id=(select id from gathering_code where bank_code = ?1)" +
			" and br.actual_income>0", nativeQuery = true)
	Double findDepositByBanKCode(String bankCode);

	/**
	 * 查询银行卡付款 通过银行卡号查询付款
	 * @param bankCode
	 * @return
	 */
	@Query(value = "select SUM(actual_income) as 'depositTotal' from bank_card_record  br where br.gathering_code_id=(select id from gathering_code where bank_code = ?1)" +
			" and br.actual_income<0 ", nativeQuery = true)
	Double findPayoutByBanKCode(String bankCode);

	/**
	 * 查询银行卡存款 通过银行卡号开始时间结束数据查询存款
	 * @param bankCode
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Query(value = "select SUM(actual_income) as 'depositTotal' from bank_card_record  br where br.gathering_code_id=(select id from gathering_code where bank_code = ?1)" +
			" and br.actual_income>0 and br.create_time>= ?2 and  br.create_time< ?3 ", nativeQuery = true)
	Double findDepositByBanKCodeAndStartTimeAndEneTime(String bankCode,String startTime,String endTime);

	/**
	 * 查询银行卡付款 通过银行卡号开始时间结束数据查询存款
	 * @param bankCode
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Query(value = "select SUM(actual_income) as 'depositTotal' from bank_card_record  br where br.gathering_code_id=(select id from gathering_code where bank_code = ?1)" +
			" and br.actual_income<0 and br.create_time>= ?2 and  br.create_time< ?3 ", nativeQuery = true)
	Double findPayoutByBanKCodeAndStartTimeAndEneTime(String bankCode,String startTime,String endTime);


	/**
	 * 查询开始时间和结束数据查询存款
	 * @param startTime
	 * @param endTime
	 * @return
			 */
	@Query(value = "select SUM(actual_income) as 'depositTotal' from bank_card_record  br  RIGHT JOIN (select a.id from gathering_code a ) b on br.gathering_code_id = b.id " +
			" and br.actual_income>0 and br.create_time>= ?1 and  br.create_time< ?2 ", nativeQuery = true)
	Double findDepositByStartTimeAndEneTime(String startTime,String endTime);

	/**
	 * 查询开始时间和结束数据查询存款
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Query(value = "select SUM(actual_income) as 'depositTotal' from bank_card_record  br  RIGHT JOIN (select a.id from gathering_code a ) b on br.gathering_code_id = b.id " +
			" and br.actual_income<0 and br.create_time>= ?1 and  br.create_time< ?2 ", nativeQuery = true)
	Double findPayoutByStartTimeAndEneTime(String startTime,String endTime);






}

