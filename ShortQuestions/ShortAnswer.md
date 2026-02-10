
## Question 1: Containers vs Virtual Machines

### Architecture Differences
- Containers:
  - containers share the same host Operating System Kernel
  - run isolated user-space processes.
- Virtual Machines:
  - every VM runs its own OS on virtualized hardware provided by hypervisor

### Resource Sharing
- Containers:
  - CPU, memory, and I/O resources are isolated and limited using cgroups
- Virtual Machines:
  - every virtual machine has virtual hardware resources allocated by hypervisor

### Startup Time Comparison
- Containers:
  - fast startup as no OS boot required
- Virtual Machines:
  - slow startup as a full guest OS must boot

### Use Case Scenarios
- When to use containers:
  - Microservices architectures 
  - Cloud-native applications 
  - CI/CD pipelines 
  - High-density deployments with rapid scaling requirements
- When to use virtual machines:
  - Strong isolation or security requirements 
  - Running different operating systems on the same host 
  - Legacy applications that require full OS control

---

## Question 2: Linux Kernel Features for Containers

### What Namespaces Provide
- Namespaces provide isolation of global system resources, making a container appear as if it has its own independent system.
- A process inside a namespace cannot see or interact with resources outside of that namespace.

### What Cgroups Provide
- Cgroups (control groups) provide resource limitation, prioritization, and accounting.
- They control how much CPU, memory, disk I/O, and network bandwidth a container can use.

### Namespace Types and Isolation
- **PID Namespace**
  - isolates process IDs
- **NET Namespace**
  - isolates the network stack
- **MNT Namespace**
  - isolates filesystem mount points
- (Optional additional namespaces)
    - IPC: isolates inter-process communication mechanisms
    - UTS: isolates hostname and domain name
    - USER: isolate user and group ids.

### How Namespaces and Cgroups Work Together
- namespace make container could not "see" things outside of it; Cgroups ensure they could not "touch" them.
- Together, they provide the foundation for container isolation and resource management.


---

## Question 3: Container Image Layering

### Image Layer Structure
- base Image → base OS Libraries → Runtime → App Dependencies → Application

### Read-only vs Writable Layers
- Read-only layers:
  - cannot overwrite, could be re-use among different containers
- Writable layer:
  - added when a container starts
  - stored all runtime changes

### Copy-on-Write Mechanism
- When a container tries to modify a file from a read-only layer, the file is copied into the writable layer.
- The modification is applied to the copied file, leaving the original layer unchanged.

### Benefits for Storage and Performance
- Storage:
  - save space cause read-only layers could be re-used and only need one instance
- Performance:
  - fast set up if those base layers already be loaded.

---

## Question 4: OCI Standards

### Purpose of OCI
- to define open standards for container formats and runtimes.
- to ensure interoperability between different container tools and platforms.

### OCI Runtime Specification
- Defines how to run a container.
- Specifications:
  - how a container process is started
  - how namespaces and cgroups are configured
  - container lifecycle
- runc is a reference implementation of the OCI

### OCI Image Specification
- Defines the format and structure of container images.
- Image layers & configuration
- Ensures images built by one tool can be run by another.

### OCI Distribution Specification
- Defines how container images are distributed.
- pushing images to registries & pulling images from registries
- Enables compatibility between different registries and clients.

### Benefits of Standardization
- Prevents vendor lock-in
- Allows different container runtimes and tools to work together
- Promotes a healthy, open container ecosystem
- Enables Kubernetes and container runtimes to evolve independently

---

## Question 5: Container Runtime Architecture

### Kubernetes / Orchestration Layer
- Responsibilities:
  - Scheduling pods onto nodes
  - Managing desired state (replicas, restarts, scaling)
  - Service discovery and load balancing
  - Handling rolling updates and self-healing

### High-level Runtime (containerd / CRI-O)
- Responsibilities:
  - Implementing the Kubernetes Container Runtime Interface (CRI)
  - Managing container lifecycle (create, start, stop)
  - Pulling and managing container images

### Low-level Runtime (runc)
- Responsibilities:
  - Creating and running containers according to the OCI Runtime Specification
  - Setting up Linux namespaces and cgroups
  - Executing the container process

### How These Layers Interact
- Kubernetes (scheduler) decides what should run and where.
- Kubernetes communicates with the high-level runtime via CRI.
- The high-level runtime prepares the container environment and image.
- The low-level runtime (runc) creates the container using kernel features.
- The Linux kernel enforces isolation and resource limits.

---

## Question 6: Container Runtime Interface (CRI)

### Purpose of CRI
- It allows Kubernetes to work with different container runtimes without modifying Kubernetes core code.
- CRI decouples Kubernetes from specific runtime implementations.

### RuntimeService Operations
- Examples:
  - Create, start, stop, and delete containers
    - Manage pods and container status

### ImageService Operations
- Examples:
  - Pull container images
    - List and remove images

### Benefits of Abstraction
- Kubernetes becomes runtime-agnostic 
- Easy to swap container runtimes (e.g., Docker → containerd → CRI-O)
- Cleaner architecture and better extensibility 
- Encourages innovation in container runtimes

---

## Question 7: Java Container Awareness

### Pre-JDK 10 Problem
- Java could not detect the resource allocated to container

### Container Support Improvements
- Modern JDK versions read cgroup information to determine available memory and CPU cores

### Recommended JVM Options for Containers
- Example options:
    - `-XX:MaxRAMPercentage=`
    - `-XX:InitialRAMPercentage=`
    -

### Why This Matters for Production
- Ensures predictable performance in containerized environments 
- Enables safe resource sharing in Kubernetes 
- Critical for running Java microservices in production

---

## Question 8: Multi-stage Docker Builds

### What Multi-stage Builds Are
- Each stage serves a different purpose (e.g., build stage, runtime stage).
- Only the final stage is included in the resulting image.

### Benefits
- Image size reduction:
  - Build tools and intermediate files are excluded from the final image.
- Security:
  - Smaller attack surface since compilers, package managers, and credentials are not shipped.

### Example Use Case (Java / Maven)
- Build stage:
  - Use a Maven base image to compile the application and generate a JAR file.
- Runtime stage:
  - Use a lightweight JDK or JRE image to run the compiled JAR.
  - Only the JAR and necessary runtime files are copied into the final image.

### Impact on Deployment
- Smaller container images
- Faster image pulls and container startup
- Reduced security risks 
- More efficient CI/CD pipelines

---

## Question 9: Container Networking

### Bridge Mode
- Description:
  - Containers connect to a virtual bridge (e.g., docker0) on the host.
- When to use:
  - Default Docker networking
  - Local development
  - Single-host container setups

### Host Mode
- Description:
  - Container shares the host network namespace.
  - No network isolation; container uses host IP and ports directly.
- When to use:
  - Performance-sensitive applications
  - Low-latency networking

### None Mode
- Description:
  - container could not communicate with internet
- When to use:
  - Fully isolated workloads
  - Security-sensitive batch jobs

### Overlay Mode
- Description:
  - Creates a virtual network across multiple pods.
  - Allows pods on different nodes to communicate as if on the same network.
- When to use:
  - Multi-node container clusters 
  - Distributed microservices

### CNI Plugins in Kubernetes
- Role of CNI:
  - CNI (Container Network Interface) defines how Kubernetes sets up pod networking. 
  - Responsible for IP assignment, routing, and network connectivity.
- Examples:
  - Calico
    - Flannel

---

## Question 10: Container Storage

### Named Volumes
- Persistence:
  - Persistent across container restarts and recreations.
- Performance:
  - Good and stable performance.
  - Managed by Docker or container runtime.
- Use cases:
  - Databases

### Bind Mounts
- Persistence:
  - Depends on the host filesystem.
  - Changes are immediately reflected on the host.
- Performance:
  - Very high (direct access to host filesystem).
- Use cases:
  - Local development

### tmpfs
- Persistence:
  - Non-persistent (stored in memory only).
  - Data is lost when the container stops.
- Performance:
  - Very fast (memory-based).
- Use cases:
  - Caches

### Summary Comparison
| Storage Type | Persistent | Performance | Typical Use Case |
|-------------|------------|-------------|------------------|
| Volume      | Y          | High        | db               |
| Bind Mount  | Y          | very high   | development      |
| tmpfs       | N          | very high   | cache            |

---

