package me.lb.model.demo;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import me.lb.support.system.annotation.MetaData;

import org.codehaus.jackson.map.annotate.JsonFilter;

@Entity
@Table(name = "ng_demo_foo", catalog = "platform_ng")
// 加入该注解，动态过滤属性
@JsonFilter("me.lb.model.demo.Foo")
public class Foo implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = -1838970134469714230L;
	private Integer id;
	@MetaData(chsName = "整型")
	private Integer col1;
	@MetaData(chsName = "字符串")
	private String col2;
	@MetaData(chsName = "小数")
	private Double col3;
	@MetaData(chsName = "日期")
	private Date col4;
	@MetaData(chsName = "日期时间")
	private Timestamp col5;
	@MetaData(chsName = "文本")
	private String col6;

	// Constructors

	/** default constructor */
	public Foo() {
	}

	/** full constructor */
	public Foo(Integer col1, String col2, Double col3, Date col4,
			Timestamp col5, String col6) {
		this.col1 = col1;
		this.col2 = col2;
		this.col3 = col3;
		this.col4 = col4;
		this.col5 = col5;
		this.col6 = col6;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "id")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "col1")
	public Integer getCol1() {
		return this.col1;
	}

	public void setCol1(Integer col1) {
		this.col1 = col1;
	}

	@Column(name = "col2")
	public String getCol2() {
		return this.col2;
	}

	public void setCol2(String col2) {
		this.col2 = col2;
	}

	@Column(name = "col3")
	public Double getCol3() {
		return this.col3;
	}

	public void setCol3(Double col3) {
		this.col3 = col3;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "col4", length = 10)
	public Date getCol4() {
		return this.col4;
	}

	public void setCol4(Date col4) {
		this.col4 = col4;
	}

	@Column(name = "col5", length = 19)
	public Timestamp getCol5() {
		return this.col5;
	}

	public void setCol5(Timestamp col5) {
		this.col5 = col5;
	}

	@Column(name = "col6", length = 65535)
	public String getCol6() {
		return this.col6;
	}

	public void setCol6(String col6) {
		this.col6 = col6;
	}

}