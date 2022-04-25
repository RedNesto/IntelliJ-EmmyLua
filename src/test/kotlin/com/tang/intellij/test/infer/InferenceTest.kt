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

package com.tang.intellij.test.infer

import com.intellij.psi.util.PsiTreeUtil
import com.tang.intellij.lua.psi.LuaClassMethod
import com.tang.intellij.lua.psi.LuaIndexExpr
import com.tang.intellij.lua.psi.LuaNameExpr
import com.tang.intellij.lua.search.SearchContext
import com.tang.intellij.lua.ty.ITyFunction
import com.tang.intellij.lua.ty.Ty
import com.tang.intellij.lua.ty.TyTuple
import com.tang.intellij.lua.ty.TyUnion
import com.tang.intellij.test.LuaTestBase
import org.intellij.lang.annotations.Language
import org.junit.Test

class InferenceTest : LuaTestBase() {

    @Test
    fun `test index expr function return tuple`() {
        @Language("LUA")
        val code = """
            local obj = {}
            
            obj.test = function()
                return 1, true
            end
        """.trimIndent()

        val file = myFixture.configureByText("index_expr_fun_ret_tuple.lua", code)

        val classMember = PsiTreeUtil.findChildrenOfType(file, LuaIndexExpr::class.java).singleOrNull()
        assertNotNull(classMember)

        val searchContext = SearchContext.get(classMember!!)
        val guess = classMember.guessType(searchContext)
        assertInstanceOf(guess, ITyFunction::class.java)
        assertEquals((guess as ITyFunction).mainSignature.returnTy, TyTuple(Ty.NUMBER, Ty.BOOLEAN))
    }

    @Test
    fun `test class method name return tuple`() {
        @Language("LUA")
        val code = """
            local obj = {}
            
            function obj.test()
                return 1, true
            end
        """.trimIndent()

        val file = myFixture.configureByText("class_method_name_ret_tuple.lua", code)

        val classMember = PsiTreeUtil.findChildrenOfType(file, LuaClassMethod::class.java).singleOrNull()
        assertNotNull(classMember)

        val searchContext = SearchContext.get(classMember!!)
        val guess = classMember.guessType(searchContext)
        assertInstanceOf(guess, ITyFunction::class.java)
        assertEquals((guess as ITyFunction).mainSignature.returnTy, TyTuple(Ty.NUMBER, Ty.BOOLEAN))
    }

    @Test
    fun `test global assign function return tuple`() {
        @Language("LUA")
        val code = """
            test = function()
                return 1, true
            end
        """.trimIndent()

        val file = myFixture.configureByText("global_assign_fun_ret_tuple.lua", code)

        val classMember = PsiTreeUtil.findChildrenOfType(file, LuaNameExpr::class.java).singleOrNull()
        assertNotNull(classMember)

        val searchContext = SearchContext.get(classMember!!)
        val guess = classMember.guessType(searchContext)
        assertInstanceOf(guess, TyUnion::class.java)
        val guessFun = (guess as TyUnion).getChildTypes().last()
        assertInstanceOf(guessFun, ITyFunction::class.java)
        assertEquals((guessFun as ITyFunction).mainSignature.returnTy, TyTuple(Ty.NUMBER, Ty.BOOLEAN))
    }

    @Test
    fun `test global function return tuple`() {
        @Language("LUA")
        val code = """
            function test()
                return 1, true
            end
        """.trimIndent()

        val file = myFixture.configureByText("global_fun_ret_tuple.lua", code)

        val classMember = PsiTreeUtil.findChildrenOfType(file, LuaClassMethod::class.java).singleOrNull()
        assertNotNull(classMember)

        val searchContext = SearchContext.get(classMember!!)
        val guess = classMember.guessType(searchContext)
        assertInstanceOf(guess, ITyFunction::class.java)
        assertEquals((guess as ITyFunction).mainSignature.returnTy, TyTuple(Ty.NUMBER, Ty.BOOLEAN))
    }
}
