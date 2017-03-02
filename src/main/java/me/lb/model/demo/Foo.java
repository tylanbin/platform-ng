package me.lb.model.demo;

import java.io.Serializable;
import java.util.Date;

import me.lb.support.system.annotation.MetaData;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;

// 加入该注解，动态过滤属性
@JsonFilter("me.lb.model.demo.Foo")
public class Foo implements Serializable {

	private static final long serialVersionUID = 4621874359487819394L;
	private int id;
	@MetaData(chsName = "整型")
	private int col1;
	@MetaData(chsName = "字符串")
	private String col2;
	@MetaData(chsName = "小数")
	private double col3;
	// Date类型的属性需要添加@JsonFormat注解
	@MetaData(chsName = "日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date col4;
	@MetaData(chsName = "日期时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date col5;
	@MetaData(chsName = "文本")
	private String col6;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCol1() {
		return col1;
	}

	public void setCol1(int col1) {
		this.col1 = col1;
	}

	public String getCol2() {
		return col2;
	}

	public void setCol2(String col2) {
		this.col2 = col2;
	}

	public double getCol3() {
		return col3;
	}

	public void setCol3(double col3) {
		this.col3 = col3;
	}

	public Date getCol4() {
		return col4;
	}

	public void setCol4(Date col4) {
		this.col4 = col4;
	}

	public Date getCol5() {
		return col5;
	}

	public void setCol5(Date col5) {
		this.col5 = col5;
	}

	public String getCol6() {
		return col6;
	}

	public void setCol6(String col6) {
		this.col6 = col6;
	}

}