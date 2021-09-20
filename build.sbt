name := "CostManager"
 
version := "1.0" 
      
lazy val `costmanager` = (project in file(".")).enablePlugins(PlayScala)

      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.13.5"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

//libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.0.1"