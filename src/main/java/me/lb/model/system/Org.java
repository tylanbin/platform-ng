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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;

@Entity
@Table(name = "ng_sys_org")
// 加入该注解，动态过滤属性
@JsonFilter("me.lb.model.system.Org")
public class Org implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = 1229345759454896097L;
	private Integer id;
	private Org org;
	private String name;
	private String serialNum;
	private String workPlace;
	private String contact;
	private String leader;
	private Set<Emp> emps = new HashSet<Emp>(0);
	private Set<Role> roles = new HashSet<Role>(0);
	private Set<Org> orgs = new HashSet<Org>(0);

	// Constructors

	/** default constructor */
	public Org() {
	}

	/** full constructor */
	public Org(Org org, String name, String serialNum, String workPlace,
			String contact, String leader, Set<Emp> emps, Set<Role> roles,
			Set<Org> orgs) {
		this.org = org;
		this.name = name;
		this.serialNum = serialNum;
		this.workPlace = workPlace;
		this.contact = contact;
		this.leader = leader;
		this.emps = emps;
		this.roles = roles;
		this.orgs = orgs;
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

	@Column(name = "serialNum")
	public String getSerialNum() {
		return this.serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	@Column(name = "workPlace")
	public String getWorkPlace() {
		return this.workPlace;
	}

	public void setWorkPlace(String workPlace) {
		this.workPlace = workPlace;
	}

	@Column(name = "contact")
	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Column(name = "leader")
	public String getLeader() {
		return this.leader;
	}

	public void setLeader(String leader) {
		this.leader = leader;
	}

	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "org")
	public Set<Emp> getEmps() {
		Set<Emp> set = new TreeSet<Emp>(new Comparator<Emp>() {
			@Override
			public int compare(Emp o1, Emp o2) {
				return o1.getId() - o2.getId();
			}
		});
		set.addAll(this.emps);
		return set;
	}

	public void setEmps(Set<Emp> emps) {
		this.emps = emps;
	}

	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "org")
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

	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "org")
	public Set<Org> getOrgs() {
		Set<Org> set = new TreeSet<Org>(new Comparator<Org>() {
			@Override
			public int compare(Org o1, Org o2) {
				return o1.getId() - o2.getId();
			}
		});
		set.addAll(this.orgs);
		return set;
	}

	public void setOrgs(Set<Org> orgs) {
		this.orgs = orgs;
	}

	// 欺骗EasyUI的操作
	@Transient
	public String getText() {
		return this.name;
	}

	// 欺骗EasyUI的操作
	@Transient
	public Set<Org> getChildren() {
		return this.orgs;
	}

}