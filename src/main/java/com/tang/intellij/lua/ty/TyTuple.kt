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

package com.tang.intellij.lua.ty

import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.tang.intellij.lua.search.SearchContext

class TyTuple(val list: List<ITy>) : Ty(TyKind.Tuple) {

    constructor(vararg types: ITy) : this(types.toList())

    val size: Int get() {
        return list.size
    }

    override fun substitute(substitutor: ITySubstitutor): ITy {
        val list = list.map { it.substitute(substitutor) }
        return TyTuple(list)
    }

    override fun accept(visitor: ITyVisitor) {
        visitor.visitTuple(this)
    }

    override fun acceptChildren(visitor: ITyVisitor) {
        list.forEach { it.accept(visitor) }
    }

    override fun subTypeOf(other: ITy, context: SearchContext, strict: Boolean): Boolean {
        if (other is TyTuple && other.size == size) {
            for (i in 0 until size) {
                if (!list[i].subTypeOf(other.list[i], context, strict)) {
                    return false
                }
            }
            return true
        }
        return false
    }

    override fun hashCode(): Int {
        var hash = 0
        for (ty in list) {
            hash = hash * 31 + ty.hashCode()
        }
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (other is TyTuple && other.size == size) {
            for (i in 0 until size) {
                if (list[i] != other.list[i]) {
                    return false
                }
            }
            return true
        }
        return super.equals(other)
    }
}

object TyTupleSerializer : TySerializer<TyTuple>() {
    override fun deserializeTy(flags: Int, stream: StubInputStream): TyTuple {
        val size = stream.readByte().toInt()
        val list = mutableListOf<ITy>()
        for (i in 0 until size) list.add(Ty.deserialize(stream))
        return TyTuple(list)
    }

    override fun serializeTy(ty: TyTuple, stream: StubOutputStream) {
        stream.writeByte(ty.list.size)
        ty.list.forEach { Ty.serialize(it, stream) }
    }
}
