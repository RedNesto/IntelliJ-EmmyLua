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

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.tang.intellij.test.fileTreeFromText
import org.junit.Test

class TestModule : TestCompletionBase() {

    @Test
    fun `test module type`() {
        fileTreeFromText("""
             --- moduleA.lua
             ---@module TypeA
             module("TypeA")

             name = "a"

             --- B.lua
             local a ---@type TypeA
             a.--[[caret]]
        """).createAndOpenFileWithCaretMarker()

        FileDocumentManager.getInstance().saveAllDocuments()
        myFixture.completeBasic()
        val elementStrings = myFixture.lookupElementStrings
        assertTrue(elementStrings!!.contains("name"))
    }

    @Test
    fun `test module field completion`() {
        fileTreeFromText("""
             --- moduleA.lua
             ---@module TypeA
             module("TypeA")

             name = "a"

             --[[caret]]
        """).createAndOpenFileWithCaretMarker()

        FileDocumentManager.getInstance().saveAllDocuments()
        myFixture.completeBasic()
        val elementStrings = myFixture.lookupElementStrings
        assertTrue(elementStrings!!.contains("name"))
    }

    @Test
    fun `test module members visibility`() {
        fileTreeFromText("""
             --- moduleA.lua
             ---@module TypeA
             module("TypeA")

             myFieldName = "a"

             function myFunction() end

             --- B.lua
             --[[caret]]
        """).createAndOpenFileWithCaretMarker()

        FileDocumentManager.getInstance().saveAllDocuments()
        myFixture.completeBasic()
        val elementStrings = myFixture.lookupElementStrings
        assertFalse(elementStrings!!.containsAll(listOf("myFieldName", "myFunction")))
    }
}
