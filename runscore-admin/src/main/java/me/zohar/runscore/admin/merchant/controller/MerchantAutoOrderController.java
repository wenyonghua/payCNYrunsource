package me.zohar.runscore.admin.merchant.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.alibaba.fastjson.JSONObject;
import io.netty.util.internal.StringUtil;
import me.zohar.runscore.common.ApiLog;
import me.zohar.runscore.common.exception.BizError;
import me.zohar.runscore.common.exception.BizException;
import me.zohar.runscore.common.vo.PageResult;
import me.zohar.runscore.common.vo.PageResult1;
import me.zohar.runscore.common.vo.Result;
import me.zohar.runscore.merchant.domain.Merchant;
import me.zohar.runscore.merchant.domain.MerchantOrder;
import me.zohar.runscore.merchant.domain.MerchantOrderPayInfo;
import me.zohar.runscore.merchant.param.*;
import me.zohar.runscore.merchant.repo.MerchantOrderPayInfoRepo;
import me.zohar.runscore.merchant.repo.MerchantRepo;
import me.zohar.runscore.merchant.service.MerchantAutoOrderService;
import me.zohar.runscore.merchant.service.MerchantOrderService;
import me.zohar.runscore.merchant.vo.BankCardListReportVO;
import me.zohar.runscore.merchant.vo.MerchantAutoOrderVO;
import me.zohar.runscore.merchant.vo.MerchantOrderVO;
import me.zohar.runscore.merchant.vo.StartOrderSuccessVO;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 自动机查询商户记录数据
 */
@Controller
@RequestMapping("/merchantAutoOrder")
public class MerchantAutoOrderController {
	private final Logger logger = LoggerFactory.getLogger(MerchantAutoOrderController.class);

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private MerchantOrderService merchantOrderService;

	@Autowired
	private MerchantOrderPayInfoRepo merchantOrderPayInfoRepo;

	@Autowired
	private MerchantRepo merchantRepo;

	@Autowired
	private MerchantAutoOrderService merchantAutoOrderService;

	/**
	 * 查询订单列表数据
	 * @param param
	 * @return
	 */
	@ApiLog
	@GetMapping("/findMerchantAutoOrderByPage")
	@ResponseBody
	public Result findMerchantOrderByPage(MerchantAutoOrderQueryCondParam param) {
		logger.info("查询订单");
		UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		System.out.println("用户登录名称="+user.getUsername());
		if(param.getBankCardAccount()!=null && "All".equals(param.getBankCardAccount())){//全部
			param.setBankCardAccount("");//如果是全部就查询所以数据
		}
		return Result.success().setData(merchantAutoOrderService.findMerchantAutoOrderByPage(param));//查询自动机银行流水记录
	}




	/**
	 * 管理员查看银行卡交易列表
	 * @param param
	 * @return
	 */
	@ApiLog
	@GetMapping("/bankPaylist")
	@ResponseBody
	public Result bankPaylist(MerchantPaylistParam param) {
		PageResult1<BankCardListReportVO> bankCardListReportVOPageResultList=merchantOrderService.merchantBankListPaylist(param);
		return Result.success().setData(bankCardListReportVOPageResultList);
	}






	/**
	 * 导出订单新方法excel文件导出
	 */
	@RequestMapping("/orderAutoNewExport")
	public void orderNewExport(MerchantAutoOrderQueryCondParam param, HttpServletResponse response) throws IOException {
		/**
		 * 查询数据
		 */
		PageResult<MerchantAutoOrderVO> orderListReportVOPageResult= merchantAutoOrderService.findMerchantAutoOrderByPage(param);//查询数据
		List<MerchantAutoOrderVO>	orderListReportVOPageResultContent=orderListReportVOPageResult.getContent();



        // 总记录数
		Integer totalRowCount = orderListReportVOPageResultContent.size();
      // 导出EXCEL文件名称
		String filaName = "自动机订单数据EXCEL_"+new DateUtil().formatDate(new Date(),DateUtil.YYYY_MM_DD_HH_MM_SS);

		// 标题
		String[] titles = {"银行流水号", "订单号", "商户订单号","收款金额", "银行卡号","自动机回调金额","附加信息","银行完整附言信息","订单状态","银行卡总余额","自动机返回数据", "记录附言码修改","提交时间"};

		// 开始导入
		try {
			PoiUtil.exportExcelToWebsite(response, totalRowCount, filaName, titles, new WriteExcelDataDelegated() {
				public void writeExcelData(SXSSFSheet eachSheet, Integer startRowCount, Integer endRowCount, Integer currentPage, Integer pageSize) throws Exception {
					if (!CollectionUtils.isEmpty(orderListReportVOPageResultContent)) {//查询数据
						// --------------   这一块变量照着抄就行  强迫症 后期也封装起来     ----------------------
						for (int i = startRowCount; i <= endRowCount; i++) {
							SXSSFRow eachDataRow = eachSheet.createRow(i);
							if ((i - startRowCount) < orderListReportVOPageResultContent.size()) {
								MerchantAutoOrderVO merchantAutoOrderVO = orderListReportVOPageResultContent.get(i - startRowCount);
								// ---------   这一块变量照着抄就行  强迫症 后期也封装起来     -----------------------
								eachDataRow.createCell(0).setCellValue(merchantAutoOrderVO.getBankFlow() == null ? "" : merchantAutoOrderVO.getBankFlow());//银行流水
								eachDataRow.createCell(1).setCellValue(merchantAutoOrderVO.getOrderNo() == null ? "" : merchantAutoOrderVO.getOrderNo());//订单号
								eachDataRow.createCell(2).setCellValue(merchantAutoOrderVO.getOutOrderNo() == null ? "" : merchantAutoOrderVO.getOutOrderNo());//商户订单号
								eachDataRow.createCell(3).setCellValue(merchantAutoOrderVO.getGatheringAmount());//收款金额
								eachDataRow.createCell(4).setCellValue(merchantAutoOrderVO.getBankNumber());//银行卡号
								eachDataRow.createCell(5).setCellValue(merchantAutoOrderVO.getAutoAmount());//自动机回调金额
								eachDataRow.createCell(6).setCellValue(merchantAutoOrderVO.getNote());//附加信息
								eachDataRow.createCell(7).setCellValue(merchantAutoOrderVO.getBankRemark());//银行完整附言信息
								eachDataRow.createCell(8).setCellValue(merchantAutoOrderVO.getOrderStateName());//订单状态
								eachDataRow.createCell(9).setCellValue(merchantAutoOrderVO.getBalance());//银行卡总余额
								eachDataRow.createCell(10).setCellValue(merchantAutoOrderVO.getReturnMessage());//自动机返回数据
								eachDataRow.createCell(11).setCellValue(merchantAutoOrderVO.getSaveFailError());//记录附言码修改
								eachDataRow.createCell(12).setCellValue(merchantAutoOrderVO.getSubmitTime()==null?"": DateUtil.formatDate(merchantAutoOrderVO.getSubmitTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));//提交时间

							}
						}
					}

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}


	}



	/**
	 * 银行卡交易列表导出数据
	 * excel文件导出
	 */
	@RequestMapping("/export")
	public void exportExcel(MerchantPaylistParam param,HttpServletResponse response) throws IOException {
		PageResult1<BankCardListReportVO> bankCardListReportVOPageResult= merchantOrderService.merchantBankListPaylist(param);
	   List<BankCardListReportVO>	BankCardListReportVOlist=bankCardListReportVOPageResult.getContent();//查询银行卡交易列表数据

		// 总记录数
		Integer totalRowCount = BankCardListReportVOlist.size();
		// 导出EXCEL文件名称
		String filaName = "银行卡交易列表_"+new DateUtil().formatDate(new Date(),DateUtil.YYYY_MM_DD_HH_MM_SS);
        // 标题
		String[] titles = {"内部订单号", "外部商户订单号", "充值金额", "实际到账金额", "手续费", "银行卡结余", "创建时间", "结算时间","银行卡号"};


		// 开始导入
		try {
			PoiUtil.exportExcelToWebsite(response, totalRowCount, filaName, titles, new WriteExcelDataDelegated() {
				public void writeExcelData(SXSSFSheet eachSheet, Integer startRowCount, Integer endRowCount, Integer currentPage, Integer pageSize) throws Exception {
					if (!CollectionUtils.isEmpty(BankCardListReportVOlist)) {//查询数据
						// --------------   这一块变量照着抄就行  强迫症 后期也封装起来     ----------------------
						for (int i = startRowCount; i <= endRowCount; i++) {
							SXSSFRow eachDataRow = eachSheet.createRow(i);
							if ((i - startRowCount) < BankCardListReportVOlist.size()) {
								BankCardListReportVO bankCardListReportVO= BankCardListReportVOlist.get(i - startRowCount);
								// ---------   这一块变量照着抄就行  强迫症 后期也封装起来     -----------------------
								eachDataRow.createCell(0).setCellValue(bankCardListReportVO.getOrderNo() == null ? "" : bankCardListReportVO.getOrderNo());//内部订单号
								eachDataRow.createCell(1).setCellValue(bankCardListReportVO.getMerchantOrderNo() == null ? "" : bankCardListReportVO.getMerchantOrderNo());//外部商户订单号
								eachDataRow.createCell(2).setCellValue(bankCardListReportVO.getRechargeAmount());//充值金额
								eachDataRow.createCell(3).setCellValue(bankCardListReportVO.getActualPayAmount());//实际到账金额
								eachDataRow.createCell(4).setCellValue(bankCardListReportVO.getServiceChange());//手续费
								eachDataRow.createCell(5).setCellValue(bankCardListReportVO.getCardTotalView());//银行卡结余
								eachDataRow.createCell(6).setCellValue(bankCardListReportVO.getCreateTime()==null?"": DateUtil.formatDate(bankCardListReportVO.getCreateTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));//创建时间
								eachDataRow.createCell(7).setCellValue(bankCardListReportVO.getSettlementTime()==null?"": DateUtil.formatDate(bankCardListReportVO.getSettlementTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));//结束时间
								eachDataRow.createCell(8).setCellValue(bankCardListReportVO.getBankNum()==null?"": bankCardListReportVO.getBankNum());//银行卡号
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
			if ( ch >= '0' && ch <= '9') {
				arr[i++] = ch;
			}
		}
		//将数组转为字符串
		return new String(arr);
	}

//	/**自动机调用此接口
//	 * @return
//	 */
//	@ApiLog
//	@PostMapping("/automaticCall")
//	@ResponseBody
//	public Result automaticCall(@RequestBody JSONObject json, HttpServletRequest request) {
//		System.out.println(json.toJSONString());
//		//进行IP拦截
//		String ip=getRemortIP(request);
//		System.out.println(">>>>>>请求的IP="+ip);//银行卡号
//
//		String card=json.getString("card");//银行卡号
//		String amount=json.getString("amount");//收款金额
//		String balance=json.getString("balance");//银行卡号余额
//		String remark=json.getString("remark");//备注信息
//		if(StringUtil.isNullOrEmpty(card)|| StringUtil.isNullOrEmpty(amount) || StringUtil.isNullOrEmpty(remark)){//判断如果是空就返回错误数据给自动机
//			Result result=new Result();
//			result.setData(json);
//			result.setMsg("数据有空请检查card和amount和remark参数");
//			result.setSuccess(false);
//			result.setCode(500);
//			return result;
//		}
//
//	    String orderId="";//获取订单ID号
//		List<MerchantOrder> merchantOrderList=merchantOrderService.automaticCall(json,request);//查询订单号
//		if(merchantOrderList!=null && merchantOrderList.size()>=1){
//			orderId=merchantOrderList.get(0).getId();
//		}else{
//			Result result=new Result();
//			result.setData(json);
//			result.setMsg("处理失败没有查询到数据!");
//			result.setSuccess(false);
//			result.setCode(500);
//			return result;
//		}
//		boolean valueResult=merchantOrderService.autoconfirmToPaidWithCancelOrderRefund(orderId);//自动机确支付
//
//		if(valueResult){//处理数据成功就开始发送数据给产品通知产品上分
//			ExecutorService executorService = Executors.newSingleThreadExecutor();
//			String finalOrderId = orderId;
//			executorService.submit(()->{
//				System.out.println("取消订单异步线程处理回调数据 =====>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>开始 =====> " + System.currentTimeMillis()+"订单号="+ finalOrderId);
//				try{
//					System.out.println("取消订单开始通知产品上分订单号>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+ finalOrderId);
//					merchantOrderService.paySuccessAsynNotice(finalOrderId);//通知产品上分
//					System.out.println("取消订单结束通知产品上分订单号>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+ finalOrderId);
//				}catch (Exception e){
//					e.printStackTrace();
//				}
//				System.out.println("取消订单异步线程处理回调数据 =====>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>结束 =====> " + System.currentTimeMillis()+"订单号="+ finalOrderId);
//			});
//			executorService.shutdown(); // 回收线程池
//		}else{
//			return Result.fail("处理失败");
//		}
//		return Result.success();
//	}



	public String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}
		return request.getHeader("x-forwarded-for");
	}


	public static void main(String args[]){
		System.out.println(getCode(6));

//		Double todayTradeAmount=2323232424.00;//获取今日收款金额
//
//		Double dailQuotaAmount=0.00;//获取今日限额配置金额
//		String dailyQuota="2323232426";//获取数据今日限额
//		if(StrUtil.isBlank(dailyQuota)){//获取数据今日限额是空
//			dailQuotaAmount=0.00;
//		}else{
//			dailQuotaAmount=Double.valueOf(dailyQuota);
//		}
//		if(todayTradeAmount>=dailQuotaAmount){//今日收款金额大于今日限额就停止这个银行卡用于收款
//			System.out.println("成功");
//		}else{
//			System.out.println("不对");
//		}

	}
}
