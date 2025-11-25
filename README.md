# Jenkins Kubernetes Project

## Overview

This project provides a comprehensive, end-to-end CI/CD and GitOps solution for a sample Python Flask REST API. It showcases the integration of various DevOps tools to automate the build, test, and deployment of a containerized application onto a Kubernetes cluster.

The entire environment, including the Kubernetes cluster and all the necessary tools, is provisioned and managed locally using Vagrant and VirtualBox.

## Features

This project sets up a complete DevOps platform with the following tools:

-   **Vagrant:** To create and manage a consistent local development environment.
-   **Kubernetes (Kind):** To orchestrate and run containerized applications.
-   **Jenkins:** As the CI/CD automation server to build, test, and deploy the application.
-   **Gitea:** For self-hosted Git service.
-   **Harbor:** As a private container registry to store and scan Docker images.
-   **SonarQube:** For continuous inspection of code quality.
-   **ArgoCD:** To implement GitOps for continuous delivery to Kubernetes.
-   **Helm:** As the package manager for Kubernetes to simplify deployment.
-   **NGINX Ingress Controller:** To manage external access to the services in the Kubernetes cluster.

## Architecture

The architecture is designed to provide a full-cycle development and deployment pipeline:

1.  A developer pushes code to a Gitea repository.
2.  A Jenkins pipeline is triggered, which:
    -   Runs unit tests.
    -   Performs a SonarQube scan for code quality.
    -   Builds a Docker image using Kaniko.
    -   Pushes the image to Harbor.
    -   Scans the image for vulnerabilities in Harbor.
    -   Deploys the application to different environments (Development, Staging).
3.  ArgoCD monitors the Git repository for changes in the Kubernetes manifests and automatically deploys the application to the cluster, following the GitOps pattern.

All of these services run within a Kubernetes cluster created by `kind`, which is provisioned inside a Vagrant VM.

## Prerequisites

Before you begin, ensure you have the following installed on your local machine:

-   [Vagrant](https://www.vagrantup.com/downloads)
-   [VirtualBox](https://www.virtualbox.org/wiki/Downloads)

## Getting Started

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd jenkins-kubernetes
    ```

2.  **Start the Vagrant environment:**
    ```bash
    vagrant up
    ```
    This command will provision a virtual machine, install all the necessary tools (like Docker, Kind, Helm), and set up the entire DevOps platform as configured in the `helmfile.yaml`.

3.  **Access the services:**
    Once the `vagrant up` command is complete, you can access the various services through the forwarded ports defined in the `Vagrantfile`:
    -   **Jenkins:** [http://localhost:8080](http://localhost:8080)
    -   **Gitea:** [http://localhost:3000](http://localhost:3000)

    Other services like Harbor, SonarQube, and ArgoCD will be accessible via the Ingress Controller running inside the Kubernetes cluster. You will need to configure your `/etc/hosts` file or use a DNS service to resolve the hostnames to the IP address of the Vagrant VM.

## CI/CD Pipeline

The CI/CD pipeline is defined in the `Jenkinsfile` and uses a shared library located in the `jenkins-shared-libraries` directory. The pipeline consists of the following stages:

-   **Unit test:** Runs the Python unit tests for the Flask application.
-   **Sonarqube Scan:** Analyzes the code for bugs, vulnerabilities, and code smells.
-   **Build and Push:** Builds a container image and pushes it to the Harbor registry.
-   **Harbor Security Scan:** Scans the pushed image for security vulnerabilities.
-   **Artifact Promotion:** Promotes the artifact for release.
-   **Infrastructure Tests on K8s:** Runs tests against the Kubernetes infrastructure.
-   **Deploy to Development:** Deploys the application to a development namespace.
-   **Deploy to Staging:** Deploys the application to a staging namespace.
-   **Create Tag?:** Pauses for user input before creating a production tag.
-   **Deploy to Production:** Deploys the application to the production namespace.

The pipeline is designed to work with a git-flow like branching model, where different stages are triggered based on the branch name (`develop`, `release-*`, `feature-*`, `hotfix-*`).

## The Application

The sample application, located in the `app` directory, is a simple REST API built with Python and Flask. It provides basic CRUD (Create, Read, Update, Delete) operations for a `User` resource and uses MongoDB as its database.

The API exposes the following endpoints:

-   `GET /users`: Retrieves a list of all users.
-   `POST /user`: Creates a new user.
-   `GET /user/<cpf>`: Retrieves a user by their CPF.
-   `PATCH /user`: Updates a user's information.
-   `DELETE /user/<cpf>`: Deletes a user by their CPF.
-   `GET /healthcheck`: A simple health check endpoint.