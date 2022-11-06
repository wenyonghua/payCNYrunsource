package me.zohar.runscore.admin.merchant.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.alibaba.fastjson.JSONObject;
import io.netty.util.internal.StringUtil;
import me.zohar.runscore.common.exception.BizError;
import me.zohar.runscore.common.exception.BizException;
import me.zohar.runscore.common.utils.IdUtils;
import me.zohar.runscore.common.vo.PageResult;
import me.zohar.runscore.common.vo.PageResult1;
import me.zohar.runscore.merchant.domain.Merchant;
import me.zohar.runscore.merchant.domain.MerchantAutoOrder;
import me.zohar.runscore.merchant.domain.MerchantOrder;
import me.zohar.runscore.merchant.domain.MerchantOrderPayInfo;
import me.zohar.runscore.merchant.param.*;
import me.zohar.runscore.merchant.repo.MerchantOrderPayInfoRepo;
import me.zohar.runscore.merchant.repo.MerchantRepo;
import me.zohar.runscore.merchant.service.MerchantAutoOrderService;
import me.zohar.runscore.merchant.vo.BankCardListReportVO;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import me.zohar.runscore.common.vo.Result;
import me.zohar.runscore.merchant.service.MerchantOrderService;
import me.zohar.runscore.common.ApiLog;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;


@Controller
@RequestMapping("/merchantOrder")
public class MerchantOrderController {
	private final Logger logger = LoggerFactory.getLogger(MerchantOrderController.class);

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
	@GetMapping("/findMerchantOrderByPage")
	@ResponseBody
	public Result findMerchantOrderByPage(MerchantOrderQueryCondParam param) {
		logger.info("查询订单");
		UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		System.out.println("用户登录名称="+user.getUsername());
		if(param.getBankCardAccount()!=null && "All".equals(param.getBankCardAccount())){//全部
			param.setBankCardAccount("");//如果是全部就查询所以数据
		}
		return Result.success().setData(merchantOrderService.findMerchantOrderByPage(param));
	}

	@ApiLog
	@GetMapping("/cancelOrderRefund")
	@ResponseBody
	public Result cancelOrderRefund(String id) {
		merchantOrderService.cancelOrderRefund(id);
		return Result.success();
	}

	/**
	 * 付款订单取消
	 * @param orderId
	 * @param note
	 * @return
	 */
	@ApiLog
	@GetMapping("/cancelOrder")
	@ResponseBody
	public Result cancelOrder(@NotBlank String orderId, String note) {
		String textValuedesk="确认取消";
		if(note!=null){
			UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			textValuedesk="操作人["+user.getUsername()+"]"+note;
		}
		merchantOrderService.cancelOrder(orderId,textValuedesk);
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
	 * 管理员重新发送回调通知
	 * @param id
	 * @return
	 */
//	@ApiLog
//	@RequestMapping(value = "/resendNotice", method = {RequestMethod.GET,RequestMethod.POST})
//	@ResponseBody
//	public Result resendNotice(String id) {
//		return Result.success().setData(merchantOrderService.paySuccessAsynNotice(id));
//	}

	/**
	 * 管理员重新发送回调通知
	 * @param id
	 * @return
	 */
	@ApiLog
	@RequestMapping(value = "/resendNewNotice", method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Result resendNewNotice(String id) {
		return Result.success().setData(merchantOrderService.paySuccessAsynNotice(id));
	}

//	/**
//	 * 支付成功以后回调这个方法 移交到手机端回调
//	 * @return
//	 */
////	@ApiLog
////	@RequestMapping(value = "/successResendNotice", method = {RequestMethod.GET,RequestMethod.POST})
////	@ResponseBody
////	public Result successResendNotice(String billNO) {
////		return Result.success().setData(merchantOrderService.paySuccessAsynNotice(billNO));
////	}

	/**
	 * 点击确认到账按钮
	 * @param userAccountId
	 * @param orderId
	 * @param note
	 * @return
	 */
	@ApiLog
	@GetMapping("/confirmToPaid")
	@ResponseBody
	public Result confirmToPaid(String userAccountId, String orderId,String note) {
		String key="confirm_order_"+orderId;//定义key
		String stringValue=redisTemplate.opsForValue().get(key);
		if(stringValue==null){
			redisTemplate.opsForValue().set(key, orderId, 180, TimeUnit.SECONDS);//放到redis里面去180秒3分钟自动在redis取消没有数据
		}else{
			throw new BizException(BizError.不能重复点击确认支付请3分钟以后在点击);
		}
		String textValuedesk="确认到账";
		if(note!=null){
			UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			textValuedesk="操作人["+user.getUsername()+"]"+note;
		}
		boolean valueResult=merchantOrderService.userConfirmToPaid(userAccountId, orderId,textValuedesk);//处理完以后数据才开始通知产品上分
		if(valueResult){//处理数据成功就开始发送数据给产品通知产品上分
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			executorService.submit(()->{
				System.out.println("异步线程处理回调数据 =====>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>开始 =====> " + System.currentTimeMillis()+"订单号="+orderId);
				try{
					//Thread.sleep(5000);
					System.out.println("开始通知产品上分订单号>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+orderId);
					merchantOrderService.paySuccessAsynNotice(orderId);//通知产品上分
					System.out.println("结束通知产品上分订单号>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+orderId);
				}catch (Exception e){
					e.printStackTrace();
				}
				System.out.println("异步线程处理回调数据 =====>>>>>>>>>>>>>>>>>>>>>>>>>>>结束 =====> " + System.currentTimeMillis()+"订单号="+orderId);
			});
			executorService.shutdown(); // 回收线程池
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		//返回数据给前端
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
	boolean valueResult=merchantOrderService.confirmToPaidWithCancelOrderRefund(orderId);
		if(valueResult){//处理数据成功就开始发送数据给产品通知产品上分
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			executorService.submit(()->{
				System.out.println("取消订单异步线程处理回调数据 =====>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>开始 =====> " + System.currentTimeMillis()+"订单号="+orderId);
				try{
					System.out.println("取消订单开始通知产品上分订单号>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+orderId);
					merchantOrderService.paySuccessAsynNotice(orderId);//通知产品上分
					System.out.println("取消订单结束通知产品上分订单号>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+orderId);
				}catch (Exception e){
					e.printStackTrace();
				}
				System.out.println("取消订单异步线程处理回调数据 =====>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>结束 =====> " + System.currentTimeMillis()+"订单号="+orderId);
			});
			executorService.shutdown(); // 回收线程池
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		return Result.success();
	}

	/**管理员创建订单
	 * 创建订单
	 * @param params
	 * @return
	 */
	@ApiLog
	@PostMapping("/startOrder")
	@ResponseBody
	public Result startOrder(@RequestBody List<ManualStartOrderParam> params, HttpServletRequest request) {
		//merchantOrderService.manualStartOrder(params);

		for (ManualStartOrderParam param : params) {
			String merchantNum=param.getMerchantNum();//商户号
			String payType=param.getGatheringChannelCode();//支付方式
			String amount=param.getGatheringAmount().toString();//订单金额
			String orderNo=param.getOrderNo();//商户订单号
			String notifyUrl=param.getNotifyUrl();//回调地址
			String sign=param.getSign();//签名
			String attch=param.getAttch();//备注信息
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
			StartOrderParam param1=new StartOrderParam();
			param1.setMerchantNum(merchantNum);//商户号
			param1.setPayType(payType);//支付方式
			param1.setAmount(amount);//金额
			param1.setOrderNo(orderNo);//订单号
			param1.setNotifyUrl(notifyUrl);//回调地址
			param1.setSign(sign);//签名
			param1.setSystemSorce(systemSorce);//如果没有系统来源就默认Pay247
			param1.setAttch(attch);//备注
			if(attch==null || attch.equals("")){//备注信息
				param1.setAttch(getCode(6).toUpperCase());//随机数值
			}
			MerchantOrderPayInfo payOrderInfo=merchantOrderPayInfoRepo.findByOrderNo(param.getOrderNo());//商户订单号查询数据
			if(payOrderInfo!=null){
			//	return Result.success().setData("商户订单号已存在,请重新输入商户订单号");
				throw new BizException(BizError.商户订单号已存在请重新输入商户订单号);
			}

			Merchant merchant = merchantRepo.findByMerchantNumAndDeletedFlagIsFalse(param.getMerchantNum());//查询商户号
			if (merchant == null) {
				throw new BizException(BizError.商户未接入);
			}
			//签名
			String signValue = param1.getMerchantNum() + param1.getOrderNo() + param1.getAmount() + param1.getNotifyUrl()
					+ merchant.getSecretKey();//商户号+订单号+金额+回调地址+秘钥
			signValue = new Digester(DigestAlgorithm.MD5).digestHex(signValue);
			param1.setSign(signValue);//设置签名
			StartOrderSuccessVO vo= merchantOrderService.payNewStartOrder(param1,request);//保存订单数据
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
		merchantOrderService.updateNoteInner(id, valueNote);
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
		//MerchantAccountDetails user = (MerchantAccountDetails) SecurityContextHolder.getContext().getAuthentication()
			//	.getPrincipal();
		//param.setMerchantId(user.getMerchantId());//商户ID号就是主键ID号


		return Result.success().setData(merchantOrderService.merchantPaylist(param));
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
	 * 导出订单excel文件导出
	 */
	@RequestMapping("/orderExport")
	public void orderExport(MerchantOrderQueryCondParam param, HttpServletResponse response) throws IOException {
		/**
		 * 查询数据
		 */
		PageResult<MerchantOrderVO> orderListReportVOPageResult= merchantOrderService.findMerchantOrderByPage(param);
		List<MerchantOrderVO>	orderListReportVOPageResultContent=orderListReportVOPageResult.getContent();



		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setCharacterEncoding("utf-8");
		//String finalName = URLEncoder.encode(fileName, "UTF-8") + "_" + DateUtil.today();
		//response.setHeader("Content-disposition", "attachment;filename=" + finalName + ".xlsx");

		ExportParams exportParams = new ExportParams();  //导出的excel相关配置
		exportParams.setTitle("订单信息表");  //导出excel的第一行标题名字
		exportParams.setSheetName("订单信息表sheet");  //导出的excel的sheet名称
		Workbook workbook = ExcelExportUtil.exportExcel(exportParams, MerchantOrderVO.class, orderListReportVOPageResultContent);
		response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode("订单信息.csv", "utf-8"));
		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		//关闭流
		outputStream.close();
		workbook.close();
	}



	/**
	 * 导出订单新方法excel文件导出
	 */
	@RequestMapping("/orderNewExport")
	public void orderNewExport(MerchantOrderQueryCondParam param, HttpServletResponse response) throws IOException {
		/**
		 * 查询数据
		 */
		PageResult<MerchantOrderVO> orderListReportVOPageResult= merchantOrderService.findMerchantOrderByPage(param);//查询数据
		List<MerchantOrderVO>	orderListReportVOPageResultContent=orderListReportVOPageResult.getContent();
        // 总记录数
		Integer totalRowCount = orderListReportVOPageResultContent.size();
      // 导出EXCEL文件名称
		String filaName = "订单数据EXCEL_"+new DateUtil().formatDate(new Date(),DateUtil.YYYY_MM_DD_HH_MM_SS);

		// 标题
		String[] titles = {"订单号", "商户订单号", "收款金额", "订单状态", "银行卡号", "银行卡姓名", "银行名称", "备注","提交时间","完成时间"};

		// 开始导入
		try {
			PoiUtil.exportExcelToWebsite(response, totalRowCount, filaName, titles, new WriteExcelDataDelegated() {
				public void writeExcelData(SXSSFSheet eachSheet, Integer startRowCount, Integer endRowCount, Integer currentPage, Integer pageSize) throws Exception {
					if (!CollectionUtils.isEmpty(orderListReportVOPageResultContent)) {//查询数据
						// --------------   这一块变量照着抄就行  强迫症 后期也封装起来     ----------------------
						for (int i = startRowCount; i <= endRowCount; i++) {
							SXSSFRow eachDataRow = eachSheet.createRow(i);
							if ((i - startRowCount) < orderListReportVOPageResultContent.size()) {
								MerchantOrderVO merchantOrderVO = orderListReportVOPageResultContent.get(i - startRowCount);
								// ---------   这一块变量照着抄就行  强迫症 后期也封装起来     -----------------------
								eachDataRow.createCell(0).setCellValue(merchantOrderVO.getOrderNo() == null ? "" : merchantOrderVO.getOrderNo());//订单号
								eachDataRow.createCell(1).setCellValue(merchantOrderVO.getMerchantOrderNo() == null ? "" : merchantOrderVO.getMerchantOrderNo());//商户订单号
								eachDataRow.createCell(2).setCellValue(merchantOrderVO.getGatheringAmountView());//收款金额
								eachDataRow.createCell(3).setCellValue(merchantOrderVO.getOrderStateName());//订单状态
								eachDataRow.createCell(4).setCellValue(merchantOrderVO.getBankCardAccount());//银行卡号
								eachDataRow.createCell(5).setCellValue(merchantOrderVO.getBankCardUserName());//银行卡姓名
								eachDataRow.createCell(6).setCellValue(merchantOrderVO.getBankCardBankName());//银行名称
								eachDataRow.createCell(7).setCellValue(merchantOrderVO.getNote());//备注
								eachDataRow.createCell(8).setCellValue(merchantOrderVO.getSubmitTime()==null?"": DateUtil.formatDate(merchantOrderVO.getSubmitTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));//提交时间
								eachDataRow.createCell(9).setCellValue(merchantOrderVO.getConfirmTime()==null?"":DateUtil.formatDate(merchantOrderVO.getConfirmTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));//结束时间
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

	/**自动机调用此接口
	 * @return
	 */
	@ApiLog
	@PostMapping("/automaticCall")
	@ResponseBody
	public Result automaticCall(@RequestBody JSONObject json, HttpServletRequest request) {
		System.out.println(json.toJSONString());
		//进行IP拦截
		String ip=getRemortIP(request);
		System.out.println(">>>>>>请求的IP="+ip);//银行卡号

		String card=json.getString("card");//银行卡号
		String amount=json.getString("amount");//收款金额
		String balance=json.getString("balance");//银行卡号余额
		String remark=json.getString("remark");//备注信息
		String bankFlow=json.getString("order");//银行流水记录
		String time=json.getString("time");//回调时间
		String bankRemark=json.getString("bankRemark");//银行完整附言信息
		logger.info("获取自动机回调数据"+json.toJSONString());


		String key="autocall_order_"+bankFlow;//定义key
		String stringValue=redisTemplate.opsForValue().get(key);
		if(stringValue==null){
			redisTemplate.opsForValue().set(key, bankFlow, 180, TimeUnit.SECONDS);//放到redis里面去180秒3分钟自动在redis取消没有数据
		}else{
			String vv="自动机银行流水号="+bankFlow+" 自动机请求参数="+json.toJSONString()+" 3分钟内部不能重复回调!";
			logger.info(vv);
			return Result.success(vv);
		}



		if(StringUtil.isNullOrEmpty(card)|| StringUtil.isNullOrEmpty(amount)  || StringUtil.isNullOrEmpty(bankFlow)|| StringUtil.isNullOrEmpty(time) || StringUtil.isNullOrEmpty(bankRemark)){//判断如果是空就返回错误数据给自动机
			Result result=new Result();
			result.setData(json);
			result.setMsg("数据有空请检查card和amount和remark参数bankFlow参数bankRemark参数");
			result.setSuccess(false);
			result.setCode(500);
			return result;
		}
		MerchantAutoOrder merchantAutoOrder=merchantAutoOrderService.findByBankFlowEquals(bankFlow);//通过银行流水查询有交易记录就返回正确给自动机
		if(merchantAutoOrder!=null){
			return Result.success();//
		}

	    String orderId="";//获取订单ID号
		List<MerchantOrder> merchantOrderList=null;
		String saveFailError="";
		List<MerchantOrder> merchantOrderNewList=merchantOrderService.automaticBankCadAndAmountCall(json,request);//通过银行卡号和金额订单状态是已接单状态 回调的时间向前查询6个小时去查询订单数据,结束时间向后+5分钟
            if(merchantOrderNewList!=null && merchantOrderNewList.size()>=1){
            	for(MerchantOrder merchantOrder:merchantOrderNewList){
				String attch=merchantOrder.getPayInfo().getAttch();//附加消息
				if(bankRemark.contains(attch)){//完整的附言信息包含查询到的附言码
					orderId=merchantOrder.getId();//订单ID号
					merchantOrderList=merchantOrderNewList;
					boolean vaa=Double.valueOf(amount).compareTo(merchantOrder.getGatheringAmount())==0;//如果等于0就表示金额一致
					if(vaa){
						saveFailError="自动机回调remark="+remark+"程序匹配成功修改成="+attch+"金额匹配成功";
						break;
					}
				}
				}
			}

//		List<MerchantOrder> merchantOrderList=merchantOrderService.automaticCall(json,request);//通过银行卡号和金额和备注 订单状态是已接单状态 回调的时间向前查询6个小时去查询订单数据
//		if(merchantOrderList!=null && merchantOrderList.size()>=1){
//			orderId=merchantOrderList.get(0).getId();
//		}else{//如果没有查询到数据就需要通过银行卡号+金额+订单状态是已接单状态+时间查询
//			List<MerchantOrder> merchantOrderNewList=merchantOrderService.automaticBankCadAndAmountCall(json,request);//通过银行卡号和金额订单状态是已接单状态 回调的时间向前查询6个小时去查询订单数据
//            if(merchantOrderNewList!=null && merchantOrderNewList.size()>=1){
//            	for(MerchantOrder merchantOrder:merchantOrderNewList){
//				String attch=merchantOrder.getPayInfo().getAttch();//附加消息
//				if(bankRemark.contains(attch)){//完整的附言信息包含查询到的附言码
//					orderId=merchantOrder.getId();//订单ID号
//					merchantOrderList=merchantOrderNewList;
//					json.put("remark","自动机回调remark="+remark+"程序匹配成功修改成="+attch);
//					break;
//				}
//				}
//			}
//		}

		logger.info("获取自动机查询数据匹配的订单号orderId="+orderId);
		boolean vv=	merchantAutoOrderService.saveMerchantAutoOrder(json,merchantOrderList,saveFailError);//保存自动机交易银行数据记录
		if(!vv){//保存失败就返回失败
			Result result=new Result();
			result.setData(json);
			result.setMsg("处理失败保存订单失败!");
			result.setSuccess(false);
			result.setCode(500);
			return result;
		}
		if(StringUtil.isNullOrEmpty(orderId)){//没有匹配成功也需要返回给自动机成功
			return Result.success();
		}
		boolean valueResult=merchantOrderService.autoconfirmToPaidWithCancelOrderRefund(orderId,bankFlow);//自动机确支付 有数据通知产品上分
		if(valueResult){//处理数据成功就开始发送数据给产品通知产品上分
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			String finalOrderId = orderId;
			executorService.submit(()->{
				System.out.println("取消订单异步线程处理回调数据 =====>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>开始 =====> " + System.currentTimeMillis()+"订单号="+ finalOrderId);
				try{
					System.out.println("取消订单开始通知产品上分订单号>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+ finalOrderId);
					merchantOrderService.paySuccessAsynNotice(finalOrderId);//通知产品上分
					System.out.println("取消订单结束通知产品上分订单号>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+ finalOrderId);
				}catch (Exception e){
					e.printStackTrace();
				}
				System.out.println("取消订单异步线程处理回调数据 =====>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>结束 =====> " + System.currentTimeMillis()+"订单号="+ finalOrderId);
			});
			executorService.shutdown(); // 回收线程池
		}else{
			return Result.success();
		}
		return Result.success();
	}



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
