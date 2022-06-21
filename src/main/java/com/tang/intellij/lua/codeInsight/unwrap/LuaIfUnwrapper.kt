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

package com.tang.intellij.lua.codeInsight.unwrap

import com.intellij.codeInsight.unwrap.AbstractUnwrapper
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.elementType
import com.intellij.psi.util.prevLeaf
import com.tang.intellij.lua.psi.LuaBlock
import com.tang.intellij.lua.psi.LuaIfStat
import com.tang.intellij.lua.psi.LuaTypes

class LuaIfUnwrapper : AbstractUnwrapper<LuaIfUnwrapper.Context>("Unwrap 'if...then'") {

    override fun isApplicableTo(e: PsiElement): Boolean = e is LuaIfStat

    override fun createContext(): Context = Context()

    override fun doUnwrap(element: PsiElement, context: Context) {
        val ifStat = element as LuaIfStat
        val thenBlock = ifStat.childrenOfType<LuaBlock>()
            .firstOrNull { it.prevLeaf(true).elementType == LuaTypes.THEN }
            ?: return
        context.extractElement(thenBlock, ifStat)
        context.delete(ifStat)
    }

    class Context : AbstractContext() {

        override fun isWhiteSpace(element: PsiElement): Boolean = element is PsiWhiteSpace
    }
}
