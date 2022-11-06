var platformOrderVM = new Vue({
	el : '#platform-order',
	data : {
		global : GLOBAL,
		orderNo : '',
		merchantName : '',
		merchantOrderNo : '',
		gatheringChannelCode : '',
		gatheringChannelDictItems : [],
		orderState : '',
		merchantOrderStateDictItems : [],
		receiverUserName : '',
		submitStartTime : dayjs().format('YYYY-MM-DD'),
		submitEndTime : dayjs().format('YYYY-MM-DD'),

		showAddOrderFlag : false,
		merchantDictItems : [],
		newOrders : [],
		selectedMerchant : '',
		notifyUrlWithAddOrder : '',
		returnUrlWithAddOrder : '',

		showSplitOrderFlag : false,
		splitAmount : '',
		newOrderIndex : '',
		splitOrderNumber : '',
		splitOrderRule : '2',
		bankCardAccount : '',
		note :'',
		gatheringAmountView : '',
		attch : '',
		bankCardUserName : '',
		bankCardBankName : '',
		confirmTime : '',
		currentTable: null,
		checkbox: false,
		timer: null,
		systemSource : '',
		useBankList : [],
		systemSourceDictItems : [],//系统来源
		merchantNum : '',//商户号
		shoukuBankPayee : '',//收款人
		shoukuBankName : '',//收款银行名称
		shoukuBankNumber :'',//收款人卡号
		shoukuBankBranch :'',//收款支行
		approvalFlag : false,
		selectedRecord : {},//获取单个数据
		merchantBankCards : [],//银行卡列表数据
		onlyShowNotApprovedBtnFlag : false,
		submitInfo: [{
			note : '',
			payCardNo : '',//付款银行卡号
			serverCharge : '',//付款手续费
			payAmount : '',//付款金额
		}],
		fukuBankNumber:'',//付款银行列表

	},
	computed : {
		newOrderTotalAmount : function() {
			var totalAmount = 0;
			for (var i = 0; i < this.newOrders.length; i++) {
				var newOrder = this.newOrders[i];
				if (newOrder.gatheringAmount != null && newOrder.gatheringAmount != '') {
					totalAmount += newOrder.gatheringAmount;
				}
			}
			return numberFormat(totalAmount);
		},

	},
	created : function() {
	},

	/**
	 * 界面选中选中或者不选中数据
	 */
	watch: {
		checkbox: function(val, oldVal) {
			console.log(val);
			let that = this;
			if(val) {
				that.timer = setInterval(() => {
					that.currentTable && $('.platform-order-table').bootstrapTable('refresh');
				}, 60000);//60秒刷新
			} else {
				clearTimeout(that.timer);
			}
		}
	},

	mounted : function() {
		var merchantName = getQueryString('merchantName');
		if (merchantName != null && merchantName != '') {
			this.merchantName = decodeURI(merchantName);
		}
		var orderState = getQueryString('orderState');
		if (orderState != null && orderState != '') {
			this.orderState = orderState;
		}
		var submitStartTime = getQueryString('submitStartTime');
		if (submitStartTime != null && submitStartTime != '') {
			if (submitStartTime == 'all') {
				this.submitStartTime = '';
			} else {
				this.submitStartTime = submitStartTime;
			}
		}
		var submitEndTime = getQueryString('submitEndTime');
		if (submitEndTime != null && submitEndTime != '') {
			if (submitEndTime == 'all') {
				this.submitEndTime = '';
			} else {
				this.submitEndTime = submitEndTime;
			}
		}
		this.loadGatheringChannelDictItem();
		this.loadMerchantOrderStateDictItem();

		this.initTable();//加载数据
		this.loadSystemSourceTypeItem();//查询系统来源


		// this.timer = setInterval(() => {
		// 	this.currentTable && $('.platform-order-table').bootstrapTable('refresh');
		// }, 10000);//默认加载定时任务

	},
	methods : {

		/**
		 * 加载收款通道字典项
		 */
		loadGatheringChannelDictItem : function() {
			var that = this;
			that.$http.get('/gatheringChannel/findAllGatheringChannel').then(function(res) {
				that.gatheringChannelDictItems = res.body.data;
			});
		},
		loadSystemSourceTypeItem : function() {//查询系统来源
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {
				params : {
					dictTypeCode : 'SystemSource'
				}
			}).then(function(res) {
				this.systemSourceDictItems = res.body.data;
			});
		},

		/**
		 * 加载商户订单状态字典项
		 */
		loadMerchantOrderStateDictItem : function() {
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {
				params : {
					dictTypeCode : 'merchantOrderState'
				}
			}).then(function(res) {
				this.merchantOrderStateDictItems = res.body.data;
			});
		},



		initTable : function() {
			console.log("进入这个方法");
			var that = this;
			this.currentTable = $('.platform-order-table').bootstrapTable({
				classes : 'table table-hover',
				url : '/merchantPayoutOrder/findMerchantPayoutOrderByPage',
				pagination : true,
				sidePagination : 'server',
				pageNumber : 1,
				pageSize : 10,
				pageList : [ 10, 25, 50, 100,200,500,1000,2000 ],
				queryParamsType : '',
				systemSorce : '',
				queryParams : function(params) {
					var condParam = {
						pageSize : params.pageSize,
						pageNum : params.pageNumber,
						orderNo : that.orderNo,
						merchantName : that.merchantName,//商户名称
						merchantOrderNo : that.merchantOrderNo,
						orderState : that.orderState,
						gatheringChannelCode : that.gatheringChannelCode,
						receiverUserName : that.receiverUserName,
						submitStartTime : that.submitStartTime,
						submitEndTime : that.submitEndTime,
						bankCardAccount : that.bankCardAccount,
						note : that.note,
						gatheringAmountView : that.gatheringAmountView,
						attch : that.attch,
						systemSource : that.systemSource,
						merchantNum : that.merchantNum,
						shoukuBankNumber :that.shoukuBankNumber,
						fukuBankNumber : that.fukuBankNumber,//付款银行列表
					};
					return condParam;
				},
				responseHandler : function(res) {
					return {
						total : res.data.total,
						rows : res.data.content
					};
				},
				detailView : true,
				detailFormatter : function(index, row, element) {
					var html = template('merchant-order-detail', {
						merchantOrderInfo : row
					});
					return html;
				},
				columns : [
					// 	{
					// 	title : '订单号/商户/商户订单号',
					// 	formatter : function(value, row, index, field) {
					// 		var text = row.orderNo + '/' + row.platformName + '/' + row.payInfo.orderNo;
					// 		return text;
					// 	}
					// },
					{
						field : 'orderNo',
						title : '订单号'
					},
					{
						field: 'merchantNum',
						title : '商户号',
					},
					{
						title : '商户名称',
						formatter : function(value, row, index, field) {
							var text =row.platformName;
							return text;
						}
					},
					{
						 field: 'merchantOrderNo',
						 title : '商户订单号',
						// formatter : function(value, row, index, field) {
						// 	var text = row.payInfo.orderNo;
						// 	return text;
						// }
					},
					{
						field : 'shoukuBankNumber',
						title : '收款银行卡号'
					},
					{
						field : 'shoukuBankPayee',
						title : '收款人'
					},
					{
						field : 'shoukuBankName',
						title : '收款银行名称'
					},
					{
						field : 'shoukuBankBranch',
						title : '收款支行'
					},
					{
						field: 'gatheringAmountView',
						title : '金额'
					},

					{
						field : 'fukuBankNumber',
						title : '付款银行卡号'
					},
					// {
					// 	field: 'bankCardUserName',
					// 	title: '付款卡姓名'
					// },
					// {
					// 	field: 'bankCardBankName',
					// 	title: '付款银行名称'
					// },
					// {
					// 	title : '附加信息',
					// 	formatter : function(value, row, index, field) {
					// 		var text = row.payInfo.attch;
					// 		return text;
					// 	}
					// },
					{
						field: 'systemSource',
						title: '系统来源'
					},
					{
						field : 'note',
						title : '备注'
					},
					{
						field : 'orderStateName',
						title : '订单状态'
					},
					// 	{
					// 	title : '通道/金额/费率',
					// 	formatter : function(value, row, index, field) {
					// 		var text = row.gatheringChannelName + '/' + row.gatheringAmount + that.global.systemSetting.currencyUnit + '/' + row.rate + '%';
					// 		return text;
					// 	}
					// },
					{
						field : 'bounty',
						title : '手续费'
					},

					{
						title : '通道',
						formatter : function(value, row, index, field) {
							var text = row.gatheringChannelName;
							return text;
						}
					},


					// {
					// 	title : '费率',
					// 	formatter : function(value, row, index, field) {
					// 		var text = + row.rate + '%';
					// 		return text;
					// 	}
					// },

					// 	{
					// 	title : '奖励金/返点',
					// 	formatter : function(value, row, index, field) {
					// 		if (row.bounty != null) {
					// 			text = row.bounty + that.global.systemSetting.currencyUnit + '/' + row.rebate + '%';
					// 			return text;
					// 		}
					// 	}
					// },
					// {
					// 	title : '接单人/接单时间',
					// 	formatter : function(value, row, index, field) {
					// 		if (row.receiverUserName == null) {
					// 			return;
					// 		}
					// 		var text = row.receiverUserName + '/' + row.receivedTime;
					// 		return text;
					// 	}
					// },
						{
						field : 'submitTime',
						title : '提交时间'
					},
					{
						field: 'confirmTime',
						title: '完成时间'
					},

					// {
					// 	title : '操作',
					// 	formatter : function(value, row, index) {
					// 		var btns = [];
					// 		if (row.orderState == '2') { //已接单
					// 			btns.push('<button type="button" class="withdraw-approval-btn btn btn-outline-primary btn-sm" style="margin-right: 4px;">进行付款</button>');
					// 			btns.push('<button type="button" class="cancel-order-btn btn btn-outline-danger btn-sm">拒绝订单</button>');
					// 		}
					//
					// 		if (row.orderState == '1') {//取消订单
					// 			btns.push('<button type="button" class="cancel-order-btn btn btn-outline-danger btn-sm">取消订单</button>');
					// 		}
					//
					// 		if (btns.length > 0) {
					// 			return btns.join('');
					// 		}
					// 	},
					// 	events : {
					// 		'click .withdraw-approval-btn' : function(event, value, row, index) {
					// 			that.onlyShowNotApprovedBtnFlag = false;
					// 			that.showApprovalModal(row.id);//进行付款
					// 		},
					// 		'click .cancel-order-btn' : function(event, value, row, index) {
					// 			that.cancelOrder(row.id);//取消
					// 		},
					// 		'click .resend-notice-btn' : function(event, value, row, index) {
					// 			that.resendNotice(row.id);
					// 		}
					// 	}
					// }
				]
			});
		},

		//查询订单数据
		showApprovalModal : function(id) {
			var that = this;
			that.$http.get('/merchantPayoutOrder/findByPayoutOrder', {
				params : {
					id : id
				}
			}).then(function(res) {
				that.selectedRecord = res.body.data;
				that.note = null;
				that.approvalFlag = true; //设置true
			});

		},

		/**
		 * 添加银行卡
		 */
		addReceiveOrderChannel: function() {
			this.submitInfo.push({
				note : '',
				payCardNo : '',//银行卡号
				serverCharge : '',//服务费
			})
		},
		<!--确定审核通过-->
		approved : function() {
			var that = this;
			console.log(this.submitInfo);
			layer.confirm('确定审核通过吗?', {
				icon : 7,
				title : '提示'
			}, function(index) {
				layer.close(index);
				that.$http.post('/merchantPayoutOrder/submitPayout', {
					id : that.selectedRecord.id,
					bankList: that.submitInfo
				}).then(function(res) {
					layer.alert('操作成功!', {
						icon : 1,
						time : 3000,
						shade : false
					});
					that.approvalFlag = false;//设置false
					that.refreshTable();//刷新表格
				});
			});
		},
		/**
		 * 下载数据
		 */
		orderloadDown : function() {//下载数据
			var that = this;
			console.log("订单号="+that.orderNo);
			console.log("商户="+that.merchantName);
			console.log("商户订单号="+that.merchantOrderNo);
			console.log("银行卡号="+that.bankCardAccount);
			console.log("订单状态="+that.orderState);
			console.log("接单人="+that.receiverUserName);
			console.log("备注="+that.note);
			console.log("附言="+that.attch);

			window.open('/merchantPayoutOrder/orderNewExport?orderNo='+that.orderNo+'&merchantName='+that.merchantName+'&merchantOrderNo='+that.merchantOrderNo+'&bankCardAccount='+that.bankCardAccount+'&orderState='+that.orderState+'&receiverUserName='+that.receiverUserName+"&submitStartTime="+that.submitStartTime+"&submitEndTime="+that.submitEndTime+"&note="+that.note+"&attch="+that.attch+"&pageNum="+1+"&pageSize="+99999999);

		},


		refreshTable : function() {
			$('.platform-order-table').bootstrapTable('refreshOptions', {
				pageNumber : 1
			});
		},

		confirmToPaidWithCancelOrderRefund : function(orderId) {//确认支付按钮
			var that = this;
			layer.confirm('确定更改为已支付状态吗', {
				icon : 7,
				title : '提示'
			}, function(index) {
				layer.close(index);
				that.$http.get('/merchantOrder/confirmToPaidWithCancelOrderRefund', {
					params : {
						orderId : orderId
					}
				}).then(function(res) {
					layer.alert('操作成功!', {
						icon : 1,
						time : 3000,
						shade : false
					});
					that.refreshTable();
				});
			});
		},

		confirmToPaid : function(userAccountId, orderId) {//确认已支付按钮
			var that = this;
			layer.prompt({
				title : '请输入备注必须是银行卡到账的金额',
				formType : 2
			}, function(text, index) {
				layer.close(index);
				that.surePayorder(userAccountId, orderId,text);//调用确认支付方法
			});
		},
		surePayorder : function(userAccountId, orderId,text){//确认支付方法
			var that = this;
			if (text == null || text == '') {
				//	alert('请输入备注描述信息'）;
				alert('请输入备注描述信息');
				return;
			}
			that.$http.get('/merchantOrder/confirmToPaid', {
				params : {
					userAccountId : userAccountId,
					orderId : orderId,
					note : text
				}
			}).then(function(res) {
				layer.alert('操作成功!', {
					icon : 1,
					time : 3000,
					shade : false
				});
				that.refreshTable();//刷新表格
			});
		},



		cancelOrderRefund : function(id) {
			var that = this;
			layer.confirm('确定要取消订单退款吗?', {
				icon : 7,
				title : '提示'
			}, function(index) {
				layer.close(index);
				that.$http.get('/merchantOrder/cancelOrderRefund', {
					params : {
						id : id
					}
				}).then(function(res) {
					layer.alert('操作成功!', {
						icon : 1,
						time : 3000,
						shade : false
					});
					that.refreshTable();
				});
			});
		},

		forceCancelOrder : function(id) {
			var that = this;
			layer.confirm('确定要强制取消订单吗?', {
				icon : 7,
				title : '提示'
			}, function(index) {
				layer.close(index);
				that.$http.get('/merchantOrder/forceCancelOrder', {
					params : {
						id : id
					}
				}).then(function(res) {
					layer.alert('操作成功!', {
						icon : 1,
						time : 3000,
						shade : false
					});
					that.refreshTable();
				});
			});
		},

		/**
		 * 取消订单按钮
		 * @param id
		 */
		cancelOrder : function(id) {//取消订单按钮
			var that = this;
			layer.prompt({
				title : '请输入备注信息',
				formType : 2
			}, function(text, index) {
				layer.close(index);
				that.cancelOrderSave(id,text);//调用取消方法
			});
		},

		cancelOrderSave : function(orderId,text){//取消方法
			var that = this;
			if (text == null || text == '') {
				//	alert('请输入备注描述信息'）;
				alert('请输入备注描述信息');
				return;
			}
			that.$http.get('/merchantPayoutOrder/cancelOrder', {
				params : {
					orderId : orderId,
					note : text
				}
			}).then(function(res) {
				layer.alert('操作成功!', {
					icon : 1,
					time : 3000,
					shade : false
				});
				that.refreshTable();//刷新表格
			});
		},


		// /**
		//  * 取消订单
		//  * @param id
		//  */
		// cancelOrderSave : function(id,text) {
		// 	var that = this;
		// 	layer.confirm('确定要取消订单吗?', {
		// 		icon : 7,
		// 		title : '提示'
		// 	}, function(index) {
		// 		layer.close(index);
		// 		that.$http.get('/merchantPayoutOrder/cancelOrder', {
		// 			params : {
		// 				id : id
		// 			}
		// 		}).then(function(res) {
		// 			layer.alert('操作成功!', {
		// 				icon : 1,
		// 				time : 3000,
		// 				shade : false
		// 			});
		// 			that.refreshTable();
		// 		});
		// 	});
		// },


		showNoteModal : function(id) {//修改备注
			var that = this;
			layer.prompt({
				title : '请输入备注',
				formType : 2
			}, function(text, index) {
				layer.close(index);
				that.updateNote(id, text);
			});
		},

		updateNote : function(id, note) {//修改备注
			var that = this;
			that.$http.post('/merchantPayoutOrder/updateNote', {
				id : id,
				note : note
			}, {
				emulateJSON : true
			}).then(function(res) {
				layer.alert('操作成功!', {
					icon : 1,
					time : 3000,
					shade : false
				});
				that.refreshTable();
			});
		},

		resendNotice : function(id) {
			var that = this;
			layer.load(0, {
				time : 8000,
				shade : [ 0.1, '#fff' ]
			});
			that.$http.get('/merchantPayoutOrder/resendNewNotice', {
				params : {
					id : id
				}
			}).then(function(res) {
				layer.closeAll('loading');
				that.refreshTable();
				var result = res.body.data;
				if (result == null || result == '') {
					result = '无内容';
				}
				layer.open({
					type : 1,
					anim : 2,
					shadeClose : true,
					area : [ '420px', '240px' ],
					content : '<div style="padding: 10px;">' + '<p>返回内容</p>' + '<div style="color: red;">' + result + '</div>' + '</div>'
				});
			});
		},

		loadMerchantDictItem : function() {
			var that = this;
			that.$http.get('/merchant/findAllMerchant').then(function(res) {
				that.merchantDictItems = res.body.data;
			});
		},

		setDefaultNotifyUrl : function() {
			if (this.selectedMerchant == null || this.selectedMerchant == '') {
				return;
			}
			this.notifyUrlWithAddOrder = this.selectedMerchant.notifyUrl;
			this.returnUrlWithAddOrder = this.selectedMerchant.returnUrl;
		},

		showAddOrderModal : function() {
			this.showAddOrderFlag = true;
			this.loadMerchantDictItem();
			this.newOrders = [];
			this.selectedMerchant = '';
			this.notifyUrlWithAddOrder = '';
			this.returnUrlWithAddOrder = '';
			this.addOrder();
		},

		addOrder : function() {
			this.newOrders.push({
				gatheringChannelCode : '',
				gatheringAmount : '',
				orderNo : '',
				attch : '',
				specifiedReceivedAccountId : '',
				publishWay : '1',
				publishTime : dayjs().format('YYYY-MM-DDTHH:mm'),
			});
		},

		showSplitOrderModal : function(newOrder, index) {
			if (newOrder.gatheringAmount === null || newOrder.gatheringAmount === '') {
				layer.alert('收款金额为空,无法拆单', {
					title : '提示',
					icon : 7,
					time : 3000
				});
				return;
			}
			if (newOrder.gatheringAmount <= 0) {
				layer.alert('收款金额必须大于0', {
					title : '提示',
					icon : 7,
					time : 3000
				});
				return;
			}
			this.showSplitOrderFlag = true;
			this.splitAmount = newOrder.gatheringAmount;
			this.newOrderIndex = index;
			this.splitOrderNumber = '';
			this.splitOrderRule = '2';
		},

		splitOrder : function() {
			if (this.splitOrderNumber === null || this.splitOrderNumber === '') {
				layer.alert('请输入单数', {
					title : '提示',
					icon : 7,
					time : 3000
				});
				return;
			}
			if (this.splitOrderNumber <= 0) {
				layer.alert('单数必须大于0', {
					title : '提示',
					icon : 7,
					time : 3000
				});
				return;
			}
			if (!(/^\+?[1-9][0-9]*$/.test(this.splitOrderNumber))) {
				layer.alert('单数必须为正整数', {
					title : '提示',
					icon : 7,
					time : 3000
				});
				return;
			}

			var splitOrderAmounts = [];
			if (this.splitOrderRule == '1') {
				var avgAmount = numberFormat(this.splitAmount / this.splitOrderNumber);
				for (var i = 0; i < this.splitOrderNumber; i++) {
					splitOrderAmounts.push(avgAmount);
				}
			} else {
				splitOrderAmounts = this.randomSplitAmount(this.splitAmount, this.splitOrderNumber);
			}
			var tmpNewOrders = [];
			for (var i = 0; i < this.newOrderIndex; i++) {
				tmpNewOrders.push(this.newOrders[i]);
			}
			for (var i = 0; i < splitOrderAmounts.length; i++) {
				var tmpNewOrder = JSON.parse(JSON.stringify(this.newOrders[this.newOrderIndex]));
				tmpNewOrder.gatheringAmount = splitOrderAmounts[i];
				tmpNewOrders.push(tmpNewOrder);
			}
			for (var i = this.newOrderIndex + 1; i < this.newOrders.length; i++) {
				tmpNewOrders.push(this.newOrders[i]);
			}
			this.newOrders = tmpNewOrders;
			this.showSplitOrderFlag = false;

		},

		randomSplitAmount : function(totalAmount, totalOrderNumber) {
			var remainAmount = +totalAmount;
			var remainOrderNumber = +totalOrderNumber;
			var arr = [];
			while (remainOrderNumber > 0) {
				var amount = numberFormat(this.randomSplitAmountInner(remainAmount, remainOrderNumber));
				remainAmount = remainAmount - amount;
				remainOrderNumber--;
				arr.push(amount);
			}
			return arr;
		},

		randomSplitAmountInner : function(remainAmount, remainOrderNumber) {
			if (remainOrderNumber === 1) {
				return remainAmount.toFixed(2);
			}
			var max = ((remainAmount / remainOrderNumber) * 2 - 0.01).toFixed(2);
			var min = 0.01;
			var range = max - min;
			var rand = Math.random();
			var amount = Math.round(rand * range);
			return amount;
		},


		batchStartOrder : function() {//添加订单
			var that = this;
			var newOrders = that.newOrders;
			if (newOrders.length == 0) {
				layer.alert('新增的订单不能为空', {
					title : '提示',
					icon : 7,
					time : 3000
				});
				return;
			}
			for (var i = 0; i < newOrders.length; i++) {
				var newOrder = newOrders[i];
				if (newOrder.gatheringChannelCode === null || newOrder.gatheringChannelCode === '') {
					layer.alert('请选择收款通道', {
						title : '提示',
						icon : 7,
						time : 3000
					});
					return;
				}
				if (newOrder.systemSource === null || newOrder.systemSource === '') {
					layer.alert('请选择系统来源', {
						title : '提示',
						icon : 7,
						time : 3000
					});
					return;
				}
				if (newOrder.gatheringAmount === null || newOrder.gatheringAmount === '') {
					layer.alert('请输入收款金额', {
						title : '提示',
						icon : 7,
						time : 3000
					});
					return;
				}
				if (newOrder.orderNo === null || newOrder.orderNo === '') {
					layer.alert('请输入商户订单号', {
						title : '提示',
						icon : 7,
						time : 3000
					});
					return;
				}
				if (newOrder.publishWay === '2') {
					if (newOrder.publishTime === null || newOrder.publishTime === '') {
						layer.alert('请输入发单时间', {
							title : '提示',
							icon : 7,
							time : 3000
						});
						return;
					}
				}
			}
			if (that.selectedMerchant === null || that.selectedMerchant === '') {
				layer.alert('请选择商户', {
					title : '提示',
					icon : 7,
					time : 3000
				});
				return;
			}
			if (that.notifyUrlWithAddOrder === null || that.notifyUrlWithAddOrder === '') {
				layer.alert('请输入异步通知地址', {
					title : '提示',
					icon : 7,
					time : 3000
				});
				return;
			}
			var batchStartOrderParams = [];
			for (var i = 0; i < newOrders.length; i++) {
				var newOrder = newOrders[i];
				batchStartOrderParams.push({
					gatheringChannelCode : newOrder.gatheringChannelCode,
					gatheringAmount : newOrder.gatheringAmount,
					orderNo : newOrder.orderNo,
					attch : newOrder.attch,
					specifiedReceivedAccountId : newOrder.specifiedReceivedAccountId,
					publishWay : newOrder.publishWay,
					publishTime : newOrder.publishWay == '1' ? null : dayjs(newOrder.publishTime).format('YYYY-MM-DD HH:mm:ss'),
					merchantNum : that.selectedMerchant.merchantNum,
					notifyUrl : that.notifyUrlWithAddOrder,
					returnUrl : that.returnUrlWithAddOrder,
					systemSource : newOrder.systemSource,
					shoukuBankPayee : newOrder.shoukuBankPayee,//收款人
					shoukuBankNumber : newOrder.shoukuBankNumber,//收款卡号
					shoukuBankName : newOrder.shoukuBankName,//银行名称
					shoukuBankBranch : newOrder.shoukuBankBranch,//银行支行
				});
			}

			that.$http.post('/merchantPayoutOrder/startOrder', batchStartOrderParams).then(function(res) {
				layer.alert('操作成功!', {
					icon : 1,
					time : 3000,
					shade : false
				});
				that.showAddOrderFlag = false;
				that.refreshTable();
			});
		}

	}
});