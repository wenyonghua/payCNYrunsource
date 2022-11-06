var rechargeOrderVM = new Vue({
	el : '#merchant-paylist',
	data : {
		orderNo : '',
		payChannelId : '',
		payChannels : [],
		orderState : '',
		rechargeOrderStateDictItems : [],
		submitStartTime : '',
		submitEndTime : '',
		approvalFlag : false,
		selectedOrder : {},
		actualPayAmount : '',
		approvalResult : '',
		merchantNo: '',
		rechargeAmount: '',
		merchantOrderNo:'',
		settlementTime:'',
	},
	computed : {},
	created : function() {
	},
	mounted : function() {
		var orderState = getQueryString('orderState');
		if (orderState == '1') {
			this.orderState = orderState;
			this.submitStartTime = '';
			this.submitEndTime = '';
		}
		var todoId = getQueryString('todoId');
		if (todoId != null) {
			this.showApprovalOrderModal(todoId);
		}
		//this.loadPayChannel();
		this.loadRechargeOrderStateDictItem();
		this.initTable();//加载表格数据
	},
	methods : {

		loadPayChannel : function() {
			var that = this;
			that.$http.get('/recharge/findAllPayChannel').then(function(res) {
				this.payChannels = res.body.data;
			});
		},

		/**
		 * 加载充值订单状态字典项
		 */
		loadRechargeOrderStateDictItem : function() {
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {
				params : {
					dictTypeCode : 'rechargeOrderState'
				}
			}).then(function(res) {
				this.rechargeOrderStateDictItems = res.body.data;
			});
		},

		initTable : function() {
			var that = this;
			$('.recharge-order-table').bootstrapTable({
				classes : 'table table-hover',
				url : '/merchantOrder/merchantPaylist',
				pagination : true,
				sidePagination : 'server',
				pageNumber : 1,
				pageSize : 10,
				pageList : [ 10, 25, 50, 100 ],
				queryParamsType : '',
				queryParams : function(params) {
					var condParam = {
						pageSize : params.pageSize,
						pageNum : params.pageNumber,
						orderNo : that.orderNo,
						payChannelId : that.payChannelId,
						orderState : that.orderState,
						submitStartTime : that.submitStartTime,
						submitEndTime : that.submitEndTime,
						merchantOrderNo : that.merchantOrderNo,
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
					var html = template('recharge-order-detail', {
						rechargeOrderInfo : row
					});
					return html;
				},
				columns : [ {
					field : 'orderNo',
					title : '订单号'
				}, {
						field : 'merchantOrderNo',
						title : '商户订单号'
					}, {
					field : 'rechargeAmount',
					title : '充值金额'
				}, {
					field : 'actualPayAmount',
					title : '实际到账金额'
				},{
					field : 'serviceChange',
					title : '手续费'
				}, {
						field : 'merchantTotalView',
						title : '商户结余'
					},
					{
					field : 'orderStateName',
					title : '订单状态'
				},
					{
					field : 'createTime',
					title : '提交时间'
				},  {
						field : 'settlementTime',
						title : '结束时间'
					},{
					title : '操作',
					formatter : function(value, row, index) {
						if (row.orderState == '1') {
							if (row.depositTime != null) {
								return [ '<button type="button" class="approval-order-btn btn btn-outline-info btn-sm">审核</button>' ].join('');
							} else {
								return [ '<button type="button" class="cancel-order-btn btn btn-outline-danger btn-sm">取消订单</button>' ].join('');
							}
						}
						if (row.orderState == '2') {
							return [ '<button type="button" class="manual-settlement-btn btn btn-outline-success btn-sm">手动结算</button>' ].join('');
						}
					},
					events : {
						'click .approval-order-btn' : function(event, value, row, index) {
							that.showApprovalOrderModal(row.id);
						},
						'click .cancel-order-btn' : function(event, value, row, index) {
							that.cancelOrder(row.id);
						},
						'click .manual-settlement-btn' : function(event, value, row, index) {
							that.manualSettlement(row.orderNo);
						}
					}
				} ]
			});
		},

		refreshTable : function() {
			$('.recharge-order-table').bootstrapTable('refreshOptions', {
				pageNumber : 1
			});
		},

		showApprovalOrderModal : function(id) {
			var that = this;
			that.$http.get('/recharge/findRechargeOrderById', {
				params : {
					id : id
				}
			}).then(function(res) {
				that.selectedOrder = res.body.data;
				that.actualPayAmount = that.selectedOrder.rechargeAmount;
				that.approvalResult = '2';
				that.approvalFlag = true;
			});
		},

		approval : function() {
			var that = this;
			if (that.approvalResult == null || that.approvalResult == '') {
				layer.alert('请选择审核结果', {
					title : '提示',
					icon : 7,
					time : 3000
				});
				return;
			}
			if (that.approvalResult == '2') {
				if (that.actualPayAmount == null || that.actualPayAmount == '') {
					layer.alert('请输入实际存款金额', {
						title : '提示',
						icon : 7,
						time : 3000
					});
					return;
				}
			}
			that.$http.post('/recharge/approval', {
				id : that.selectedOrder.id,
				actualPayAmount : that.actualPayAmount,
				approvalResult : that.approvalResult
			}, {
				emulateJSON : true
			}).then(function(res) {
				layer.alert('操作成功!', {
					icon : 1,
					time : 3000,
					shade : false
				});
				that.approvalFlag = false;
				that.refreshTable();
			});
		},

		/**
		 * 下载数据
		 */
		orderloadDown : function() {//下载数据
			var that = this;
			console.log("订单号="+that.orderNo);
			console.log("商户订单号="+that.merchantOrderNo);
			//console.log("订单状态="+that.orderState);

			//window.open('/merchantOrder/orderPayMerhantlistExport?orderNo='+that.orderNo+'&merchantOrderNo='+that.merchantOrderNo+"&submitStartTime="+that.submitStartTime+"&submitEndTime="+that.submitEndTime+"&pageNum="+1+"&pageSize="+99999999);
			//使用新方法
			window.open('/merchantOrder/newrderPayMerhantlistExport?orderNo='+that.orderNo+'&merchantOrderNo='+that.merchantOrderNo+"&submitStartTime="+that.submitStartTime+"&submitEndTime="+that.submitEndTime+"&pageNum="+1+"&pageSize="+99999999);
		},

		cancelOrder : function(id) {
			var that = this;
			layer.confirm('确定要取消订单吗?', {
				icon : 7,
				title : '提示'
			}, function(index) {
				layer.close(index);
				that.$http.get('/recharge/cancelOrder', {
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

		manualSettlement : function(orderNo) {
			var that = this;
			layer.confirm('确定要结算吗?', {
				icon : 7,
				title : '提示'
			}, function(index) {
				layer.close(index);
				that.$http.get('/recharge/manualSettlement', {
					params : {
						orderNo : orderNo
					}
				}).then(function(res) {
					layer.alert('已通知系统进行结算!', {
						icon : 1,
						time : 3000,
						shade : false
					});
					that.refreshTable();
				});
			});
		}
	}
});