

## 1. In the CAP theorem, 

- ### Q: why is Partition Tolerance (P) considered mandatory in a real distributed system? 
- ### A: P is considered mandatory because network partition cannot be avoided in real world, therefore, partition tolerance is mandatory for a real distributed system.
- ### Q: Given that P is mandatory, what is the actual trade-off a system designer must make?
- ### A: We must make trade-off between C (consistency) and A (availability)

----

## 2. Explain the difference between linearizability and eventual consistency.

- Linearizability:
  - operations are executed in real-time order
  - example: ZooKeeper, Google Spanner
- Eventual consistency:
  - operations execution order may diverge in replicas, but ensure every replica converge to same state eventually if no new updates occur.
  - example: Amazon Dynamo

----

## 3. What is vector clock? How does it determine whether two events are causally related or concurrent? Why can't simply use physical timestamps?

- vector clock is a structure use vector to determine the causal order for events.
- given two received events and their vector clock, if:
  - for all node i: clk1\[i\] $\leq$ clk2\[i\], and strictly less than for at least one node, then we say these two events are causally related;
  - else, we say they are concurrent.
- physical timestamps fails because in distributed system, we do not have reliable clock/timestamps due to clock drift could happen in every machine.

----

## 4. In single-leader replication, what is the split-brain problem during failover, and why is it dangerous?

- Split-brain problem happens when a leader is dead/unaccessible but then recover after a new leader already be elected. It still thought itself as leader and execute write operations.
- At such situation, we have two leader in our system and both of them process write operation, which would cause data inconsistency.

----

## 5. Leaderless replication

- $W + R > N$ means:
  - $W$: a write operation is considered successful only after it is executed on $W$ machines
  - $R$: a read operation gets its result from at least $R$ machines
  - $N$: the total number of machines we have in our system
- if the quorum condition not satisfied, reads may miss the latest write and return stale or conflicting versions result.

----

## 6. Compare 2PC and Saga pattern

- Saga avoids locking resources during the commit/abort process.
- in return, Saga sacrifices:
  - Atomicity: intermediate states are visible
  - Isolation: partial results may be observed by other services

----

## 7. Raft consensus algorithm

- a candidate need its log to be at least as up-to-date as the majority of the cluster in order to win an election because:
  - an operation considered as committed and persist if the majority of the cluster have logged it
  - a candidate to win an election should ensure it will not lose any committed operations, so its log has to be up-to-date
  - this guarantee leader completeness/ safety

----

## 8. what is a CRDT?

- CRDT is a structure that mathematically guarantee clusters will be eventually consistency
  - All updates are commutative
  - Merges are associative and idempotent
- G-Counter maintain a map for all nodes and their values, and always merge to the maximum value.

----

## 9. Circuit Breaker pattern

### Definition

- The **Circuit Breaker pattern** is a resilience design pattern used in distributed and microservices architectures to **prevent cascading failures** when a downstream service becomes slow, overloaded, or unavailable.
- Instead of continuously sending requests that are likely to fail or time out, the circuit breaker **fails fast** and temporarily stops requests to the unhealthy service.

---

### Motivation

In a microservices system:
- Services depend on other services over the network
- Failures are common and often slow (timeouts)
- Repeated failed calls can exhaust:
    - threads
    - connection pools
    - CPU and memory

Without protection, a failure in one service can **propagate and take down the entire system**.

---

### Three States of a Circuit Breaker

- Closed
  - Normal operating state
  - All requests are allowed to pass through
  - The circuit breaker monitors:
      - failure rate
      - timeout rate
      - latency
  - If failures exceed a predefined threshold, the circuit transitions to **Open**

- Open
  - Requests are **immediately rejected** (fail fast)
  - No calls are sent to the downstream service
  - Prevents further load on an already failing service
  - After a fixed cooldown period, the circuit transitions to **Half-Open**

- Half-Open
  - A small number of test requests are allowed through
  - Purpose: check whether the downstream service has recovered
  - Outcomes:
      - If requests succeed → transition to **Closed**
      - If requests fail → transition back to **Open**

### How Circuit Breaker Prevents Cascading Failures

- Avoids blocking threads on repeated slow or failing calls
- Reduces resource exhaustion in upstream services
- Isolates failures to a limited part of the system
- Enables faster recovery by giving downstream services time to stabilize


----

## 10. Partitioning

- range-based partitioning: split data in different partitions based on their value range
  - e.g partition1: [0-100], partition2: [101-200], ...
- hash-based partitioning: split data in different partitions based on their hashed value
- consistent hashing solve the problem that when a new sever is added, all existed data need to be rehashed.