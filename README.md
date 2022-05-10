1. CI/CD Workflow using Github action to deploy to CloudRun 

[Setting up Workload Identity Federation](https://github.com/google-github-actions/auth#setting-up-workload-identity-federation)
1. ```
   export PROJECT_ID="my-project"
   export SERVICE_ACCOUNT_NAME="my-service-account"
   export POOL_ID="my-pool"
   export PROVIDER_ID="my-provider"
   ```
2. ```
   gcloud iam service-accounts create "${SERVICE_ACCOUNT_NAME}" \
   --project "${PROJECT_ID}"
   ```
3. Grant the Google Cloud Service Account permissions to access Google Cloud resources
4. ```
   gcloud services enable iamcredentials.googleapis.com \
   --project "${PROJECT_ID}"
   ```
5. ```
   gcloud iam workload-identity-pools create "${POOL_ID}" \
   --project="${PROJECT_ID}" \
   --location="global" \
   --display-name="Demo pool"
   ```
6. ```
   gcloud iam workload-identity-pools describe "${POOL_ID}" \
   --project="${PROJECT_ID}" \
   --location="global" \
   --format="value(name)"
   ```
   `export WORKLOAD_IDENTITY_POOL_ID="..." # value from above`
7. ```
   gcloud iam workload-identity-pools providers create-oidc "${PROVIDER_ID}" \
   --project="${PROJECT_ID}" \
   --location="global" \
   --workload-identity-pool="${POOL_ID}" \
   --display-name="Demo provider" \
   --attribute-mapping="google.subject=assertion.sub,attribute.actor=assertion.actor,attribute.repository=assertion.repository" \
   --issuer-uri="https://token.actions.githubusercontent.com"
   ```
8. ```
   export REPO="LinhVu1027/gcp-hello" # e.g. "google/chrome"
   gcloud iam service-accounts add-iam-policy-binding "${SERVICE_ACCOUNT_NAME}@${PROJECT_ID}.iam.gserviceaccount.com" \
   --project="${PROJECT_ID}" \
   --role="roles/iam.workloadIdentityUser" \
   --member="principalSet://iam.googleapis.com/${WORKLOAD_IDENTITY_POOL_ID}/attribute.repository/${REPO}"
   ```
9. ```
   gcloud iam workload-identity-pools providers describe "${PROVIDER_ID}" \
   --project="${PROJECT_ID}" \
   --location="global" \
   --workload-identity-pool="${POOL_ID}" \
   --format="value(name)"
   ```