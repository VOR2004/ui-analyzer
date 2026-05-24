plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "ui-analyzer"

include(":core:api")
include(":core:utils")
include(":analyzer:api")
include(":analyzer:impl")
include(":xml:api")
include(":xml:impl")
include(":compose:api")
include(":compose:impl")
include(":android:api")
include(":android:impl")
include(":report:api")
include(":report:impl")
include(":cli")
include(":desktop")

project(":core:api").name = "core-api"
project(":core:utils").name = "core-utils"
project(":analyzer:api").name = "analyzer-api"
project(":analyzer:impl").name = "analyzer-impl"
project(":xml:api").name = "xml-api"
project(":xml:impl").name = "xml-impl"
project(":compose:api").name = "compose-api"
project(":compose:impl").name = "compose-impl"
project(":android:api").name = "android-api"
project(":android:impl").name = "android-impl"
project(":report:api").name = "report-api"
project(":report:impl").name = "report-impl"
