/*
 * Copyright 2010, 2011 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jakewharton.android.mapsforge_fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;

/**
 * This class holds all patterns that can be rendered on the map. All bitmaps are created when the
 * MapPatterns constructor is called and are recycled when the recycle() method is called.
 */
class MapPatterns {
	private final Bitmap cemetery;
	private final Bitmap marsh;
	private final Bitmap military;
	private final Bitmap natureReserve;
	private final Bitmap woodConiferous;
	private final Bitmap woodDeciduous;
	private final Bitmap woodMixed;
	final Shader cemeteryShader;
	final Shader marshShader;
	final Shader militaryShader;
	final Shader natureReserveShader;
	final Shader woodConiferousShader;
	final Shader woodDeciduousShader;
	final Shader woodMixedShader;

	MapPatterns() {
		this.cemetery = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"patterns/cemetery.png"));
		if (this.cemetery == null) {
			this.cemeteryShader = null;
		} else {
			this.cemeteryShader = new BitmapShader(this.cemetery, TileMode.REPEAT,
					TileMode.REPEAT);
		}

		this.marsh = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"patterns/marsh.png"));
		if (this.marsh == null) {
			this.marshShader = null;
		} else {
			this.marshShader = new BitmapShader(this.marsh, TileMode.REPEAT, TileMode.REPEAT);
		}

		this.military = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"patterns/military.png"));
		if (this.military == null) {
			this.militaryShader = null;
		} else {
			this.militaryShader = new BitmapShader(this.military, TileMode.REPEAT,
					TileMode.REPEAT);
		}

		this.natureReserve = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"patterns/nature-reserve.png"));
		if (this.natureReserve == null) {
			this.natureReserveShader = null;
		} else {
			this.natureReserveShader = new BitmapShader(this.natureReserve, TileMode.REPEAT,
					TileMode.REPEAT);
		}

		this.woodConiferous = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"patterns/wood-coniferous.png"));
		if (this.woodConiferous == null) {
			this.woodConiferousShader = null;
		} else {
			this.woodConiferousShader = new BitmapShader(this.woodConiferous, TileMode.REPEAT,
					TileMode.REPEAT);
		}
		this.woodDeciduous = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"patterns/wood-deciduous.png"));
		if (this.woodDeciduous == null) {
			this.woodDeciduousShader = null;
		} else {
			this.woodDeciduousShader = new BitmapShader(this.woodDeciduous, TileMode.REPEAT,
					TileMode.REPEAT);
		}

		this.woodMixed = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"patterns/wood-mixed.png"));
		if (this.woodMixed == null) {
			this.woodMixedShader = null;
		} else {
			this.woodMixedShader = new BitmapShader(this.woodMixed, TileMode.REPEAT,
					TileMode.REPEAT);
		}
	}

	void recycle() {
		if (this.cemetery != null) {
			this.cemetery.recycle();
		}
		if (this.marsh != null) {
			this.marsh.recycle();
		}
		if (this.military != null) {
			this.military.recycle();
		}
		if (this.natureReserve != null) {
			this.natureReserve.recycle();
		}
		if (this.woodConiferous != null) {
			this.woodConiferous.recycle();
		}
		if (this.woodDeciduous != null) {
			this.woodDeciduous.recycle();
		}
		if (this.woodMixed != null) {
			this.woodMixed.recycle();
		}
	}
}