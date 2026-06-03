# 🚀 Distributed Lovable

> A comprehensive distributed system for AI-powered project collaboration and code execution management

## 📋 Overview

**Distributed Lovable** is an innovative platform designed to revolutionize collaborative development through intelligent AI assistance and distributed architecture. It seamlessly integrates frontend applications, backend services, and Kubernetes orchestration to provide a scalable, secure, and efficient environment for teams building together.

The system leverages cutting-edge technologies including:
- 🤖 **AI-Powered Assistance** via Claude AI
- ☁️ **Cloud-Native Architecture** with Kubernetes
- 🔐 **Enterprise-Grade Security** with OIDC authentication
- 📊 **Real-time Collaboration** across distributed teams
- ⚡ **Optimized Performance** with intelligent caching

---

## 🏗️ Architecture Overview

### System Components

Our platform is built on a sophisticated distributed architecture that combines multiple interconnected systems:

#### **1. Code Execution System Architecture**

The code execution pipeline enables remote command execution and real-time development synchronization:

![Code Execution System Architecture](image-1.png)

**Key Features:**
- Frontend and Backend deployment coordination
- Real-time file synchronization with Hot Module Reload (HMR)
- Kubernetes-based pod orchestration with network isolation
- Redis caching for optimized performance
- Reverse proxy for secure external access
- Fabric8 K8s client for seamless container management

---

#### **2. Lovable Clone AI System Design**

The intelligent AI system processes user requests and generates contextually-aware responses:

![Lovable Clone AI System Design](image-2.png)

**System Flow:**
- React-based frontend sends prompts to Spring Boot backend
- Streaming response processing with real-time updates
- Multi-stage file processing pipeline:
  - Message buffering and parsing
  - Template-based response generation
  - Smart file tree extraction
  - LLM-powered context generation
- Circuit breaker pattern for reliability
- Minio-based storage for templates and assets

---

#### **3. Data Model Architecture**

Complete relational schema for multi-user collaboration:

![Database Schema](image-3.png)

**Core Entities:**
- **USER**: Authentication and profile management
- **PROJECT**: Collaborative workspace management
- **PROJECT_MEMBER**: Role-based access control
- **SUBSCRIPTION**: Flexible subscription tiers
- **USAGE_LOG**: Activity tracking and analytics
- **PROJECT_FILE**: File hierarchy and content management
- **CHAT_SESSION**: Real-time collaboration conversations
- **PREVIEW**: Version control and snapshot management

---

#### **4. Security & Deployment Pipeline**

Enterprise-grade authentication and secure GCP deployment:

![GitHub OIDC to GKE Deployment](image-4.png)

**Security Flow:**
1. GitHub Actions initiates deployment workflow
2. OIDC token requested from GitHub
3. GitHub OIDC validates and signs token
4. Workload Identity Pool verifies credentials
5. Security Token Service generates federated identity token
6. GCP access token exchanged for GKE cluster access
7. Impersonated service account enables secure deployment
8. Zero-trust authentication throughout the pipeline

---

## ✨ Key Features

### 🎯 **Intelligent Collaboration**
- AI-assisted code generation and reviews
- Real-time collaborative editing
- Smart project management with AI insights

### 🔒 **Enterprise Security**
- OpenID Connect (OIDC) authentication
- Zero-trust security model
- Role-based access control (RBAC)
- Encrypted data transmission

### 📈 **Scalability**
- Kubernetes-native orchestration
- Auto-scaling pod management
- Load balancing across distributed nodes
- Optimized resource utilization

### 🚀 **Performance**
- Hot Module Reload (HMR) for instant updates
- Redis caching layer
- Optimized network policies
- Smart file synchronization

### 💾 **Data Management**
- Multi-tenant database architecture
- Comprehensive audit logging
- Version control and snapshots
- File tree extraction and management

---

## 🛠️ Technology Stack

| Component | Technology |
|-----------|-----------|
| **Frontend** | React, TypeScript |
| **Backend** | Spring Boot, Java |
| **Orchestration** | Kubernetes, Fabric8 |
| **AI/ML** | Claude API, LLM Integration |
| **Storage** | Redis, Minio, PostgreSQL |
| **Authentication** | GitHub OIDC, GCP Identity |
| **Infrastructure** | Google Cloud Platform (GKE) |
| **Deployment** | GitHub Actions, Docker |

---

## 🚀 Getting Started

### Prerequisites
- Docker & Kubernetes
- Java 11+
- Node.js 16+
- GCP Account with GKE cluster
- GitHub repository access

### Installation

```bash
# Clone the repository
git clone https://github.com/abhiUmap-tech/distributed-lovable.git
cd distributed-lovable

# Install dependencies
npm install
./gradlew build

# Deploy to Kubernetes
kubectl apply -f deployment/
```

---

## 📚 Documentation

- [Architecture Guide](./docs/ARCHITECTURE.md)
- [API Documentation](./docs/API.md)
- [Deployment Guide](./docs/DEPLOYMENT.md)
- [Security Documentation](./docs/SECURITY.md)

---

## 🤝 Contributing

We welcome contributions! Please feel free to submit issues and pull requests.

```bash
# Create a feature branch
git checkout -b feature/your-feature

# Make your changes and commit
git commit -am 'Add new feature'

# Push to the branch
git push origin feature/your-feature

# Open a Pull Request
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 💬 Support & Community

- 📧 Email: support@distributed-lovable.dev
- 🐛 Issues: [GitHub Issues](https://github.com/abhiUmap-tech/distributed-lovable/issues)
- 💡 Discussions: [GitHub Discussions](https://github.com/abhiUmap-tech/distributed-lovable/discussions)

---

## 🎓 Learn More

- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Spring Boot Guide](https://spring.io/guides/gs/spring-boot/)
- [React Documentation](https://react.dev/)
- [GitHub OIDC Guide](https://docs.github.com/en/actions/deployment/security-hardening-your-deployments/about-security-hardening-with-openid-connect)

---

<div align="center">

**Built with ❤️ by the Distributed Lovable Team**

⭐ If you find this project helpful, please consider giving it a star!

</div>
