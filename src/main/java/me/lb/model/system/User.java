package me.lb.model.system;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFilter;

// 加入该注解，动态过滤属性
@JsonFilter("me.lb.model.system.User")
public class User implements Serializable {

	private static final long serialVersionUID = -4042382664204057699L;
	private int id;
	private Integer empId;
	private String loginName;
	private String loginPass;
	private int enabled;
	private Date createDate;
	private int loginRange;

	// 用于反序列化json，存储关联的角色信息
	private String roleIds;

	public User() {
	}

	public User(String loginName, String loginPass) {
		this.loginName = loginName;
		this.loginPass = loginPass;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getEmpId() {
		return empId;
	}

	public void setEmpId(Integer empId) {
		this.empId = empId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginPass() {
		return loginPass;
	}

	public void setLoginPass(String loginPass) {
		this.loginPass = loginPass;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getLoginRange() {
		return loginRange;
	}

	public void setLoginRange(int loginRange) {
		this.loginRange = loginRange;
	}

	public String getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}

}