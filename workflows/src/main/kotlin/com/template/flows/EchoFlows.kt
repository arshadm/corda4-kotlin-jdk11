package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap


// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class EchoInitiatorFlow(private val message: String, private val recipient: String) : FlowLogic<Void?>() {
    override val progressTracker = ProgressTracker()
    @Suspendable
    override fun call(): Void? {
        val identityService = serviceHub.identityService
        val recipientParty = identityService.partiesFromName(recipient, true)
            .iterator().next()

        val session = initiateFlow(recipientParty)
        session.send(message)
        println("=================================")

        val echo = session.receive(String::class.java).unwrap {s -> s}
        println("---------------------------------")
        println("Echo: $echo")

        return null
    }
}

@InitiatedBy(EchoInitiatorFlow::class)
class EchoResponderFlow(private val counterpartySession: FlowSession) : FlowLogic<Void?>() {
    @Suspendable
    override fun call(): Void? {
        val message = counterpartySession.receive(String::class.java).unwrap { s -> s }
        val reversed = message.reversed()

        println("Inbound message: $message")
        println("Reversed: $reversed")
        counterpartySession.send(reversed)

        return null
    }
}

