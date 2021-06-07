/*
* SPDX-License-Identifier: Apache-2.0
*
* The OpenSearch Contributors require contributions made to
* this file be licensed under the Apache-2.0 license or a
* compatible open source license.
*
* Modifications Copyright OpenSearch Contributors. See
* GitHub history for details.
*/
/*
 *    Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License").
 *    You may not use this file except in compliance with the License.
 *    A copy of the License is located at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    or in the "license" file accompanying this file. This file is distributed
 *    on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *    express or implied. See the License for the specific language governing
 *    permissions and limitations under the License.
 *
 */

package org.opensearch.indexmanagement.util

import org.apache.logging.log4j.LogManager
import org.opensearch.action.admin.cluster.node.info.NodesInfoRequest
import org.opensearch.action.admin.cluster.node.info.NodesInfoResponse
import org.opensearch.client.Client
import org.opensearch.cluster.node.DiscoveryNode
import org.opensearch.common.io.stream.NamedWriteableRegistry
import org.opensearch.common.network.NetworkModule
import org.opensearch.common.settings.Settings
import org.opensearch.common.transport.TransportAddress
import org.opensearch.env.Environment
import org.opensearch.http.HttpInfo
import org.opensearch.plugins.Plugin
import org.opensearch.test.OpenSearchTestCase
import org.opensearch.test.TestCluster
import org.opensearch.transport.MockTransportClient
import org.opensearch.transport.nio.MockNioTransportPlugin
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicInteger

class CustomExternalTestCluster(
    tempDir: Path?,
    additionalSettings: Settings,
    pluginClasses: Collection<Class<out Plugin?>?>,
    vararg transportAddresses: TransportAddress?
) : TestCluster(0) {
    private val client: MockTransportClient
    private val httpAddresses: Array<InetSocketAddress>
    private var clusterName: String? = null
    private var numDataNodes = 0
    private var numMasterAndDataNodes = 0
    override fun afterTest() {}
    override fun client(): Client {
        return client
    }

    override fun size(): Int {
        return httpAddresses.size
    }

    override fun numDataNodes(): Int {
        return numDataNodes
    }

    override fun numDataAndMasterNodes(): Int {
        return numMasterAndDataNodes
    }

    override fun httpAddresses(): Array<InetSocketAddress> {
        return httpAddresses
    }

    @Throws(IOException::class)
    override fun close() {
        client.close()
    }

    /**
     * This custom ExternalCluster class has ensureEstimatedStats() emptied out to prevent the transport client error
     * made from making a request using a closed client after the tests are complete.
     */
    override fun ensureEstimatedStats() {}

    override fun getClusterName(): String {
        return clusterName!!
    }

    override fun getClients(): Iterable<Client> {
        return setOf(client)
    }

    override fun getNamedWriteableRegistry(): NamedWriteableRegistry? {
        return client.namedWriteableRegistry
    }

    companion object {
        private val logger = LogManager.getLogger(
            CustomExternalTestCluster::class.java
        )
        private val counter = AtomicInteger()
        const val EXTERNAL_CLUSTER_PREFIX = "external_"
    }

    init {
        var pluginClasses = pluginClasses
        val clientSettingsBuilder: Settings.Builder = Settings.builder()
            .put(additionalSettings)
            .put(
                "node.name",
                "dev-dsk-bowenlan-2b-41a78fb8.us-west-2.amazon.com"
//                InternalTestCluster.TRANSPORT_CLIENT_PREFIX + EXTERNAL_CLUSTER_PREFIX +
//                    counter.getAndIncrement()
            )
            .put("client.transport.ignore_cluster_name", true)
            .put(Environment.PATH_HOME_SETTING.key, tempDir)
        logger.info("temp dir: $tempDir")
        val addMockTcpTransport = additionalSettings[NetworkModule.TRANSPORT_TYPE_KEY] == null
        if (addMockTcpTransport) {
            val transport: String = OpenSearchTestCase.getTestTransportType()
            clientSettingsBuilder.put(NetworkModule.TRANSPORT_TYPE_KEY, transport)
            if (pluginClasses.contains(MockNioTransportPlugin::class.java) == false) {
                pluginClasses = ArrayList(pluginClasses)
                if (transport == MockNioTransportPlugin.MOCK_NIO_TRANSPORT_NAME) {
                    pluginClasses.add(MockNioTransportPlugin::class.java)
                }
            }
        }
        val clientSettings = clientSettingsBuilder.build()
        val client = MockTransportClient(clientSettings, pluginClasses)
        try {
            client.addTransportAddresses(*transportAddresses)
            val nodeInfos: NodesInfoResponse = client.admin().cluster().prepareNodesInfo().clear()
                .addMetrics(NodesInfoRequest.Metric.SETTINGS.metricName(), NodesInfoRequest.Metric.HTTP.metricName())
                .get()
            httpAddresses = emptyArray()
            clusterName = nodeInfos.clusterName.value()
            var dataNodes = 0
            var masterAndDataNodes = 0
            for (i in nodeInfos.nodes.indices) {
                val nodeInfo = nodeInfos.nodes[i]
                httpAddresses.plus(nodeInfo.getInfo(HttpInfo::class.java).address().publishAddress().address())
                if (DiscoveryNode.isDataNode(nodeInfo.settings)) {
                    dataNodes++
                    masterAndDataNodes++
                } else if (DiscoveryNode.isMasterNode(nodeInfo.settings)) {
                    masterAndDataNodes++
                }
            }
            numDataNodes = dataNodes
            numMasterAndDataNodes = masterAndDataNodes
            this.client = client
            logger.info(
                "Setup ExternalTestCluster [{}] made of [{}] nodes",
                nodeInfos.clusterName.value(), size()
            )
        } catch (e: Exception) {
            client.close()
            throw e
        }
    }
}
