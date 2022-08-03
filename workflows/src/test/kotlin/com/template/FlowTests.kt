package com.template

import com.template.flows.EchoInitiatorFlow
import com.template.states.TemplateState
import net.corda.core.identity.CordaX500Name
import net.corda.core.node.services.Vault.StateStatus
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.testing.node.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Future


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
        a = network.createPartyNode(CordaX500Name("PartyA", "Acme", "London", "GB"))
        b = network.createPartyNode(CordaX500Name("PartyB", "Acme", "London", "GB"))
        network.runNetwork()
    }

    @After
    fun tearDown() {
        network.stopNodes()
    }

    @Test
    fun dummyTest() {
        val flow = EchoInitiatorFlow(b.info.legalIdentities[0].name.commonName!!)
        val future: Future<SignedTransaction> = a.startFlow(flow)
        network.runNetwork()

        //successful query means the state is stored at node b's vault. Flow went through.
        val inputCriteria: QueryCriteria = QueryCriteria.VaultQueryCriteria().withStatus(StateStatus.UNCONSUMED)
        val states = b.services.vaultService.queryBy(TemplateState::class.java, inputCriteria).states[0].state.data
    }
}
