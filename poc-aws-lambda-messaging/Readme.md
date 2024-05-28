# SQS code examples for the SDK for Java

## Overview
This README discusses how to run and test the AWS SDK for Java (v2) examples for Amazon SQS.

Amazon Simple Queue Service (SQS) is a fully managed message queuing service that enables decoupling and communication between the components of a distributed system. We can send, store, and receive messages at any volume, without losing messages or requiring other systems to be available.

Amazon SQS provides two types of message queues:

**Standard queues:** They provide maximum throughput, best-effort ordering, and at-least-once delivery. The standard queue is the default queue type in SQS. When using standard queues, we should design our applications to be idempotent so that there is no negative impact when processing the same message more than once.

**FIFO queues:** FIFO (First-In-First-Out) queues are used for messaging when the order of operations and events exchanged between applications is important, or in situations where we want to avoid processing duplicate messages. FIFO queues guarantee that messages are processed exactly once, in the exact order that they are sent.

- Common properties which we configure: 
**_Dead-letter Queue:_** A dead-letter queue is a queue that one or more source queues can use for messages that are not consumed successfully. 
**_Dead-letter Queue Redrive:_** We use this configuration to define the time after which unconsumed messages are moved out of an existing dead-letter queue back to their source queues.
**_Visibility Timeout:_** The visibility timeout is a period during which a message received from a queue by one consumer is not visible to the other message consumers. 
**_Message Retention Period:_** The amount of time for which a message remains in the queue. The messages in the queue should be received and processed before this time is crossed.
**_DelaySeconds:_** The length of time for which the delivery of all messages in the queue is delayed.
**_MaximumMessageSize:_** The limit on the size of a message in bytes that can be sent to SQS before being rejected.
**_ReceiveMessageWaitTimeSeconds:_** The length of time for which a message receiver waits for a message to arrive. This value defaults to 0 and can take any value from 0 to 20 seconds.
**_Short and long polling:_** Amazon SQS uses short polling and long polling mechanisms to receive messages from a queue. Short polling returns immediately, even if the message queue being polled is empty, while long polling does not return a response until a message arrives in the message queue, or the long polling period expires. The SQS client uses short polling by default. Long polling is preferable to short polling in most cases.

## ⚠️ Important
* The SDK for Java examples perform AWS operations for the account and AWS Region for which you've specified credentials. Running these examples might incur charges on your account. For details about the charges that you can expect for a given service and API operation, see [AWS Pricing](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

The credential provider used in all code examples is the default credentials provider. For more information, see [Using credentials](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html).

### Single actions

The following examples use the **MemoryDbClient** object:

- [Create a cluster](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/memorydb/src/main/java/com/example/memorydb/CreateCluster.java) (createCluster command)
- [Create a snapshot](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/memorydb/src/main/java/com/example/memorydb/CreateSnapshot.java) (createSnapshot command)
- [Delete a snapshot](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/memorydb/src/main/java/com/example/memorydb/DeleteCluster.java) (deleteCluster command)
- [Describe a cluster](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/memorydb/src/main/java/com/example/memorydb/DeleteCluster.java) (describeClusters command)
- [Describe a snapshot](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/memorydb/src/main/java/com/example/memorydb/DeleteCluster.java) (describeSnapshots command)
- [Describe a specific cluster](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/memorydb/src/main/java/com/example/memorydb/DeleteCluster.java) (describeClusters command)


## Run the MemoryDB Java files

Some of these examples perform *destructive* operations on AWS resources, such as deleting a cluster. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. We recommend creating separate test-only resources when experimenting with these examples.

To run these examples, set up your development environment. For more information, 
see [Get started with the SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html). 


 ## Test the MemoryDB Java files

You can test the Java code examples for Amazon MemoryDB for Redis by running a test file named **MemoryDBTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and might incur charges on your account._

 ### Properties file
Before running the JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define a crawler name used in the tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **clusterName** - The name of the cluster.   
- **nodeType** - The compute and memory capacity of the nodes in the cluster.
- **subnetGroupName** - The name of the subnet group to be used for the cluster.
- **aclName** - The name of the Access Control List to associate with the cluster.
- **snapShotName** - The name of the snapshot.

## Additional resources
* [Developer Guide - AWS SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html).
* [Developer Guide - Amazon MemoryDB for Redis](https://docs.aws.amazon.com/memorydb/latest/devguide/getting-started.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

