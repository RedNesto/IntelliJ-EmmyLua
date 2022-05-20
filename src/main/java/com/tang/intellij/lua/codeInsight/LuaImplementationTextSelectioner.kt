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

import com.intellij.codeInsight.hint.ImplementationTextSelectioner
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.tang.intellij.lua.psi.LuaFuncDef
import kotlin.math.max
import kotlin.math.min

class LuaImplementationTextSelectioner : ImplementationTextSelectioner {

    override fun getTextStartOffset(element: PsiElement): Int {
        val function = element.parentOfType<LuaFuncDef>()
        if (function != null) {
            return function.textRange.startOffset
        }

        val document = PsiDocumentManager.getInstance(element.project).getDocument(element.containingFile)
        if (document != null) {
            val endLine = document.getLineNumber(element.textRange.startOffset) - 10
            return document.getLineStartOffset(max(endLine, 0))
        }

        return element.textRange.endOffset
    }

    override fun getTextEndOffset(element: PsiElement): Int {
        val function = element.parentOfType<LuaFuncDef>()
        if (function != null) {
            return function.textRange.endOffset
        }

        val document = PsiDocumentManager.getInstance(element.project).getDocument(element.containingFile)
        if (document != null) {
            val endLine = document.getLineNumber(element.textRange.endOffset) + 10
            return document.getLineEndOffset(min(endLine, document.lineCount - 1))
        }

        return element.textRange.endOffset
    }
}
