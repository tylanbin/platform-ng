NG开发平台（个人使用）
======
NG开发平台是一个人使用的JavaWeb开发基础框架
* 该平台可以使用POJO类（自己实现或使用反向工具生成），一次性生成各层代码，只需要通过少量的调整，即可直接使用
* 该平台已经完成了基础的用户-角色-权限控制（借助Shiro），并附带了组织/员工的基本管理
* 该平台放弃了传统的JSP页面，转用HTML+AJAX的方式实现，旨在减少开发人员与美工之间的冲突

## 目录
* [框架介绍](#框架介绍)
* [核心模块](#核心模块)
	* [组织机构-员工模块](#组织机构-员工模块)
	* [用户-角色-权限模块](#用户-角色-权限模块)
* [开始使用](#开始使用)
	* [POJO类](#POJO类)
	* [项目生成](#项目生成)
	* [代码调整](#代码调整)
* [注意事项](#注意事项)
* [备注](#备注)

## 框架介绍
* [Hibernate 4.0.x](http://hibernate.org/orm/)
* [Spring Framework 4.0.x](http://projects.spring.io/spring-framework/)
* [Shiro 1.2.3](http://shiro.apache.org/)
* [jQuery 1.8.3](http://jquery.com/)
* [jQuery EasyUI 1.4.2](http://www.jeasyui.com/)

## 核心模块
### 组织机构-员工模块
* 对组织机构、员工的基本管理
* 对员工账号、角色的基本管理

### 用户-角色-权限模块
* 对系统角色、权限的基本管理
* 对用户、角色、权限关系的管理
* 与Shiro框架整合，进行精细化权限控制

## 开始使用
### POJO类
* 本框架使用Hibernate注解形式，所以可以手动完成POJO类，或使用反向工具生成
* 为POJO类加入注解`@JsonFilter(包.类名)`，目的是为Json序列化时，动态过滤属性
* 为POJO类属性加入注解`@MetaData(chsName = "xxx")`，目的是在生成页面代码时取得中文属性名
* 示例代码如下（或直接参照`me.lb.model.demo.Foo`）

```Java
package me.lb.model.demo;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import me.lb.support.system.annotation.MetaData;

import org.codehaus.jackson.map.annotate.JsonFilter;

@Entity
@Table(name = "ng_demo_foo")
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

	// Constructors

	/** default constructor */
	public Foo() {
	}

	/** full constructor */
	public Foo(Integer col1, String col2) {
		this.col1 = col1;
		this.col2 = col2;
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

}
```

### 项目生成
* 修改数据库配置`src/main/resources/jdbc.properties`，配置数据库参数（暂时只支持MySQL）
* 执行数据库生成工具`me.lb.utils.InitDB`，生成数据表及关联
* 调整代码生成工具`me.lb.utils.InitCode`，列出自定义的POJO类所在的文件夹（可以多个）

```Java
// 如果你的POJO类为me.lb.xxx.Pojo，则xxx就是所指的名称
public static final String[] POJOFOLDER = { "demo1", "demo2" };
```

* 直接执行 `me.lb.support.system.CodeGenerator`，即可生成代码
* 代码生成说明（以POJO类为`me.lb.model.xxx.Pojo`为例）
	* `me.lb.dao.xxx.PojoDao`为持久层接口
	* `me.lb.dao.xxx.impl.PojoDaoImpl`为持久层实现
	* `me.lb.service.xxx.PojoService`为业务层接口
	* `me.lb.service.xxx.impl.PojoServiceImpl`为业务层实现
	* `me.lb.controller.admin.xxx.PojoController`为控制层
	* `src/main/webapp/web/admin/xxx/pojo/list.html`为页面代码
	* `src/main/webapp/assets/admin/xxx/pojo/list.js`为JS代码

### 代码调整
* 代码生成完毕后，需要手动调整修改如下几处
1. 修改`me.lb.dao.xxx.impl.PojoDaoImpl`文件，带参数的`pagingQuery`方法（处理模糊查询的参数）
2. 修改`me.lb.controller.admin.xxx.PojoController`文件，`edit`方法（处理修改对象的哪些属性）
3. 修改`src/main/webapp/web/admin/xxx/pojo/list.html`文件，修改对话框的表单控件（参考EasyUI文档）
4. 其余代码不调整也可使用，但推荐根据情况进行修改（如业务代码、页面等）
5. 修改`src/main/webapp/web/admin/common/main.html`文件，加入功能链接`../xxx/pojo/list.html`

## 注意事项
* 需要Maven的支持，并需要计算机联网（下载Jar）
* 推荐使用Eclipse或MyEclipse进行平台的使用
* 如果项目部署路径修改（默认http://localhost/），需要修改`src/main/webapp/assets/common/framework.js`及错误页面

## 备注
* [jQuery EasyUI 文档](http://www.jeasyui.com/documentation/index.php)