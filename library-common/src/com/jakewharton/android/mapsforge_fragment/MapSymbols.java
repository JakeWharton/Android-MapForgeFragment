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

/**
 * This class holds all symbols that can be rendered on the map. All bitmaps are created when the
 * MapSymbols constructor is called and are recycled when the recycle() method is called.
 */
class MapSymbols {
	final Bitmap airport;
	final Bitmap atm;
	final Bitmap bakery;
	final Bitmap bank;
	final Bitmap bicycle_rental;
	final Bitmap bus;
	final Bitmap bus_sta;
	final Bitmap cafe;
	final Bitmap cave_entrance;
	final Bitmap chair_lift_2;
	final Bitmap church;
	final Bitmap cinema;
	final Bitmap fastfood;
	final Bitmap firebrigade;
	final Bitmap fountain;
	final Bitmap helipad;
	final Bitmap hospital;
	final Bitmap hostel;
	final Bitmap hotel;
	final Bitmap information;
	final Bitmap library;
	final Bitmap oneway;
	final Bitmap parking;
	final Bitmap peak;
	final Bitmap petrolStation;
	final Bitmap pharmacy;
	final Bitmap playground;
	final Bitmap postbox;
	final Bitmap postoffice;
	final Bitmap pub;
	final Bitmap railway_crossing;
	final Bitmap recycling;
	final Bitmap restaurant;
	final Bitmap school;
	final Bitmap shelter;
	final Bitmap supermarket;
	final Bitmap telephone;
	final Bitmap theatre;
	final Bitmap toilets;
	final Bitmap traffic_signal;
	final Bitmap university;
	final Bitmap viewpoint;
	final Bitmap vulcan;
	final Bitmap windmill;

	MapSymbols() {
		this.airport = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/airport.png"));
		this.atm = BitmapFactory
				.decodeStream(getClass().getResourceAsStream("symbols/atm.png"));
		this.bakery = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/bakery.png"));
		this.bank = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/bank.png"));
		this.bicycle_rental = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/bicycle_rental.png"));
		this.bus = BitmapFactory
				.decodeStream(getClass().getResourceAsStream("symbols/bus.png"));
		this.bus_sta = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/bus_sta.png"));
		this.cafe = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/cafe.png"));
		this.cave_entrance = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/cave_entrance.png"));
		this.chair_lift_2 = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/chair_lift_2.png"));
		this.church = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/church.png"));
		this.cinema = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/cinema.png"));
		this.oneway = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/oneway.png"));
		this.fastfood = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/fastfood.png"));
		this.firebrigade = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/firebrigade.png"));
		this.fountain = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/fountain.png"));
		this.helipad = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/helipad.png"));
		this.hospital = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/hospital.png"));
		this.hostel = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/hostel.png"));
		this.hotel = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/hotel.png"));
		this.information = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/information.png"));
		this.library = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/library.png"));
		this.parking = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/parking.png"));
		this.peak = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/peak.png"));
		this.petrolStation = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/petrolStation.png"));
		this.pharmacy = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/pharmacy.png"));
		this.playground = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/playground.png"));
		this.postbox = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/postbox.png"));
		this.postoffice = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/postoffice.png"));
		this.pub = BitmapFactory
				.decodeStream(getClass().getResourceAsStream("symbols/pub.png"));
		this.railway_crossing = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/railway-crossing.png"));
		this.recycling = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/recycling.png"));
		this.restaurant = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/restaurant.png"));
		this.school = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/school.png"));
		this.shelter = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/shelter.png"));
		this.supermarket = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/supermarket.png"));
		this.telephone = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/telephone.png"));
		this.theatre = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/theatre.png"));
		this.toilets = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/toilets.png"));
		this.traffic_signal = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/traffic_signal.png"));
		this.university = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/university.png"));
		this.viewpoint = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/viewpoint.png"));
		this.vulcan = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/vulcan.png"));
		this.windmill = BitmapFactory.decodeStream(getClass().getResourceAsStream(
				"symbols/windmill.png"));
	}

	void recycle() {
		if (this.airport != null) {
			this.airport.recycle();
		}
		if (this.atm != null) {
			this.atm.recycle();
		}
		if (this.bakery != null) {
			this.bakery.recycle();
		}
		if (this.bank != null) {
			this.bank.recycle();
		}
		if (this.bicycle_rental != null) {
			this.bicycle_rental.recycle();
		}
		if (this.bus != null) {
			this.bus.recycle();
		}
		if (this.bus_sta != null) {
			this.bus_sta.recycle();
		}
		if (this.cafe != null) {
			this.cafe.recycle();
		}
		if (this.cave_entrance != null) {
			this.cave_entrance.recycle();
		}
		if (this.chair_lift_2 != null) {
			this.chair_lift_2.recycle();
		}
		if (this.church != null) {
			this.church.recycle();
		}
		if (this.cinema != null) {
			this.cinema.recycle();
		}
		if (this.oneway != null) {
			this.oneway.recycle();
		}
		if (this.fastfood != null) {
			this.fastfood.recycle();
		}
		if (this.firebrigade != null) {
			this.firebrigade.recycle();
		}
		if (this.fountain != null) {
			this.fountain.recycle();
		}
		if (this.helipad != null) {
			this.helipad.recycle();
		}
		if (this.hospital != null) {
			this.hospital.recycle();
		}
		if (this.hostel != null) {
			this.hostel.recycle();
		}
		if (this.hotel != null) {
			this.hotel.recycle();
		}
		if (this.information != null) {
			this.information.recycle();
		}
		if (this.library != null) {
			this.library.recycle();
		}
		if (this.parking != null) {
			this.parking.recycle();
		}
		if (this.peak != null) {
			this.peak.recycle();
		}
		if (this.petrolStation != null) {
			this.petrolStation.recycle();
		}
		if (this.pharmacy != null) {
			this.pharmacy.recycle();
		}
		if (this.playground != null) {
			this.playground.recycle();
		}
		if (this.postbox != null) {
			this.postbox.recycle();
		}
		if (this.postoffice != null) {
			this.postoffice.recycle();
		}
		if (this.pub != null) {
			this.pub.recycle();
		}
		if (this.railway_crossing != null) {
			this.railway_crossing.recycle();
		}
		if (this.recycling != null) {
			this.recycling.recycle();
		}
		if (this.restaurant != null) {
			this.restaurant.recycle();
		}
		if (this.school != null) {
			this.school.recycle();
		}
		if (this.shelter != null) {
			this.shelter.recycle();
		}
		if (this.supermarket != null) {
			this.supermarket.recycle();
		}
		if (this.telephone != null) {
			this.telephone.recycle();
		}
		if (this.theatre != null) {
			this.theatre.recycle();
		}
		if (this.toilets != null) {
			this.toilets.recycle();
		}
		if (this.traffic_signal != null) {
			this.traffic_signal.recycle();
		}
		if (this.university != null) {
			this.university.recycle();
		}
		if (this.viewpoint != null) {
			this.viewpoint.recycle();
		}
		if (this.vulcan != null) {
			this.vulcan.recycle();
		}
		if (this.windmill != null) {
			this.windmill.recycle();
		}
	}
}