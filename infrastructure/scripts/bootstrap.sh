#!/bin/bash
# =============================================================================
# bootstrap.sh - Instala herramientas necesarias y prepara el proyecto GCP
# Uso: chmod +x bootstrap.sh && ./bootstrap.sh
# =============================================================================

set -euo pipefail

# --- Colores ---
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
info()    { echo -e "${GREEN}[INFO]${NC} $1"; }
warn()    { echo -e "${YELLOW}[WARN]${NC} $1"; }
error()   { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

echo ""
echo "========================================="
echo "  Hiberus Payment API - GCP Bootstrap   "
echo "========================================="
echo ""

# --- 1. Verificar Homebrew ---
if ! command -v brew &>/dev/null; then
  error "Homebrew no encontrado. Instálalo en: https://brew.sh"
fi
info "Homebrew detectado: $(brew --version | head -1)"

# --- 2. Instalar gcloud CLI ---
if command -v gcloud &>/dev/null; then
  info "gcloud ya instalado: $(gcloud --version | head -1)"
else
  info "Instalando Google Cloud CLI..."
  brew install --cask google-cloud-sdk
  info "gcloud instalado correctamente."
fi

# --- 3. Instalar Terraform ---
if command -v terraform &>/dev/null; then
  info "Terraform ya instalado: $(terraform --version | head -1)"
else
  info "Instalando Terraform..."
  brew tap hashicorp/tap
  brew install hashicorp/tap/terraform
  info "Terraform instalado correctamente."
fi

# --- 4. Autenticar gcloud ---
info "Iniciando autenticación con Google Cloud..."
gcloud auth login
gcloud auth application-default login

# --- 5. Crear proyecto GCP ---
echo ""
warn "Introduce un ID para el nuevo proyecto GCP (minúsculas, guiones, sin espacios):"
warn "Ejemplo: hiberus-payment-api-2024"
read -r PROJECT_ID

BILLING_ACCOUNT=$(gcloud billing accounts list --format="value(ACCOUNT_ID)" --filter="open=true" | head -1)
if [ -z "$BILLING_ACCOUNT" ]; then
  error "No se encontró ninguna cuenta de Billing activa. Crea una en: https://console.cloud.google.com/billing"
fi

info "Creando proyecto: $PROJECT_ID"
gcloud projects create "$PROJECT_ID" --name="Hiberus Payment API" || warn "El proyecto ya existe, continuando..."

info "Vinculando cuenta de Billing: $BILLING_ACCOUNT"
gcloud billing projects link "$PROJECT_ID" --billing-account="$BILLING_ACCOUNT"

info "Estableciendo proyecto por defecto..."
gcloud config set project "$PROJECT_ID"

# --- 6. Habilitar APIs necesarias ---
info "Habilitando APIs de GCP (esto puede tardar ~1 minuto)..."
gcloud services enable \
  run.googleapis.com \
  sqladmin.googleapis.com \
  artifactregistry.googleapis.com \
  secretmanager.googleapis.com \
  cloudresourcemanager.googleapis.com \
  iam.googleapis.com \
  vpcaccess.googleapis.com \
  compute.googleapis.com \
  storage.googleapis.com

# --- 7. Crear bucket Terraform state ---
REGION="us-central1"
BUCKET_NAME="${PROJECT_ID}-tf-state"
info "Creando bucket para estado de Terraform: gs://$BUCKET_NAME"
gcloud storage buckets create "gs://${BUCKET_NAME}" \
  --location="$REGION" \
  --uniform-bucket-level-access 2>/dev/null || warn "Bucket ya existe."

# --- 8. Crear Service Account para Terraform ---
SA_NAME="terraform-sa"
SA_EMAIL="${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com"
info "Creando Service Account para Terraform: $SA_EMAIL"
gcloud iam service-accounts create "$SA_NAME" \
  --display-name="Terraform Service Account" 2>/dev/null || warn "Service Account ya existe."

for ROLE in \
  roles/run.admin \
  roles/cloudsql.admin \
  roles/artifactregistry.admin \
  roles/secretmanager.admin \
  roles/iam.serviceAccountAdmin \
  roles/iam.serviceAccountUser \
  roles/vpcaccess.admin \
  roles/compute.networkAdmin \
  roles/storage.admin; do
  gcloud projects add-iam-policy-binding "$PROJECT_ID" \
    --member="serviceAccount:$SA_EMAIL" \
    --role="$ROLE" --quiet
done

KEY_FILE="infrastructure/terraform/sa-key.json"
info "Descargando clave del Service Account a: $KEY_FILE"
gcloud iam service-accounts keys create "$KEY_FILE" \
  --iam-account="$SA_EMAIL"

# --- 9. Actualizar variables de Terraform ---
info "Actualizando infrastructure/terraform/terraform.tfvars..."
cat > infrastructure/terraform/terraform.tfvars <<EOF
project_id    = "${PROJECT_ID}"
region        = "${REGION}"
app_image     = "${REGION}-docker.pkg.dev/${PROJECT_ID}/hiberus-payment/hiberus-payment-api:latest"
tf_state_bucket = "${BUCKET_NAME}"
EOF

echo ""
echo "========================================="
info "Bootstrap completado con éxito. ✅"
echo ""
warn "Siguiente paso: Actualiza los secrets de GitHub Actions:"
echo "  GCP_PROJECT_ID    = $PROJECT_ID"
echo "  GCP_SA_KEY        = (contenido de $KEY_FILE en base64)"
echo ""
echo "  Para codificar la clave:"
echo "  cat $KEY_FILE | base64"
echo "========================================="
