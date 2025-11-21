/**
 * Represents a version consisting of four parts: prefix, version part, pre-release part, and build metadata.
 *
 * The format is: [prefix][version][-preRelease][+buildMetadata]
 * - Prefix: Optional string before the version number (e.g., "v", "release-").
 * - Version: Required, dot-separated (e.g., "1.2.3"). Major must be a number.
 * - PreRelease: Optional, dot-separated, after a hyphen (e.g., "-alpha.1").
 * - BuildMetadata: Optional, dot-separated, after a plus (e.g., "+build.20251121").
 *
 * Comparison ignores prefix and build metadata. Version and pre-release are compared numerically or lexicographically.
 *
 * @constructor Parses the version string into its components.
 * @param version The version string to parse.
 */
class Version(version: String): Comparable<Version> {
    /** Returns null if the string is empty, otherwise returns the string itself. */
    private fun String.nullIfEmpty() = if (this.isEmpty()) null else this

    /**
     * Normalizes a string for comparison:
     * - null or empty becomes "0"
     * - numeric strings are converted to canonical integer string
     * - otherwise, returns the string itself
     */
    private fun String?.normalize() = when {
        this == null -> "0"
        this.isEmpty() -> "0"
        this.toIntOrNull() != null -> this.toInt().toString()
        else -> this
    }

    /** Gets the part at the given 1-based index, normalized. */
    private fun List<String>.getPart(index: Int) = (this.getOrNull(index-1).normalize())

    // Regex to parse version string into main parts
    private val result = Regex("([0-9]+(?:\\.[0-9a-zA-Z.]*)?)(?:-([0-9a-zA-Z.-]*))?(?:\\+(.*))?$")
        .find(version.trim()) ?: error("Invalid version format: $version")
    private val groups = result.groupValues ?: error("Invalid version format: $version")

    /** Optional prefix before the version number. */
    val prefix = version.substring(0, result.range.first)
    /** Dot-separated version parts (major, minor, patch, ...). */
    val parts = groups.getOrNull(1)?.nullIfEmpty()?.split('.') ?: emptyList()
    /** Dot-separated pre-release parts. */
    val preRelease = groups.getOrNull(2)?.nullIfEmpty()?.split('.') ?: emptyList()
    /** Dot-separated build metadata parts. */
    val buildMetadata = groups.getOrNull(3)?.nullIfEmpty()?.split('.') ?: emptyList()

    /** Returns the normalized version part at the given 1-based index. */
    fun part(index: Int) = parts.getPart(index)
    /** Returns the normalized pre-release part at the given 1-based index. */
    fun preRelease(index: Int) = preRelease.getPart(index)
    /** Returns the normalized build metadata part at the given 1-based index. */
    fun buildMetadata(index: Int) = buildMetadata.getPart(index)

    /**
     * Compares two strings numerically if both are numbers, lexicographically otherwise.
     * Returns negative, zero, or positive depending on order.
     */
    private fun compare(a: String, b: String): Int {
        val aInt = a.toIntOrNull()
        val bInt = b.toIntOrNull()
        when {
            aInt != null && bInt != null && aInt != bInt -> return aInt - bInt
            aInt == null && bInt == null && a != b -> return a.compareTo(b)
            aInt == null && bInt != null -> return -1
            aInt != null && bInt == null -> return 1
        }
        return 0
    }

    /**
     * Compares two lists of strings element-wise using [compare].
     * If all elements are equal, compares by list size.
     */
    private fun compare(aList: List<String>, bList: List<String>): Int {
        aList.zip(bList).forEach { (a, b) ->
            val c = compare(a.normalize(), b.normalize())
            if (c != 0) return c
        }
        if (aList.size != bList.size) return aList.size - bList.size
        return 0
    }

    /** Major version (first part, always a number). */
    val major = part(1)
    /** Minor version (second part, may be number or alphanumeric). */
    val minor = part(2)
    /** Patch version (third part, may be number or alphanumeric). */
    val patch = part(3)

    /**
     * Compares this version to another version.
     * Comparison order: version parts, pre-release parts, build metadata (if needed).
     * Prefix is ignored.
     */
    override fun compareTo(other: Version): Int {
        compare(parts, other.parts).let { if (it!=0) return it }
        if (preRelease.isEmpty() && other.preRelease.isNotEmpty()) return 1
        if (preRelease.isNotEmpty() && other.preRelease.isEmpty()) return -1
        compare(preRelease, other.preRelease).let { if (it!=0) return it }
        return 0
    }

    /** Returns the string representation of the version. */
    override fun toString(): String = "$prefix${parts.joinToString("."){ it.normalize() }}${if (preRelease.isNotEmpty()) "-"+preRelease.joinToString(".") { it.normalize() } else ""}${if (buildMetadata.isNotEmpty()) "+"+buildMetadata.joinToString(".")  { it.normalize() } else ""}"

    /** Checks equality by comparing version precedence. */
    override fun equals(other: Any?): Boolean {
        return (other as? Version)?.compareTo(this)==0
    }

    /** Returns a detailed string representation of all version parts. */
    fun toLongString() = "Version(major=$major, minor=$minor, patch=$patch, smaller=${parts.drop(3)}, prerelease=$preRelease, buildMetadata=$buildMetadata, prefix=$prefix)"
}

/**
 * Extension function to convert a string to a [Version] object.
 *
 * Example:
 *   val v = "1.2.3-alpha.1+build.20251121".toVersion()
 */
fun String.toVersion() = Version(this)
