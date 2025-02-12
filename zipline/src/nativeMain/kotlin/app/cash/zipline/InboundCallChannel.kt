/*
 * Copyright (C) 2021 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.cash.zipline

import app.cash.zipline.internal.bridge.CallChannel
import app.cash.zipline.internal.bridge.inboundChannelName
import app.cash.zipline.quickjs.JS_FreeAtom
import app.cash.zipline.quickjs.JS_FreeValue
import app.cash.zipline.quickjs.JS_GetGlobalObject
import app.cash.zipline.quickjs.JS_GetPropertyStr
import app.cash.zipline.quickjs.JS_Invoke
import app.cash.zipline.quickjs.JS_NewAtom
import app.cash.zipline.quickjs.JS_NewString
import kotlinx.cinterop.memScoped

internal class InboundCallChannel(
  private val quickJs: QuickJs,
) : CallChannel {
  private val context = quickJs.context

  override fun serviceNamesArray(): Array<String> {
    quickJs.checkNotClosed()

    val globalThis = JS_GetGlobalObject(context)
    val inboundChannel = JS_GetPropertyStr(context, globalThis, inboundChannelName)
    val property = JS_NewAtom(context, "serviceNamesArray")

    val jsResult = JS_Invoke(context, inboundChannel, property, 0, null)
    val kotlinResult = with(quickJs) { jsResult.toKotlinInstanceOrNull() } as Array<String>

    JS_FreeValue(context, jsResult)
    JS_FreeAtom(context, property)
    JS_FreeValue(context, inboundChannel)
    JS_FreeValue(context, globalThis)

    return kotlinResult
  }

  override fun invoke(
    instanceName: String,
    funName: String,
    encodedArguments: Array<String>,
  ): Array<String> {
    quickJs.checkNotClosed()

    val globalThis = JS_GetGlobalObject(context)
    val inboundChannel = JS_GetPropertyStr(context, globalThis, inboundChannelName)
    val property = JS_NewAtom(context, "invoke")
    val arg0 = JS_NewString(context, instanceName)
    val arg1 = JS_NewString(context, funName)
    val arg2 = with(quickJs) { encodedArguments.toJsValue() }

    val jsResult = memScoped {
      val args = allocArrayOf(arg0, arg1, arg2)
      JS_Invoke(context, inboundChannel, property, 3, args)
    }
    val kotlinResult = with(quickJs) { jsResult.toKotlinInstanceOrNull() } as Array<String>

    JS_FreeValue(context, jsResult)
    JS_FreeValue(context, arg2)
    JS_FreeValue(context, arg1)
    JS_FreeValue(context, arg0)
    JS_FreeAtom(context, property)
    JS_FreeValue(context, inboundChannel)
    JS_FreeValue(context, globalThis)

    return kotlinResult
  }

  override fun invokeSuspending(
    instanceName: String,
    funName: String,
    encodedArguments: Array<String>,
    callbackName: String,
  ) {
    quickJs.checkNotClosed()

    val globalThis = JS_GetGlobalObject(context)
    val inboundChannel = JS_GetPropertyStr(context, globalThis, inboundChannelName)
    val property = JS_NewAtom(context, "invokeSuspending")
    val arg0 = JS_NewString(context, instanceName)
    val arg1 = JS_NewString(context, funName)
    val arg2 = with(quickJs) { encodedArguments.toJsValue() }
    val arg3 = JS_NewString(context, callbackName)

    val jsResult = memScoped {
      val args = allocArrayOf(arg0, arg1, arg2, arg3)
      JS_Invoke(context, inboundChannel, property, 4, args)
    }

    JS_FreeValue(context, jsResult)
    JS_FreeValue(context, arg3)
    JS_FreeValue(context, arg2)
    JS_FreeValue(context, arg1)
    JS_FreeValue(context, arg0)
    JS_FreeAtom(context, property)
    JS_FreeValue(context, inboundChannel)
    JS_FreeValue(context, globalThis)
  }

  override fun disconnect(instanceName: String): Boolean {
    quickJs.checkNotClosed()

    val globalThis = JS_GetGlobalObject(context)
    val inboundChannel = JS_GetPropertyStr(context, globalThis, inboundChannelName)
    val property = JS_NewAtom(context, "disconnect")
    val arg0 = JS_NewString(context, instanceName)

    val jsResult = memScoped {
      val args = allocArrayOf(arg0)
      JS_Invoke(context, inboundChannel, property, 1, args)
    }
    val kotlinResult = with(quickJs) { jsResult.toKotlinInstanceOrNull() } as Boolean

    JS_FreeValue(context, jsResult)
    JS_FreeAtom(context, property)
    JS_FreeValue(context, inboundChannel)
    JS_FreeValue(context, globalThis)

    return kotlinResult
  }
}
