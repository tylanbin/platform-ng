package me.lb.model.system;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFilter;

// 加入该注解，动态过滤属性
@JsonFilter("me.lb.model.system.Role")
public class Role implements Serializable {

	private static final long serialVersionUID = 7054859324355061454L;
	private int id;
	private Integer orgId;
	private String name;
	private String description;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		// 重写toString，为方便用户分配角色的信息显示
		return String.valueOf(id);
	}

}