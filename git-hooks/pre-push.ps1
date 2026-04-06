# Pre-push Gradle para Spring Boot no Windows
$DIR = git rev-parse --show-toplevel
Write-Host "🔄 Executando gradlew clean build..." -ForegroundColor Yellow
& "$DIR\gradlew.bat" clean build -x test
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Falha no Gradle clean build. Push ABORTADO!" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Build OK. Prosseguindo push..." -ForegroundColor Green
