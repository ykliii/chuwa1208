# Concurrency & Reliability — Homework Answers (No Code)

Date: 2026-02-24

---

## Question 1: Thread vs Async (Non-blocking I/O)

### When to use multi-threading
Use threads when:
- **Work is CPU-bound** (lots of computation per request).
- You need **true parallelism on multiple cores** (Java/C/C++ CPU work).
- You have **blocking APIs** you cannot easily replace (legacy libraries, JDBC drivers, filesystem calls).
- The logic is naturally sequential and benefits from **simple, linear control flow**.

**Examples**
- Image/video transcoding, compression, ML feature extraction.
- Batch processing jobs (ETL transforms) where compute dominates.
- A service that must call a legacy blocking SDK (e.g., a payment gateway client that blocks on network I/O).

### When to use async / non-blocking I/O
Use async when:
- Work is **I/O-bound** (spends most time waiting on network/disk).
- You need **very high concurrency** (tens/hundreds of thousands of in-flight requests).
- Latency and throughput depend on efficiently using sockets without tying up OS threads.
- You can use libraries designed for non-blocking I/O (event loop, callbacks/futures).

**Examples**
- A high-throughput gateway/proxy handling many concurrent HTTP/WebSocket connections.
- Chat server maintaining long-lived connections (idle most of the time).
- Crawlers/fetchers performing many parallel network calls with small per-call computation.

### Practical rule of thumb
- **CPU-heavy** → fewer threads, scale with cores.
- **I/O-heavy** → async I/O or lightweight concurrency (e.g., virtual threads), to avoid “one OS thread per connection”.

---

## Question 2: Virtual Threads (Java 21)

### What are Virtual Threads?
**Virtual Threads** (Java 21) are lightweight threads managed by the JVM (Project Loom). They let you write blocking-style code while the JVM schedules many virtual threads onto a smaller set of **platform threads** (OS threads, sometimes called *carrier threads*).

### How they differ from platform threads
- **Platform thread**: 1:1 mapping to an OS thread. Creating thousands can be expensive (memory + scheduling overhead).
- **Virtual thread**: not 1:1. You can create **hundreds of thousands** (or more) because:
    - Stack is typically **grown/shrunk** as needed rather than reserving a large fixed stack per OS thread.
    - When a virtual thread blocks on many JDK blocking operations, the JVM can **unmount** it so the carrier thread can run something else.

### When you should use them
Virtual threads are excellent for:
- **Request-per-thread server style** with massive concurrency.
- Codebases with lots of **blocking I/O** (HTTP calls, database calls) where rewriting into async would be costly.
- Improving throughput/latency by eliminating the need for complicated async control flow while still scaling concurrency.

### When you should NOT (or be cautious) using them
Avoid or be cautious when:
- Work is **purely CPU-bound**.
- Code causes **carrier thread pinning** (long synchronized blocks around blocking calls, certain native calls).
- You rely heavily on large numbers of ThreadLocal values without understanding lifecycle impact.

---

## Question 3: Lock vs CAS

### Lock-based synchronization
Locks (e.g., `synchronized`, `ReentrantLock`) provide mutual exclusion:
- Simple mental model.
- Protect multiple variables as one invariant.
- Support fairness and condition variables.

**Downsides**
- Blocking and context switching.
- Deadlocks possible.
- Poor scalability under high contention.

### CAS (Compare-And-Swap)
CAS is a hardware atomic primitive used in non-blocking algorithms:
- Faster under low/moderate contention.
- Avoids blocking.
- Enables lock-free designs.

**Downsides**
- Harder to reason about.
- Retry loops under contention.
- ABA problem.
- Usually limited to single-location updates.

### When to choose
- Use **locks** for complex invariants and clarity.
- Use **CAS** for simple atomic updates on hot paths.

---

## Question 4: Deadlock Conditions and Lock Ordering

### Four necessary conditions
1. Mutual exclusion
2. Hold and wait
3. No preemption
4. Circular wait

All four must be present for deadlock to occur.

### How lock ordering prevents deadlock
Define a global ordering of locks and require all threads to acquire them in that order.  
This eliminates **circular wait**, therefore deadlock cannot occur.

---

## Question 5: Thread Pool Sizing

### Formula
Threads ≈ NCPU × UCPU × (1 + W/C)

Where:
- NCPU = number of cores
- UCPU = target CPU utilization
- W = wait time
- C = compute time

### A) CPU-bound task on 8 cores
W ≈ 0  
Threads ≈ 8

### B) I/O-bound (wait 100ms, compute 5ms)
W/C = 100/5 = 20  
Threads ≈ 8 × (1 + 20) = 168

---

## Question 6: Exponential Backoff with Jitter

### Definition
Exponential backoff increases retry delays exponentially (e.g., 50ms, 100ms, 200ms, 400ms...).  
Jitter adds randomness to the delay.

### Why jitter is important
Without jitter, clients retry in sync → retry storms.  
Jitter spreads retries over time and improves system stability.

---

## Question 7: Circuit Breaker Pattern

### CLOSED
Requests flow normally. Failures tracked.  
If threshold exceeded → OPEN.

### OPEN
Requests fail fast.  
After cool-down → HALF-OPEN.

### HALF-OPEN
Allow limited probe requests.  
If success → CLOSED.  
If failure → OPEN.

---

## Question 8: Exactly-Once Semantics

### Why exactly-once delivery is impossible
Network failures create ambiguity:
- Ack lost but message processed.
- Sender retries → duplicates.
- If no retry → possible loss.

Cannot guarantee both no-loss and no-duplicate in all failure cases.

### Achieving exactly-once processing
- Idempotency keys
- Deduplication storage
- Atomic state + offset commit
- Transactional outbox pattern

---

## Question 9: Caching Patterns

### Cache-Aside
App loads on miss and writes to DB then invalidates cache.  
Pros: simple.  
Cons: stale risk, stampede risk.

### Write-Through
Writes go to cache and DB synchronously.  
Pros: consistent reads.  
Cons: slower writes.

### Write-Behind
Writes go to cache, DB updated asynchronously.  
Pros: fast writes.  
Cons: possible data loss, complexity.

---

## Question 10: Cache Stampede

### Definition
Many requests miss simultaneously and overwhelm DB.

### Solutions
1. Single-flight/mutex per key
2. Staggered TTL or proactive refresh

---