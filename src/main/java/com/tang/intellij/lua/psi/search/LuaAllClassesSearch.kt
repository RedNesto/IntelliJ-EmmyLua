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
import com.intellij.openapi.project.Project
import com.intellij.psi.search.SearchScope
import com.intellij.psi.search.searches.ExtensibleQueryFactory
import com.intellij.util.Query
import com.intellij.util.QueryExecutor
import com.tang.intellij.lua.ty.ITyClass

/**
 *
 * Created by tangzx on 2017/3/29.
 */
class LuaAllClassesSearch : ExtensibleQueryFactory<ITyClass, LuaAllClassesSearch.SearchParameters>(EP_NAME) {

    class SearchParameters(val searchScope: SearchScope, val project: Project)

    companion object {

        private val EP_NAME = ExtensionPointName.create<QueryExecutor<ITyClass, SearchParameters>>("com.tang.intellij.lua.luaAllClassesSearch")
        private val INSTANCE = LuaAllClassesSearch()

        fun search(searchScope: SearchScope, project: Project): Query<ITyClass> {
            return INSTANCE.createUniqueResultsQuery(SearchParameters(searchScope, project))
        }
    }
}
