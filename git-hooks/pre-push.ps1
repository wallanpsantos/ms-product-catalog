# Pre-push Gradle para Spring Boot no Windows
$gitRoot = git rev-parse --show-toplevel
$gradlewPath = Join-Path $gitRoot "gradlew.bat"

Write-Host "🔄 Executando gradlew clean build (sem testes)..." -ForegroundColor Yellow

& "$gradlewPath" clean build -x test

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Falha no Gradle clean build. Push ABORTADO!" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Build OK. Prosseguindo push..." -ForegroundColor Green
exit 0
