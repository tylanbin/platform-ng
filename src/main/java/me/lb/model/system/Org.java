package me.lb.model.system;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;

// 加入该注解，动态过滤属性
@JsonFilter("me.lb.model.system.Org")
public class Org implements Serializable {

	private static final long serialVersionUID = -6152120454402940738L;
	private int id;
	private Integer parentId;
	private String name;
	private String serialNum;
	private String workPlace;
	private String contact;
	private String leader;

	// 构造EasyUI的树
	private List<Org> children;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public String getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(String workPlace) {
		this.workPlace = workPlace;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getLeader() {
		return leader;
	}

	public void setLeader(String leader) {
		this.leader = leader;
	}

	public List<Org> getChildren() {
		return children;
	}

	public void setChildren(List<Org> children) {
		this.children = children;
	}

	// 欺骗EasyUI的操作
	public String getText() {
		return this.name;
	}

}