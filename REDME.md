Everything
简介：仿照Everything桌面工具，基于Java语言开发的命令行文件搜索工具。

背景：有时候在windows命令行下需要查询一些文件，由于 for 命令并不如Linux下的 find 命令好用，所以DIY开发一款命
令行工具，用来实现Windows命令行中搜索文件

意义：1.解决Windows命令行下文件搜索问题；2.基于Java开发的工具可以在Windows和Linux平台上无差异使用；3.锻炼编码能力

使用技术：
  Java（文件操作）
  Database（嵌入式H2数据库或者MySQL数据库）
  JDBC
  Lombok库（IDEA安装Lombok插件）
  Java多线程
  文件系统监控（Apache Commons IO）
  
 功能：1.建立索引，索引文件信息;2.根据文件名检索文件信息
        
 实现：主要分为四个层次：
 1.持久化层：主要是进行数据的存储，H2主要有嵌入式数据库存储和服务器式数据库存储，这里采用H2数据库嵌入式存储，因为这种方式存储数据，使得本地数据库和Java源码很好的结合在一起使用，只需要提供url接口和setValidationQuery接口以验证数据库是否被查询。
 
 2.业务层：是Everything项目的核心部分。分为三大部分：检索文件信息，索引文件信息，文件系统监控。
 
 检索文件信息：
  
