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

class TagIDsWays {
	Integer admin_level$10;
	Integer admin_level$2;
	Integer admin_level$4;
	Integer admin_level$6;
	Integer admin_level$8;
	Integer admin_level$9;
	Integer aerialway$cable_car;
	Integer aerialway$chair_lift;
	Integer aerialway$drag_lift;
	Integer aerialway$gondola;
	Integer aerialway$magic_carpet;
	Integer aerialway$mixed_lift;
	Integer aerialway$rope_tow;
	Integer aeroway$aerodrome;
	Integer aeroway$apron;
	Integer aeroway$runway;
	Integer aeroway$taxiway;
	Integer aeroway$terminal;
	Integer amenity$college;
	Integer amenity$fountain;
	Integer amenity$grave_yard;
	Integer amenity$hospital;
	Integer amenity$parking;
	Integer amenity$school;
	Integer amenity$university;
	Integer area$yes;
	Integer barrier$fence;
	Integer barrier$wall;
	Integer boundary$administrative;
	Integer boundary$national_park;
	Integer bridge$yes;
	Integer building$apartments;
	Integer building$embassy;
	Integer building$government;
	Integer building$gym;
	Integer building$roof;
	Integer building$ruins;
	Integer building$sports;
	Integer building$train_station;
	Integer building$university;
	Integer building$yes;
	Integer highway$bridleway;
	Integer highway$byway;
	Integer highway$construction;
	Integer highway$cycleway;
	Integer highway$footway;
	Integer highway$living_street;
	Integer highway$motorway;
	Integer highway$motorway_link;
	Integer highway$path;
	Integer highway$pedestrian;
	Integer highway$primary;
	Integer highway$primary_link;
	Integer highway$residential;
	Integer highway$road;
	Integer highway$secondary;
	Integer highway$secondary_link;
	Integer highway$service;
	Integer highway$steps;
	Integer highway$tertiary;
	Integer highway$track;
	Integer highway$trunk;
	Integer highway$trunk_link;
	Integer highway$unclassified;
	Integer historic$ruins;
	Integer landuse$allotments;
	Integer landuse$basin;
	Integer landuse$brownfield;
	Integer landuse$cemetery;
	Integer landuse$commercial;
	Integer landuse$construction;
	Integer landuse$farm;
	Integer landuse$farmland;
	Integer landuse$forest;
	Integer landuse$grass;
	Integer landuse$greenfield;
	Integer landuse$industrial;
	Integer landuse$military;
	Integer landuse$recreation_ground;
	Integer landuse$reservoir;
	Integer landuse$residential;
	Integer landuse$retail;
	Integer landuse$village_green;
	Integer landuse$wood;
	Integer leisure$common;
	Integer leisure$garden;
	Integer leisure$golf_course;
	Integer leisure$nature_reserve;
	Integer leisure$park;
	Integer leisure$pitch;
	Integer leisure$playground;
	Integer leisure$sports_centre;
	Integer leisure$stadium;
	Integer leisure$track;
	Integer leisure$water_park;
	Integer man_made$pier;
	Integer military$airfield;
	Integer military$barracks;
	Integer military$naval_base;
	Integer natural$beach;
	Integer natural$coastline;
	Integer natural$glacier;
	Integer natural$heath;
	Integer natural$land;
	Integer natural$marsh;
	Integer natural$scrub;
	Integer natural$water;
	Integer natural$wetland;
	Integer natural$wood;
	Integer oneway$yes;
	Integer piste$difficulty$advanced;
	Integer piste$difficulty$easy;
	Integer piste$difficulty$expert;
	Integer piste$difficulty$freeride;
	Integer piste$difficulty$intermediate;
	Integer piste$difficulty$novice;
	Integer piste$type$downhill;
	Integer piste$type$nordic;
	Integer place$locality;
	Integer railway$light_rail;
	Integer railway$narrow_gauge;
	Integer railway$rail;
	Integer railway$station;
	Integer railway$subway;
	Integer railway$tram;
	Integer route$ferry;
	Integer sport$shooting;
	Integer sport$swimming;
	Integer sport$tennis;
	Integer tourism$attraction;
	Integer tourism$zoo;
	Integer tunnel$no;
	Integer tunnel$yes;
	Integer waterway$canal;
	Integer waterway$drain;
	Integer waterway$river;
	Integer waterway$riverbank;
	Integer waterway$stream;
	Integer wood$coniferous;
	Integer wood$deciduous;
	Integer wood$mixed;

	void update(Map<String, Integer> wayTags) {
		this.admin_level$10 = wayTags.get("admin_level=10");
		this.admin_level$2 = wayTags.get("admin_level=2");
		this.admin_level$4 = wayTags.get("admin_level=4");
		this.admin_level$6 = wayTags.get("admin_level=6");
		this.admin_level$8 = wayTags.get("admin_level=8");
		this.admin_level$9 = wayTags.get("admin_level=9");

		this.aerialway$cable_car = wayTags.get("aerialway=cable_car");
		this.aerialway$chair_lift = wayTags.get("aerialway=chair_lift");
		this.aerialway$drag_lift = wayTags.get("aerialway=drag_lift");
		this.aerialway$gondola = wayTags.get("aerialway=gondola");
		this.aerialway$magic_carpet = wayTags.get("aerialway=magic_carpet");
		this.aerialway$mixed_lift = wayTags.get("aerialway=mixed_lift");
		this.aerialway$rope_tow = wayTags.get("aerialway=rope_tow");

		this.aeroway$aerodrome = wayTags.get("aeroway=aerodrome");
		this.aeroway$apron = wayTags.get("aeroway=apron");
		this.aeroway$runway = wayTags.get("aeroway=runway");
		this.aeroway$taxiway = wayTags.get("aeroway=taxiway");
		this.aeroway$terminal = wayTags.get("aeroway=terminal");

		this.amenity$college = wayTags.get("amenity=college");
		this.amenity$fountain = wayTags.get("amenity=fountain");
		this.amenity$grave_yard = wayTags.get("amenity=grave_yard");
		this.amenity$hospital = wayTags.get("amenity=hospital");
		this.amenity$parking = wayTags.get("amenity=parking");
		this.amenity$school = wayTags.get("amenity=school");
		this.amenity$university = wayTags.get("amenity=university");

		this.area$yes = wayTags.get("area=yes");

		this.barrier$fence = wayTags.get("barrier=fence");
		this.barrier$wall = wayTags.get("barrier=wall");

		this.boundary$administrative = wayTags.get("boundary=administrative");
		this.boundary$national_park = wayTags.get("boundary=national_park");

		this.bridge$yes = wayTags.get("bridge=yes");

		this.building$apartments = wayTags.get("building=apartments");
		this.building$embassy = wayTags.get("building=embassy");
		this.building$government = wayTags.get("building=government");
		this.building$gym = wayTags.get("building=gym");
		this.building$roof = wayTags.get("building=roof");
		this.building$ruins = wayTags.get("building=ruins");
		this.building$sports = wayTags.get("building=sports");
		this.building$train_station = wayTags.get("building=train_station");
		this.building$university = wayTags.get("building=university");
		this.building$yes = wayTags.get("building=yes");

		this.highway$bridleway = wayTags.get("highway=bridleway");
		this.highway$byway = wayTags.get("highway=byway");
		this.highway$construction = wayTags.get("highway=construction");
		this.highway$cycleway = wayTags.get("highway=cycleway");
		this.highway$footway = wayTags.get("highway=footway");
		this.highway$living_street = wayTags.get("highway=living_street");
		this.highway$motorway = wayTags.get("highway=motorway");
		this.highway$motorway_link = wayTags.get("highway=motorway_link");
		this.highway$path = wayTags.get("highway=path");
		this.highway$pedestrian = wayTags.get("highway=pedestrian");
		this.highway$primary = wayTags.get("highway=primary");
		this.highway$primary_link = wayTags.get("highway=primary_link");
		this.highway$residential = wayTags.get("highway=residential");
		this.highway$road = wayTags.get("highway=road");
		this.highway$secondary = wayTags.get("highway=secondary");
		this.highway$secondary_link = wayTags.get("highway=secondary_link");
		this.highway$service = wayTags.get("highway=service");
		this.highway$steps = wayTags.get("highway=steps");
		this.highway$tertiary = wayTags.get("highway=tertiary");
		this.highway$track = wayTags.get("highway=track");
		this.highway$trunk = wayTags.get("highway=trunk");
		this.highway$trunk_link = wayTags.get("highway=trunk_link");
		this.highway$unclassified = wayTags.get("highway=unclassified");

		this.historic$ruins = wayTags.get("historic=ruins");

		this.landuse$allotments = wayTags.get("landuse=allotments");
		this.landuse$basin = wayTags.get("landuse=basin");
		this.landuse$brownfield = wayTags.get("landuse=brownfield");
		this.landuse$cemetery = wayTags.get("landuse=cemetery");
		this.landuse$commercial = wayTags.get("landuse=commercial");
		this.landuse$construction = wayTags.get("landuse=construction");
		this.landuse$farm = wayTags.get("landuse=farm");
		this.landuse$farmland = wayTags.get("landuse=farmland");
		this.landuse$forest = wayTags.get("landuse=forest");
		this.landuse$grass = wayTags.get("landuse=grass");
		this.landuse$greenfield = wayTags.get("landuse=greenfield");
		this.landuse$industrial = wayTags.get("landuse=industrial");
		this.landuse$military = wayTags.get("landuse=military");
		this.landuse$recreation_ground = wayTags.get("landuse=recreation_ground");
		this.landuse$reservoir = wayTags.get("landuse=reservoir");
		this.landuse$residential = wayTags.get("landuse=residential");
		this.landuse$retail = wayTags.get("landuse=retail");
		this.landuse$village_green = wayTags.get("landuse=village_green");
		this.landuse$wood = wayTags.get("landuse=wood");

		this.leisure$common = wayTags.get("leisure=common");
		this.leisure$garden = wayTags.get("leisure=garden");
		this.leisure$golf_course = wayTags.get("leisure=golf_course");
		this.leisure$nature_reserve = wayTags.get("leisure=nature_reserve");
		this.leisure$park = wayTags.get("leisure=park");
		this.leisure$pitch = wayTags.get("leisure=pitch");
		this.leisure$playground = wayTags.get("leisure=playground");
		this.leisure$sports_centre = wayTags.get("leisure=sports_centre");
		this.leisure$stadium = wayTags.get("leisure=stadium");
		this.leisure$track = wayTags.get("leisure=track");
		this.leisure$water_park = wayTags.get("leisure=water_park");

		this.man_made$pier = wayTags.get("man_made=pier");

		this.military$airfield = wayTags.get("military=airfield");
		this.military$barracks = wayTags.get("military=barracks");
		this.military$naval_base = wayTags.get("military=naval_base");

		this.natural$beach = wayTags.get("natural=beach");
		this.natural$coastline = wayTags.get("natural=coastline");
		this.natural$glacier = wayTags.get("natural=glacier");
		this.natural$heath = wayTags.get("natural=heath");
		this.natural$land = wayTags.get("natural=land");
		this.natural$marsh = wayTags.get("natural=marsh");
		this.natural$scrub = wayTags.get("natural=scrub");
		this.natural$water = wayTags.get("natural=water");
		this.natural$wetland = wayTags.get("natural=wetland");
		this.natural$wood = wayTags.get("natural=wood");

		this.oneway$yes = wayTags.get("oneway=yes");

		this.piste$type$downhill = wayTags.get("piste:type=downhill");
		this.piste$type$nordic = wayTags.get("piste:type=nordic");

		this.piste$difficulty$novice = wayTags.get("piste:difficulty=novice");
		this.piste$difficulty$easy = wayTags.get("piste:difficulty=easy");
		this.piste$difficulty$intermediate = wayTags.get("piste:difficulty=intermediate");
		this.piste$difficulty$advanced = wayTags.get("piste:difficulty=advanced");
		this.piste$difficulty$expert = wayTags.get("piste:difficulty=expert");
		this.piste$difficulty$freeride = wayTags.get("piste:difficulty=freeride");

		this.place$locality = wayTags.get("place=locality");

		this.railway$light_rail = wayTags.get("railway=light_rail");
		this.railway$narrow_gauge = wayTags.get("railway=narrow_gauge");
		this.railway$rail = wayTags.get("railway=rail");
		this.railway$station = wayTags.get("railway=station");
		this.railway$subway = wayTags.get("railway=subway");
		this.railway$tram = wayTags.get("railway=tram");

		this.route$ferry = wayTags.get("route=ferry");

		this.sport$shooting = wayTags.get("sport=shooting");
		this.sport$swimming = wayTags.get("sport=swimming");
		this.sport$tennis = wayTags.get("sport=tennis");

		this.tourism$attraction = wayTags.get("tourism=attraction");
		this.tourism$zoo = wayTags.get("tourism=zoo");

		this.tunnel$no = wayTags.get("tunnel=no");
		this.tunnel$yes = wayTags.get("tunnel=yes");

		this.waterway$canal = wayTags.get("waterway=canal");
		this.waterway$drain = wayTags.get("waterway=drain");
		this.waterway$river = wayTags.get("waterway=river");
		this.waterway$riverbank = wayTags.get("waterway=riverbank");
		this.waterway$stream = wayTags.get("waterway=stream");

		this.wood$coniferous = wayTags.get("wood=coniferous");
		this.wood$deciduous = wayTags.get("wood=deciduous");
		this.wood$mixed = wayTags.get("wood=mixed");
	}
}