package de.cyface.persistence;

import de.cyface.persistence.model.GeoLocation;

/**
 * Contains constants and utility methods required during testing.
 *
 * @author Armin Schnabel
 * @version 1.1.0
 * @since 4.0.0
 */
public final class TestUtils {

    /**
     * The content provider authority used for testing.
     */
    public final static String AUTHORITY = "de.cyface.persistence.provider.test";
    /**
     * The account type used during testing. This must be the same as in the authenticator configuration.
     */
    public final static String ACCOUNT_TYPE = "de.cyface.persistence.account.test";

    /**
     * The following constants were selected so that adding each base+constant results in coordinates with approximately
     * 1 meter distance between base coordinates and base+1*constant coordinates
     */
    private final static double BASE_LAT = 51.100;
    /**
     * see {@link #BASE_LAT}
     */
    private final static double BASE_LON = 13.100;
    /**
     * see {@link #BASE_LAT}
     */
    private final static double LAT_CONSTANT = 0.000008993199995;
    /**
     * see {@link #BASE_LAT}
     */
    private final static double LON_CONSTANT = 0.0000000270697;

    /**
     * Private constructor to avoid instantiation of utility class.
     */
    private TestUtils() {
        // Nothing to do here.
    }

    /**
     * Generates {@link GeoLocation}s with coordinates for testing.
     * <p>
     * See {@param relativeDistance} if you need locations with a specific distance from each other.
     *
     * @param distanceFromBase an integer which defines how much the generated {@code GeoLocation}s are away from each
     *            other. E.g.: If you generate {@code GeoLocation}s using {@param relativeDistance} 1, 3, 5 the
     *            generated locations will be approximately 1 meter per {@param relativeDistance}-difference away from
     *            each other. In this case (1, 5) = 4m distance and (1, 3) or (3, 5) = 2m distance.
     * @return the generated {@code GeoLocation}
     */
    public static GeoLocation generateGeoLocation(final int distanceFromBase) {
        final double salt = Math.random();
        return new GeoLocation(BASE_LAT + distanceFromBase * LAT_CONSTANT, BASE_LON + distanceFromBase * LON_CONSTANT,
                1000000000L + distanceFromBase * 1000L, salt * 15.0, (float)salt * 30f);
    }
}
