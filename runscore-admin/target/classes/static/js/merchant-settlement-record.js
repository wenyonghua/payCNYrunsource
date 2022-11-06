var withdrawRecordVM = new Vue({
	el : '#withdraw-record',
	data : {
		orderNo : '',
		merchantId : '',
		merchants : [],
		state : '',
		merchantSettlementStateDictItems : [],//状态
		applyStartTime : '',   //dayjs().format('YYYY-MM-DD'),
		applyEndTime : '',//dayjs().format('YYYY-MM-DD'),

		approvalFlag : false,
		onlyShowNotApprovedBtnFlag : false,
		selectedRecord : {},
		currentTable: null,
		checkbox: false,
		// note : '',
		merchantBankCards : [],//银行卡列表数据
		submitInfo: [{
			amount: '',//卡余额
			note : '',
			payCardNo : '',//付款银行卡号
			serverCharge : '',//付款手续费
			payAmount : '',//付款金额
		}]
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
			let that = this;
			if(val) {
				that.timer = setInterval(() => {
					that.currentTable && $('.merchant-settlement-record-table').bootstrapTable('refresh');//60秒刷新表格数据
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
			this.applyStartTime = '';
			this.applyEndTime = '';
		}
		var todoId = getQueryString('todoId');
		if (todoId != null) {
			this.showApprovalModal(todoId);
		}

		this.loadAllMerchant();
		this.loadMerchantSettlementStateDictItem();
		this.initTable();
		this.loadBankCardList();//加载银行卡列表数据

	},
	methods : {

		/**
		 * 加载银行卡列表数据
		 */
		loadBankCardList : function() {
			var that = this;
			// that.$http.get('/merchant/findMerchantBankCardByMerchantId').then(function(res) {
			// 	that.merchantBankCards = res.body.data;
			// });
			that.$http.get('/gatheringCode/getUseBankList', {
				// params : {
				// 	dictTypeCode : 'merchantOrderState'
				// }
			}).then(function(res) {
				this.merchantBankCards = res.body.data;//获取银行卡列表数据

				that.$nextTick(function() {
					$('.selectpicker').selectpicker('refresh');
				});
				$('.selectpicker').on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {
					console.log($('.selectpicker').val());
					that.payCardNo = $('.selectpicker').val();
				});

			});
		},

		addReceiveOrderChannel: function() {
			this.submitInfo.push({
				amount: '',//银行卡额度
				note : '',
				payCardNo : '',
				serverCharge : '',
			})
		},

		loadAllMerchant : function() {
			var that = this;
			that.$http.get('/merchant/findAllMerchant').then(function(res) {
				this.merchants = res.body.data;
			});
		},

		loadMerchantSettlementStateDictItem : function() {
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {
				params : {
					dictTypeCode : 'merchantSettlementState'
				}
			}).then(function(res) {
				this.merchantSettlementStateDictItems = res.body.data;
			});
		},

		initTable : function() {
			var that = this;
			this.currentTable =	$('.merchant-settlement-record-table').bootstrapTable({
				classes : 'table table-hover',
				url : '/merchant/findMerchantSettlementRecordByPage',
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
						merchantId : that.merchantId,
						state : that.state,
						applyStartTime : that.applyStartTime,
						applyEndTime : that.applyEndTime
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
					var html = template('record-detail', {
						record : row
					});
					return html;
				},
				columns : [ {
					field : 'orderNo',
					title : '订单号(Order No)'
				}, {
					field : 'merchantName',
					title : '商户(Merchant)'
				}, {
					field : 'withdrawAmount',
					title : '提现金额(Amount)'
				},
					{
						field : 'serverCharge',
						title : '提现手续费(Fee)'
					},
					{
					title : '结算银行卡(Receiver Account)',
					formatter : function(value, row, index) {
						return row.openAccountBank + '/' + row.accountHolder + '/' + row.bankCardAccount;
					}
				},
					{
						field : 'payCardNo',
						title : '付款银行卡(Payout Account)'
					},
					{
					field : 'stateName',
					title : '状态(Status))'
				}, {
					field : 'applyTime',
					title : '申请时间(Submit Time)'
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
							that.showApprovalModal(row.id);
						},
						'click .confirm-credited-btn' : function(event, value, row, index) {
							that.confirmCredited(row.id);
						},
						'click .not-approved-btn' : function(event, value, row, index) {
							that.onlyShowNotApprovedBtnFlag = true;
							that.showApprovalModal(row.id);
						}
					}
				} ]
			});
		},

		refreshTable : function() {
			$('.merchant-settlement-record-table').bootstrapTable('refreshOptions', {
				pageNumber : 1
			});
		},

		showApprovalModal : function(id) {
			var that = this;
			that.$http.get('/merchant/findByMerchantSettlementRecordId', {
				params : {
					id : id
				}
			}).then(function(res) {
				that.selectedRecord = res.body.data;
				that.note = null;
				that.approvalFlag = true;
			});

		},


		// 获取余额
		getBankBalance: function(index) {
			var that = this;
			var bankCard = that.submitInfo[index].payCardNo;
			console.log("银行卡号="+bankCard);
			that.$http.get('/gatheringCode/getBankBalance', {
				params: {
					bankCard:bankCard
				}
			}).then(function(res) {
				console.log(res.body.data)
				if(res.body.data==null){
					alert("请输入正确的银行卡号(Please enter the correct bank card number)");
					that.submitInfo[index].amount="请输入正确的银行卡号(Please enter the correct bank card number)";
				}else {
					that.submitInfo[index].amount = res.body.data.bankTotalAmountView;
				}
			});
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
				that.$http.post('/merchant/settlementApproved', {
						id : that.selectedRecord.id,
						bankList: that.submitInfo
				}).then(function(res) {
					layer.alert('操作成功!', {
						icon : 1,
						time : 3000,
						shade : false
					});
					that.approvalFlag = false;
					that.submitInfo = [];//清除数据
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
				that.$http.get('/merchant/settlementNotApproved', {
					params : {
						id : that.selectedRecord.id,
						note : that.note
					}
				}).then(function(res) {
					layer.alert('操作成功!', {
						icon : 1,
						time : 3000,
						shade : false
					});
					that.approvalFlag = false;
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
				that.$http.get('/merchant/settlementConfirmCredited', {
					params : {
						id : id,
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