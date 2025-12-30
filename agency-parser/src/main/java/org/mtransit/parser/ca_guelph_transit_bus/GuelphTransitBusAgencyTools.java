package org.mtransit.parser.ca_guelph_transit_bus;

import static org.mtransit.commons.RegexUtils.DIGITS;
import static org.mtransit.commons.StringUtils.EMPTY;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CleanUtils;
import org.mtransit.commons.StringUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.mt.data.MAgency;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://explore.guelph.ca/pages/transit-gtfs-data
public class GuelphTransitBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new GuelphTransitBusAgencyTools().start(args);
	}

	@Nullable
	@Override
	public List<Locale> getSupportedLanguages() {
		return LANG_EN;
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "Guelph Transit";
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	private static final String COMMUNITY_BUS_RSN = "Com";
	private static final long COMMUNITY_BUS_RID = 9_998L;

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return true;
	}

	@SuppressWarnings("SpellCheckingInspection")
	@Nullable
	@Override
	public Long convertRouteIdFromShortNameNotSupported(@NotNull String routeShortName) {
		switch (routeShortName) {
		case COMMUNITY_BUS_RSN:
			return COMMUNITY_BUS_RID;
		case "GorEdi":
			return 10_001L;
		case "EdiCol":
			return 10_006L;
		case "ODSout":
			return 10_002L;
		case "VicClr":
			return 10_003L;
		case "WHanSco":
			return 10_004L;
		case "BREW":
			return 10_005L;
		case "IroHarv":
			return 10_007L;
		case "SunLN":
			return 10_008L;
		case "ZehrsGS":
			return 10_009L;
		case "99Lite":
			return 11_099L;
		}
		return super.convertRouteIdFromShortNameNotSupported(routeShortName);
	}

	@Override
	public @Nullable Long convertRouteIdPreviousChars(@NotNull String previousChars) {
		switch (previousChars) {
		case "NYE":
			return 14_25_05_00_00L;
		}
		return super.convertRouteIdPreviousChars(previousChars);
	}

	@Override
	public @Nullable Long convertRouteIdNextChars(@NotNull String nextChars) {
		switch (nextChars) {
		case "NE":
			return 14_05L;
		case "NW":
			return 14_23L;
		case "MW":
			return 13_23L;
		case "SW":
			return 19_23L;
		case "SE":
			return 19_05L;
		case "DT":
			return 4_20L;

		}
		return super.convertRouteIdNextChars(nextChars);
	}

	private static final Pattern ALL_WHITESPACES = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanRouteShortName(@NotNull String routeShortName) {
		routeShortName = ALL_WHITESPACES.matcher(routeShortName).replaceAll(EMPTY);
		return routeShortName;
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	private static final Pattern STARTS_WITH_ROUTE_RSN = Pattern.compile("(route\\d*[A-Z]*-?\\s*)", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = STARTS_WITH_ROUTE_RSN.matcher(routeLongName).replaceAll(EMPTY);
		if (StringUtils.isEmpty(routeLongName)) {
			throw new MTLog.Fatal("getRouteLongName() > Unexpected route long name name '%s'!", routeLongName);
		}
		return CleanUtils.cleanLabel(getFirstLanguageNN(), routeLongName);
	}

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	private static final String AGENCY_COLOR = "00A6E5"; // BLUE

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@SuppressWarnings("DuplicateBranchesInSwitch")
	@Nullable
	@Override
	public String provideMissingRouteColor(@NotNull GRoute gRoute) {
		final String rsnS = gRoute.getRouteShortName();
		if (COMMUNITY_BUS_RSN.equals(rsnS)) {
			return "D14625";
		}
		if (rsnS.startsWith("Zone ") //
				|| rsnS.startsWith("NYE ")) {
			return "ED1C24";
		}
		final Matcher matcher = DIGITS.matcher(rsnS);
		if (matcher.find()) {
			final int rsn = Integer.parseInt(matcher.group());
			switch (rsn) {
			// @formatter:off
			case 1: return "EC008C";
			case 2: return "EC008C";
			case 3: return "91469B";
			case 4: return "1988B7";
			case 5: return "921B1E";
			case 6: return "ED1C24";
			case 7: return "682C91";
			case 8: return "0082B1";
			case 9: return "5C7AAE";
			case 10: return "A54686";
			case 11: return "5C7AAE";
			case 12: return "008290";
			case 13: return "811167";
			case 14: return "485E88";
			case 15: return "8F7140";
			case 16: return "29712A";
			case 17: return "CB640A";
			case 18: return "CB640A";
			case 20: return "556940";
			case 40: return "005689";
			case 41: return "405D18";
			case 50: return "A54686"; // 50 U
			case 51: return "405D18"; // 51 U
			case 52: return "485E88"; // 52 U
			case 56: return "ED1C24"; // 56 U
			case 57: return "5C7AAE"; // 57 U
			case 58: return "91469b"; // 58 U
			case 99: return "4F832E ";
			// @formatter:on
			}
		}
		throw new MTLog.Fatal("getRouteColor() > Unexpected route color for '%s'!", gRoute);
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern STARTS_WITH_RSN = Pattern.compile("(^\\d+ )", Pattern.CASE_INSENSITIVE);

	private static final Pattern STARTS_W_COMMUNITY_BUS_ = Pattern.compile("(^(community bus|mainline) (?=(.{3,})))", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = CleanUtils.keepToAndRemoveVia(tripHeadsign);
		tripHeadsign = STARTS_WITH_RSN.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = STARTS_W_COMMUNITY_BUS_.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = CleanUtils.CLEAN_AT.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		tripHeadsign = CleanUtils.CLEAN_AND.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		tripHeadsign = CleanUtils.cleanBounds(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		return CleanUtils.cleanLabel(getFirstLanguageNN(), tripHeadsign);
	}

	private static final Pattern CLEAN_DEPART_ARRIVE = Pattern.compile("( (arrival|depart)$)", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = CLEAN_DEPART_ARRIVE.matcher(gStopName).replaceAll(EMPTY);
		gStopName = CleanUtils.cleanBounds(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		return CleanUtils.cleanLabel(getFirstLanguageNN(), gStopName);
	}

}
