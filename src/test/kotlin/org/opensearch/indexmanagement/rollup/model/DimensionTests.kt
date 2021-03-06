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

package org.opensearch.indexmanagement.rollup.model

import org.opensearch.indexmanagement.rollup.randomDateHistogram
import org.opensearch.indexmanagement.rollup.randomHistogram
import org.opensearch.indexmanagement.rollup.randomTerms
import org.opensearch.test.OpenSearchTestCase
import kotlin.test.assertFailsWith

class DimensionTests : OpenSearchTestCase() {

    fun `test date histogram empty fields`() {
        assertFailsWith(IllegalArgumentException::class, "Source and target field must not be empty") {
            randomDateHistogram().copy(sourceField = "", targetField = "")
        }

        assertFailsWith(IllegalArgumentException::class, "Source and target field must not be empty") {
            randomDateHistogram().copy(sourceField = "source", targetField = "")
        }

        assertFailsWith(IllegalArgumentException::class, "Source and target field must not be empty") {
            randomDateHistogram().copy(sourceField = "", targetField = "target")
        }
    }

    fun `test precisely one date histogram interval`() {
        assertFailsWith(IllegalArgumentException::class, "Must specify a fixed or calendar interval") {
            randomDateHistogram().copy(fixedInterval = null, calendarInterval = null)
        }

        assertFailsWith(IllegalArgumentException::class, "Can only specify a fixed or calendar interval") {
            randomDateHistogram().copy(fixedInterval = "30m", calendarInterval = "1d")
        }
    }

    fun `test histogram empty fields`() {
        assertFailsWith(IllegalArgumentException::class, "Source and target field must not be empty") {
            randomHistogram().copy(sourceField = "", targetField = "")
        }

        assertFailsWith(IllegalArgumentException::class, "Source and target field must not be empty") {
            randomHistogram().copy(sourceField = "source", targetField = "")
        }

        assertFailsWith(IllegalArgumentException::class, "Source and target field must not be empty") {
            randomHistogram().copy(sourceField = "", targetField = "target")
        }
    }

    fun `test histogram interval must be positive decimal`() {
        assertFailsWith(IllegalArgumentException::class, "Interval must be a positive decimal") {
            randomHistogram().copy(interval = 0.0)
        }

        assertFailsWith(IllegalArgumentException::class, "Interval must be a positive decimal") {
            randomHistogram().copy(interval = -1.3)
        }
    }

    fun `test terms empty fields`() {
        assertFailsWith(IllegalArgumentException::class, "Source and target field must not be empty") {
            randomTerms().copy(sourceField = "", targetField = "")
        }

        assertFailsWith(IllegalArgumentException::class, "Source and target field must not be empty") {
            randomTerms().copy(sourceField = "source", targetField = "")
        }

        assertFailsWith(IllegalArgumentException::class, "Source and target field must not be empty") {
            randomTerms().copy(sourceField = "", targetField = "target")
        }
    }
}
