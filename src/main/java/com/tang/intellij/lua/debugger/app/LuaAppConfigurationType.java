/*
 * Copyright (c) 2017. tangzx(love.tangzx@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tang.intellij.lua.debugger.app;

import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.SimpleConfigurationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyValue;
import com.tang.intellij.lua.lang.LuaIcons;
import org.jetbrains.annotations.NotNull;

/**
 * Created by tangzx on 2017/5/7.
 */
public class LuaAppConfigurationType extends SimpleConfigurationType {

    protected LuaAppConfigurationType() {
        super("lua.app", "Lua Application", "Lua Application Runner", NotNullLazyValue.createValue(() -> LuaIcons.FILE));
    }

    @Override
    public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new LuaAppRunConfiguration(project, this);
    }

    @NotNull
    public static LuaAppConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(LuaAppConfigurationType.class);
    }
}
