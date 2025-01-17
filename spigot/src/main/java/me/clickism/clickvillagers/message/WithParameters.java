/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.message;

import java.lang.annotation.Documented;

@Documented
public @interface WithParameters {
    String[] value();
}
