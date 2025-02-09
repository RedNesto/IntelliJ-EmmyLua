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

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.search.searches.ExtensibleQueryFactory
import com.intellij.util.Query
import com.intellij.util.QueryExecutor
import com.tang.intellij.lua.psi.LuaClassMethod

class LuaOverridenMethodsSearch : ExtensibleQueryFactory<LuaClassMethod, LuaOverridenMethodsSearch.SearchParameters>(EP_NAME) {
    class SearchParameters(val method: LuaClassMethod, val deep: Boolean)

    companion object {
        private val EP_NAME = ExtensionPointName.create<QueryExecutor<LuaClassMethod, SearchParameters>>("com.tang.intellij.lua.luaOverridenMethodsSearch")
        private val INSTANCE = LuaOverridenMethodsSearch()

        fun search(methodDef: LuaClassMethod, deep: Boolean = true): Query<LuaClassMethod> =
                INSTANCE.createUniqueResultsQuery(SearchParameters(methodDef, deep))
    }
}
