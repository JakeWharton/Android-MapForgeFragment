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

import java.util.Map;

class TagIDsNodes {
	Integer aeroway$aerodrome;
	Integer aeroway$helipad;
	Integer amenity$atm;
	Integer amenity$bank;
	Integer amenity$bicycle_rental;
	Integer amenity$bus_station;
	Integer amenity$cafe;
	Integer amenity$cinema;
	Integer amenity$fast_food;
	Integer amenity$fire_station;
	Integer amenity$fountain;
	Integer amenity$fuel;
	Integer amenity$hospital;
	Integer amenity$library;
	Integer amenity$parking;
	Integer amenity$pharmacy;
	Integer amenity$place_of_worship;
	Integer amenity$post_box;
	Integer amenity$post_office;
	Integer amenity$pub;
	Integer amenity$recycling;
	Integer amenity$restaurant;
	Integer amenity$school;
	Integer amenity$shelter;
	Integer amenity$telephone;
	Integer amenity$theatre;
	Integer amenity$toilets;
	Integer amenity$university;
	Integer barrier$bollard;
	Integer highway$bus_stop;
	Integer highway$traffic_signals;
	Integer historic$memorial;
	Integer historic$monument;
	Integer leisure$playground;
	Integer man_made$windmill;
	Integer natural$cave_entrance;
	Integer natural$peak;
	Integer natural$volcano;
	Integer place$city;
	Integer place$country;
	Integer place$island;
	Integer place$suburb;
	Integer place$town;
	Integer place$village;
	Integer railway$halt;
	Integer railway$level_crossing;
	Integer railway$station;
	Integer railway$tram_stop;
	Integer shop$bakery;
	Integer shop$organic;
	Integer shop$supermarket;
	Integer station$light_rail;
	Integer station$subway;
	Integer tourism$attraction;
	Integer tourism$hostel;
	Integer tourism$hotel;
	Integer tourism$information;
	Integer tourism$museum;
	Integer tourism$viewpoint;

	void update(Map<String, Integer> nodeTags) {
		this.aeroway$aerodrome = nodeTags.get("aeroway=aerodrome");
		this.aeroway$helipad = nodeTags.get("aeroway=helipad");

		this.amenity$atm = nodeTags.get("amenity=atm");
		this.amenity$bank = nodeTags.get("amenity=bank");
		this.amenity$bicycle_rental = nodeTags.get("amenity=bicycle_rental");
		this.amenity$bus_station = nodeTags.get("amenity=bus_station");
		this.amenity$cafe = nodeTags.get("amenity=cafe");
		this.amenity$cinema = nodeTags.get("amenity=cinema");
		this.amenity$fast_food = nodeTags.get("amenity=fast_food");
		this.amenity$fire_station = nodeTags.get("amenity=fire_station");
		this.amenity$fountain = nodeTags.get("amenity=fountain");
		this.amenity$fuel = nodeTags.get("amenity=fuel");
		this.amenity$hospital = nodeTags.get("amenity=hospital");
		this.amenity$library = nodeTags.get("amenity=library");
		this.amenity$parking = nodeTags.get("amenity=parking");
		this.amenity$pharmacy = nodeTags.get("amenity=pharmacy");
		this.amenity$place_of_worship = nodeTags.get("amenity=place_of_worship");
		this.amenity$post_box = nodeTags.get("amenity=post_box");
		this.amenity$post_office = nodeTags.get("amenity=post_office");
		this.amenity$pub = nodeTags.get("amenity=pub");
		this.amenity$recycling = nodeTags.get("amenity=recycling");
		this.amenity$restaurant = nodeTags.get("amenity=restaurant");
		this.amenity$school = nodeTags.get("amenity=school");
		this.amenity$shelter = nodeTags.get("amenity=shelter");
		this.amenity$telephone = nodeTags.get("amenity=telephone");
		this.amenity$theatre = nodeTags.get("amenity=theatre");
		this.amenity$toilets = nodeTags.get("amenity=toilets");
		this.amenity$university = nodeTags.get("amenity=university");

		this.barrier$bollard = nodeTags.get("barrier=bollard");

		this.highway$bus_stop = nodeTags.get("highway=bus_stop");
		this.highway$traffic_signals = nodeTags.get("highway=traffic_signals");

		this.historic$memorial = nodeTags.get("historic=memorial");
		this.historic$monument = nodeTags.get("historic=monument");

		this.leisure$playground = nodeTags.get("leisure=playground");

		this.man_made$windmill = nodeTags.get("man_made=windmill");

		this.natural$cave_entrance = nodeTags.get("natural=cave_entrance");
		this.natural$peak = nodeTags.get("natural=peak");
		this.natural$volcano = nodeTags.get("natural=volcano");

		this.place$city = nodeTags.get("place=city");
		this.place$country = nodeTags.get("place=country");
		this.place$island = nodeTags.get("place=island");
		this.place$suburb = nodeTags.get("place=suburb");
		this.place$town = nodeTags.get("place=town");
		this.place$village = nodeTags.get("place=village");

		this.railway$halt = nodeTags.get("railway=halt");
		this.railway$level_crossing = nodeTags.get("railway=level_crossing");
		this.railway$station = nodeTags.get("railway=station");
		this.railway$tram_stop = nodeTags.get("railway=tram_stop");

		this.shop$bakery = nodeTags.get("shop=bakery");
		this.shop$organic = nodeTags.get("shop=organic");
		this.shop$supermarket = nodeTags.get("shop=supermarket");

		this.station$light_rail = nodeTags.get("station=light_rail");
		this.station$subway = nodeTags.get("station=subway");

		this.tourism$attraction = nodeTags.get("tourism=attraction");
		this.tourism$hostel = nodeTags.get("tourism=hostel");
		this.tourism$hotel = nodeTags.get("tourism=hotel");
		this.tourism$information = nodeTags.get("tourism=information");
		this.tourism$museum = nodeTags.get("tourism=museum");
		this.tourism$viewpoint = nodeTags.get("tourism=viewpoint");
	}
}