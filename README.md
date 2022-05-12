1. CI/CD Workflow using Github action to deploy to CloudRun 

Enable Cloud Run
gcloud services enable run.googleapis.com
Enable Artifact Registry
gcloud services enable artifactregistry.googleapis.com

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

[Setting up External HTTP Load Balancer]
1. Load balancers use a serverless Network Endpoint Group (NEG) backend to direct requests to a serverless Cloud Run service.
   ```
   gcloud compute network-endpoint-groups create gcp-hello-neg \
    --region=asia-southeast1 \
    --network-endpoint-type=serverless  \
    --cloud-run-service=gcp-hello
   ```
2. Create a backend service.
   ```
   gcloud compute backend-services create gcp-hello-backendservice \
    --global
   ```
3. Add the serverless NEG as a backend to this backend service.
   ```
   gcloud compute backend-services add-backend gcp-hello-backendservice \
    --global \
    --network-endpoint-group=gcp-hello-neg \
    --network-endpoint-group-region=asia-southeast1
   ```
4. Create a URL map to route incoming requests to the backend service (this is a load-balancer in Google Console).
   ```
   gcloud compute url-maps create simple-lb-urlmap \
    --default-service gcp-hello-backendservice
   ```
5. Create a target HTTP(S) proxy to route requests to your URL map.
   ```
   gcloud compute target-http-proxies create simple-lb-targetproxy \
    --url-map=simple-lb-urlmap
   ```
6. Create a global forwarding rule to route incoming requests to the proxy.
   ```
   gcloud compute forwarding-rules create simple-lb-forwardingrule \
    --address=static-ip \
    --target-http-proxy=simple-lb-targetproxy \
    --global \
    --ports=80
   ```
   ```
   gcloud compute forwarding-rules create simple-lb-forwardingrule \
    --target-http-proxy=simple-lb-targetproxy \
    --global \
    --ports=80
   ```
Overral flow: 
simple-lb-forwardingrule(ephemeral-ip:80) 
-> simple-lb-targetproxy 
-> simple-lb-urlmap (loadbalancer, route request based on host/path)
-> gcp-hello-backendservice
-> gcp-hello-neg (Network Endpoint Group)
-> gcp-hello (Cloud Run)
