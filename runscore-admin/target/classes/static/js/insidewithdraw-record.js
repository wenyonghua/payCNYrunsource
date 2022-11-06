var withdrawRecordVM = new Vue({
	el : '#withdraw-record',
	data : {
		orderNo : '',
		state : '',
		withdrawRecordStateDictItems : [],
		submitStartTime : '',
		submitEndTime : '',
		withdrawApprovalFlag : false,
		onlyShowNotApprovedBtnFlag : false,
		selectedWithdrawRecord : {},
		note : '',
		currentTable: null,
		checkbox: false,
		serviceCharge : '',//手续费
		shoukuanNumber : '',//收款卡号
		withdrawAmount : '',//提现金额
		accountHolder : '',//开户人姓名
		openAccountBank : '',//银行名称
		bankCardAccount : '',//银行账号
		addMerchantFlag : false,
		editMerchant : {},
		userName : {},
	},
	computed : {},
	created : function() {
	},
	/**
	 * 界面选中选中或者不选中数据
	 */
	watch: {
		checkbox: function(val, oldVal) {
			console.log(val);
			//alert("进入这个方法");
			let that = this;
			if(val) {
				that.timer = setInterval(() => {
					that.currentTable && $('.withdraw-record-table').bootstrapTable('refresh');//60秒刷新表格数据
				}, 60000);//60秒刷新
			} else {
				clearTimeout(that.timer);
			}
		}
	},

	mounted : function() {
		var state = getQueryString('state');
		if (state == '1') {
			this.state = state;
			this.submitStartTime = '';
			this.submitEndTime = '';
		}
		var todoId = getQueryString('todoId');
		if (todoId != null) {
			this.showWithdrawApprovalModal(todoId);
		}
		this.loadWithdrawRecordStateDictItem();
		this.initTable();
	},
	methods : {

		/**
		 * 加载提现记录状态字典项
		 */
		loadWithdrawRecordStateDictItem : function() {
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {
				params : {
					dictTypeCode : 'withdrawRecordState'
				}
			}).then(function(res) {
				this.withdrawRecordStateDictItems = res.body.data;
			});
		},

		initTable : function() {
			var that = this;
			this.currentTable =$('.withdraw-record-table').bootstrapTable({
				classes : 'table table-hover',
				url : '/insidewithdraw/insidewRecordByPage',
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
						state : that.state,
						submitStartTime : that.submitStartTime,
						submitEndTime : that.submitEndTime
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
					var html = template('withdraw-record-detail', {
						withdrawRecord : row
					});
					return html;
				},
				columns : [ {
					field : 'orderNo',
					title : '订单号(Oder No)'
				}, {
					field : 'userName',
					title : '发起用户(Transfer Type)'
				}, {
					field : 'bankCardAccount',
					title : '银行卡号(Bank Account No)'
				},
					{
						field : 'openAccountBank',
						title : '银行名称(Bank Name)'
					},
					{
						field : 'accountHolder',
						title : '卡姓名(Account Name)'
					},
					{
					field : 'withdrawAmount',
					title : '提现金额(Amount)'
				}, {
					field : 'withdrawWayName',
					title : '提现方式(Type)'
				}, {
					field : 'submitTime',
					title : '提交时间(Creat Time)'
				}, {
					field : 'stateName',
					title : '状态(Status)'
				}, {
					title : '操作(Operation)',
					formatter : function(value, row, index) {
						if (row.state == '1') {
							return [ '<button type="button" class="withdraw-approval-btn btn btn-outline-primary btn-sm" style="margin-right: 4px;">进行审核(Verify)</button>' ].join('');
						} else if (row.state == '2') {
							return [ '<button type="button" class="confirm-credited-btn btn btn-outline-primary btn-sm" style="margin-right: 4px;">确认到帐(Confirm Trans)</button>', '<button type="button" class="not-approved-btn btn btn-outline-secondary btn-sm">审核不通过</button>' ].join('');
						}
					},
					events : {
						'click .withdraw-approval-btn' : function(event, value, row, index) {
							that.onlyShowNotApprovedBtnFlag = false;
							that.showWithdrawApprovalModal(row.id);
						},
						'click .confirm-credited-btn' : function(event, value, row, index) {
							that.selectedWithdrawRecord = row;
							that.confirmCredited();
						},
						'click .not-approved-btn' : function(event, value, row, index) {
							that.onlyShowNotApprovedBtnFlag = true;
							that.showWithdrawApprovalModal(row.id);
						}
					}
				} ]
			});
		},

		refreshTable : function() {
			$('.withdraw-record-table').bootstrapTable('refreshOptions', {
				pageNumber : 1
			});
		},

		showWithdrawApprovalModal : function(id) {
			var that = this;
			that.$http.get('/insidewithdraw/findWithdrawRecordById', {
				params : {
					id : id
				}
			}).then(function(res) {
				that.selectedWithdrawRecord = res.body.data;
				that.note = null;
				that.withdrawApprovalFlag = true;
			});

		},

		approved : function() {
			var that = this;
			layer.confirm('确定审核通过吗?', {
				icon : 7,
				title : '提示'
			}, function(index) {
				layer.close(index);
				that.$http.get('/insidewithdraw/approved', {
					params : {
						id : that.selectedWithdrawRecord.id,
						note : that.note,//备注说明
						serviceCharge : that.serviceCharge,//手续费
						shoukuanNumber : that.shoukuanNumber,//卡号
						withdrawAmount : that.withdrawAmount//金额
					}
				}).then(function(res) {
					layer.alert('操作成功!', {
						icon : 1,
						time : 3000,
						shade : false
					});
					that.withdrawApprovalFlag = false;
					that.refreshTable();
				});
			});
		},

		notApproved : function() {
			var that = this;
			layer.confirm('确定审核不通过吗?', {
				icon : 7,
				title : '提示'
			}, function(index) {
				layer.close(index);
				that.$http.get('/insidewithdraw/notApproved', {
					params : {
						id : that.selectedWithdrawRecord.id,
						note : that.note
					}
				}).then(function(res) {
					layer.alert('操作成功!', {
						icon : 1,
						time : 3000,
						shade : false
					});
					that.withdrawApprovalFlag = false;
					that.refreshTable();
				});
			});
		},

		showAddMerchantModal : function() {
			this.addMerchantFlag = true;
			this.editMerchant = {
				userName : '',
				loginPwd : '',
				state : '',
				merchantNum : '',
				merchantName : '',
				secretKey : '',
				notifyUrl : '',
				returnUrl : '',
				ipWhitelist:''
			}
		},


		/**
		 * 确定添加内部转账
		 */
		saveInsideWithdraw : function() {
			var that = this;
			layer.confirm('确定添加内部转账(Are You Sure Want Creat Inside Transfer)', {
				icon : 7,
				title : '提示'
			}, function(index) {
				layer.close(index);
				that.$http.get('/insidewithdraw/saveInsideWithdraw', {
					params : {
						openAccountBank : that.openAccountBank,//银行名称
						accountHolder : that.accountHolder,//开户人姓名
						bankCardAccount : that.bankCardAccount,//银行卡号
						userName : that.userName,
						note : that.note,//备注说明
						serviceCharge : that.serviceCharge,//手续费
						shoukuanNumber : that.shoukuanNumber,//卡号
						withdrawAmount : that.withdrawAmount//金额
					}
				}).then(function(res) {
					layer.alert('操作成功!', {
						icon : 1,
						time : 3000,
						shade : false
					});
					that.addMerchantFlag = false;
					that.refreshTable();
				});
			});
		},

		confirmCredited : function(id) {
			var that = this;
			layer.confirm('确认是已到帐了吗?', {
				icon : 7,
				title : '提示'
			}, function(index) {
				layer.close(index);
				that.$http.get('/insidewithdraw/confirmCredited', {
					params : {
						id : that.selectedWithdrawRecord.id,
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
		}
	}
});