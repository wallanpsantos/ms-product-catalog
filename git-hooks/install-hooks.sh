#!/bin/bash
# Instalador de hooks corrigido (remove \r se existir)
DIR="$(git rev-parse --show-toplevel | tr -d '\r')"

echo "ðŸš€ Instalando pre-push hook Gradle..."

# Copia e garante que o script bash e ps1 estejam no lugar
cp "$DIR/git-hooks/pre-push"     "$DIR/.git/hooks/pre-push"
cp "$DIR/git-hooks/pre-push.ps1" "$DIR/.git/hooks/pre-push.ps1"

# Torna executÃ¡vel
chmod +x "$DIR/.git/hooks/pre-push"

echo "âœ… INSTALADO com sucesso!"
echo "Teste agora com: git push"
