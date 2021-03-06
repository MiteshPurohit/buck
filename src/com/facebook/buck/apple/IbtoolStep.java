/*
 * Copyright 2015-present Facebook, Inc.
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

package com.facebook.buck.apple;

import com.facebook.buck.io.filesystem.ProjectFilesystem;
import com.facebook.buck.model.BuildTarget;
import com.facebook.buck.shell.ShellStep;
import com.facebook.buck.step.ExecutionContext;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * {@link ShellStep} implementation which invokes Apple's {@code ibtool} utility to compile {@code
 * XIB} files to {@code NIB} files.
 */
class IbtoolStep extends ShellStep {

  private final ProjectFilesystem filesystem;
  private final ImmutableMap<String, String> environment;
  private final ImmutableList<String> ibtoolCommand;
  private final Path input;
  private final Path output;
  private final ImmutableList<String> ibtoolModuleParams;
  private final ImmutableList<String> additionalParams;

  public IbtoolStep(
      BuildTarget buildTarget,
      ProjectFilesystem filesystem,
      ImmutableMap<String, String> environment,
      List<String> ibtoolCommand,
      List<String> ibtoolModuleParams,
      List<String> additionalParams,
      Path input,
      Path output) {
    super(Optional.of(buildTarget), filesystem.getRootPath());
    this.filesystem = filesystem;
    this.environment = environment;
    this.ibtoolCommand = ImmutableList.copyOf(ibtoolCommand);
    this.input = input;
    this.output = output;
    this.ibtoolModuleParams = ImmutableList.copyOf(ibtoolModuleParams);
    this.additionalParams = ImmutableList.copyOf(additionalParams);
  }

  @Override
  protected ImmutableList<String> getShellCommandInternal(ExecutionContext context) {
    ImmutableList.Builder<String> commandBuilder = ImmutableList.builder();

    commandBuilder.addAll(ibtoolCommand);
    commandBuilder.add(
        "--output-format", "human-readable-text", "--notices", "--warnings", "--errors");
    commandBuilder.addAll(ibtoolModuleParams);
    commandBuilder.addAll(additionalParams);
    commandBuilder.add(filesystem.resolve(output).toString(), filesystem.resolve(input).toString());

    return commandBuilder.build();
  }

  @Override
  public ImmutableMap<String, String> getEnvironmentVariables(ExecutionContext context) {
    return environment;
  }

  @Override
  public String getShortName() {
    return "ibtool";
  }
}
