package de.cyface.synchronization;

import static de.cyface.synchronization.HttpConnection.BOUNDARY;
import static de.cyface.synchronization.HttpConnection.TAIL;
import static de.cyface.testutils.SharedTestUtils.generateGeoLocation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

import de.cyface.persistence.model.GeoLocation;

/**
 * Tests whether our default implementation of the {@link Http} protocol works as expected.
 *
 * @author Armin Schnabel
 * @version 1.1.2
 * @since 4.0.0
 */
public class HttpConnectionTest {

    private HttpConnection oocut;

    /**
     * Initializes all required properties and adds the <code>testListener</code> to the <code>CapturingProcess</code>.
     */
    @Before
    public void setUp() {
        oocut = new HttpConnection();
    }

    /**
     * Tests that the MultiPart header is generated correctly.
     * <p>
     * This test reproduced MOV-655 where the header parts were assembled in the wrong order. As a consequence
     * the file binary which is added directly at the end of the header was added to the locationCount header part
     * instead of the file header part which was the last part of the header before.
     */
    @Test
    public void testGenerateHeader() {

        final GeoLocation startLocation = generateGeoLocation(0);
        final GeoLocation endLocation = generateGeoLocation(10);
        final SyncAdapter.MetaData metaData = new SyncAdapter.MetaData(startLocation, endLocation, "test-did", 78,
                "test_deviceType", "test_osVersion", "test_appVersion", 10.0, 5);

        // Act
        final String header = oocut.generateHeader(metaData, "test-did_78.cyf");

        // Assert
        final String expectedHeader = "--" + BOUNDARY + "\r\n"
                + "Content-Disposition: form-data; name=\"startLocation\"\r\n" + "\r\n"
                + "lat: 51.1, lon: 13.1, time: 1000000000\r\n" + "--" + BOUNDARY + "\r\n"
                + "Content-Disposition: form-data; name=\"endLocation\"\r\n" + "\r\n"
                + "lat: 51.10008993199995, lon: 13.100000270697, time: 1000010000\r\n" + "--" + BOUNDARY + "\r\n"
                + "Content-Disposition: form-data; name=\"deviceId\"\r\n" + "\r\n" + "test-did\r\n" + "--" + BOUNDARY
                + "\r\n" + "Content-Disposition: form-data; name=\"measurementId\"\r\n" + "\r\n" + "78\r\n" + "--"
                + BOUNDARY + "\r\n" + "Content-Disposition: form-data; name=\"deviceType\"\r\n" + "\r\n"
                + "test_deviceType\r\n" + "--" + BOUNDARY + "\r\n"
                + "Content-Disposition: form-data; name=\"osVersion\"\r\n" + "\r\n" + "test_osVersion\r\n" + "--"
                + BOUNDARY + "\r\n" + "Content-Disposition: form-data; name=\"appVersion\"\r\n" + "\r\n"
                + "test_appVersion\r\n" + "--" + BOUNDARY + "\r\n"
                + "Content-Disposition: form-data; name=\"length\"\r\n" + "\r\n" + "10.0\r\n" + "--" + BOUNDARY + "\r\n"
                + "Content-Disposition: form-data; name=\"locationCount\"\r\n" + "\r\n" + "5\r\n" + "--" + BOUNDARY
                + "\r\n" + "Content-Disposition: form-data; name=\"fileToUpload\"; filename=\"test-did_78.cyf\"\r\n"
                + "Content-Type: application/octet-stream\r\n" + "Content-Transfer-Encoding: binary\r\n" + "\r\n";
        assertThat(header, is(equalTo(expectedHeader)));
    }

    /**
     * Tests that the number of bytes written to {@code OutputStream} is calculated correctly.
     * <p>
     * This test is indented to avoid MOV-669 where we accidentally used the number of characters in the header
     * instead of the number of bytes to set the fixed content length for steaming mode.
     */
    @Test
    public void testCalculateBytesWrittenToOutputStream() {

        // Arrange
        final SyncAdapter.MetaData metaData = new SyncAdapter.MetaData(generateGeoLocation(0), generateGeoLocation(10),
                "test-did", 78, "test_deviceType", "test_osVersion", "test_appVersion", 10.0, 5);
        final String header = oocut.generateHeader(metaData, "test-did_78.cyf");

        final byte[] testFile = "TEST_FÄ`&ô»ω_CONTENT".getBytes(); // with chars which require > 1 Byte
        final long filePartSize = testFile.length;

        // Act
        final long requestLength = oocut.calculateBytesWrittenToOutputStream(header, filePartSize);

        // Assert
        assertThat((int)requestLength, is(equalTo(header.getBytes().length + testFile.length + TAIL.length())));
    }
}