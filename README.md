# 涉密信息系统/Java
##系统设计需求：
###-综合需求 设计目标与展现方式：
（1）所有子需求以服务或接口方式提交
（2）构建验证 UI，实现系统能力验证

###-子需求一 大容量数据秘密通信
需求：两个主体间实现远程大容量数据文件秘密传输(超过 1G 大 小的容量 )
（1）设计并实现支持 大容量数据秘密传输的系统；
（2）该系统支持数据 该系统支持数据完整性和来源验证。

###-子需求二 网络监听设计与实现
需求：应用层实现本机指定网络端口的通信监听；
（1）实现监听网络连接所使用的协议、源 IP 地址、目标 IP 地址 等信息的功能；
（2）将监听内容在 UI 控件中显示。 

###-子需求三 涉密文件分级管控
需求：对系统监管目录区域的文件自动定密，并实现按级检索
（1）构建涉密关键词库；
（2）设计并实现管控密级标签；
（3）实现按密级检索。