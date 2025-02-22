/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.indexmanagement.indexstatemanagement.step

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.opensearch.action.support.master.AcknowledgedResponse
import org.opensearch.client.node.NodeClient
import org.opensearch.cluster.service.ClusterService
import org.opensearch.common.settings.Settings
import org.opensearch.commons.replication.action.StopIndexReplicationRequest
import org.opensearch.core.action.ActionListener
import org.opensearch.indexmanagement.indexstatemanagement.step.stopreplication.AttemptStopReplicationStep
import org.opensearch.indexmanagement.spi.indexstatemanagement.Step
import org.opensearch.indexmanagement.spi.indexstatemanagement.model.ManagedIndexMetaData
import org.opensearch.indexmanagement.spi.indexstatemanagement.model.StepContext
import org.opensearch.jobscheduler.spi.utils.LockService
import org.opensearch.script.ScriptService
import org.opensearch.test.OpenSearchTestCase

class AttemptStopReplicationStepTests : OpenSearchTestCase() {
    private val clusterService: ClusterService = mock()
    private val scriptService: ScriptService = mock()
    private val settings: Settings = Settings.EMPTY
    private val lockService: LockService = LockService(mock(), clusterService)

    fun `test stop replication step sets step status to completed when successful`() {
//        val stopReplicationResponse = AcknowledgedResponse(true)
//        val indicesAdminClient: IndicesAdminClient = mock()
        val client = getClient(true, false)

        runBlocking {
//            val replicationPluginInterface: ReplicationPluginInterface = mock()
//            whenever(replicationPluginInterface.stopReplication(any(), any(), any()))
//                .thenAnswer { invocation ->
//                    val listener = invocation.getArgument<ActionListener<AcknowledgedResponse>>(2)
//                    listener.onResponse(stopReplicationResponse) // Simulate a successful response
//                }
            val managedIndexMetaData = ManagedIndexMetaData(
                "test",
                "indexUuid",
                "policy_id",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            )
            val context = StepContext(
                managedIndexMetaData,
                clusterService,
                client,
                null,
                null,
                scriptService,
                settings,
                lockService,
            )
            val attemptStopReplicationStep = AttemptStopReplicationStep()
//            attemptStopReplicationStep.setReplicationPluginInterface(replicationPluginInterface)
            attemptStopReplicationStep.preExecute(logger, context).execute()
            val updatedManagedIndexMetaData = attemptStopReplicationStep.getUpdatedManagedIndexMetadata(managedIndexMetaData)
            assertEquals(
                "Step status is not COMPLETED",
                Step.StepStatus.COMPLETED,
                updatedManagedIndexMetaData.stepMetaData?.stepStatus,
            )
        }
    }

    fun `test stop replication step sets step status to failed when not acknowledged`() {
//        val stopReplicationResponse = AcknowledgedResponse(false)
//        val indicesAdminClient: IndicesAdminClient = mock()
        val client = getClient(false, false)
        println("Client class: " + client::class.java.name)

        runBlocking {
//            val replicationPluginInterface: ReplicationPluginInterface = mock()
//            whenever(replicationPluginInterface.stopReplication(any(), any(), any()))
//                .thenAnswer { invocation ->
//                    val listener = invocation.getArgument<ActionListener<AcknowledgedResponse>>(2)
//                    listener.onResponse(stopReplicationResponse) // Simulate a successful response
//                }
            val managedIndexMetaData = ManagedIndexMetaData(
                "test",
                "indexUuid",
                "policy_id",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            )
            val context = StepContext(
                managedIndexMetaData,
                clusterService,
                client,
                null,
                null,
                scriptService,
                settings,
                lockService,
            )
            val attemptStopReplicationStep = AttemptStopReplicationStep()
//            attemptStopReplicationStep.setReplicationPluginInterface(replicationPluginInterface)
            attemptStopReplicationStep.preExecute(logger, context).execute()
            val updatedManagedIndexMetaData = attemptStopReplicationStep.getUpdatedManagedIndexMetadata(managedIndexMetaData)
            assertEquals(
                "Step status is not FAILED",
                Step.StepStatus.FAILED,
                updatedManagedIndexMetaData.stepMetaData?.stepStatus,
            )
        }
    }

    fun `test stop replication step sets step status to failed when error thrown`() {
//        val indicesAdminClient: IndicesAdminClient = mock()
        val client = getClient(true, true)
//        val exception = Exception("Test exception")

        runBlocking {
//            val replicationPluginInterface: ReplicationPluginInterface = mock()
//            whenever(replicationPluginInterface.stopReplication(any(), any(), any()))
//                .thenAnswer { invocation ->
//                    val listener = invocation.getArgument<ActionListener<AcknowledgedResponse>>(2)
//                    listener.onFailure(exception)
//                }

            val managedIndexMetaData = ManagedIndexMetaData(
                "test",
                "indexUuid",
                "policy_id",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            )
            val context = StepContext(
                managedIndexMetaData,
                clusterService,
                client,
                null,
                null,
                scriptService,
                settings,
                lockService,
            )
            val attemptStopReplicationStep = AttemptStopReplicationStep()
//            attemptStopReplicationStep.setReplicationPluginInterface(replicationPluginInterface)
            attemptStopReplicationStep.preExecute(logger, context).execute()
            val updatedManagedIndexMetaData = attemptStopReplicationStep.getUpdatedManagedIndexMetadata(managedIndexMetaData)
            println("Step status for 3rd test: " + updatedManagedIndexMetaData.stepMetaData?.stepStatus)
            assertEquals(
                "Step status is not FAILED",
                Step.StepStatus.FAILED,
                updatedManagedIndexMetaData.stepMetaData?.stepStatus,
            )
            println("Step status for 3rd test: " + updatedManagedIndexMetaData.stepMetaData?.toString())
        }
    }

    private fun getClient(ack: Boolean, exception: Boolean): NodeClient = mock<NodeClient> {
        doAnswer { invocationOnMock ->
            val listener = invocationOnMock.getArgument<ActionListener<AcknowledgedResponse>>(2)
            if (exception) {
                listener.onFailure(java.lang.Exception())
            } else {
                if (ack) {
                    listener.onResponse(AcknowledgedResponse(true))
                } else {
                    listener.onResponse(AcknowledgedResponse(false))
                }
            }
            null
        }.whenever(this.mock).execute<StopIndexReplicationRequest, AcknowledgedResponse>(any(), any(), any())
    }
}
