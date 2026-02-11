#!/bin/bash
set -e

echo "Destruyendo infraestructura de FranchiseApp..."

cd terraform

read -p "¿Estás seguro de que quieres destruir toda la infraestructura? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
  echo "Destrucción cancelada"
  exit 0
fi

terraform destroy -auto-approve

echo "Infraestructura destruida exitosamente"
