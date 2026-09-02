#!/usr/bin/env bash
#
# Destroi o cluster local criado por deploy-local.sh.
#
set -euo pipefail
CLUSTER_NAME="${CLUSTER_NAME:-oficina-local}"

if kind get clusters 2>/dev/null | grep -qx "$CLUSTER_NAME"; then
  echo "==> Deletando cluster '$CLUSTER_NAME'"
  kind delete cluster --name "$CLUSTER_NAME"
else
  echo "==> Cluster '$CLUSTER_NAME' nao existe"
fi
