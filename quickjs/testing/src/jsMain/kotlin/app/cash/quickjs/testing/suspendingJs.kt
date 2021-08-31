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
package app.cash.quickjs.testing

import app.cash.quickjs.ktBridge
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class JsSuspendingEchoService(
  private val greeting: String
) : SuspendingEchoService {
  override suspend fun suspendingEcho(request: EchoRequest): EchoResponse {
    return EchoResponse("$greeting from suspending JavaScript, ${request.message}")
  }
}

@JsExport
fun prepareSuspendingJsBridges() {
  ktBridge.set<SuspendingEchoService>(
    "jsSuspendingEchoService",
    EchoJsAdapter,
    JsSuspendingEchoService("hello")
  )
}

@JsExport
fun callSuspendingEchoService(message: String) {
  val service = ktBridge.get<SuspendingEchoService>("jvmSuspendingEchoService", EchoJsAdapter)
  val logger = ktBridge.get<Logger>("testLogger", Logger.Adapter)
  GlobalScope.launch {
    val echoResponse = service.suspendingEcho(EchoRequest(message))
    logger.log("JavaScript received '${echoResponse.message}' in a suspending call from the JVM")
  }
}