package com.sksamuel.pulsar4s.avro

import java.util.UUID

import com.sksamuel.pulsar4s._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class AvroProducerConsumerTest extends AnyFunSuite with Matchers {

  test("producer and consumer synchronous round trip") {

    val client = PulsarClient("pulsar://localhost:6650")
    val topic = Topic("persistent://sample/standalone/ns1/test_" + UUID.randomUUID)

    val producer = client.producer[Cafe](ProducerConfig(topic))
    val cafe = Cafe("le table", Place(1, "Paris"))
    val messageId = producer.send(cafe)
    producer.close()

    val consumer = client.consumer[Cafe](ConsumerConfig(topics = Seq(topic), subscriptionName = Subscription.generate))
    consumer.seek(MessageId.earliest)
    val msg = consumer.receive
    msg.get.value shouldBe cafe
    consumer.close()

    client.close()
  }

}
