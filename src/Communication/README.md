#功能一大文件加密传输的通信基础
##服务器
系统首先运行Sever.java，监听端口，等待客户端的连接，一旦有客户端连接成功，则启动SocketThread,处理服务端的数据。
##客户端
运行ClientFrame.java，即开启一个客户端，同时自启动ClientThread，处理客户端传输的数据。

DecodeFile类是客户端接受一个加密文件后，解密文件的功能。
