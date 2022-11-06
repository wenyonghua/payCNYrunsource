package me.zohar.runscore.admin;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.Data;

import java.util.Date;

@Data
@ExcelTarget("user")
public class User {
    @Excel(name = "编号")
    private String id;
    @Excel(name = "姓名", width = 30)
    private String name;
    @Excel(name = "生日",format = "yyyy年MM月dd日", width = 20)
    private Date bir;
    @Excel(name = "爱好", width = 30)
    private String habby;
    @Excel(name = "身份证号", width = 30)
    private String num;
    @Excel(name = "家庭住址", width = 20)
    private String address;
    //savaPath 图片储存的地址  type = 2 表示导入的问图片类型     1 为文本
    @Excel(name = "照片",type = 2, savePath = "D:\\mine\\easypoi_springboot\\src\\main\\resources\\static\\image")
    private String photo;
}
