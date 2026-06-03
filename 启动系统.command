#!/bin/bash
cd "$(dirname "$0")"

echo "========================================"
echo "    在线考试系统 - 一键启动"
echo "========================================"

# 检查 Java
if ! command -v java &> /dev/null; then
    echo "❌ 未检测到 Java，请先安装 JDK 17+"
    echo "   下载: https://adoptium.net/download/"
    read -p "按回车退出..." && exit 1
fi
echo "✅ Java: $(java -version 2>&1 | head -1)"

# 检查 Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ 未检测到 Maven，请先安装 Maven 3.8+"
    echo "   下载: https://maven.apache.org/download.cgi"
    read -p "按回车退出..." && exit 1
fi
echo "✅ Maven: $(mvn --version 2>&1 | head -1)"

# 编译并启动后端
if curl -s http://localhost:8080/ > /dev/null 2>&1; then
    echo "✅ 后端已在运行"
else
    echo "🚀 正在编译并启动后端（首次约1-2分钟）..."
    cd exam-server
    mvn clean package -DskipTests -q 2>&1
    if [ $? -ne 0 ]; then
        echo "❌ 后端编译失败，请检查 JDK 版本是否为 17+"
        read -p "按回车退出..." && exit 1
    fi
    java -jar target/exam-server-1.0.0.jar > /tmp/exam-backend.log 2>&1 &
    cd ..
    for i in {1..30}; do
        curl -s http://localhost:8080/ > /dev/null 2>&1 && echo "✅ 后端启动成功" && break
        sleep 2
    done
fi

# 检查 Node.js
if ! command -v npm &> /dev/null; then
    echo "⚠️  未检测到 Node.js，跳过前端启动"
    echo "   后端 API: http://localhost:8080/doc.html （可用 API 调试）"
else
    if curl -s http://localhost:5173/ > /dev/null 2>&1; then
        echo "✅ 前端已在运行"
    else
        echo "🚀 正在启动前端..."
        cd exam-web
        [ ! -d "node_modules" ] && echo "   安装前端依赖..." && npm install --silent 2>&1
        npx vite --host > /tmp/exam-frontend.log 2>&1 &
        cd ..
        sleep 3
        echo "✅ 前端启动成功"
    fi
fi

echo ""
echo "========================================"
echo "  ✅ 系统就绪！"
echo "  前端页面: http://localhost:5173"
echo "  API 文档: http://localhost:8080/doc.html"
echo "  默认账号: admin / 123456"
echo "========================================"
echo ""

command -v open &> /dev/null && open http://localhost:5173
read -p "按回车关闭此窗口..."
