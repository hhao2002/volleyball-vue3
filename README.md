# 排球积分系统3.0

## 介绍
1. 回忆复习MVC项目
2. 修复一些bug
3. 完善一些小细节

[PingCode项目开发管理网址](https://iotc20220428104021552.pingcode.com/pjm/projects/VBC/backlog/g5LbaiMJ)

### 软件架构
java-SSM框架

SpringMVC Spring Mybatis

## 安装教程(Win10)
- 前置条件
  1.  JDK:1.8
  2.  TomCat:8.0
  3.  MySql:8.0
  4.  idea或eclipse
- eclipse 导入项目文件项目
  1.  解压文件
  2.  File -> Open Project From File System.. -> Dirctory.. -> 选择解压过的文件夹 -> Finish
- idea 导入项目文件项目
  1.  解压文件
  2.  File -> Open -> 选择解压过的文件夹 -> OK
### 使用说明

JDK:1.8		TomCat:8.0
MySql:8.0	编码：UTF-8
idea注释风格文档文件为：codetemplates.xml

MySql导入：mysql -u root -p密码 》 进入mysql 》 source volleyball.sql的路径

#### 关于语音播报问题
![语音播放浏览器支持](https://foruda.gitee.com/images/1681787954728435739/387910d1_8948347.png "语音播放浏览器支持")

win10的系统基本都是可以的
如果没有声音，就是没有安装或配置TTS引擎。[TTS下载](http://www.lingoes.cn/zh/translator/speech.htm#tts)