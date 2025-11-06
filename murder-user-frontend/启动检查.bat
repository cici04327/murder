@echo off
chcp 65001 > nul
echo =======================================
echo    剧本杀系统 - 启动前服务检查
echo =======================================
echo.

echo [1/3] 检查 Node.js...
node --version > nul 2>&1
if %errorlevel% neq 0 (
    echo ✗ Node.js 未安装
    pause
    exit /b 1
)
echo ✓ Node.js 已安装

echo.
echo [2/3] 检查后端服务...
netstat -ano | findstr "8080" > nul
if %errorlevel% neq 0 (
    echo ✗ 网关服务未运行 (端口 8080)
    echo.
    echo 请先启动后端服务：
    echo   1. cd murder-gateway ^&^& mvn spring-boot:run
    echo   2. cd murder-user ^&^& mvn spring-boot:run
    echo   3. cd murder-store ^&^& mvn spring-boot:run
    echo   4. cd murder-script ^&^& mvn spring-boot:run
    echo   5. cd murder-reservation ^&^& mvn spring-boot:run
    echo.
    pause
    exit /b 1
)
echo ✓ 网关服务运行中

echo.
echo [3/3] 运行详细健康检查...
node check-services.js
if %errorlevel% neq 0 (
    echo.
    echo ⚠ 部分服务未启动，建议先启动所有后端服务
    echo.
    choice /C YN /M "是否继续启动前端"
    if errorlevel 2 exit /b 1
)

echo.
echo =======================================
echo ✓ 检查完成，启动前端开发服务器...
echo =======================================
echo.

npm run dev


