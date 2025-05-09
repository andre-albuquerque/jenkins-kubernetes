.DEFAULT_GOAL := create

pre:
	@kubectl apply -f https://raw.githubusercontent.com/metallb/metallb/v0.14.9/config/manifests/metallb-native.yaml
	@kubectl wait --namespace metallb-system \
		--for=condition=ready pod \
		--selector=app=metallb \
		--timeout=300s
	@kubectl apply -f ./manifests

helm:
	@helmfile apply

create:
	@kind create cluster --config config.yaml

up: create pre helm

destroy:
	@kind delete cluster

passwd:
	@echo "JENKINS: "
	@kubectl get secret -n jenkins jenkins -ojson | jq -r '.data."jenkins-admin-password"' | base64 -d
	@echo ""
	@echo "GITEA: "
	@echo "r8sA8CPHD9!bt6d | jenkins: WFisEmbEGlAy"
	@echo "SONARQUBE: "
	@echo "eleMeTHYtocK"
	@echo "ARGOCD: "
	@kubectl get secret -n argocd argocd-initial-admin-secret -ojson | jq -r '.data.password' | base64 -d
	@echo ""
	@echo "HARBOR: "
	@echo "Harbor12345"