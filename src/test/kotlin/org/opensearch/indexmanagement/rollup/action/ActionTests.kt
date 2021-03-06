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
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.opensearch.indexmanagement.rollup.action

import org.opensearch.indexmanagement.rollup.action.delete.DeleteRollupAction
import org.opensearch.indexmanagement.rollup.action.get.GetRollupAction
import org.opensearch.indexmanagement.rollup.action.get.GetRollupsAction
import org.opensearch.indexmanagement.rollup.action.index.IndexRollupAction
import org.opensearch.indexmanagement.rollup.action.start.StartRollupAction
import org.opensearch.indexmanagement.rollup.action.stop.StopRollupAction
import org.opensearch.test.OpenSearchTestCase

class ActionTests : OpenSearchTestCase() {

    fun `test delete action name`() {
        assertNotNull(DeleteRollupAction.INSTANCE.name())
        assertEquals(DeleteRollupAction.INSTANCE.name(), DeleteRollupAction.NAME)
    }

    fun `test index action name`() {
        assertNotNull(IndexRollupAction.INSTANCE.name())
        assertEquals(IndexRollupAction.INSTANCE.name(), IndexRollupAction.NAME)
    }

    fun `test get action name`() {
        assertNotNull(GetRollupAction.INSTANCE.name())
        assertEquals(GetRollupAction.INSTANCE.name(), GetRollupAction.NAME)
    }

    fun `test get(s) action name`() {
        assertNotNull(GetRollupsAction.INSTANCE.name())
        assertEquals(GetRollupsAction.INSTANCE.name(), GetRollupsAction.NAME)
    }

    fun `test start action name`() {
        assertNotNull(StartRollupAction.INSTANCE.name())
        assertEquals(StartRollupAction.INSTANCE.name(), StartRollupAction.NAME)
    }

    fun `test stop action name`() {
        assertNotNull(StopRollupAction.INSTANCE.name())
        assertEquals(StopRollupAction.INSTANCE.name(), StopRollupAction.NAME)
    }
}
