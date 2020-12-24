<h1 style="text-align: center">CC-ADMIN 企业级快速开发平台</h1>
<div style="text-align: center">

[![AUR](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/zhy6599/cc-admin-api/blob/master/LICENSE)
[![star](https://gitee.com/zhy6599/cc-admin-api/badge/star.svg?theme=white)](https://gitee.com/zhy6599/cc-admin-api)
[![GitHub stars](https://img.shields.io/github/stars/zhy6599/cc-admin-api.svg?style=social&label=Stars)](https://github.com/zhy6599/cc-admin-api)
[![GitHub forks](https://img.shields.io/github/forks/zhy6599/cc-admin-api.svg?style=social&label=Fork)](https://github.com/zhy6599/cc-admin-api)

</div>

#### 项目简介
一个基于 Spring Boot 2.1.3 、 Spring Boot Mybatis plus、 JWT、Spring Security、Redis、Vue quasar的前后端分离的后台管理系统(quasar 是重点！)

本项目以后的发展方向打算向数据分析和BI方向靠拢！（不排除会变）

**体验地址：**  [http://8.131.83.13:10000/](http://8.131.83.13:10000/)

**账号密码：** `admin / 123456`

#### 项目源码

|     |   后端源码  |   前端源码  |
|---  |--- | --- |
|  github   |  https://github.com/zhy6599/cc-admin-api   |  https://github.com/zhy6599/cc-admin-web   |
|  码云   |  https://gitee.com/zhy6599/cc-admin-api   |  https://gitee.com/zhy6599/cc-admin-web   |

#### 主要特性
- 使用最新技术栈，社区资源丰富。
- 高效率开发，代码生成器可一键生成前后端代码
- 支持数据字典，可方便地对一些状态进行管理
- 支持接口级别的功能权限与数据权限，可自定义操作
- 自定义权限注解与匿名接口注解，可快速对接口拦截与放行
- 前后端统一异常拦截处理，统一输出异常，避免繁琐的判断

####  系统功能
- 用户管理：提供用户的相关配置
- 角色管理：对权限与菜单进行分配
- 菜单管理：已实现菜单动态路由
- 职位管理：配置各个部门的职位
- 字典管理：可维护常用一些固定的数据，如：状态，性别等
- 系统日志：记录用户操作日志与异常日志，方便开发人员定位排错
- 定时任务：整合Quartz做定时任务，加入任务日志，任务运行情况一目了然
- 代码生成：高灵活度生成前后端代码，减少大量重复的工作任务

#### 项目结构
项目采用按功能分模块的开发方式，结构如下

- `auto-poi` 提供Excel导入导出服务

- `base-common` 提供常用基础类抽象与封装

- `module-system` 核心业务代码


#### 特别鸣谢

- 感谢 [JEECG](https://gitee.com/jeecg/jeecg-boot) 后台模板始于JEECG

#### 反馈交流

- QQ交流群：群：965940297


#### 系统效果预览

![1](https://gitee.com/zhy6599/preview/raw/master/1.png)
![2](https://gitee.com/zhy6599/preview/raw/master/2.png)
![3](https://gitee.com/zhy6599/preview/raw/master/3.png)
![4](https://gitee.com/zhy6599/preview/raw/master/4.png)
![5](https://gitee.com/zhy6599/preview/raw/master/5.png)
![6](https://gitee.com/zhy6599/preview/raw/master/6.png)
![7](https://gitee.com/zhy6599/preview/raw/master/7.png)
![8](https://gitee.com/zhy6599/preview/raw/master/8.png)
![9](https://gitee.com/zhy6599/preview/raw/master/9.png)
![10](https://gitee.com/zhy6599/preview/raw/master/10.png)
![11](https://gitee.com/zhy6599/preview/raw/master/11.png)
![12](https://gitee.com/zhy6599/preview/raw/master/12.png)