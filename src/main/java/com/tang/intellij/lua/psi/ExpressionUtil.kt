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

package com.tang.intellij.lua.psi

import com.intellij.psi.tree.IElementType

data class ComputeResult(val kind: ComputeKind,
                         var bValue: Boolean = false,
                         var nValue: Float = 0f,
                         var sValue: String = "",
                         var expr: LuaExpr? = null) {
    val string: String get() = when (kind) {
        ComputeKind.Number -> {
            val int = nValue.toInt()
            if (int.compareTo(nValue) == 0) int.toString() else nValue.toString()
        }
        ComputeKind.Hash -> hashValue.toString()
        ComputeKind.String -> sValue
        ComputeKind.Bool -> bValue.toString()
        else -> sValue
    }

    val hashValue: UInt get() = java.lang.Float.floatToIntBits(nValue).toUInt()
}

enum class ComputeKind {
    String, Hash, Bool, Number, Nil, Other
}

class ExpressionUtil {
    companion object {

        fun compute(expr: LuaExpr): ComputeResult? {
            return when (expr) {
                is LuaLiteralExpr -> {
                    when (expr.kind) {
                        LuaLiteralKind.String -> ComputeResult(ComputeKind.String, sValue = expr.stringValue)
                        LuaLiteralKind.Hash -> ComputeResult(ComputeKind.Hash, nValue = ooatHashAsFloat(expr.stringValue), sValue = expr.stringValue)
                        LuaLiteralKind.Bool -> ComputeResult(ComputeKind.Bool, bValue = expr.boolValue)
                        LuaLiteralKind.Number -> ComputeResult(ComputeKind.Number, nValue = expr.numberValue)
                        LuaLiteralKind.Nil -> ComputeResult(ComputeKind.Nil, sValue = "nil")
                        else -> null
                    }
                }
                is LuaBinaryExpr -> {
                    val left = compute(expr.left!!) ?: return null
                    val rExpr = expr.right ?: return null
                    val right = compute(rExpr) ?: return null
                    val op = expr.binaryOp
                    return calcBinary(left, right, op.node.firstChildNode.elementType)
                }
                is LuaParenExpr -> {
                    val inner = expr.expr
                    if (inner != null) compute(inner) else null
                }
                else -> return ComputeResult(ComputeKind.Other, true, expr = expr)
            }
        }

        fun ooatHash(str: String): UInt {
            var hash = 0u
            val bytes = str.lowercase().toByteArray(Charsets.UTF_8)
            for (char in bytes) {
                hash += char.toUInt()
                hash += hash shl 10
                hash = hash xor (hash shr 6)
            }

            hash += hash shl 3
            hash = hash xor (hash shr 11)
            hash += hash shl 15
            println("${hash}, ${java.lang.Float.intBitsToFloat(hash.toInt())}, ${java.lang.Float.floatToIntBits(java.lang.Float.intBitsToFloat(hash.toInt())).toUInt()}")
            return hash
        }

        fun ooatHashAsFloat(str: String): Float {
            return java.lang.Float.intBitsToFloat(ooatHash(str).toInt())
        }

        private fun calcBinary(l: ComputeResult, r: ComputeResult, op: IElementType): ComputeResult? {
            var b = false
            var n = 0f
            var s = ""
            var k = l.kind
            var isValid = false

            when (op) {
                LuaTypes.OR -> {
                    return if (l.bValue) l else r
                }
                LuaTypes.AND -> {
                    return if (l.bValue) r else l
                }
                // +
                LuaTypes.PLUS -> {
                    if (l.kind == ComputeKind.Number && r.kind == ComputeKind.Number) {
                        n = l.nValue + r.nValue
                        isValid = true
                    } else if (l.kind == ComputeKind.Hash && r.kind == ComputeKind.Hash) {
                        n = java.lang.Float.intBitsToFloat((l.hashValue + r.hashValue).toInt())
                        k = ComputeKind.Number
                        isValid = true
                    } else {
                        isValid = false
                    }
                }
                // -
                LuaTypes.MINUS -> {
                    if (l.kind == ComputeKind.Number && r.kind == ComputeKind.Number) {
                        n = l.nValue - r.nValue
                        isValid = true
                    } else if (l.kind == ComputeKind.Hash && r.kind == ComputeKind.Hash) {
                        n = java.lang.Float.intBitsToFloat((l.hashValue - r.hashValue).toInt())
                        k = ComputeKind.Number
                        isValid = true
                    } else {
                        isValid = false
                    }
                }
                // *
                LuaTypes.MULT -> {
                    if (l.kind == ComputeKind.Number && r.kind == ComputeKind.Number) {
                        n = l.nValue * r.nValue
                        isValid = true
                    } else if (l.kind == ComputeKind.Hash && r.kind == ComputeKind.Hash) {
                        n = java.lang.Float.intBitsToFloat((l.hashValue * r.hashValue).toInt())
                        k = ComputeKind.Number
                        isValid = true
                    } else {
                        isValid = false
                    }
                }
                // /
                LuaTypes.DIV -> {
                    if (l.kind == ComputeKind.Number && r.kind == ComputeKind.Number) {
                        n = l.nValue / r.nValue
                        isValid = r.nValue != 0f
                    } else if (l.kind == ComputeKind.Hash && r.kind == ComputeKind.Hash) {
                        n = l.hashValue.toFloat() / r.hashValue.toFloat()
                        k = ComputeKind.Number
                        isValid = r.hashValue != 0u
                    } else {
                        isValid = false
                    }
                }
                // //
                LuaTypes.DOUBLE_DIV -> {
                    if (l.kind == ComputeKind.Number && r.kind == ComputeKind.Number) {
                        n = l.nValue / r.nValue
                        isValid = r.nValue != 0f
                        n = n.toInt().toFloat()
                    } else if (l.kind == ComputeKind.Hash && r.kind == ComputeKind.Hash) {
                        n = java.lang.Float.intBitsToFloat((l.hashValue / r.hashValue).toInt())
                        k = ComputeKind.Number
                        isValid = r.hashValue != 0u
                        n = n.toInt().toFloat()
                    } else {
                        isValid = false
                    }
                }
                // %
                LuaTypes.MOD -> {
                    if (l.kind == ComputeKind.Number && r.kind == ComputeKind.Number) {
                        n = l.nValue % r.nValue
                        isValid = true
                    } else if (l.kind == ComputeKind.Hash && r.kind == ComputeKind.Hash) {
                        n = java.lang.Float.intBitsToFloat((l.hashValue % r.hashValue).toInt())
                        k = ComputeKind.Number
                        isValid = true
                    } else {
                        isValid = false
                    }
                }
                // ..
                LuaTypes.CONCAT -> {
                    if (l.kind == ComputeKind.String) {
                        isValid = r.kind == ComputeKind.String || r.kind == ComputeKind.Number
                    } else if (r.kind == ComputeKind.String) {
                        isValid = l.kind == ComputeKind.Number
                    }
                    k = ComputeKind.String
                    if (isValid)
                        s = l.string + r.string
                }
            }

            b = b || when (k) {
                ComputeKind.Other,
                ComputeKind.Number,
                ComputeKind.Hash,
                ComputeKind.String -> true
                else -> false
            }

            return if (isValid) ComputeResult(k, b, n, s) else null
        }
    }
}
