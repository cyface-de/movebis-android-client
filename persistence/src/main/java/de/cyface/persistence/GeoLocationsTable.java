package de.cyface.persistence;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import de.cyface.persistence.model.GeoLocation;
import de.cyface.persistence.model.Measurement;

/**
 * Table for storing {@link GeoLocation} measuring points. The data in this table is intended for storage prior to
 * processing it by either transfer to a server or export to some external file or device.
 *
 * @author Klemens Muthmann
 * @author Armin Schnabel
 * @version 2.4.0
 * @since 1.0.0
 */
public class GeoLocationsTable extends AbstractCyfaceMeasurementTable {

    /**
     * The path segment in the table URI identifying the {@link GeoLocationsTable}.
     */
    final static String URI_PATH = "locations";
    /**
     * Column name for the column storing the {@link GeoLocation} timestamp.
     */
    public static final String COLUMN_GEOLOCATION_TIME = "gps_time";
    /**
     * Column name for the column storing the {@link GeoLocation} latitude.
     */
    public static final String COLUMN_LAT = "lat";
    /**
     * Column name for the column storing the {@link GeoLocation} longitude.
     */
    public static final String COLUMN_LON = "lon";
    /**
     * Column name for the column storing the {@link GeoLocation} speed in meters per second.
     */
    public static final String COLUMN_SPEED = "speed";
    /**
     * Column name for the column storing the {@link GeoLocation} accuracy.
     */
    public static final String COLUMN_ACCURACY = "accuracy";
    /**
     * Column name for the column storing the foreign key referencing the {@link Measurement} for this
     * {@link GeoLocation}.
     */
    public static final String COLUMN_MEASUREMENT_FK = "measurement_fk";
    /**
     * An array containing all the column names used by a geo location table.
     */
    private static final String[] COLUMNS = {BaseColumns._ID, COLUMN_GEOLOCATION_TIME, COLUMN_LAT, COLUMN_LON,
            COLUMN_SPEED, COLUMN_ACCURACY, COLUMN_MEASUREMENT_FK};

    /**
     * Provides a completely initialized object as a representation of a table containing geo locations in the database.
     */
    protected GeoLocationsTable() {
        super(URI_PATH);
    }

    @Override
    protected String getCreateStatement() {
        return "CREATE TABLE " + getName() + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_GEOLOCATION_TIME + " INTEGER NOT NULL, " + COLUMN_LAT + " REAL NOT NULL, " + COLUMN_LON
                + " REAL NOT NULL, " + COLUMN_SPEED + " REAL NOT NULL, " + COLUMN_ACCURACY + " INTEGER NOT NULL, "
                + COLUMN_MEASUREMENT_FK + " INTEGER NOT NULL);";
    }

    /**
     * Don't forget to update the {@link DatabaseHelper}'s {@code DATABASE_VERSION} if you upgrade this table.
     * <p>
     * The Upgrade is automatically executed in a transaction, do not wrap the code in another transaction!
     * <p>
     * This upgrades are called incrementally by {@link DatabaseHelper#onUpgrade(SQLiteDatabase, int, int)}.
     * <p>
     * Remaining documentation: {@link CyfaceMeasurementTable#onUpgrade}
     */
    @Override
    public void onUpgrade(final SQLiteDatabase database, final int fromVersion, final int toVersion) {

        switch (fromVersion) {

            case 8:
                // To drop columns we need to copy the table. We anyway renamed the table to locations.
                database.execSQL("ALTER TABLE gps_points RENAME TO _locations_old;");

                // To drop columns "is_synced" we need to create a new table
                database.execSQL(
                        "CREATE TABLE locations (_id INTEGER PRIMARY KEY AUTOINCREMENT, gps_time INTEGER NOT NULL, "
                                + "lat REAL NOT NULL, lon REAL NOT NULL, speed REAL NOT NULL, accuracy INTEGER NOT NULL, "
                                + "measurement_fk INTEGER NOT NULL);");
                // and insert the old data accordingly. This is anyway cleaner (no defaults)
                // We ignore the value as we upload to a new API.
                database.execSQL("INSERT INTO locations " + "(_id,gps_time,lat,lon,speed,accuracy,measurement_fk) "
                        + "SELECT _id,gps_time,lat,lon,speed,accuracy,measurement_fk " + "FROM _locations_old");

                // Remove temp table
                database.execSQL("DROP TABLE _locations_old;");

                break; // onUpgrade is called incrementally by DatabaseHelper
        }

    }

    @Override
    protected String[] getDatabaseTableColumns() {
        return COLUMNS;
    }
}
