package me.lb.model.system;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.map.annotate.JsonFilter;

@Entity
@Table(name = "ng_sys_role")
// 加入该注解，动态过滤属性
@JsonFilter("me.lb.model.system.Role")
public class Role implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = -3764199839627824865L;
	private Integer id;
	private Org org;
	private String name;
	private String description;
	private Set<User> users = new HashSet<User>(0);
	private Set<Perm> perms = new HashSet<Perm>(0);

	// Constructors

	/** default constructor */
	public Role() {
	}

	/** minimal constructor */
	public Role(String name) {
		this.name = name;
	}

	/** full constructor */
	public Role(Org org, String name, String description, Set<User> users,
			Set<Perm> perms) {
		this.org = org;
		this.name = name;
		this.description = description;
		this.users = users;
		this.perms = perms;
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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "orgId")
	public Org getOrg() {
		return this.org;
	}

	public void setOrg(Org org) {
		this.org = org;
	}

	@Column(name = "name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "description")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "roles")
	public Set<User> getUsers() {
		return this.users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	@ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JoinTable(name = "ng_sys_role_perm", joinColumns = { @JoinColumn(name = "roleId") }, inverseJoinColumns = { @JoinColumn(name = "permId") })
	public Set<Perm> getPerms() {
		return this.perms;
	}

	public void setPerms(Set<Perm> perms) {
		this.perms = perms;
	}

}