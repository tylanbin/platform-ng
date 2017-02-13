package me.lb.model.system;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;

@Entity
@Table(name = "ng_sys_perm")
// 加入该注解，动态过滤属性
@JsonFilter("me.lb.model.system.Perm")
public class Perm implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = 6009162980756042884L;
	private Integer id;
	private Perm perm;
	private String name;
	private String token;
	private String url;
	private Set<Role> roles = new HashSet<Role>(0);
	private Set<Perm> perms = new HashSet<Perm>(0);

	// Constructors

	/** default constructor */
	public Perm() {
	}

	/** minimal constructor */
	public Perm(String name, String token, String url) {
		this.name = name;
		this.token = token;
		this.url = url;
	}

	/** full constructor */
	public Perm(Perm perm, String name, String token, String url,
			Set<Role> roles, Set<Perm> perms) {
		this.perm = perm;
		this.name = name;
		this.token = token;
		this.url = url;
		this.roles = roles;
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
	@JoinColumn(name = "parentId")
	public Perm getPerm() {
		return this.perm;
	}

	public void setPerm(Perm perm) {
		this.perm = perm;
	}

	@Column(name = "name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "token")
	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Column(name = "url")
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "perms")
	public Set<Role> getRoles() {
		Set<Role> set = new TreeSet<Role>(new Comparator<Role>() {
			@Override
			public int compare(Role o1, Role o2) {
				return o1.getId() - o2.getId();
			}
		});
		set.addAll(this.roles);
		return set;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "perm")
	public Set<Perm> getPerms() {
		Set<Perm> set = new TreeSet<Perm>(new Comparator<Perm>() {
			@Override
			public int compare(Perm o1, Perm o2) {
				return o1.getId() - o2.getId();
			}
		});
		set.addAll(this.perms);
		return set;
	}

	public void setPerms(Set<Perm> perms) {
		this.perms = perms;
	}

	// 欺骗EasyUI的操作
	@Transient
	public String getText() {
		return this.name;
	}

	// 欺骗EasyUI的操作
	@Transient
	public Set<Perm> getChildren() {
		return this.perms;
	}

}