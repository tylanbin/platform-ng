package me.lb.model.system;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;

// 加入该注解，动态过滤属性
@JsonFilter("me.lb.model.system.Perm")
public class Perm implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private Integer parentId;
	private String name;
	private String token;
	private String url;

	// 构造EasyUI的树
	private List<Perm> children;

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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Perm> getChildren() {
		return children;
	}

	public void setChildren(List<Perm> children) {
		this.children = children;
	}

	// 欺骗EasyUI的操作
	public String getText() {
		return this.name;
	}

}