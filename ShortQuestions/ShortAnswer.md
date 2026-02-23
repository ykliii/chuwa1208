## 1. Core Concepts

### 1.1 Topic
A **Topic** is a logical category or stream of records in Kafka.  
Producers publish messages to a topic, and consumers subscribe to it.

---

### 1.2 Partition
A **Partition** is a physical subdivision of a topic.

- Each topic is split into multiple partitions.
- Each partition is an **ordered, immutable log**.
- Messages are appended sequentially.
- Ordering is guaranteed **within a partition only**.

Partitions enable:
- Horizontal scalability
- Parallel consumption
- High throughput

---

### 1.3 Broker
A **Broker** is a Kafka server.

- Stores partitions.
- Handles read/write requests.
- Replicates partitions for fault tolerance.
- A Kafka cluster consists of multiple brokers.

Each partition has:
- **Leader replica** (handles reads/writes)
- **Follower replicas** (replicate data)

---

### 1.4 Producer
A **Producer** publishes messages to a topic.

Responsibilities:
- Choose partition (via key or round-robin)
- Send message to broker
- Optionally enable idempotence

Producers do not directly talk to consumers.

---

### 1.5 Consumer
A **Consumer** subscribes to topics and reads messages.

Consumers:
- Pull data from brokers
- Track their own offset
- Can be part of a **consumer group**

---

### 1.6 Consumer Group
A **Consumer Group** is a set of consumers working together.

Key properties:
- Each partition is assigned to only **one consumer in a group**
- Enables parallel processing
- Different consumer groups can independently consume the same topic

---

### 1.7 Offset
An **Offset** is the position of a message in a partition.

- Monotonically increasing number
- Maintained per partition per consumer group
- Consumers commit offsets after processing

Kafka does not track which messages are “consumed” globally — only offsets per consumer group.

---

### 1.8 ZooKeeper (Legacy Architecture)
Historically, Kafka used **ZooKeeper** for:

- Broker coordination
- Leader election
- Metadata storage

Modern Kafka (KRaft mode) removes ZooKeeper and uses an internal consensus protocol.

---

# How These Work Together

1. Producer sends message to a Topic.
2. Topic is divided into Partitions.
3. Partitions live on Brokers.
4. Consumer Groups pull messages from partitions.
5. Consumers track Offset.
6. ZooKeeper (or KRaft) manages cluster metadata.

---

## 1. Given N (partitions) and M (consumers)

### Case 1: N ≥ M
- Each consumer gets at least one partition.
- Some consumers may handle multiple partitions.
- Full parallelism achieved.
- Ideal case.

### Case 2: N < M
- Some consumers remain inactive.
- Maximum parallelism = number of partitions.
- Extra consumers do nothing.

**Important Rule:**  
Number of active consumers ≤ number of partitions.

---

## 2. How Brokers Work with Topics

- Topics are logical constructs.
- Brokers store topic partitions physically.
- Each partition has replicas across brokers.
- One broker acts as leader for a partition.
- All reads/writes go through leader broker.

---

## 3. Push or Pull?

Kafka uses a **Pull Model**.

Consumers:
- Send fetch requests to brokers.
- Control read rate.
- Avoid overload.
- Can rewind offsets.

Advantages:
- Backpressure support
- Flexible consumption

---

## 4. How to Avoid Duplicate Consumption

Duplicates can occur due to:
- Consumer crash before committing offset
- Network failures

Solutions:

1. Enable idempotent producer
2. Use manual offset commit after processing
3. Use transactions (exactly-once semantics)
4. Make consumers idempotent (recommended)

Kafka guarantees:
- At-least-once by default
- Exactly-once with transactions

---

## 5. If Some Consumers in a Group Are Down

What happens:
- Kafka triggers **rebalance**
- Partitions reassigned to remaining consumers

Will data loss occur?
- No (if replication configured properly)
- Offsets stored in Kafka

May cause:
- Temporary pause during rebalance

---

## 6. If Entire Consumer Group Is Down

What happens:
- Messages remain in topic
- Offsets preserved

Will data loss occur?
- No, as long as retention period not expired

Kafka stores messages for configured retention time.

Data loss occurs only if:
- Retention expires
- Or replication factor insufficient and brokers fail

---

## 7. Consumer Lag

### Definition
Consumer Lag =  
Latest Offset − Committed Offset

Indicates:
- How far behind consumer is

Causes:
- Slow processing
- Insufficient consumers
- Downstream dependency issues

Solutions:
- Increase consumers (if partitions allow)
- Increase partitions
- Optimize processing
- Batch processing
- Improve hardware

---

## 8. How Kafka Tracks Message Delivery

Kafka does NOT track per-message acknowledgments.

Instead:
- Consumer commits offset after processing.
- Offset stored in internal topic: `__consumer_offsets`.

Delivery semantics:

| Mode | Guarantee |
|------|-----------|
| At most once | Commit before processing |
| At least once | Commit after processing |
| Exactly once | Use transactions |

Kafka tracks progress via offsets, not per-message ack.

---

## 9. Kafka vs RabbitMQ

### Architecture

| Feature     | Kafka           | RabbitMQ         |
|-------------|-----------------|------------------|
| Model       | Distributed log | Message queue    |
| Storage     | Disk-based      | Memory + disk    |
| Scalability | Very high       | Moderate         |
| Ordering    | Per partition   | Per queue        |
| Replay      | Yes             | Limited          |
| Throughput  | Very high       | Lower            |

Kafka:
- High throughput
- Event streaming
- Big data pipelines

RabbitMQ:
- Traditional message broker
- Complex routing (exchanges)
- Low latency task queue

---

# Kafka vs MySQL

### Messaging Framework vs Database

| Feature             | Kafka            | MySQL                  |
|---------------------|------------------|------------------------|
| Purpose             | Event streaming  | Transactional storage  |
| Write pattern       | Append-only log  | Random read/write      |
| Throughput          | Millions/sec     | Much lower             |
| Replay              | Yes              | No                     |
| Ordering            | Per partition    | Not log-structured     |
| Horizontal scaling  | Easy             | Harder                 |

Why Kafka instead of MySQL for messaging?

1. MySQL not built for streaming scale.
2. No replay mechanism.
3. Poor consumer fan-out model.
4. Tight coupling between producer and consumer.
5. High write contention.

Kafka is optimized for:
- High-throughput streaming
- Decoupled systems
- Distributed event processing
