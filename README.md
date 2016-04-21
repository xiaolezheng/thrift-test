# thrift-test
thrift 测试, thrift rpc测试

1. 首先安装thrift, maven插件根据IDL文件生成代码的时候需要本地thrift执行命令路径
   安装目录 /usr/local/app/thrift
2. 定义IDL文件 
   参考文档 https://thrift.apache.org/docs/idl
   参考文档 https://thrift.apache.org/docs/types
3. 到project目录执行 mvn package thrift插件会根据IDL文件生成对应源码文件
4. 实现接口
5. 启动server即可
