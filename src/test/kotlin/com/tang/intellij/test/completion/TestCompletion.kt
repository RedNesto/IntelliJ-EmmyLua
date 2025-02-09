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

package com.tang.intellij.test.completion

import org.junit.Test

/**
 *
 * Created by tangzx on 2017/4/23.
 */
class TestCompletion : TestCompletionBase() {

    @Test
    fun testLocalCompletion() {
        myFixture.configureByFiles("testCompletion.lua")
        doTestWithResult(listOf("a", "b", "func1"))
    }

    @Test
    fun testGlobalCompletion() {
        //test 1
        myFixture.configureByFiles("globals.lua")
        myFixture.configureByText("test.lua", "<caret>")

        doTestWithResult(listOf("gVar1", "gVar2"))

        //test 2
        myFixture.configureByFiles("globals.lua")
        myFixture.configureByText("test.lua", "gVar2.<caret>")

        doTestWithResult(listOf("aaa", "bbb", "ccc"))
    }

    @Test
    fun testSelfCompletion() {
        myFixture.configureByFiles("testSelf.lua")

        doTestWithResult(listOf("self:aaa", "self:abb"))
    }

    @Test
    fun testParamCompletion() {
        myFixture.configureByFiles("testParam.lua")

        doTestWithResult(listOf("param1", "param2"))
    }

    @Test
    fun testAnnotation() {
        val code = "---@class MyClass\n" +
                "---@field public name string\n" +
                "local s = {}\n" +
                "function s:method()end\n" +
                "function s.staticMethod()end\n" +
                "---@type MyClass\n" +
                "local instance\n"

        // fields and methods
        myFixture.configureByText("test.lua", code + "instance.<caret>")
        doTestWithResult(listOf("name", "method", "staticMethod"))


        // methods
        myFixture.configureByText("test.lua", code + "instance:<caret>")
        doTestWithResult("method")
    }

    @Test
    fun testAnnotationArray() {
        myFixture.configureByFiles("testAnnotationArray.lua", "class.lua")

        doTestWithResult(listOf("name", "age", "sayHello"))
    }

    @Test
    fun testAnnotationFun() {
        myFixture.configureByFiles("testAnnotationFun.lua", "class.lua")

        doTestWithResult(listOf("name", "age", "sayHello"))
    }

    @Test
    fun testAnnotationDict() {
        myFixture.configureByFiles("testAnnotationDict.lua", "class.lua")

        doTestWithResult(listOf("name", "age", "sayHello"))
    }

    @Test
    fun testAnonymous() {
        doTest("""
            --- testAnonymous.lua

            local function test()
                local v = xx()
                v.pp = 123
                return v
            end
            local v = test()
            v.--[[caret]]
        """) {
            assertTrue("pp" in it)
        }
    }

    @Test
    fun `test doc table 1`() {
        doTest("""
             --- doc_table_test_A.lua

             ---@return { name:string, value:number }
             function getData() end

             --- doc_table_test_B.lua
             local a = getData()
             a.--[[caret]]
        """) {
            assertTrue("name" in it)
        }
    }
}
