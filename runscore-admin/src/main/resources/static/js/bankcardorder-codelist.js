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
		bankNum:'',
		page:'',
		cardTotalView :'',
		note :'',
		bankDepositTotal :'',
		bankPayoutTotal :'',

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
		this.loadPayChannel();//查询渠道
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
		 * 下载数据
		 */
		loadDown : function() {//下载数据
			var that = this;
			console.log("订单号"+that.orderNo);
			console.log("订单状态"+that.orderState);
			console.log("商户订单号"+that.merchantOrderNo);
			console.log("银行卡号"+that.bankNum);
			window.open('/merchantOrder/export?orderNo='+that.orderNo+'&merchantOrderNo='+that.merchantOrderNo+'&orderState='+that.orderState+"&submitStartTime="+that.submitStartTime+"&submitEndTime="+that.submitEndTime+"&bankNum="+that.bankNum+"&pageNum="+1+"&pageSize="+99999999);
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
				url : '/merchantOrder/bankPaylist',//银行卡交易列表数据
				pagination : true,
				sidePagination : 'server',
				pageNumber : 1,
				pageSize : 10,
				pageList : [ 10, 25, 50, 100,200,500,1000,2000 ],
				queryParamsType : '',
				queryParams : function(params) {
					var condParam = {
						pageSize : params.pageSize,
						pageNum : params.pageNumber,
						orderNo : that.orderNo,
						payChannelId : that.payChannelId,
						orderState : that.orderState,//订单状态
						submitStartTime : that.submitStartTime,//开始时间
						submitEndTime : that.submitEndTime,//结束时间
						merchantOrderNo : that.merchantOrderNo,//商户订单号
						bankNum : that.bankNum,//银行卡号
					};
					return condParam;
				},
				// onPageChange : function (page, size) {
				// 	this.page = page;
				// 	this.size = size;
				// },
				responseHandler : function(res) {
					console.log("存款金额:>>>>>>>>>"+res.data.bankDepositTotal)
					console.log("付款金额:>>>>>>>>>"+res.data.bankPayoutTotal)
					that.bankDepositTotal=res.data.bankDepositTotal;
					that.bankPayoutTotal=res.data.bankPayoutTotal;
					return {
						total : res.data.total,
						rows : res.data.content,
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
					title : '订单号(Oder No)'
				}, {
					field : 'merchantOrderNo',
					title : '商户订单号(Merchant Order No)'
				},
					{
						field : 'bankNum',
						title : '银行卡号(Bank Acc No)'
					},
					{
					field : 'rechargeAmount',
					title : '充值金额(Deposit Amount)'
				}, {
					field : 'actualPayAmount',
					title : '实际到账金额(Real Amount)'
				},{
					field : 'serviceChange',
					title : '手续费(Fee)'
				}, {
					field : 'cardTotalView',
					title : '卡号结余(Bank Balance)'
				},
					{
						field : 'orderStateName',
						title : '订单状态(Status)'
					},
					{
						field: 'note',
						title: '备注(Note)'
					},
					{
						field : 'createTime',
						title : '提交时间(Creat Time)'
					},  {
						field : 'settlementTime',
						title : '结束时间(Process Time)'
					},{
						title : '操作(Operation)',
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




	}
});