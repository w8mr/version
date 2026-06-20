# Version Project Documentation

## Version Structure

A version in this project consists of **four parts**:

1. **Prefix**: An optional string that appears before the version number (e.g., `v`, `release-`).
2. **Version Part**: The main version number, typically in the format `major.minor.patch` (e.g., `1.2.3`).
3. **PreRelease Part**: An optional part indicating pre-release status, separated by a hyphen (e.g., `-alpha.1`).
4. **Build Metadata**: An optional part for build information, separated by a plus sign (e.g., `+build.20251121`).

### Dot-Separated Subparts
- **Version Part**, **PreRelease Part**, and **Build Metadata** each consist of one or more **dot-separated subparts** (e.g., `1.2.3`, `alpha.1`, `build.20251121`).
- The **Prefix** is not dot-separated and is treated as a single string.
- **Empty subparts** (for example, in `1..1`) are **treated as `"0"`**. This means `1..1` is parsed as `1.0.1`, and similarly, missing pre-release or build metadata subparts are also treated as `"0"` when accessed by index.

#### Example: Empty Parts Treated as Zero
- `1..1` → major=`1`, minor=`0`, patch=`1`
- `1.2.3-alpha..1` → preRelease=`["alpha", "0", "1"]`
- Accessing out-of-range subparts (e.g., `part(4)` for `1.2.3`) returns `"0"`.

### Major Version Number
- The **major version number** (the first part of the Version Part) **must be a number** (e.g., `1` in `1.2.3`).
- Other subparts (minor, patch, pre-release, build metadata) **can be either numbers or alphanumeric strings** (e.g., `alpha`, `rc1`, `20251121`).

### Summary Table
| Part            | Example         | Notes                                  |
|-----------------|----------------|----------------------------------------|
| Prefix          | v, release-    | Optional, not dot-separated            |
| Version Part    | 1.2.3          | Major must be a number                 |
| PreRelease Part | alpha.1        | Optional, alphanumeric or numeric      |
| Build Metadata  | build.20251121 | Optional, alphanumeric or numeric      |

## Usage Examples

### Creating Versions
```kotlin
import nl.w8mr.version.Version

val v1 = Version("1.2.3")
val v2 = Version("v2.0.0-alpha.1+build.20251121")
val v3 = Version("release-2.5.0-rc.2")
```

### Creating Versions from Strings
You can easily create a `Version` object from a string using the extension function `String.toVersion()`:

```kotlin
val v = "1.2.3-alpha.1+build.20251121".toVersion()
println(v.major) // "1"
println(v.preRelease) // ["alpha", "1"]
```

This is equivalent to calling `Version("1.2.3-alpha.1+build.20251121")` directly, but provides a more idiomatic way to convert strings to version objects in Kotlin.

### Accessing Version Parts
```kotlin
println(v1.major) // "1"
println(v2.prefix) // "v"
println(v2.preRelease) // ["alpha", "1"]
println(v2.buildMetadata) // ["build", "20251121"]

// Empty parts are treated as "0"
val v = Version("1..1")
println(v.major) // "1"
println(v.minor) // "0"
println(v.patch) // "1"
```

### Comparing Versions

Versions can be compared to determine which is newer, older, or if they are equal. The comparison follows these rules:

### Comparison Logic
1. **Prefix**: Prefix is ignored for ordering; only used for display or grouping.
2. **Version Part**: Compare each dot-separated subpart (major, minor, patch, etc.) numerically if both are numbers, or lexicographically if either is alphanumeric. The first difference determines the order. Numeric parts are considered higher than alphanumeric parts. A missing subpart is considerer as being lower than a filled part
   - Example: `1.2.3` > `1.2.2`, `1.2.10` > `1.2.3`, `1.2.a` > `1.2.9`
3. **PreRelease Part**: A version without a pre-release part is considered newer than one with a pre-release part (e.g., `1.2.3` > `1.2.3-alpha`). If both have pre-release parts, compare each dot-separated subpart in order, using numeric or lexicographic comparison as above.
   - Example: `1.2.3-alpha.2` > `1.2.3-alpha.1`, `1.2.3-beta` > `1.2.3-alpha`
4. **Build Metadata**: Build metadata is ignored for ordering; it is only used for identifying builds.

### Summary of Comparison Steps
- Compare **Version Part** subparts left to right.
- If equal, compare **PreRelease Part** (absence means higher precedence).
- Ignore **Prefix** and **Build Metadata** for ordering.

### Example Comparisons
- `1.2.3` > `1.2.3-alpha.1`
- `1.2.3-alpha.2` > `1.2.3-alpha.1`
- `1.2.10` > `1.2.3`
- `v1.2.3` == `1.2.3` (prefix ignored)
- `1.2.3+build.1` == `1.2.3+build.2` (build metadata ignored)


### Comparing Versions
```kotlin
val v1 = Version("1.2.3")
val v2 = Version("1.2.3-alpha.1")
val v3 = Version("1.2.10")

println(v1 > v2) // true
println(v3 > v1) // true
println(v1 == Version("v1.2.3")) // true (prefix ignored)
println(Version("1.2.3+build.1") == Version("1.2.3+build.2")) // true (build metadata ignored)
```

### Custom Comparison
```kotlin
val versions = listOf(
    Version("1.2.3"),
    Version("1.2.3-alpha.1"),
    Version("1.2.10"),
    Version("v1.2.3"),
    Version("1.2.3+build.1")
)
val sorted = versions.sorted()
sorted.forEach { println(it) }
```
