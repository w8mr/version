import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith

class TestVersion {
    /** Basic version parsing and toString consistency. */
    @Test
    fun testBasicVersion() {
        val version = Version("1.2.3")
        assertEquals("1", version.major)
        assertEquals("2", version.minor)
        assertEquals("3", version.patch)
        assertEquals("1.2.3", version.toString())
    }

    @Test
    fun testPrefix() {
        val version = Version("v1.2.3")
        assertEquals("v", version.prefix)
        assertEquals("1", version.major)
        assertEquals("2", version.minor)
        assertEquals("3", version.patch)
    }

    @Test
    fun testPreRelease() {
        val version = Version("1.2.3-alpha.1")
        assertEquals(listOf("alpha", "1"), version.preRelease)
        assertEquals("1.2.3-alpha.1", version.toString())
    }

    @Test
    fun testBuildMetadata() {
        val version = Version("1.2.3+build.20251121")
        assertEquals(listOf("build", "20251121"), version.buildMetadata)
        assertEquals("1.2.3+build.20251121", version.toString())
    }

    @Test
    fun testAllParts() {
        val version = Version("release-2.5.0-rc.2+exp.sha.5114f85")
        assertEquals("release-", version.prefix)
        assertEquals("2", version.major)
        assertEquals("5", version.minor)
        assertEquals("0", version.patch)
        assertEquals(listOf("rc", "2"), version.preRelease)
        assertEquals(listOf("exp", "sha", "5114f85"), version.buildMetadata)
        assertEquals("release-2.5.0-rc.2+exp.sha.5114f85", version.toString())
    }

    @Test
    fun testComparison() {
        val v1 = Version("1.2.3")
        val v2 = Version("1.2.3-alpha.1")
        val v3 = Version("1.2.10")
        val v4 = Version("v1.2.3")
        val v5 = Version("1.2.3+build.1")
        val v6 = Version("1.2.3-alpha.2")
        val v7 = Version("1.2.3-beta")

        assertTrue(v1 > v2) // release > pre-release
        assertTrue(v3 > v1) // 10 > 3
        assertTrue(v1 == v4) // prefix ignored
        assertTrue(v5 == v1) // build metadata ignored
        assertTrue(v6 > v2) // alpha.2 > alpha.1
        assertTrue(v7 > v2) // beta > alpha
        assertFalse(v2 > v1)
    }

    @Test
    fun testIndexBasedMethods() {
        val version = Version("prefix1.2.3-alpha.4+build.20251121.extra")
        // Version parts
        assertEquals("1", version.part(1))
        assertEquals("2", version.part(2))
        assertEquals("3", version.part(3))
        assertEquals("0", version.part(4)) // Out of range returns "0"
        // PreRelease parts
        assertEquals("alpha", version.preRelease(1))
        assertEquals("4", version.preRelease(2))
        assertEquals("0", version.preRelease(3)) // Out of range returns "0"
        // BuildMetadata parts
        assertEquals("build", version.buildMetadata(1))
        assertEquals("20251121", version.buildMetadata(2))
        assertEquals("extra", version.buildMetadata(3))
        assertEquals("0", version.buildMetadata(4)) // Out of range returns "0"
    }

    /** Edge cases: missing minor/patch, empty string, malformed input. */
    @Test
    fun testEdgeCases() {
        val v1 = Version("1")
        assertEquals("1", v1.major)
        assertEquals("0", v1.minor)
        assertEquals("0", v1.patch)
        assertEquals("1", v1.toString())

        val v2 = Version("1.2")
        assertEquals("1", v2.major)
        assertEquals("2", v2.minor)
        assertEquals("0", v2.patch)
        assertEquals("1.2", v2.toString())

        val v3 = Version("1.2.3-alpha")
        assertEquals("alpha", v3.preRelease(1))
        assertEquals("0", v3.preRelease(2))
        assertEquals("1.2.3-alpha", v3.toString())

        val v4 = Version("1.2.3+build.123")
        assertEquals("build", v4.buildMetadata(1))
        assertEquals("123", v4.buildMetadata(2))
        assertEquals("1.2.3+build.123", v4.toString())
    }

    /** Test empty part, 1..1, to be treated as 1.0.1 */
    @Test
    fun testEmptyPart() {
        val v1 = Version("1..1")
        assertEquals(Version("1.0.1"), v1)
    }


    /** Malformed input should throw an error. */
    @Test
    fun testMalformedInputThrows() {
        assertFailsWith<IllegalStateException> { Version("") }
        assertFailsWith<IllegalStateException> { Version("abc") }
    }

    /** Equality and comparison tests. */
    @Test
    fun testEqualityAndComparison() {
        val v1 = Version("1.2.3")
        val v2 = Version("1.2.3")
        val v3 = Version("1.2.4")
        val v4 = Version("1.2.3-alpha")
        val v5 = Version("1.2.3+build.1")
        assertTrue(v1 == v2)
        assertFalse(v1 == v3)
        assertTrue(v1 > v4) // Release > pre-release
        assertTrue(v1 == v5) // Build metadata ignored in equality
        assertTrue(v3 > v1)
    }

    /** toString consistency for various cases. */
    @Test
    fun testToStringConsistency() {
        val cases = listOf(
            "v1.2.3",
            "release-1.2.3-alpha.1+build.20251121",
            "1.2.3",
            "1.2.3-alpha",
            "1.2.3+build.1",
            "1.2.3-alpha.1+build.1.2"
        )
        for (str in cases) {
            val v = Version(str)
            assertEquals(v.toString(), str)
        }
    }

    /** Immutability check: properties should not be reassignable. */
    @Test
    fun testImmutability() {
        val v = Version("1.2.3")
        // The following lines should not compile if uncommented:
        // v.major = "2"
        // v.minor = "3"
        // v.patch = "4"
        // This test is a placeholder to indicate immutability is enforced by Kotlin.
        assertEquals("1", v.major)
        assertEquals("2", v.minor)
        assertEquals("3", v.patch)
    }
}