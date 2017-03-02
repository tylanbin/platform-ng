package me.lb.model.system;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFilter;

// 加入该注解，动态过滤属性
@JsonFilter("me.lb.model.system.Emp")
public class Emp implements Serializable {

	private static final long serialVersionUID = -1277412995749122201L;
	private int id;
	private Integer orgId;
	private String name;
	private String gender;
	private String job;
	private String education;
	private Date birthday;
	private String contact;
	private String idCard;
	private String email;
	private int isOnJob;
	private Date dateOfEntry;
	private Date dateOfConfirm;
	private Date dateOfLeave;

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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getIsOnJob() {
		return isOnJob;
	}

	public void setIsOnJob(int isOnJob) {
		this.isOnJob = isOnJob;
	}

	public Date getDateOfEntry() {
		return dateOfEntry;
	}

	public void setDateOfEntry(Date dateOfEntry) {
		this.dateOfEntry = dateOfEntry;
	}

	public Date getDateOfConfirm() {
		return dateOfConfirm;
	}

	public void setDateOfConfirm(Date dateOfConfirm) {
		this.dateOfConfirm = dateOfConfirm;
	}

	public Date getDateOfLeave() {
		return dateOfLeave;
	}

	public void setDateOfLeave(Date dateOfLeave) {
		this.dateOfLeave = dateOfLeave;
	}

}