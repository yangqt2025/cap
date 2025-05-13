#!/bin/bash

# 备份原文件
cp src/main/resources/application-prod.yml src/main/resources/application-prod.yml.bak

# 使用sed命令替换密码
sed -i.bak 's/password: 123456/password: Mian2002@lmn/g' src/main/resources/application-prod.yml

echo "密码已更新完成！" 