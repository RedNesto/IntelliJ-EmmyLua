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

package com.tang.intellij.lua.psi.search

import com.intellij.openapi.progress.ProgressManager
import com.intellij.util.Processor
import com.intellij.util.QueryExecutor
import com.tang.intellij.lua.psi.LuaClassMethod
import com.tang.intellij.lua.psi.guessClassType
import com.tang.intellij.lua.search.SearchContext
import com.tang.intellij.lua.stubs.index.LuaClassMemberIndex
import com.tang.intellij.lua.ty.TyClass

class LuaOverridenMethodsSearchExecutor : QueryExecutor<LuaClassMethod, LuaOverridenMethodsSearch.SearchParameters> {
    override fun execute(searchParameters: LuaOverridenMethodsSearch.SearchParameters, processor: Processor<in LuaClassMethod>): Boolean {
        val method = searchParameters.method
        val context = SearchContext.get(method)
        val type = method.guessClassType(context)
        val methodName = method.name
        if (type != null && methodName != null) {
            TyClass.processSuperClass(type, context) { superType->
                ProgressManager.checkCanceled()
                val superTypeName = superType.className
                val superMethod = LuaClassMemberIndex.findMethod(superTypeName, methodName, context)
                if (superMethod == null) true else processor.process(superMethod)
            }
        }
        return false
    }
}
