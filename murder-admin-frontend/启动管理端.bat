@echo off
echo ============================================
echo 启动管理端前端
echo ============================================
echo.

cd /d %~dp0

echo [1] 清理缓存...
if exist node_modules\.vite (
    rmdir /s /q node_modules\.vite
    echo 缓存清理完成
)

echo.
echo [2] 启动开发服务器...
echo 访问地址: http://localhost:3000
echo.

npm run dev

pause

