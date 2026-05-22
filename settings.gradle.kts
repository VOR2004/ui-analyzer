plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "ui-analyzer"

include(":common:api")
include(":analyzer:api")
include(":analyzer:impl")
include(":xml:api")
include(":xml:impl")
include(":compose:api")
include(":compose:impl")
include(":android:api")
include(":android:impl")
include(":report:impl")
include(":cli")

project(":common:api").name = "common-api"
project(":analyzer:api").name = "analyzer-api"
project(":analyzer:impl").name = "analyzer-impl"
project(":xml:api").name = "xml-api"
project(":xml:impl").name = "xml-impl"
project(":compose:api").name = "compose-api"
project(":compose:impl").name = "compose-impl"
project(":android:api").name = "android-api"
project(":android:impl").name = "android-impl"
project(":report:impl").name = "report-impl"
