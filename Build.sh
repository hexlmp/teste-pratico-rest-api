#!/bin/bash

echo "Incluir keycloak nos hosts..."
echo ""

# Verifica se já existe a entrada para 'keycloak' no /etc/hosts
if grep -q "keycloak" /etc/hosts; then
    echo "Atualizando entrada existente para keycloak no /etc/hosts..."
    sudo sed -i '/keycloak/s/.*/127.0.0.1    keycloak/' /etc/hosts
else
    echo "Adicionando nova entrada para keycloak no /etc/hosts..."
    echo "127.0.0.1    keycloak" | sudo tee -a /etc/hosts
fi

# Verifica se já existe a entrada para 'minio' no /etc/hosts
if grep -q "minio" /etc/hosts; then
    echo "Atualizando entrada existente para minio no /etc/hosts..."
    sudo sed -i '/minio/s/.*/127.0.0.1    minio/' /etc/hosts
else
    echo "Adicionando nova entrada para minio no /etc/hosts..."
    echo "127.0.0.1    minio" | sudo tee -a /etc/hosts
fi


echo "Entrada no /etc/hosts:"
grep "keycloak" /etc/hosts

cd docker

echo "Criar containers..."
echo ""

docker-compose down && docker-compose up --build -d --remove-orphans
