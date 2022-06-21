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

package com.tang.intellij.lua.codeInsight.intention

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.util.IncorrectOperationException
import com.tang.intellij.lua.lang.type.LuaString
import com.tang.intellij.lua.psi.*
import org.jetbrains.annotations.Nls

/**
 *
 * Created by tangzx on 2017/2/11.
 */
class SwitchQuotesIntention : BaseIntentionAction() {
    @Nls
    override fun getFamilyName(): String {
        return "Switch quotes type"
    }

    override fun isAvailable(project: Project, editor: Editor, psiFile: PsiFile): Boolean {
        val stringLiteral = getStringLiteral(editor, psiFile)
            ?: return false
        text = when (stringLiteral.textToCharArray()[0]) {
            '"' -> "Switch to single quotes"
            '\'' -> "Switch to double quotes"
            else -> {
                assert(false) { "Unreachable" }
                familyName
            }
        }
        return true
    }

    private fun getStringLiteral(editor: Editor, psiFile: PsiFile): LuaLiteralExpr? {
        val offset = editor.caretModel.offset
        return LuaPsiTreeUtil.findElementOfClassAtOffset(psiFile, offset, LuaLiteralExpr::class.java, false)
            ?.takeIf { literal ->
                if (literal.kind != LuaLiteralKind.String) {
                    return@takeIf false
                }

                val text = literal.text
                if (text[0] == '"') {
                    return@takeIf !text.contains("\\'")
                }

                if (text[0] == '\'') {
                    return@takeIf !text.contains("\\\"")
                }

                return@takeIf false
            }
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, psiFile: PsiFile) {
        val stringLiteral = getStringLiteral(editor, psiFile) ?: return
        val psiText = stringLiteral.text
        when (psiText[0]) {
            '"' -> {
                val content = LuaString.getContent(psiText).value.replace("\\\"", "\"").replace("'", "\\'")
                val replacement = LuaElementFactory.createLiteral(project, "'$content'")
                stringLiteral.replace(replacement)
            }
            '\'' -> {
                val content = LuaString.getContent(psiText).value.replace("\\'", "'").replace("\"", "\\\"")
                val replacement = LuaElementFactory.createLiteral(project, "\"$content\"")
                stringLiteral.replace(replacement)
            }
        }
    }
}
