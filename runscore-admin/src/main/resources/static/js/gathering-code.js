var gatheringCodeVM = new Vue({
	el : '#gathering-code',
	data : {
		global : GLOBAL,
		state : '',
		gatheringCodeStateDictItems : [],
		gatheringChannelId : '',
		gatheringChannelDictItems : [],
		payee : '',
		userName : '',
		channelCode :'',
		addOrUpdateGatheringCodeFlag : false,
		gatheringCodeActionTitle : '',
		editGatheringCode : {},
		bankCode : '',//银行卡号
		approvalFlag : false,
		bankTotalAmount : '',//银行卡总余额
		bankReflect: '',//银行卡提现限制
		gatheringCodeUseStateDictItems: [],//卡使用状态数据
		cardUseName : '',//卡用途
		cardUse : '',//卡用途枚举
		gatheringCodeState:[],//卡状态列表
		inUse:'',
		inUseName:'',
		bankAddress:'',//银行名称
		bankTotalAmountView :'',
		titleBankCode :'',
		gatheringQixianState:[],//1:永久使用,0:永久停止
		qiXianUser:'',
		qiXianUserName:'',

		viewGatheringCodeFlag : false,
		viewGatheringCodeUrl : '',

	},
	computed : {},
	created : function() {
	},
	mounted : function() {
		var that = this;
		var state = getQueryString('state');
		if (state == '2') {
			that.state = state;
		}
		var todoId = getQueryString('todoId');
		if (todoId != null) {
			that.showAuditGatheringCodeModal(todoId);
		}

		that.loadGatheringCodeStateDictItem();
		that.loadGatheringChannelDictItem();////查看渠道列表 银行卡,支付宝，微信
		that.initTable();
		that.loadGatheringCodeCardUseItem();//查看卡使用用途
		that.loadGatheringCodeStatus();
		that.loadGatheringCodeBankQixianStatus();//查看银行银行卡期限1:永久使用,0:永久停止


		$('.gathering-code-pic').on('fileuploaded', function(event, data, previewId, index) {
			//alert(data.response.data.join(',')+"返回图片ID号");
			that.editGatheringCode.storageId = data.response.data.join(',');
			that.addOrUpdateGatheringCodeInner();//添加或者更新数据
		});
	},
	methods : {

		loadGatheringCodeStateDictItem : function() {
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {//查看收款码状态
				params : {
					dictTypeCode : 'gatheringCodeState'
				}
			}).then(function(res) {
				this.gatheringCodeStateDictItems = res.body.data;
			});
		},

		loadGatheringCodeCardUseItem : function() {//查看卡使用用途列表数据 存款卡,备用金卡，付款卡
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {//存款卡,备用金卡，付款卡
				params : {
					dictTypeCode : 'cardUseState'
				}
			}).then(function(res) {
				this.gatheringCodeUseStateDictItems = res.body.data;
			});
		},
		loadGatheringCodeStatus : function() {//银行卡状态  1使用,0停用
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {//查看卡使用状态列表数据
				params : {
					dictTypeCode : 'cardState'
				}
			}).then(function(res) {
				this.gatheringCodeState = res.body.data;
			});
		},
		loadGatheringCodeBankQixianStatus : function() {//查看银行卡期限  1永久使用,0永久停用
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {//查看银行卡期限  1:永久使用,0:永久停止
				params : {
					dictTypeCode : 'cardQixianState'
				}
			}).then(function(res) {
				this.gatheringQixianState = res.body.data;
			});
		},

		/**
		 * 查看通道 支付宝，银行卡，微信
		 */
		loadGatheringChannelDictItem : function() {
			var that = this;
			that.$http.get('/gatheringChannel/findAllGatheringChannel').then(function(res) {
				that.gatheringChannelDictItems = res.body.data;
			});
		},

		initTable : function() {
			var that = this;
			$('.gathering-code-table').bootstrapTable({
				classes : 'table table-hover',
				url : '/gatheringCode/findGatheringCodeByPage',////查询银行卡列表数据
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
						state : that.state,
						gatheringChannelId : that.gatheringChannelId,
						payee : that.payee,
						userName : that.userName,//所属账号
						bankCode : that.bankCode,
						cardUse : that.cardUse,
						inUse : that.inUse,
						inUseName : that.inUseName,
						qiXianUser:that.qiXianUser,//期限使用

					};
					return condParam;
				},
				responseHandler : function(res) {
					return {
						total : res.data.total,
						rows : res.data.content
					};
				},
				columns : [
					{
						field : 'inUseName',
						title : '卡状态'
					},
					{
						field : 'qiXianUserName',
						title : '卡使用期限'
					},
					{
						title : '通道(Chanel)',
						formatter : function(value, row, index, field) {
							// var text = '';
							// if (row.fixedGatheringAmount) {
							// 	text = row.gatheringAmount;
							// } else {
							// 	text = '不固定'
							// }
							return row.gatheringChannelName;
						}
					},
					{
						title : '收款人(Account Name)',
						formatter : function(value, row, index, field) {
							return row.payee;
						}
					},
					{
						title : '所属账号(Agent)',
						formatter : function(value, row, index, field) {
							return row.userName;
						}
					},
					{
						field : 'bankCode',
						title : '银行卡号(Account No)'
					},
					{
						field: 'bankAddress',
						title: '银行名称(Bank Name)'
					},
					{
						field : 'cardUseName',
						title : '卡用途(Account usage)'
					},
					{
						field : 'bankTotalAmountView',
						title : '银行卡总余额(Bank Balance)'
					},
					{
						field : 'bankReflect',
						title : '银行卡提现额度限制'
					},
					{
					field : 'createTime',
					title : '创建时间'
				    },
					{
						field : 'dailyQuota',
						title : '每日限额(daily limit)'
					},
					{
						title : '今日收款金额/收款次数/接单次数/成功率',
						formatter : function(value, row, index, field) {
							return row.todayTradeAmount + that.global.systemSetting.currencyUnit + '/' + row.todayPaidOrderNum + '次' + '/' + row.todayOrderNum + '次' + '/' + row.todaySuccessRate + '%';
						}
					},

					{
					title : '操作',
						formatter : function(value, row, index) {
							var btns = [ '<button type="button" class="view-gathering-code-btn btn btn-outline-secondary btn-sm" style="margin-right: 4px;">查看收款码</button>', '<button type="button" class="edit-gathering-code-btn btn btn-outline-success btn-sm" style="margin-right: 4px;">编辑</button>', '<button type="button" class="del-gathering-code-btn btn btn-outline-danger btn-sm">删除</button>' ];
							if (row.state == '2') {
								btns.unshift('<button type="button" class="audit-gathering-code-btn btn btn-outline-info btn-sm" style="margin-right: 4px;">审核</button>');
							}
							if(row.inUse==0){//银行卡状态停用状态
								btns.unshift('<button type="button" class="updateUse-gathering-code-btn btn btn-outline-info btn-sm" style="margin-right: 4px;">使用银行卡</button>');
							}
							if(row.inUse==1){//使用中
								btns.unshift('<button type="button" class="updateStop-gathering-code-btn btn btn-outline-info btn-sm" style="margin-right: 4px;">停用银行卡</button>');
							}
							return btns.join('');
						},
						events : {
							'click .audit-gathering-code-btn' : function(event, value, row, index) {
								that.showAuditGatheringCodeModal(row.id);
							},
							'click .view-gathering-code-btn' : function(event, value, row, index) {
								if(row.gatheringChannelId=='1149365394574671872'){//银行卡查询
									that.viewBankInfo(row);
								}else{
									that.viewImage('/storage/fetch/' + row.storageId);
								}

							},
							'click .edit-gathering-code-btn' : function(event, value, row, index) {
								that.openEditGatheringCodeModal(row.id);//编辑
							},
							'click .del-gathering-code-btn' : function(event, value, row, index) {
								that.delGatheringCode(row.id);//删除数据
							},
							'click .updateUse-gathering-code-btn' : function(event, value, row, index) {
								that.updateUse(row.id);//使用银行卡
							},
							'click .updateStop-gathering-code-btn' : function(event, value, row, index) {
								that.updateStopInuse(row.id);//停用银行卡
							},
						}
				}

				]
			});
		},

		refreshTable : function() {
			$('.gathering-code-table').bootstrapTable('refreshOptions', {
				pageNumber : 1
			});
		},

		//添加图片
		initFileUploadWidget : function(storageId) {
			var initialPreview = [];
			var initialPreviewConfig = [];
			if (storageId != null) {
				initialPreview.push('/storage/fetch/' + storageId);
				initialPreviewConfig.push({
					downloadUrl : '/storage/fetch/' + storageId
				});
			}
			$('.gathering-code-pic').fileinput('destroy').fileinput({
				browseOnZoneClick : true,
				showBrowse : false,
				showCaption : false,
				showClose : true,
				showRemove : false,
				showUpload : false,
				dropZoneTitle : '点击选择图片',
				dropZoneClickTitle : '',
				layoutTemplates : {
					footer : ''
				},
				maxFileCount : 1,
				uploadUrl : '/storage/uploadPic',
				enctype : 'multipart/form-data',
				allowedFileExtensions : [ 'jpg', 'png', 'bmp', 'jpeg' ],
				initialPreview : initialPreview,
				initialPreviewAsData : true,
				initialPreviewConfig : initialPreviewConfig
			});
		},

		openViewGatheringCodeModal : function(gatheringCode) {
			this.viewGatheringCodeFlag = true;
			this.viewGatheringCodeUrl = '/storage/fetch/' + gatheringCode.storageId;
		},

		switchGatheringAmountMode : function() {
			if (!this.editGatheringCode.fixedGatheringAmount) {
				this.editGatheringCode.gatheringAmount = '';
			}
		},

		openAddGatheringCodeModal : function() {
			this.addOrUpdateGatheringCodeFlag = true;
			this.gatheringCodeActionTitle = '新增收款码';
			this.editGatheringCode = {
				userName : '',
				gatheringChannelCode : '',
				state : '',
				fixedGatheringAmount : true,
				gatheringAmount : '',
				payee : ''
			};
			this.initFileUploadWidget();//添加图片
		},

		openEditGatheringCodeModal : function(gatheringCodeId) {
			var that = this;
			that.$http.get('/gatheringCode/findGatheringCodeById', {
				params : {
					id : gatheringCodeId,
				}
			}).then(function(res) {
				that.addOrUpdateGatheringCodeFlag = true;
				that.gatheringCodeActionTitle = '编辑收款码';
				that.editGatheringCode = res.body.data;
				that.initFileUploadWidget(res.body.data.storageId);
			});
		},

		addOrUpdateGatheringCode : function() {//添加或者修改
			var that = this;
			var editGatheringCode = that.editGatheringCode;
			if (editGatheringCode.userName == null || editGatheringCode.userName == '') {
				layer.alert('请输入所属账号', {
					title : '提示',
					icon : 7,
					time : 3000
				});
				return;
			}
			if (editGatheringCode.gatheringChannelId == null || editGatheringCode.gatheringChannelId == '') {
				layer.alert('请选择收款渠道', {
					title : '提示',
					icon : 7,
					time : 3000
				});
				return;
			}
			// if (editGatheringCode.state == null || editGatheringCode.state == '') {
			// 	layer.alert('请选择状态', {
			// 		title : '提示',
			// 		icon : 7,
			// 		time : 3000
			// 	});
			// 	return;
			// }
			// if (editGatheringCode.fixedGatheringAmount == null) {
			// 	layer.alert('请选择是否固定收款金额', {
			// 		title : '提示',
			// 		icon : 7,
			// 		time : 3000
			// 	});
			// 	return;
			// }
			// if (editGatheringCode.fixedGatheringAmount) {
			// 	if (editGatheringCode.gatheringAmount == null || editGatheringCode.gatheringAmount == '') {
			// 		layer.alert('请输入收款金额', {
			// 			title : '提示',
			// 			icon : 7,
			// 			time : 3000
			// 		});
			// 		return;
			// 	}
			// }
			if (editGatheringCode.payee == null || editGatheringCode.payee == '') {
				layer.alert('请选择收款人', {
					title : '提示',
					icon : 7,
					time : 3000
				});
				return;
			}
			if(editGatheringCode.gatheringChannelId =='1149365394574671872'){//银行卡就可以不用添加图片
				if ($('.gathering-code-pic').fileinput('getPreview').content.length == 0) {
					that.addOrUpdateGatheringCodeInner();
					$('.gathering-code-pic').fileinput('upload');//上转图片
				}else{
				//	var filesCount = $('.gathering-code-pic').fileinput('getFilesCount');

				}
			}else{//如果是支付宝或者微信必须上传图片
				if ($('.gathering-code-pic').fileinput('getPreview').content.length != 0) {
				that.addOrUpdateGatheringCodeInner();
				} else {
					var filesCount = $('.gathering-code-pic').fileinput('getFilesCount');
					if (filesCount == 0) {
						layer.alert('请选择要上传的图片', {
							title : '提示',
							icon : 7,
							time : 3000
						});
						return;
					}
					$('.gathering-code-pic').fileinput('upload');
				}
			}

		},

		addOrUpdateGatheringCodeInner : function() {////添加或者修改
			var that = this;
			that.$http.post('/gatheringCode/addOrUpdateGatheringCode', that.editGatheringCode).then(function(res) {
				layer.alert('操作成功!', {
					icon : 1,
					time : 3000,
					shade : false
				});
				that.addOrUpdateGatheringCodeFlag = false;
				that.refreshTable();
			});
		},

		//删除银行卡
		deleteBank : function(gatheringCodeId,text){//确认支付方法
			var that = this;
			if (text == null || text == '') {
				alert('请输入银行卡信息');
				return;
			}

			that.$http.get('/gatheringCode/delGatheringCodeById', {
				params : {
					id : gatheringCodeId,
					titleBankCode : text,
				}
			}).then(function(res) {
				layer.alert('操作成功!', {
					icon : 1,
					time : 3000,
					shade : false
				});
				that.addOrUpdateGatheringCodeFlag = false;
				that.refreshTable();
			});
		},

		loadGatheringCodeBankQixianStatus : function() {//查看银行卡期限  1永久使用,0永久停用
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {//查看银行卡期限  1:永久使用,0:永久停止
				params : {
					dictTypeCode : 'cardQixianState'
				}
			}).then(function(res) {
				this.gatheringQixianState = res.body.data;
			});
		},


		//删除银行卡
		delGatheringCode : function(gatheringCodeId) {
			var that = this;
			layer.prompt({
				title : '请输入银行卡号和删除的这条记录的银行卡号必须匹配',
				formType : 2
			}, function(text, index) {
				layer.close(index);
				that.deleteBank(gatheringCodeId,text);//调用确认支付方法
			});
		},
		//删除银行卡
		deleteBank : function(gatheringCodeId,text){//确认支付方法
			var that = this;
			if (text == null || text == '') {
				alert('请输入银行卡信息');
				return;
			}

			that.$http.get('/gatheringCode/delGatheringCodeById', {
				params : {
					id : gatheringCodeId,
					titleBankCode : text,
				}
			}).then(function(res) {
				layer.alert('操作成功!', {
					icon : 1,
					time : 3000,
					shade : false
				});
				that.addOrUpdateGatheringCodeFlag = false;
				that.refreshTable();
			});
		},
		//启用收款码
		updateUse : function(id) {
			var that = this;
			layer.confirm('确定要启用吗?', {
				icon : 7,
				title : '提示'
			}, function(index) {
				layer.close(index);
				that.$http.get('/gatheringCode/updateUseInuse', {
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

		//停用收款码
		updateStopInuse : function(id) {
			var that = this;
			layer.confirm('确定要停用吗?', {
				icon : 7,
				title : '提示'
			}, function(index) {
				layer.close(index);
				that.$http.get('/gatheringCode/updateStopInuse', {
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
		 * 查看图片路径显示图片
		 * @param imagePath
		 */
		viewImage : function(imagePath) {
			console.log("图片路径="+imagePath)
			var image = new Image();
			image.src = imagePath;
			var viewer = new Viewer(image, {
				hidden : function() {
					viewer.destroy();
				},
			});
			viewer.show();
		},
		/**
		 * 银行卡显示数据
		 * @param row
		 */
		viewBankInfo(row){
			layer.open({
				title:'银行卡信息',
				type: 1,
				skin: 'layui-layer-demo', //样式类名
				closeBtn: 0, //不显示关闭按钮
				anim: 2,
				area: ['50%', '50%'],
				shadeClose: true, //开启遮罩关闭
				content: '<table class="table common-table" >\n' +
					'\t\t\t\t\t\t\t\t\t<thead>\n' +
					'\t\t\t\t\t\t\t\t\t<tr>\n' +
					'\t\t\t\t\t\t\t\t\t\t<th>银行卡号</th>\n' +
					'\t\t\t\t\t\t\t\t\t\t<th>银行开户行</th>\n' +
					'\t\t\t\t\t\t\t\t\t\t<th>卡户主</th>\n' +
					'\t\t\t\t\t\t\t\t\t</tr>\n' +
					'\t\t\t\t\t\t\t\t\t</thead>\n' +
					'\t\t\t\t\t\t\t\t\t<tbody>\n' +
					'\t\t\t\t\t\t\t\t\t<tr>\n' +
					'\t\t\t\t\t\t\t\t\t\t<td>'+row.bankCode+'</td>\n' +
					'\t\t\t\t\t\t\t\t\t\t<td>'+row.bankAddress+'</td>\n' +
					'\t\t\t\t\t\t\t\t\t\t<td>'+row.bankUsername+'</td>\n' +
					'\t\t\t\t\t\t\t\t\t</tr>\n' +
					'\t\t\t\t\t\t\t\t\t</tbody>\n' +
					'\t\t\t\t\t\t\t\t</table>'
			});
		},
	}
});