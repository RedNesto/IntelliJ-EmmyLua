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

package com.tang.intellij.lua.codeInsight

import com.intellij.lang.ExpressionTypeProvider
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.SyntaxTraverser
import com.intellij.util.SlowOperations
import com.tang.intellij.lua.LuaBundle
import com.tang.intellij.lua.psi.LuaExpr
import com.tang.intellij.lua.psi.LuaPsiElement
import com.tang.intellij.lua.psi.LuaTypeGuessable
import com.tang.intellij.lua.search.SearchContext

class LuaTypeProvider : ExpressionTypeProvider<LuaPsiElement>() {

    override fun getInformationHint(element: LuaPsiElement): String = when (element) {
        is LuaTypeGuessable -> {
            val searchContext = SearchContext.get(element.project)
            val guessedType = SlowOperations.allowSlowOperations(ThrowableComputable { element.guessType(searchContext) })
            StringUtil.escapeXmlEntities(guessedType.displayName)
        }
        else -> StringUtil.escapeXmlEntities("<unknown>")
    }

    override fun getErrorHint(): String = LuaBundle.message("ui.error.hint.no.expression.found")

    override fun getExpressionsAt(elementAt: PsiElement): List<LuaPsiElement> =
        SyntaxTraverser.psiApi().parents(elementAt)
            .filter(LuaExpr::class.java)
            .toList()
}
