/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.featureflags.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Collections;

import org.apache.sling.featureflags.Feature;
import org.apache.sling.featureflags.Features;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Constants;

public class FeatureManagerTest {

    private Feature createFeature(final String name) {
        final Feature f = Mockito.mock(Feature.class);
        Mockito.when(f.getName()).thenReturn(name);
        return f;
    }

    @Test
    public void testFeatures() {
        final Features features = new FeatureManager();
        assertEquals(0, features.getAllFeatures().size());
    }

    @Test
    public void testDuplicateFeatureName() {
        final FeatureManager features = new FeatureManager();
        final Feature f1 = createFeature("a");
        final Feature f2 = createFeature("b");
        final Feature f3 = createFeature("b");
        features.bindFeature(f1, Collections.singletonMap(Constants.SERVICE_ID, 1L));
        features.bindFeature(f2, Collections.singletonMap(Constants.SERVICE_ID, 2L));
        features.bindFeature(f3, Collections.singletonMap(Constants.SERVICE_ID, 3L));

        // a and b
        assertEquals(2, features.getAllFeatures().size());
        assertNotNull(features.getFeature("a"));
        assertSame(f1, features.getFeature("a"));
        assertNotNull(features.getFeature("b"));
        assertSame(f2, features.getFeature("b"));

        // remove a - b remains
        features.unbindFeature(f1, Collections.singletonMap(Constants.SERVICE_ID, 1L));
        assertEquals(1, features.getAllFeatures().size());
        assertNotNull(features.getFeature("b"));
        assertSame(f2, features.getFeature("b"));

        // remove higher b - lower b remains
        features.unbindFeature(f2, Collections.singletonMap(Constants.SERVICE_ID, 2L));
        assertEquals(1, features.getAllFeatures().size());
        assertNotNull(features.getFeature("b"));
        assertSame(f3, features.getFeature("b"));

        // remove lower b - no features
        features.unbindFeature(f3, Collections.singletonMap(Constants.SERVICE_ID, 3L));
        assertEquals(0, features.getAllFeatures().size());
    }
}
