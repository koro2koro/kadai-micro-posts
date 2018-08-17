resolvers += "Flyway" at "https://flywaydb.org/repo"

addSbtPlugin("org.flywaydb" % "flyway-sbt" % "4.2.0")
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.16")

addSbtPlugin("com.heroku" % "sbt-heroku" % "2.1.0")