package com.template

import net.corda.testing.node.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.template.states.TemplateState
import net.corda.core.node.services.vault.QueryCriteria
import com.template.flows.EchoInitiatorFlow
import net.corda.core.concurrent.CordaFuture
import net.corda.core.identity.CordaX500Name
import net.corda.core.node.services.Vault.StateStatus


class FlowTests {
    private lateinit var network: MockNetwork
    private lateinit var a: StartedMockNode
    private lateinit var b: StartedMockNode

    @Before
    fun setup() {
        network = MockNetwork(MockNetworkParameters(cordappsForAllNodes = listOf(
                TestCordapp.findCordapp("com.template.contracts"),
                TestCordapp.findCordapp("com.template.flows")
        ),
            notarySpecs = listOf(MockNetworkNotarySpec(CordaX500Name("Notary","London","GB")))))
        a = network.createPartyNode()
        b = network.createPartyNode()
        network.runNetwork()
    }

    @After
    fun tearDown() {
        network.stopNodes()
    }

    @Test
    fun `DummyTest`() {
        val message = "Hello World"
        val recipient = b.info.legalIdentities[0].name.commonName!!
        val flow = EchoInitiatorFlow(message, recipient)
        val future: CordaFuture<Void?> = a.startFlow(flow)
        network.runNetwork()
   }
}
