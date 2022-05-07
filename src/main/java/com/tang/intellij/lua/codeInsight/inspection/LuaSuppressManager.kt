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

package com.tang.intellij.lua.codeInsight.inspection

import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfType
import com.tang.intellij.lua.comment.psi.LuaDocTagSuppress
import com.tang.intellij.lua.comment.psi.LuaDocTypes
import com.tang.intellij.lua.psi.LuaCommentOwner

class LuaSuppressManager : InspectionSuppressor {
    override fun getSuppressActions(element: PsiElement?, toolId: String): Array<SuppressQuickFix> =
        arrayOf(LuaSuppressQuickFix(toolId))

    override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean {
        val commentOwner = element.parentOfType<LuaCommentOwner>(true)
        val suppresses = commentOwner?.comment?.findTags(LuaDocTagSuppress::class.java)
            ?: return false
        for (suppress in suppresses) {
            var child = suppress.firstChild
            while (child != null) {
                if (child.node.elementType == LuaDocTypes.ID) {
                    if (child.textMatches(toolId)) {
                        return true
                    }
                }
                child = child.nextSibling
            }
        }
        return false
    }

    private class LuaSuppressQuickFix(private val toolId: String) : SuppressQuickFix {

        override fun getFamilyName(): String = "Suppress inspection"

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val document = PsiDocumentManager.getInstance(project).getDocument(descriptor.psiElement.containingFile)
                ?.takeIf { it.isWritable }
                ?: return
            val commentOwner = getCommentOwner(descriptor.psiElement)
                ?: return

            val suppress = commentOwner.comment?.findTag(LuaDocTagSuppress::class.java)
            if (suppress != null) {
                document.insertString(suppress.textRange.endOffset, ",$toolId")
            } else {
                document.insertString(commentOwner.textRange.startOffset, "---@suppress $toolId\n")
            }
        }

        override fun isAvailable(project: Project, context: PsiElement): Boolean =
            context.isValid && context.isWritable && getCommentOwner(context) != null

        private fun getCommentOwner(context: PsiElement): LuaCommentOwner? {
            val adjustedContext = if (context is PsiWhiteSpace) {
                PsiTreeUtil.skipWhitespacesBackward(context) ?: context
            } else {
                context
            }
            return adjustedContext.parentOfType(true)
        }

        override fun isSuppressAll(): Boolean = toolId.equals("ALL", true)
    }
}
