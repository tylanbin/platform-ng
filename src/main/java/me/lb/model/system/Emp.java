package me.lb.model.system;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.map.annotate.JsonFilter;

@Entity
@Table(name = "ng_sys_emp")
// 加入该注解，动态过滤属性
@JsonFilter("me.lb.model.system.Emp")
public class Emp implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = 7465772304297370307L;
	private Integer id;
	private Org org;
	private String name;
	private String gender;
	private String job;
	private String education;
	private Date birthday;
	private String contact;
	private String idCard;
	private String email;
	private Integer isOnJob;
	private Date dateOfEntry;
	private Date dateOfConfirm;
	private Date dateOfLeave;
	private Set<User> users = new HashSet<User>(0);

	// Constructors

	/** default constructor */
	public Emp() {
	}

	/** full constructor */
	public Emp(Org org, String name, String gender, String job,
			String education, Date birthday, String contact, String idCard,
			String email, Integer isOnJob, Date dateOfEntry,
			Date dateOfConfirm, Date dateOfLeave, Set<User> users) {
		this.org = org;
		this.name = name;
		this.gender = gender;
		this.job = job;
		this.education = education;
		this.birthday = birthday;
		this.contact = contact;
		this.idCard = idCard;
		this.email = email;
		this.isOnJob = isOnJob;
		this.dateOfEntry = dateOfEntry;
		this.dateOfConfirm = dateOfConfirm;
		this.dateOfLeave = dateOfLeave;
		this.users = users;
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

	@Column(name = "gender")
	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Column(name = "job")
	public String getJob() {
		return this.job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	@Column(name = "education")
	public String getEducation() {
		return this.education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "birthday", length = 10)
	public Date getBirthday() {
		return this.birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	@Column(name = "contact")
	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Column(name = "idCard")
	public String getIdCard() {
		return this.idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	@Column(name = "email")
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "isOnJob")
	public Integer getIsOnJob() {
		return this.isOnJob;
	}

	public void setIsOnJob(Integer isOnJob) {
		this.isOnJob = isOnJob;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dateOfEntry", length = 10)
	public Date getDateOfEntry() {
		return this.dateOfEntry;
	}

	public void setDateOfEntry(Date dateOfEntry) {
		this.dateOfEntry = dateOfEntry;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dateOfConfirm", length = 10)
	public Date getDateOfConfirm() {
		return this.dateOfConfirm;
	}

	public void setDateOfConfirm(Date dateOfConfirm) {
		this.dateOfConfirm = dateOfConfirm;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dateOfLeave", length = 10)
	public Date getDateOfLeave() {
		return this.dateOfLeave;
	}

	public void setDateOfLeave(Date dateOfLeave) {
		this.dateOfLeave = dateOfLeave;
	}

	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "emp")
	public Set<User> getUsers() {
		return this.users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

}