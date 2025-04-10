#!/bin/bash

cd docker

echo "Destruir containers..."
echo ""

# Remover imagens e volumens
docker-compose down -v

# Excluir imagens com TAG <none>
docker rmi $(docker images -f "dangling=true" -q) --force
