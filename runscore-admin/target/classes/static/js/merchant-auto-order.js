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
		payFukuanName :'',//付款人
		bankFlow :'',//银行流水号
		outOrderNo:'',//商户订单号
		autoAmount :'',//自动机回调金额
		bankRemark :'',//完整银行附言信息
		saveFailError : '',//记录附言码失败数据
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
		this.loadUseBankList();//加载使用的银行卡列表数据
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
		 * 加载自动机订单状态字典项
		 */
		loadMerchantOrderStateDictItem : function() {
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {
				params : {
					dictTypeCode : 'merchantAutoOrderState'
				}
			}).then(function(res) {
				this.merchantOrderStateDictItems = res.body.data;
			});
		},

		/**
		 * 加载可以使用的银行的银行卡列表
		 */
		loadUseBankList : function() {
			var that = this;
			that.$http.get('/gatheringCode/getUseBankList', {
				// params : {
				// 	dictTypeCode : 'merchantOrderState'
				// }
			}).then(function(res) {
				//console.log("打印数据1="+res);
				//console.log("打印数据2="+res.body);
				//console.log("打印数据3="+res.body.data);
				this.useBankList = res.body.data;
				console.log("打印数据4="+this.useBankList);
				that.$nextTick(function() {
					$('.selectpicker').selectpicker('refresh');
				});
				$('.selectpicker').on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {
					console.log($('.selectpicker').val().join(';'));
					that.bankCardAccount = $('.selectpicker').val().join(';');
				});

			});
		},


		initTable : function() {
			console.log("进入这个方法");
			var that = this;
			this.currentTable = $('.platform-order-table').bootstrapTable({
				classes : 'table table-hover',
				url : '/merchantAutoOrder/findMerchantAutoOrderByPage',
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
						orderState : that.orderState,//订单状态
						gatheringChannelCode : that.gatheringChannelCode,
						receiverUserName : that.receiverUserName,
						submitStartTime : that.submitStartTime,
						submitEndTime : that.submitEndTime,
						bankCardAccount : that.bankCardAccount,
						note : that.note,//附言码
						gatheringAmountView : that.gatheringAmountView,
						attch : that.attch,
						systemSource : that.systemSource,
						merchantNum : that.merchantNum,
						payFukuanName : that.payFukuanName,
						bankFlow : that.bankFlow,//银行流水
						outOrderNo : that.outOrderNo,//商户订单号
						autoAmount : that.autoAmount,//自动机回调金额
						bankRemark : that.bankRemark,//银行完整附言备注
						saveFailError :that.saveFailError,//记录失败数据
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
					{
						field : 'bankFlow',
						title : '银行流水号'
					},
					{
						field : 'orderNo',
						title : '订单号'
					},
					{
						field : 'outOrderNo',
						title : '商户订单号'
					},
					{
						field : 'gatheringAmount',
						title : '收款金额'
					},
					{
						field : 'bankNumber',
						title : '银行卡号'
					},
					{
						field : 'cardName',
						title : '卡户主'
					},
					{
						field : 'bankName',
						title : '银行名称'
					},
					{
						field : 'autoAmount',
						title : '自动机回调金额'
					},
					{
						field : 'note',
						title : '附加信息'
					},
					{
						field : 'bankRemark',
						title : '银行完整附言信息'
					},
					{
						field : 'orderStateName',
						title : '订单状态'
					},
					{
						field : 'balance',
						title : '银行卡总余额'
					},

					{
						field : 'returnMessage',
						title : '自动机返回数据'
					},

					{
						field : 'saveFailError',
						title : '记录附言码修改'
					},

	                 {
						field : 'submitTime',
						title : '提交时间'
					},


					// {
					// 	field: 'systemSource',
					// 	title: '系统来源'
					// },

					// {
					// 	title : '操作',
					// 	formatter : function(value, row, index) {
					// 		var btns = [];
					// 		if (row.orderState == '1') {
					// 			btns.push('<button type="button" class="cancel-order-btn btn btn-outline-danger btn-sm">取消订单</button>');
					// 		}
					// 		if (row.orderState == '4' && row.payInfo.noticeState == '3') {//通知失败就需要重发通知   订单状态是支付成功4的状态，通知状态 1未通知 2通知成功 3通知失败 才有重发按钮
					// 			btns.push('<button type="button" class="resend-notice-btn btn btn-outline-info btn-sm">重发通知</button>');
					// 		}
					// 		if (btns.length > 0) {
					// 			return btns.join('');
					// 		}
					// 	},
					// 	events : {
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

		/**
		 * 下载数据
		 */
		orderloadDown : function() {//下载数据
			var that = this;
			console.log("银行流水="+that.bankFlow);
			console.log("订单号="+that.orderNo);
			console.log("商户订单号="+that.outOrderNo);
			console.log("银行卡号="+that.bankCardAccount);
			console.log("订单状态="+that.orderState);
			console.log("备注="+that.note);
			console.log("自动金回调金额="+that.autoAmount)


			window.open('/merchantAutoOrder/orderAutoNewExport?bankFlow='+that.bankFlow+'&orderNo='+that.orderNo+'&outOrderNo='+that.outOrderNo+'&bankCardAccount='+that.bankCardAccount+'&orderState='+that.orderState+"&note="+that.note+"&autoAmount"+that.autoAmount+"&submitStartTime="+that.submitStartTime+"&submitEndTime="+that.submitEndTime+"&pageNum="+1+"&pageSize="+99999999);

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
			// layer.prompt({
			// 	title : '请输入备注信息',
			// 	formType : 2
			// },
			layer.confirm('确认取消订单吗?', {
				title : '确认取消订单吗?',
				content:  '<div class="form-group">'+
					'<select id="confirmText" class="form-control" rows="3"><option name="NOT RECEIVED MONEY">NOT RECEIVED MONEY </option> <option name="WRONG AMOUNT">WRONG AMOUNT </option></select>'+
					'</div>',
				btn : [ '确认', '返回' ]
			}, function(index) {
				//alert($('#confirmText').val());
				let textValuedesk= $('#confirmText').val();//获取值
				//alert("弹出数据="+textValuedesk);
				layer.close(index);//
				that.cancelOrderSave(id,textValuedesk);//调用取消方法
			});
		},

		cancelOrderSave : function(orderId,text){//取消方法
			var that = this;
			if (text == null || text == '') {
				alert('请输入备注描述信息');
				return;
			}
			that.$http.get('/merchantOrder/cancelOrder', {
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
			that.$http.post('/merchantOrder/updateNote', {
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
			that.$http.get('/merchantOrder/resendNewNotice', {
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
				});
			}

			that.$http.post('/merchantOrder/startOrder', batchStartOrderParams).then(function(res) {
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