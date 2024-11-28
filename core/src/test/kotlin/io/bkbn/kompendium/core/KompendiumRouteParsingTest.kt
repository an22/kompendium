package io.bkbn.kompendium.core

import io.bkbn.kompendium.core.fixtures.TestHelpers
import io.bkbn.kompendium.core.util.nestedUnderRoot
import io.bkbn.kompendium.core.util.paramWrapper
import io.bkbn.kompendium.core.util.rootRoute
import io.bkbn.kompendium.core.util.simplePathParsing
import io.bkbn.kompendium.core.util.trailingSlash
import io.bkbn.kompendium.core.util.authPathParsing
import io.bkbn.kompendium.core.util.authPathParsingTrailingSlash
import io.bkbn.kompendium.oas.component.Components
import io.bkbn.kompendium.oas.security.BasicAuth
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.basic
import io.ktor.server.application.install
import io.ktor.server.auth.UserIdPrincipal

class KompendiumRouteParsingTest : DescribeSpec({
  describe("Route Parsing") {
    it("Can parse a simple path and store it under the expected route") {
      TestHelpers.openApiTestAllSerializers("T0012__path_parser.json") { simplePathParsing() }
    }
    it("Can notarize the root route") {
      TestHelpers.openApiTestAllSerializers("T0013__root_route.json") { rootRoute() }
    }
    it("Can notarize a route under the root module without appending trailing slash") {
      TestHelpers.openApiTestAllSerializers("T0014__nested_under_root.json") { nestedUnderRoot() }
    }
    it("Can notarize a route with a trailing slash") {
      TestHelpers.openApiTestAllSerializers("T0015__trailing_slash.json") { trailingSlash() }
    }
    it("Can notarize a route with a parameter") {
      TestHelpers.openApiTestAllSerializers("T0068__param_wrapper.json") { paramWrapper() }
    }
    it("Can notarize a route with an authentication info on a on a type safe route") {
      TestHelpers.openApiTestAllSerializers(
        snapshotName = "T0079_authentication_on_rest_route_parsing.json",
        applicationSetup = {
          install(Authentication) {
            basic("basic") {
              realm = "Ktor Server"
              validate { UserIdPrincipal("Placeholder") }
            }
          }
        },
        specOverrides = {
          this.copy(
            components = Components(
              securitySchemes = mutableMapOf(
                "basic" to BasicAuth()
              )
            )
          )
        }
      ) { authPathParsing() }
    }
    it("Can notarize a route with an authentication info on a type safe route with trailing slash") {
      TestHelpers.openApiTestAllSerializers(
        snapshotName = "T0080_authentication_on_rest_route_parsing_trailing_slash.json",
        applicationSetup = {
          install(Authentication) {
            basic("basic") {
              realm = "Ktor Server"
              validate { UserIdPrincipal("Placeholder") }
            }
          }
        },
        specOverrides = {
          this.copy(
            components = Components(
              securitySchemes = mutableMapOf(
                "basic" to BasicAuth()
              )
            )
          )
        }
      ) { authPathParsingTrailingSlash() }
    }
  }
})
