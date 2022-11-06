package me.zohar.runscore.admin.merchant.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import me.zohar.runscore.common.ApiLog;
import me.zohar.runscore.common.exception.BizError;
import me.zohar.runscore.common.exception.BizException;
import me.zohar.runscore.common.vo.PageResult;
import me.zohar.runscore.common.vo.PageResult1;
import me.zohar.runscore.common.vo.Result;
import me.zohar.runscore.merchant.domain.Merchant;
import me.zohar.runscore.merchant.domain.MerchantOrderPayInfo;
import me.zohar.runscore.merchant.domain.MerchantPayOutOrder;
import me.zohar.runscore.merchant.param.*;
import me.zohar.runscore.merchant.repo.MerchantOrderPayInfoRepo;
import me.zohar.runscore.merchant.repo.MerchantRepo;
import me.zohar.runscore.merchant.service.MerchantOrderService;
import me.zohar.runscore.merchant.vo.*;
import me.zohar.runscore.useraccount.vo.UserAccountDetails;
import me.zohar.runscore.util.excel.DateUtil;
import me.zohar.runscore.util.excel.PoiUtil;
import me.zohar.runscore.util.excel.WriteExcelDataDelegated;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.JoinType;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * 付款订单列表数据
 */
@Controller
@RequestMapping("/merchantPayoutOrder")
public class MerchantPayOutOrderController {
	private final Logger logger = LoggerFactory.getLogger(MerchantPayOutOrderController.class);


	@Autowired
	private MerchantOrderService merchantOrderService;

	@Autowired
	private MerchantOrderPayInfoRepo merchantOrderPayInfoRepo;

	@Autowired
	private MerchantRepo merchantRepo;

	/**
	 * 查询付款订单列表数据
	 * @param param
	 * @return
	 */
	@ApiLog
	@GetMapping("/findMerchantPayoutOrderByPage")
	@ResponseBody
	public Result findMerchantOrderByPage(MerchantPayOutOrderQueryCondParam param) {
		logger.info("查询订单");
		UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		System.out.println("用户登录名称="+user.getUsername());
		if(param.getBankCardAccount()!=null && "All".equals(param.getBankCardAccount())){//全部
			param.setBankCardAccount("");//如果是全部就查询所以数据
		}
		if("payout1".equals(user.getUsername()) || "payout2".equals(user.getUsername()) || "payout3".equals(user.getUsername()) || "payout4".equals(user.getUsername()) || "payout5".equals(user.getUsername()) || "payout6".equals(user.getUsername())|| "payout7".equals(user.getUsername())){
			param.setReceiverUserName(user.getUsername());//就需要查询自己用户接单数据
		}
		return Result.success().setData(merchantOrderService.findMerchantPayoutOrderByPage(param));
	}

	@ApiLog
	@GetMapping("/cancelOrderRefund")
	@ResponseBody
	public Result cancelOrderRefund(String id) {
		merchantOrderService.cancelOrderRefund(id);
		return Result.success();
	}

	/**
	 * 付款订单取消订单
	 * @param orderId
	 * @param note
	 * @return
	 */
	@ApiLog
	@GetMapping("/cancelOrder")
	@ResponseBody
	public Result cancelOrder(String orderId,String note) {
		String textValuedesk="确认取消";
		if(note!=null){
			UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			textValuedesk="操作人["+user.getUsername()+"]"+note;
		}

		merchantOrderService.cancelPayoutOrder(orderId,textValuedesk);
		return Result.success();
	}

	@ApiLog
	@GetMapping("/forceCancelOrder")
	@ResponseBody
	public Result forceCancelOrder(String id) {
		merchantOrderService.forceCancelOrder(id);
		return Result.success();
	}


	/**
	 * 取消订单退款以后在点击确认支付按钮
	 * @param orderId
	 * @return
	 */
	@ApiLog
	@GetMapping("/confirmToPaidWithCancelOrderRefund")
	@ResponseBody
	public Result confirmToPaidWithCancelOrderRefund(String orderId) {
		merchantOrderService.confirmToPaidWithCancelOrderRefund(orderId);
		return Result.success();
	}

	/**管理员创建订单 付款创建订单
	 * 创建订单
	 * @param params
	 * @return
	 */
	@ApiLog
	@PostMapping("/startOrder")
	@ResponseBody
	public Result startOrder(@RequestBody List<ManualPayOutStartOrderParam> params, HttpServletRequest request) {
		for (ManualPayOutStartOrderParam param : params) {
			String merchantNum=param.getMerchantNum();//商户号
			String payType=param.getGatheringChannelCode();//支付方式
			String amount=param.getGatheringAmount().toString();//订单金额
			String orderNo=param.getOrderNo();//商户订单号
			String notifyUrl=param.getNotifyUrl();//回调地址
			String sign=param.getSign();//签名
			String attch=param.getAttch();//备注信息
			String shoukuBankPayee=param.getShoukuBankPayee();//收款人名字
			String shoukuBankNumber=param.getShoukuBankNumber();//收款人卡号
			String shoukuBankName=param.getShoukuBankName();//收款银行名称
			String shoukuBankBranch=param.getShoukuBankBranch();//收款人银行支行

			String systemSorce=param.getSystemSource();//系统来源
			if(systemSorce==null){
				systemSorce="Pay247";//如果没有系统来源就默认Pay247
			}
			if(orderNo==null){//商户订单号不能为空
				return Result.fail("商户订单号不能为空!");
			}
			if(merchantNum==null && payType==null && amount==null && orderNo==null && notifyUrl==null && sign==null){
				return Result.fail("请输入正确的参数！");
			}
			StartPayOutOrderParam param1=new StartPayOutOrderParam();
			param1.setMerchantNum(merchantNum);//商户号
			param1.setPayType(payType);//支付方式
			param1.setAmount(amount);//金额
			param1.setOrderNo(orderNo);//订单号
			param1.setNotifyUrl(notifyUrl);//回调地址
			param1.setSign(sign);//签名
			param1.setSystemSorce(systemSorce);//如果没有系统来源就默认Pay247
			param1.setAttch(attch);//备注
			param1.setShoukuBankPayee(shoukuBankPayee);//收款人
			param1.setShoukuBankName(shoukuBankName);//收款银行名称
			param1.setShoukuBankNumber(shoukuBankNumber);//收款卡号
			param1.setShoukuBankBranch(shoukuBankBranch);//收款支行
			if(attch==null || attch.equals("")){//备注信息
				param1.setAttch(getCode(6).toUpperCase());//随机数值
			}
			List<MerchantPayOutOrder> merchantPayOutOrder=merchantOrderService.findByPayoutOrderNo(param.getOrderNo());//商户订单号查询数据
			if(merchantPayOutOrder.size()>=1){
				throw new BizException(BizError.商户订单号已存在请重新输入商户订单号);
			}

			Merchant merchant = merchantRepo.findByMerchantNumAndDeletedFlagIsFalse(param.getMerchantNum());//查询商户号
			if (merchant == null) {
				throw new BizException(BizError.商户未接入);
			}
			//签名
			String signValue = param1.getMerchantNum() + param1.getOrderNo() + param1.getAmount()
					+ merchant.getSecretKey();//商户号+订单号+金额+秘钥
			signValue = new Digester(DigestAlgorithm.MD5).digestHex(signValue);
			param1.setSign(signValue);//设置签名
			MerchantPayOutOrderVO vo= merchantOrderService.payNewPayoutStartOrder(param1,request);//保存订单数据
		}
		return Result.success();
	}

	/**
	 * 修改备注信息
	 * @param id
	 * @param note
	 * @return
	 */
	@ApiLog
	@PostMapping("/updateNote")
	@ResponseBody
	public Result updateNote(String id, String note) {
			UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
		String valueNote="操作人["+user.getUsername()+"]"+note;
		merchantOrderService.updatePayOutNoteInner(id, valueNote);
		return Result.success();
	}
	/**
	 * 管理员查看商户银行交易列表
	 * @param param
	 * @return
	 */
	@ApiLog
	@GetMapping("/merchantPaylist")
	@ResponseBody
	public Result merchantPaylist(MerchantPaylistParam param) {
		return Result.success().setData(merchantOrderService.merchantPaylist(param));
	}





	/**
	 * 导出订单新方法excel文件导出
	 */
	@RequestMapping("/orderNewExport")
	public void orderNewExport(MerchantPayOutOrderQueryCondParam param, HttpServletResponse response) throws IOException {
		/**
		 * 查询数据
		 */
		PageResult<MerchantPayOutOrderVO> orderListReportVOPageResult= merchantOrderService.findMerchantPayoutOrderByPage(param);
				//merchantOrderService.findMerchantOrderByPage(param);//查询数据
		List<MerchantPayOutOrderVO>	orderListReportVOPageResultContent=orderListReportVOPageResult.getContent();
        // 总记录数
		Integer totalRowCount = orderListReportVOPageResultContent.size();
      // 导出EXCEL文件名称
		String filaName = "付款订单数据EXCEL_"+new DateUtil().formatDate(new Date(),DateUtil.YYYY_MM_DD_HH_MM_SS);

		// 标题
		String[] titles = {"订单号", "商户订单号", "收款金额", "订单状态", "收款银行卡号", "收款银行卡姓名", "收款银行名称", "收款银行支行","备注","付款银行卡号","提交时间","完成时间"};

		// 开始导入
		try {
			PoiUtil.exportExcelToWebsite(response, totalRowCount, filaName, titles, new WriteExcelDataDelegated() {
				public void writeExcelData(SXSSFSheet eachSheet, Integer startRowCount, Integer endRowCount, Integer currentPage, Integer pageSize) throws Exception {
					if (!CollectionUtils.isEmpty(orderListReportVOPageResultContent)) {//查询数据
						// --------------   这一块变量照着抄就行  强迫症 后期也封装起来     ----------------------
						for (int i = startRowCount; i <= endRowCount; i++) {
							SXSSFRow eachDataRow = eachSheet.createRow(i);
							if ((i - startRowCount) < orderListReportVOPageResultContent.size()) {
								MerchantPayOutOrderVO merchantOrderVO = orderListReportVOPageResultContent.get(i - startRowCount);
								// ---------   这一块变量照着抄就行  强迫症 后期也封装起来     -----------------------
								eachDataRow.createCell(0).setCellValue(merchantOrderVO.getOrderNo() == null ? "" : merchantOrderVO.getOrderNo());//订单号
								eachDataRow.createCell(1).setCellValue(merchantOrderVO.getMerchantOrderNo() == null ? "" : merchantOrderVO.getMerchantOrderNo());//商户订单号
								eachDataRow.createCell(2).setCellValue(merchantOrderVO.getGatheringAmountView());//收款金额
								eachDataRow.createCell(3).setCellValue(merchantOrderVO.getOrderStateName());//订单状态
								eachDataRow.createCell(4).setCellValue(merchantOrderVO.getShoukuBankNumber());//银行卡号
								eachDataRow.createCell(5).setCellValue(merchantOrderVO.getShoukuBankPayee());//银行卡姓名
								eachDataRow.createCell(6).setCellValue(merchantOrderVO.getShoukuBankName());//银行名称
								eachDataRow.createCell(7).setCellValue(merchantOrderVO.getShoukuBankBranch());//银行支行
								eachDataRow.createCell(8).setCellValue(merchantOrderVO.getNote());//备注
								eachDataRow.createCell(9).setCellValue(merchantOrderVO.getFukuBankNumber());//付款银行卡号
								eachDataRow.createCell(10).setCellValue(merchantOrderVO.getSubmitTime()==null?"": DateUtil.formatDate(merchantOrderVO.getSubmitTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));//提交时间
								eachDataRow.createCell(11).setCellValue(merchantOrderVO.getConfirmTime()==null?"":DateUtil.formatDate(merchantOrderVO.getConfirmTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));//结束时间
							}
						}
					}

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}


	}




	@RequestMapping(value = "/testPayOrder", method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public String payOrder() {
		return "success";
		//Result.success().setData(platformOrderService.paySuccessAsynNotice(orderNo));
	}

	/**
	 * 随机生成由数字、字母组成的N位验证码
	 *
	 * @return 返回一个字符串
	 */
	public static String getCode(int n) {
		char arr[] = new char[n];
		int i = 0;
		while (i < n) {
			char ch = (char) (int) (Math.random() * 124);
			if (ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch >= '0' && ch <= '9') {
				arr[i++] = ch;
			}
		}
		//将数组转为字符串
		return new String(arr);
	}


	/**
	 * 查看付款订单详情数据
	 * @param id
	 * @return
	 */
	@GetMapping("/findByPayoutOrder")
	@ResponseBody
	public Result findByPayoutOrder(String id) {
		return Result.success().setData(merchantOrderService.findByPayoutOrder(id));
	}

	/**
	 * 提交确认付款支付
	 * @param param
	 * @return
	 */
	@PostMapping("/submitPayout")
	@ResponseBody
	public Result submitPayout(@RequestBody MerchantBankSettlementParam param) {

		UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		String valueNote="操作人["+user.getUsername()+"]"+"付款成功";
		merchantOrderService.submitPayout(param,valueNote);//添加付款银行卡号

		return Result.success();
	}

	public static void main(String args[]){

		Double todayTradeAmount=2323232424.00;//获取今日收款金额

		Double dailQuotaAmount=0.00;//获取今日限额配置金额
		String dailyQuota="2323232426";//获取数据今日限额
		if(StrUtil.isBlank(dailyQuota)){//获取数据今日限额是空
			dailQuotaAmount=0.00;
		}else{
			dailQuotaAmount=Double.valueOf(dailyQuota);
		}
		if(todayTradeAmount>=dailQuotaAmount){//今日收款金额大于今日限额就停止这个银行卡用于收款
			System.out.println("成功");
		}else{
			System.out.println("不对");
		}

	}
}
