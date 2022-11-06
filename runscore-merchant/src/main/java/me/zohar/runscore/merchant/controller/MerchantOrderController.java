package me.zohar.runscore.merchant.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import lombok.extern.slf4j.Slf4j;
import me.zohar.runscore.common.ApiLog;
import me.zohar.runscore.common.exception.BizError;
import me.zohar.runscore.common.exception.BizException;
import me.zohar.runscore.common.vo.PageResult;
import me.zohar.runscore.constants.Constant;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;
import me.zohar.runscore.mastercontrol.service.MasterControlService;
import me.zohar.runscore.mastercontrol.vo.SystemSettingVO;
import me.zohar.runscore.merchant.domain.GatheringChannel;
import me.zohar.runscore.merchant.domain.Merchant;
import me.zohar.runscore.merchant.domain.MerchantOrder;
import me.zohar.runscore.merchant.domain.MerchantOrderPayInfo;
import me.zohar.runscore.merchant.param.*;
import me.zohar.runscore.merchant.repo.GatheringChannelRepo;
import me.zohar.runscore.merchant.repo.MerchantOrderPayInfoRepo;
import me.zohar.runscore.merchant.repo.MerchantOrderRepo;
import me.zohar.runscore.merchant.repo.MerchantRepo;
import me.zohar.runscore.merchant.vo.ActuallncomeRecrdReportVO;
import me.zohar.runscore.merchant.vo.MerchantOrderVO;
import me.zohar.runscore.merchant.vo.MerchantSettlementRecordVO;
import me.zohar.runscore.merchant.vo.StartOrderSuccessVO;
import me.zohar.runscore.rechargewithdraw.service.RechargeService;
import me.zohar.runscore.useraccount.domain.AccountReceiveOrderChannel;
import me.zohar.runscore.useraccount.domain.UserAccount;
import me.zohar.runscore.useraccount.repo.AccountReceiveOrderChannelRepo;
import me.zohar.runscore.useraccount.repo.UserAccountRepo;
import me.zohar.runscore.useraccount.service.UserAccountService;
import me.zohar.runscore.util.excel.DateUtil;
import me.zohar.runscore.util.excel.PoiUtil;
import me.zohar.runscore.util.excel.WriteExcelDataDelegated;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import me.zohar.runscore.common.vo.Result;
import me.zohar.runscore.merchant.service.MerchantOrderService;
import me.zohar.runscore.useraccount.vo.MerchantAccountDetails;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping("/merchantOrder")
public class MerchantOrderController {

	@Autowired
	private MerchantOrderService merchantOrderService;
	@Autowired
	private RechargeService rechargeService;

	@Autowired
	private MerchantOrderRepo merchantOrderRepo;
	@Autowired
	private MerchantRepo merchantRepo;

	@Autowired
	private MerchantOrderPayInfoRepo merchantOrderPayInfoRepo;
	@Autowired
	private GatheringChannelRepo gatheringChannelRepo;

	@Autowired
	private AccountReceiveOrderChannelRepo accountReceiveOrderChannelRepo;
	@Autowired
	private MasterControlService service;
	@Autowired
	private UserAccountService userAccountService;

	@Autowired
	private UserAccountRepo userAccountRepo;


	/**
	 * 查询订单列表数据
	 * @param param
	 * @return
	 */
	@ApiLog
	@GetMapping("/findMerchantOrderByPage")
	@ResponseBody
	public Result findMerchantOrderByPage(MerchantOrderQueryCondParam param) {
		MerchantAccountDetails user = (MerchantAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		param.setMerchantName(user.getMerchantName());
		return Result.success().setData(merchantOrderService.findMerchantOrderByPage(param));
	}

	@ApiLog
	@GetMapping("/findMerchantOrderDetailsById")
	@ResponseBody
	public Result findMerchantOrderDetailsById(String orderId) {
		return Result.success().setData(merchantOrderService.findMerchantOrderDetailsById(orderId));
	}


	/**
	 * 商户添加订单测试系统
	 * @param params
	 * @return
	 */
	@ApiLog
	@PostMapping("/startOrder")
	@ResponseBody
	public Result startOrder(@RequestBody List<ManualStartOrderParam> params) {
		MerchantAccountDetails user = (MerchantAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		for (ManualStartOrderParam param : params) {
			param.setMerchantNum(user.getMerchantNum());//商户号
		}
		merchantOrderService.manualStartOrder(params);
		return Result.success();
	}

//	/**
//	 * 商户重新发送通知
//	 * @param id
//	 * @return
//	 */
//	@ApiLog
//	@GetMapping("/resendNotice")
//	@ResponseBody
//	public Result resendNotice(String id) {
//		return Result.success().setData(merchantOrderService.paySuccessAsynNotice(id));
//	}

	/**
	 * 商户重新发送通知
	 * @param id
	 * @return
	 */
	@ApiLog
	@GetMapping("/resendMerchantNotice")
	@ResponseBody
	public Result resendMerchantNotice(String id) {
		return Result.success().setData(merchantOrderService.paySuccessAsynNotice(id));
	}


	@PostMapping("/updateNote")
	@ResponseBody
	@ApiLog
	public Result updateNote(String id, String note) {
		MerchantAccountDetails user = (MerchantAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		merchantOrderService.updateNote(id, note, user.getMerchantId());
		return Result.success();
	}

	/**
	 * 商户取消订单
	 * @param id
	 * @return
	 */
	@ApiLog
	@GetMapping("/merchantCancelOrder")
	@ResponseBody
	public Result merchatCancelOrder(String id) {
		MerchantAccountDetails user = (MerchantAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		merchantOrderService.merchatCancelOrder(user.getMerchantId(), id);
		return Result.success();
	}

	/**
	 * 商户银行交易列表
	 * @param param
	 * @return
	 */
	@ApiLog
	@GetMapping("/merchantPaylist")
	@ResponseBody
	public Result merchantPaylist(MerchantPaylistParam param) {
		MerchantAccountDetails user = (MerchantAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		param.setMerchantId(user.getMerchantId());//商户ID号就是主键ID号
		return Result.success().setData(merchantOrderService.merchantPaylist(param));
	}

	/**
	 * 外部添加订单接口使用外部系统调用
	 * @param request
	 * @return
	 */
	@ApiLog
	@RequestMapping(value = "/outstartAddOrder", method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Result outstartAddOrder(HttpServletRequest request) {
		String merchantNum=request.getParameter("merchantNum");//商户号
		String payType=request.getParameter("payType");//bankcard 表示银行卡
		String amount=request.getParameter("amount");//订单金额
		String orderNo=request.getParameter("orderNo");//订单号
		String notifyUrl=request.getParameter("notifyUrl");//回调地址是客服提供过来的
		String sign=request.getParameter("sign");//签名
		String attch=request.getParameter("attch");//备注信息
		String systemSorce=request.getParameter("systemSorce");//系统来源
		if(systemSorce==null){
			systemSorce="Pay247";//如果没有系统来源就默认Pay247
		}

      if(merchantNum==null && payType==null && amount==null && orderNo==null && notifyUrl==null && sign==null){
		  return Result.fail("请输入正确的参数！");
	  }
		StartOrderParam param=new StartOrderParam();
		param.setMerchantNum(merchantNum);
		param.setPayType(payType);
		param.setAmount(amount);
		param.setOrderNo(orderNo);//订单号
		param.setNotifyUrl(notifyUrl);//回调地址
		param.setSign(sign);
		param.setSystemSorce(systemSorce);//如果没有系统来源就默认Pay247
		if(attch==null){//备注信息
			param.setAttch(getCode(6).toUpperCase());//随机数值
		}
		MerchantOrderPayInfo payOrderInfo=merchantOrderPayInfoRepo.findByOrderNo(param.getOrderNo());//商户订单号查询数据
		if(payOrderInfo!=null){
			return Result.success().setData("商户订单号已存在,请重新输入商户订单号");
		}
		StartOrderSuccessVO vo= merchantOrderService.payStartOrder(param,request);//保存订单数据
		log.info("产品给我的订单号,商户订单号={},系统订单号={},支付地址={}", orderNo,vo.getBillNo(),vo.getPayUrl());

//*****************************************************************下面就是设置自动接单***************************************************
           //系统设置的通道
		GatheringChannel gatheringChannel = gatheringChannelRepo
				.findByChannelCodeAndDeletedFlagIsFalse(param.getPayType());//查询渠道列表 bankcard 支付类型
		if(gatheringChannel==null){
			return Result.success().setData("卡转卡支付类型没有开通");
		}

           //通过渠道去找用户  通过银行卡类型查询接单用户和
		List<AccountReceiveOrderChannel> accountReceiveOrderChannelList=accountReceiveOrderChannelRepo.findByChannelIdAndStateAndChannelDeletedFlagFalse(gatheringChannel.getId(),"1");//卡转卡渠道 account_receive_order_channel 用户和渠道关系表
		log.info("产品给我的订单号,id为{}", orderNo);
		if(accountReceiveOrderChannelList.size()==0){
			return Result.success().setData("没有接单用户开始接单!");
		}

		List<UserAccount> userAccounts=new ArrayList<>();
		//SystemSettingVO systemTitle=service.getSystemSetting();//查询网站标题
		String webSiteTitle=systemSorce;//
				//systemTitle.getWebsiteTitle();//获取网站标题
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>产品给我的订单号,id为={},获取产品调用接口转入的系统来源={}", orderNo,webSiteTitle);
		for(AccountReceiveOrderChannel accountReceiveOrderChannel:accountReceiveOrderChannelList){
			UserAccount userAccount=accountReceiveOrderChannel.getUserAccount();//获取用户信息
			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>产品给我的订单号,id为={},接单用户={},获取网站标题={},用户的系统来源={}", orderNo,userAccount.getUserName(),webSiteTitle,userAccount.getSystemSource());
			if(userAccount.getSystemSource().equals(webSiteTitle)) {//获取网站标题进行对比 必须是同一个系统下面的用户才能使用
				if (userAccount.getReceiveOrderState().equals(Constant.账号接单通道状态_开启中)) {//判断用户接单状态 必须是接单状态才行
					userAccounts.add(userAccount);//添加用户信息
				}
			}
		}
		if(userAccounts.size()==0){
			return Result.success().setData("没有接单用户开始接单!");
		}
		UserAccount userAccount = userAccounts.get((int) (Math.random() * userAccounts.size()));//随机使用一个用户
		log.info("产品给我的订单号,id为={},接单用户={}", orderNo,userAccount.getUserName());

		log.info("产品给我的订单号设置自动接单开始,id为={},接单用户={},订单号={}", orderNo,userAccount.getUserName(),vo.getBillNo());
		merchantOrderService.receiveOrder(userAccount.getId(), vo.getBillNo());//设置自动接单
		log.info("产品给我的订单号设置自动接单结束,id为={},接单用户={},订单号={}", orderNo,userAccount.getUserName(),vo.getBillNo());

		return Result.success().setData(vo);
	}

	/**
	 * 外部接口查询订单，使用外部系统调用
	 * @param request
	 * @return
	 */
	@ApiLog
	@RequestMapping(value = "/outFindOrderDetailsById", method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Result findMerchantOrderDetailsById(HttpServletRequest request) {
		String merchantNum=request.getParameter("merchantNum");//商户号
		String orderNo=request.getParameter("orderNo");//商户订单号
		String signValeu=request.getParameter("sign");//签名

		if(merchantNum==null   && orderNo==null  && signValeu==null ){
			return Result.fail("请输入正确的参数！");
		}
		StartOrderParam param=new StartOrderParam();
		param.setMerchantNum(merchantNum);//商户号
		param.setOrderNo(orderNo);//订单号
		param.setSign(signValeu);//签名

		MerchantOrderPayInfo payOrderInfo=merchantOrderPayInfoRepo.findByOrderNo(param.getOrderNo());//商户订单号查询数据
		if(payOrderInfo==null){
			 return Result.success().setData("查询接口订单不存在,请重新输入商户订单号");
		 }

		if (Constant.商户订单支付通知状态_通知成功.equals(payOrderInfo.getNoticeState())) {
			return Result.success().setData(Constant.商户订单通知成功返回值);
		}
		Merchant merchant = merchantRepo.findByMerchantNumAndDeletedFlagIsFalse(param.getMerchantNum());//查询商户号
		if (merchant == null) {
			throw new BizException(BizError.商户未接入);
		}

		String sign =  param.getMerchantNum() + param.getOrderNo() + merchant.getSecretKey();
		sign = new Digester(DigestAlgorithm.MD5).digestHex(sign);//查询接口签名 商户号+订单号+秘钥

       if(!param.getSign().equals(sign)){
		   return Result.fail("签名错误");
	   }
		return Result.success().setData(merchantOrderService.findMerchantOrderDetailsById(payOrderInfo.getMerchantOrderId()));
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
		    public static void main(String args[]){
				String str = getCode(6);
				System.out.println(str);
			}


	/**
	 * 导出订单excel文件导出
	 */
	@RequestMapping("/merchantOrderExport")
	public void merchantOrderExport(MerchantOrderQueryCondParam param, HttpServletResponse response) throws IOException {
		MerchantAccountDetails user = (MerchantAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		param.setMerchantName(user.getMerchantName());
		/**
		 * 查询数据
		 */
		PageResult<MerchantOrderVO> orderListReportVOPageResult= merchantOrderService.findMerchantOrderByPage(param);


		List<MerchantOrderVO> orderListReportVOPageResultContent=orderListReportVOPageResult.getContent();


		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setCharacterEncoding("utf-8");

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
	 * 新方法导出订单excel文件导出
	 */
	@RequestMapping("/merchantOrdernewExport")
	public void merchantOrdernewExport(MerchantOrderQueryCondParam param, HttpServletResponse response) throws IOException {
		MerchantAccountDetails user = (MerchantAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		param.setMerchantName(user.getMerchantName());//商户名称
		/**
		 * 查询数据
		 */
		PageResult<MerchantOrderVO> orderListReportVOPageResult= merchantOrderService.findMerchantOrderByPage(param);
		List<MerchantOrderVO> orderListReportVOPageResultContent=orderListReportVOPageResult.getContent();//查询订单数据


		//response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		//	response.setCharacterEncoding("utf-8");

//		ExportParams exportParams = new ExportParams();  //导出的excel相关配置
//		exportParams.setTitle("订单信息表");  //导出excel的第一行标题名字
//		exportParams.setSheetName("订单信息表sheet");  //导出的excel的sheet名称
//		Workbook workbook = ExcelExportUtil.exportExcel(exportParams, MerchantOrderVO.class, orderListReportVOPageResultContent);
//		response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode("订单信息.csv", "utf-8"));
//		ServletOutputStream outputStream = response.getOutputStream();
//		workbook.write(outputStream);
//		//关闭流
//		outputStream.close();
//		workbook.close();

		// 总记录数
		Integer totalRowCount = orderListReportVOPageResultContent.size();

		// 导出EXCEL文件名称
		String filaName = "订单交易记录EXCEL";

		// 标题
		String[] titles = {"订单号", "商户订单号", "收款金额", "订单状态", "银行卡号", "银行卡姓名", "银行名称", "备注","提交时间","完成时间"};
		// 开始导入
		try {
			PoiUtil.exportExcelToWebsite(response, totalRowCount, filaName, titles, new WriteExcelDataDelegated() {
				public void writeExcelData(SXSSFSheet eachSheet, Integer startRowCount, Integer endRowCount, Integer currentPage, Integer pageSize) throws Exception {
					if (!CollectionUtils.isEmpty(orderListReportVOPageResultContent)) {
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
	 * 导出商户交易记录excel文件导出
	 */
	@RequestMapping("/orderPayMerhantlistExport")
	public void orderExport(MerchantPaylistParam param, HttpServletResponse response) throws IOException {

		MerchantAccountDetails user = (MerchantAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		param.setMerchantId(user.getMerchantId());//商户ID号就是主键ID号


		/**
		 * 查询数据
		 */
		PageResult<ActuallncomeRecrdReportVO> orderMerchantListReportVOPageResult= merchantOrderService.merchantPaylist(param);
		List<ActuallncomeRecrdReportVO>	orderListReportVOPageResultContent=orderMerchantListReportVOPageResult.getContent();//获取商户交易记录数据

		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setCharacterEncoding("utf-8");


		ExportParams exportParams = new ExportParams();  //导出的excel相关配置
		exportParams.setTitle("商户交易记录信息表");  //导出excel的第一行标题名字
		exportParams.setSheetName("商户交易记录sheet");  //导出的excel的sheet名称
		Workbook workbook = ExcelExportUtil.exportExcel(exportParams, ActuallncomeRecrdReportVO.class, orderListReportVOPageResultContent);
		response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode("商户交易记录.csv", "utf-8"));
		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		//关闭流
		outputStream.close();
		workbook.close();

	}

	/**
	 * 新方法导出商户交易记录excel文件导出
	 */
	@RequestMapping("/newrderPayMerhantlistExport")
	public void newOrderExport(MerchantPaylistParam param, HttpServletResponse response) throws IOException {

		MerchantAccountDetails user = (MerchantAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		param.setMerchantId(user.getMerchantId());//商户ID号就是主键ID号
		/**
		 * 查询数据
		 */
		PageResult<ActuallncomeRecrdReportVO> orderMerchantListReportVOPageResult= merchantOrderService.merchantPaylist(param);
		List<ActuallncomeRecrdReportVO>	orderListReportVOPageResultContent=orderMerchantListReportVOPageResult.getContent();//获取商户交易记录数据

		// 总记录数
		Integer totalRowCount = orderListReportVOPageResultContent.size();

		// 导出EXCEL文件名称
		String filaName = "商户交易记录EXCEL";

		// 标题
		String[] titles = {"订单号", "商户订单号", "充值金额", "实际到账金额", "手续费", "商户结余", "创建时间", "结算时间"};

		// 开始导入
		try {
			PoiUtil.exportExcelToWebsite(response, totalRowCount, filaName, titles, new WriteExcelDataDelegated() {
				public void writeExcelData(SXSSFSheet eachSheet, Integer startRowCount, Integer endRowCount, Integer currentPage, Integer pageSize) throws Exception {

					//PageHelper.startPage(currentPage, pageSize);
					//List<UserVO> userVOList = userMapper.selectUserVOList(userVO);

					if (!CollectionUtils.isEmpty(orderListReportVOPageResultContent)) {
						// --------------   这一块变量照着抄就行  强迫症 后期也封装起来     ----------------------
						for (int i = startRowCount; i <= endRowCount; i++) {
							SXSSFRow eachDataRow = eachSheet.createRow(i);
							if ((i - startRowCount) < orderListReportVOPageResultContent.size()) {
								ActuallncomeRecrdReportVO eachUserVO = orderListReportVOPageResultContent.get(i - startRowCount);
								// ---------   这一块变量照着抄就行  强迫症 后期也封装起来     -----------------------
								eachDataRow.createCell(0).setCellValue(eachUserVO.getOrderNo() == null ? "" : eachUserVO.getOrderNo());
								eachDataRow.createCell(1).setCellValue(eachUserVO.getMerchantOrderNo() == null ? "" : eachUserVO.getMerchantOrderNo());//商户订单号
								eachDataRow.createCell(2).setCellValue(eachUserVO.getRechargeAmount());//充值金额
								eachDataRow.createCell(3).setCellValue(eachUserVO.getActualPayAmount());//实际到账金额
								eachDataRow.createCell(4).setCellValue(eachUserVO.getServiceChange());//手续费
								eachDataRow.createCell(5).setCellValue(eachUserVO.getMerchantTotalView());//商户结余
								eachDataRow.createCell(6).setCellValue(DateUtil.formatDate(eachUserVO.getCreateTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));//创建时间
								eachDataRow.createCell(7).setCellValue(DateUtil.formatDate(eachUserVO.getSettlementTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));//结束时间
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
	 * 外部添加订单接口使用外部系统调用 新方法使用
	 * @param request
	 * @return
	 */
	@ApiLog
	@RequestMapping(value = "/outstartNewAddOrder", method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Result outstartNewAddOrder(HttpServletRequest request) {
		String merchantNum=request.getParameter("merchantNum");//商户号
		String payType=request.getParameter("payType");//bankcard 表示银行卡 支付方式
		String amount=request.getParameter("amount");//订单金额
		String orderNo=request.getParameter("orderNo");//订单号
		String notifyUrl=request.getParameter("notifyUrl");//回调地址是客服提供过来的
		String sign=request.getParameter("sign");//签名
		String attch=request.getParameter("attch");//备注信息
		String systemSorce=request.getParameter("systemSorce");//系统来源
		if(systemSorce==null){
			systemSorce="Pay247";//如果没有系统来源就默认Pay247
		}
		if(orderNo==null){//商户订单号不能为空
			return Result.fail("商户订单号不能为空!");
		}

		if(merchantNum==null && payType==null && amount==null && orderNo==null && notifyUrl==null && sign==null){
			return Result.fail("请输入正确的参数！");
		}
		StartOrderParam param=new StartOrderParam();
		param.setMerchantNum(merchantNum);
		param.setPayType(payType);
		param.setAmount(amount);
		param.setOrderNo(orderNo);//订单号
		param.setNotifyUrl(notifyUrl);//回调地址
		param.setSign(sign);
		param.setSystemSorce(systemSorce);//如果没有系统来源就默认Pay247
		if(attch==null){//备注信息
			param.setAttch(getCode(6).toUpperCase());//随机数值
		}
		MerchantOrderPayInfo payOrderInfo=merchantOrderPayInfoRepo.findByOrderNo(param.getOrderNo());//商户订单号查询数据
		if(payOrderInfo!=null){
			return Result.success().setData("商户订单号已存在,请重新输入商户订单号");
		}
		StartOrderSuccessVO vo= merchantOrderService.payNewStartOrder(param,request);//保存订单数据
		log.info("产品给我的订单号,商户订单号={},系统订单号={},支付地址={}", orderNo,vo.getBillNo(),vo.getPayUrl());
		return Result.success().setData(vo);
	}

	/**
	 * 修改订单付款人
	 * @param id
	 * @param fukuan
	 * @return
	 */
	@PostMapping("/updatePayFuKuan")
	@ResponseBody
	@ApiLog
	public Result updatePayFuKuan(String id, String fukuan) {
		merchantOrderService.updatePayFukuanName(id,fukuan);
		return Result.success();
	}
}
