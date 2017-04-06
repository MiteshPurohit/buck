/*
 * Copyright 2016-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.autodeps;

import static org.junit.Assert.assertEquals;

import com.facebook.buck.testutil.integration.ProjectWorkspace;
import com.facebook.buck.testutil.integration.TemporaryPaths;
import com.facebook.buck.testutil.integration.TestDataHelper;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

/**
 * The {@code buck autodeps} command does not generate deps for all programming languages; however,
 * end users may write their own tools to generate {@code BUCK.autodeps} files that should still be
 * consumed by Buck.
 */
public class CustomAutodepsIntegrationTest {
  @Rule
  public TemporaryPaths tmpFolder = new TemporaryPaths();

  /**
   * Tests a case where a set of {@code python_library()} and {@code python_binary()} rules specify
   * {@code autodeps=True} to load their {@code deps} from a {@code BUCK.autodeps} file that is
   * generated by something other than {@code buck autodeps}. This ensures that external users can
   * perform their own experiments with auto-generating dependencies.
   */
  @Test
  public void useCustomAutodepsForPythonRules() throws IOException {
    ProjectWorkspace workspace = TestDataHelper.createProjectWorkspaceForScenario(
        this, "custom_autodeps", tmpFolder);
    workspace.setUp();

    ProjectWorkspace.ProcessResult result = workspace.runBuckCommand("run", "//:app");
    result.assertSuccess();
    assertEquals(
        "If the dependencies were loaded correctly, main.py should load code from lib1.py.",
        "Hello!",
        result.getStdout().trim());
  }

}
