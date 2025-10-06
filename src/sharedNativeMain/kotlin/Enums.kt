/*
 * Context.kt
 * Native implementation of PC/SC API enumerations
 *
 * Copyright 2019 Michael Farrell <micolous+git@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.id.micolous.kotlin.pcsc

import au.id.micolous.kotlin.pcsc.internal.DWORD
import au.id.micolous.kotlin.pcsc.internal.SCARD_EJECT_CARD
import au.id.micolous.kotlin.pcsc.internal.SCARD_LEAVE_CARD
import au.id.micolous.kotlin.pcsc.internal.SCARD_PROTOCOL_ANY
import au.id.micolous.kotlin.pcsc.internal.SCARD_PROTOCOL_RAW
import au.id.micolous.kotlin.pcsc.internal.SCARD_PROTOCOL_T0
import au.id.micolous.kotlin.pcsc.internal.SCARD_PROTOCOL_T1
import au.id.micolous.kotlin.pcsc.internal.SCARD_PROTOCOL_T15
import au.id.micolous.kotlin.pcsc.internal.SCARD_PROTOCOL_UNDEFINED
import au.id.micolous.kotlin.pcsc.internal.SCARD_PROTOCOL_UNSET
import au.id.micolous.kotlin.pcsc.internal.SCARD_RESET_CARD
import au.id.micolous.kotlin.pcsc.internal.SCARD_SCOPE_SYSTEM
import au.id.micolous.kotlin.pcsc.internal.SCARD_SCOPE_TERMINAL
import au.id.micolous.kotlin.pcsc.internal.SCARD_SCOPE_USER
import au.id.micolous.kotlin.pcsc.internal.SCARD_SHARE_DIRECT
import au.id.micolous.kotlin.pcsc.internal.SCARD_SHARE_EXCLUSIVE
import au.id.micolous.kotlin.pcsc.internal.SCARD_SHARE_SHARED
import au.id.micolous.kotlin.pcsc.internal.SCARD_UNPOWER_CARD
import kotlinx.cinterop.convert

actual enum class Scope(
    internal val v: DWORD,
) {
    User(SCARD_SCOPE_USER.convert<DWORD>()),
    Terminal(SCARD_SCOPE_TERMINAL.convert<DWORD>()),
    System(SCARD_SCOPE_SYSTEM.convert<DWORD>()),
}

actual enum class ShareMode(
    internal val v: DWORD,
) {
    Shared(SCARD_SHARE_SHARED.convert<DWORD>()),
    Exclusive(SCARD_SHARE_EXCLUSIVE.convert<DWORD>()),
    Direct(SCARD_SHARE_DIRECT.convert<DWORD>()),
}

actual enum class Protocol(
    internal val v: DWORD,
) {
    Unset(SCARD_PROTOCOL_UNSET.convert<DWORD>()),
    Any(SCARD_PROTOCOL_ANY.convert<DWORD>()),
    T0(SCARD_PROTOCOL_T0.convert<DWORD>()),
    T1(SCARD_PROTOCOL_T1.convert<DWORD>()),
    T15(SCARD_PROTOCOL_T15.convert<DWORD>()),
    Raw(SCARD_PROTOCOL_RAW.convert<DWORD>()),
    Undefined(SCARD_PROTOCOL_UNDEFINED.convert<DWORD>()),
}

internal fun DWORD.toProtocol(): Protocol =
    Protocol.entries.find { it.v == this } ?: throw PCSCError(PCSCErrorCode.F_INTERNAL_ERROR)

internal fun Set<Protocol>.toDWord(): DWORD = map { it.v }.reduce { acc, it -> acc or it }

actual enum class DisconnectDisposition(
    internal val v: DWORD,
) {
    Leave(SCARD_LEAVE_CARD.convert<DWORD>()),
    Reset(SCARD_RESET_CARD.convert<DWORD>()),
    Unpower(SCARD_UNPOWER_CARD.convert<DWORD>()),
    Eject(SCARD_EJECT_CARD.convert<DWORD>()),
}

actual enum class Initialization(
    internal val v: DWORD,
) {
    Leave(SCARD_LEAVE_CARD.convert<DWORD>()),
    Reset(SCARD_RESET_CARD.convert<DWORD>()),
    Unpower(SCARD_UNPOWER_CARD.convert<DWORD>()),
}
