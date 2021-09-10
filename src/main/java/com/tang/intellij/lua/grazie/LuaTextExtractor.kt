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

package com.tang.intellij.lua.grazie

import com.intellij.grazie.text.TextContent
import com.intellij.grazie.text.TextExtractor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.tang.intellij.lua.comment.psi.LuaDocCommentString
import com.tang.intellij.lua.comment.psi.LuaDocTypes
import com.tang.intellij.lua.comment.psi.api.LuaComment
import com.tang.intellij.lua.psi.LuaLiteralExpr
import com.tang.intellij.lua.psi.LuaLiteralKind
import com.tang.intellij.lua.psi.kind

class LuaTextExtractor : TextExtractor() {

    override fun buildTextContent(element: PsiElement, allowedDomains: MutableSet<TextContent.TextDomain>): TextContent? {
        if (TextContent.TextDomain.LITERALS in allowedDomains && element is LuaLiteralExpr && element.kind == LuaLiteralKind.String) {
            val rawText = element.text
            val range = TextRange.from(1, rawText.length - 2)
            return TextContent.psiFragment(TextContent.TextDomain.LITERALS, element, range)
        }

        if (element is LuaDocCommentString && TextContent.TextDomain.DOCUMENTATION in allowedDomains) {
            return element.string?.let { this.commentContent(it, TextContent.TextDomain.DOCUMENTATION) }
        }

        if (element is LuaComment) {
            if (TextContent.TextDomain.DOCUMENTATION in allowedDomains) {
                return element.node.findChildByType(LuaDocTypes.STRING)?.psi?.let { this.commentContent(it, TextContent.TextDomain.DOCUMENTATION) }
            }
        } else if (element is PsiComment && TextContent.TextDomain.COMMENTS in allowedDomains) {
            return commentContent(element, TextContent.TextDomain.COMMENTS)
        }

        return null
    }

    private fun commentContent(element: PsiElement, domain: TextContent.TextDomain): TextContent? {
        val rawText = element.text
        var prefixLength = 0
        for (c in rawText) {
            if (c != '-') {
                break
            }
            prefixLength++
        }
        val range = TextRange.from(prefixLength, rawText.length - prefixLength)
        return TextContent.psiFragment(domain, element, range)
    }
}
