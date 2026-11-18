val commonDir     = "common"      // Common source & test directory
val serverDir     = "server"      // Server source & test directory
val clientDir     = "client"      // Client source & test directory
val distDir       = "dist"        // Distributable directory
val clientTarget  = "main.js"     // The target js file
val serverTarget  = "server.jar"  // The target server 'fat' jar

ThisBuild / scalaVersion := "3.10.0"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = project
  .in(file("."))
  .aggregate(server, client, common.jvm, common.js)
  .settings(
    // Removes only the files inside distDir, not distDir itself
    cleanFiles += baseDirectory.value / distDir / clientTarget,
    cleanFiles += baseDirectory.value / distDir / serverTarget
  )

lazy val common = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file(commonDir))
  .settings(
    name := commonDir,
    libraryDependencies += "org.scalameta"  %% "munit"        % "1.3.6" % Test,
  )

lazy val server = project
  .dependsOn(common.jvm)
  .in(file(serverDir))
  .settings(
    name := serverDir,
    libraryDependencies += "org.scalameta"  %% "munit"        % "1.3.6" % Test,
  )

lazy val client = project
  .dependsOn(common.js)
  .in(file(clientDir))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := clientDir,
    libraryDependencies += "org.scalameta"  %% "munit"        % "1.3.6" % Test,
    libraryDependencies += "org.scala-js"   %% "scalajs-dom"  % "2.8.1",
    libraryDependencies += "com.raquo"      %% "laminar"      % "17.2.1",
    scalaJSUseMainModuleInitializer := true
  )

lazy val buildDist = taskKey[Unit](s"Builds the application to the `$distDir` distrubtable directory") 
ThisBuild / buildDist := Def.uncached {
  // Create a logger
  val logger = streams.value.log
  // Create a file converter
  val converter = fileConverter.value
  
  logger.info("Compiling client...")
  // Compile js modules and get an iterable list of each unit file
  val clientTargetBlob = 
    (client / Compile / fullLinkJS).value.data.publicModules.map[java.io.File]({
      // Get the directory in which each module is stored
      val clientTargetDir = 
        (client / Compile / fullLinkJS / scalaJSLinkerOutputDirectory).value

      // Map each module input to its corresponding unit file
      m =>  clientTargetDir / m.jsFileName
    })

  logger.info("Building server...")
  val serverTargetBlob = converter.toPath((server / assembly).value).toFile()

  // Create the resulting distributable directory
  IO.createDirectory(file(distDir))

  // Copy each js unit file to the distributable directory
  clientTargetBlob.foreach(f => IO.copyFile(f, file(distDir) / clientTarget))

  // Copy the server fat jar to the distributable directory 
  IO.copyFile(serverTargetBlob, file(distDir) / serverTarget)
}
