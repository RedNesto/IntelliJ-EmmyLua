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

package com.tang.intellij.lua.editor

import com.intellij.lang.Language
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider
import com.tang.intellij.lua.lang.LuaLanguage
import com.tang.intellij.lua.psi.*

class LuaBreadcrumbsProvider : BreadcrumbsProvider {

    private val supportedLanguages: Array<Language> = arrayOf(LuaLanguage.INSTANCE)

    override fun getLanguages(): Array<Language> = supportedLanguages

    private val MAX_LEN = 15

    private fun cutText(txt: String): String {
        var t = txt
        if (t.length > MAX_LEN) {
            t = t.substring(0, MAX_LEN) + "..."
        }
        return t
    }

    override fun getElementInfo(element: PsiElement): String {
        return when (element) {
            is LuaBlock -> {
                when (val blockParent = element.parent) {
                    is LuaFuncBody ->{
                        when (val parent2 = blockParent.parent) {
                            is LuaClassMethodDef -> "${parent2.classMethodName.text}${parent2.paramSignature}"
                            is LuaClosureExpr -> "function${parent2.paramSignature}"
                            is LuaFuncDef -> "function ${parent2.name}${parent2.paramSignature}"
                            is LuaLocalFuncDef -> "local function ${parent2.name}${parent2.paramSignature}"
                            else -> "<?>"
                        }
                    }
                    is LuaIfStat -> {
                        val prevVisibleLeaf = PsiTreeUtil.prevVisibleLeaf(element)
                        when (prevVisibleLeaf?.node?.elementType) {
                            LuaTypes.ELSE -> "else"
                            LuaTypes.THEN -> {
                                val expr = LuaPsiTreeUtil.skipWhitespacesAndCommentsBackward(prevVisibleLeaf)!!
                                val prefix = LuaPsiTreeUtil.skipWhitespacesAndCommentsBackward(expr)!!

                                "${prefix.text} ${cutText(expr.text)} then"
                            }
                            else -> "if"
                        }
                    }
                    is LuaForAStat -> "for"
                    is LuaForBStat -> "for"
                    is LuaRepeatStat -> "repeat"
                    is LuaWhileStat -> "while"
                    else -> "<?>"
                }
            }
            is LuaTableField -> element.fieldName ?: element.idExpr?.text
                ?: element.parent?.childrenOfType<LuaTableField>()?.indexOf(element).toString()
            is LuaLocalDef -> "local " + (element.nameList?.nameDefList?.joinToString { it.name } ?: "<?>")
            is LuaAssignStat -> element.varExprList.exprList.joinToString { (it as? LuaNameExpr)?.name ?: "<?>" }
            else -> element.text
        }
    }

    override fun acceptElement(element: PsiElement): Boolean {
        return when (element) {
            is LuaBlock -> true
            is LuaTableField -> true
            is LuaLocalDef -> true
            is LuaAssignStat -> true
            else -> false
        }
    }
}
