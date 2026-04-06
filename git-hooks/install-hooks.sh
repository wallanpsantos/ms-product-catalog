#!/bin/sh
DIR=$(git rev-parse --show-toplevel)

echo "🚀 Instalando pre-push hook Gradle cross-platform..."

cp "$DIR/git-hooks/pre-push"     "$DIR/.git/hooks/pre-push"
cp "$DIR/git-hooks/pre-push.ps1" "$DIR/.git/hooks/pre-push.ps1"
chmod +x "$DIR/.git/hooks/pre-push"

echo "✅ INSTALADO! Teste com: git push"
echo "💡 Pular hook: git push --no-verify"
echo "⚠️  Requisitos: Java 21 no PATH"
