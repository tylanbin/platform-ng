NG开发平台（个人使用）
======
NG开发平台是一个人使用的JavaWeb开发基础框架
* 该平台可以使用POJO类（自己实现或使用反向工具生成），一次性生成各层代码，只需要通过少量的调整，即可直接使用
* 该平台已经完成了基础的用户-角色-权限控制（借助Shiro），并附带了组织/员工的基本管理
* 该平台放弃了传统的JSP页面，转用HTML+AJAX的方式实现，旨在减少开发人员与美工之间的冲突
* 该平台的[Spring Boot版本](https://github.com/tylanbin/platform-ng-springboot/)正在重写中...

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
* [MyBatis 3.4.x](http://www.mybatis.org/mybatis-3/zh/)
* [Spring Framework 4.0.x](http://projects.spring.io/spring-framework/)
* [Shiro 1.2.x](http://shiro.apache.org/)
* [jQuery 1.8.3](http://jquery.com/)
* [jQuery EasyUI 1.4.x](http://www.jeasyui.com/)

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
* 示例代码直接参照`me.lb.model.demo.Foo`

### 项目生成
* 修改数据库配置`src/main/resources/jdbc.properties`，配置数据库参数（暂时只支持MySQL）
* 执行数据库生成工具`me.lb.utils.InitDB`（src/test/java下），生成数据表及关联，并初始化数据（可选）
* 调整代码生成工具`me.lb.utils.InitCode`（src/test/java下），列出自定义的POJO类所在的文件夹（可以多个）
* 代码生成说明（以POJO类为`me.lb.model.xxx.Pojo`为例）
	* `me.lb.dao.xxx.PojoDao`为持久层接口
	* `me.lb.dao.xxx.impl.PojoDaoImpl`为持久层实现
	* `me.lb.service.xxx.PojoService`为业务层接口
	* `me.lb.service.xxx.impl.PojoServiceImpl`为业务层实现
	* `me.lb.controller.admin.xxx.PojoController`为控制层
	* `src/main/webapp/web/admin/xxx/pojo/list.html`为页面代码
	* `src/main/webapp/assets/admin/xxx/pojo/list.js`为JS代码
* 注意：
	* 初始化数据时，需要先初始化表结构（db_init方法），再初始化数据（data_init方法）
	* 在eclipse中，项目依赖的`activiti-modeler`可能与JUnit存在冲突，导致无法正常执行测试用例
	* 如果出现冲突，需要通过执行mvn test来执行测试用例（mvn test -Dtest=类名#方法名）

### 代码调整
* 代码生成完毕后，需要手动调整修改如下几处
1. 修改`me.lb.dao.xxx.impl.PojoDaoImpl`文件的`getTableName`方法（设置该类对应的数据库表名称）
2. 修改`me.lb.dao.xxx.impl.PojoDaoImpl`文件的`getIgnored`方法（设置新增修改数据时不处理的字段）
3. 修改`me.lb.controller.admin.xxx.PojoController`文件，`edit`方法（处理修改对象的哪些属性）
4. 修改`src/main/webapp/web/admin/xxx/pojo/list.html`文件，修改对话框的表单控件（参考EasyUI文档）
5. 修改`src/main/webapp/web/admin/common/main.html`文件，加入功能链接`../xxx/pojo/list.html`
6. 其余代码不调整也可使用，但推荐根据情况进行修改（如业务代码、页面等）

## 注意事项
* 需要Maven的支持，并需要计算机联网（下载Jar）
* 如果项目部署路径修改（默认localhost），需要修改全局的ajax设置js和错误页面：
	* `src/main/webapp/assets/common/framework.js`中开头处的`baseUrl`属性
	* `src/main/webapp/web/admin/common/404.html`中开头处的`base`标签
	* `src/main/webapp/web/admin/common/500.html`中开头处的`base`标签

## 备注
* [jQuery EasyUI 文档](http://www.jeasyui.com/documentation/index.php)
