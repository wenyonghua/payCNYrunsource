package me.zohar.runscore.common.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageResult1<T> {

	/**
	 * 页码
	 */
	private int pageNum;

	/**
	 * 每页大小
	 */
	private int pageSize;

	/**
	 * 总页数
	 */
	private long totalPage;

	/**
	 * 总记录数
	 */
	private long total;

	/**
	 * 实际记录数
	 */
	private int size;

	/**
	 * 数据集
	 */
	private List<T> content;

	private String bankDepositTotal;//收款总数
	private String bankPayoutTotal;//付款总数

	public PageResult1(List<T> content, int pageNum, int pageSize, long total, String bankDepositTotal, String bankPayoutTotal) {
		this.content = content;
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.totalPage = total == 0 ? 0 : total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1);
		this.total = total;
		this.size = content.size();
		this.bankDepositTotal=bankDepositTotal;//存款款总金额
		this.bankPayoutTotal=bankPayoutTotal;//付款
	}


}
