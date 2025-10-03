/*
 * Errors.kt
 * Error handling helpers for native PC/SC API
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
package au.id.micolous.kotlin.pcsc.native

import au.id.micolous.kotlin.pcsc.PCSCError
import au.id.micolous.kotlin.pcsc.internal.SCARDSTATUS
import au.id.micolous.kotlin.pcsc.internal.SCARD_S_SUCCESS
import kotlinx.cinterop.convert

internal inline fun wrapPCSCErrors(
    trueValue: SCARDSTATUS = SCARD_S_SUCCESS.convert(),
    falseValue: SCARDSTATUS? = null,
    f: () -> SCARDSTATUS,
): Boolean =
    when (val errorCode = f()) {
        trueValue -> true
        falseValue -> false
        else -> throw PCSCError.fromCode(errorCode)
    }

/*
 * This definition of `wrapPCSCErrors` is needed for macOS:
 *
 * - `SCARDSTATUS` is `int32_t` (which matches the return type of all `SCard*` functions
 * - `SCARD_S_*` macros are interpreted by Kotlin C/Interop as `uint32_t`.
 *
 * So we convert all error constants to their "signed" form...
 */
internal inline fun wrapPCSCErrors(
    trueValue: SCARDSTATUS = SCARD_S_SUCCESS.convert(),
    falseValue: UInt? = null,
    f: () -> SCARDSTATUS,
): Boolean = wrapPCSCErrors(trueValue, falseValue?.convert<SCARDSTATUS>(), f)

/*
 * This definition of `wrapPCSCErrors` is needed to work around overload resolution ambigity for
 * bare calls with only a `f` parameter introduced by the (previous) macOS work-around.
 */
internal inline fun wrapPCSCErrors(f: () -> SCARDSTATUS): Boolean =
    wrapPCSCErrors(falseValue = (null as SCARDSTATUS?), f = f)
