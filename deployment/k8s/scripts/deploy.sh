pwd

kubectl apply -f ../secrets/auth-token-private-key.yaml
kubectl apply -f ../secrets/ecommer-oauth2-secret.yaml
kubectl apply -f ../deployments/ecommer-oauth2.yaml
kubectl apply -f ../services/ecommer-oauth2-nodeport.yaml

read -p "Press any key to exit"