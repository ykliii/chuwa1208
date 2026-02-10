
## Question 1: Three Pillars of Observability

### Definition
- Observability is the ability ot understand the internal state of a system by examining its external outputs

### Three Pillars
- Metrics
  - "What is happening"
- logs
  - "What happened"
- traces
  - "How did it happen"

---

## Question 2: Metrics Data Types

- Counter
  - counting events that only increase
- Gauge
  - counting values that can go up and down
- Histogram
  - evaluate distribution of values
- Summary
  - pre-calculated percentiles
---

## Question 3: Structured Logging

- recording log as JSON instead natural language text
- benefits:
  - Machine-parseable
  - Easy to query and filter
  - Can be indexed efficiently
  - Supports correlation across services
---

## Question 4: Distributed Tracing Concepts

- Trace:
  - represents the entire journey of a request
  - contains multiple spans
- Span:
  - single unit of work within a trace
  - start time + duration + operation name + tags + logs + baggage
- Context Propagation
  - Mechanism to pass trace context across service boundaries

---

## Question 5: Prometheus Pull Model

- Prometheus server periodically sends HTTP request for each target service to pull their metrics data.
- pros:
  - simple, decoupling
  - configurable
  - built-in health detection
  - well-suited for dynamic environments
- cons:
  - network accessibility requirement
  - poor fit for short-lived jobs
  - scalability considerations

---

## Question 6: Control Plane Components

- api server
  - exposes the Kubernetes REST API
  - validates and processes requests, then persists cluster state to etcd
  - controller/scheduler also make api call to it to get LIST/WATCH
- etcd
  - distributed, consistent key-value store
  - stores the entire cluster state
  - single source of truth for the cluster
- scheduler
  - assign pods to nodes
- controller manager
  - run a set of controllers continuously reconcile desired state with actual state
  - node controller, replicaSet controller, deployment controller

---

## Question 7: kubelet

- kubelet is the primary node-level agent that runs on every Kubernetes worker node.
- after get the pods assigned by scheduler to a worker node, kubelet
  - create containers
  - monitors them
  - reports status back
- to ensure that containers described by PodSpecs are running and healthy on that node.

---

## Question 8: etcd

- distributed, consistent key-value store:
  - all persistent state of the cluster
- single source of truth for all cluster state

---

## Question 9: kube-proxy

- kube-proxy is a node-level network component that runs on every Kubernetes worker node.
- to implement Kubernetes Service networking by routing traffic destined for a Service to one of the backing Pods.

---

## Question 10: Scheduler

- filter → score → make decision
- key factors:
  - CPU, memory
  - node labels and affinities
  - taints and tolerations
  - pod affinity
  - topology spread constraints
  - storage and volume constraints
  - current cluster utilization

---

