name := "a2s2t"

import android.Keys._

android.Plugin.androidBuild

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")
scalaVersion := "2.11.8"
scalacOptions in Compile += "-feature"

updateCheck in Android := {} // disable update check

proguardOptions in Android ++= Seq("-dontobfuscate",
  "-dontoptimize",
  "-keepattributes Signature",
  "-printseeds target/seeds.txt",
  "-printusage target/usage.txt",
  "-dontwarn scala.collection.**", // required from Scala 2.11.4
  "-dontwarn com.unicorncoding.a2s2t.**", // this can be omitted if current Android Build target is android-16

  //  AKKA START
  //  see https://gist.github.com/bjornharrtell/3307987
  "-keep class akka.actor.LightArrayRevolverScheduler { *; }",
  "-keep class akka.actor.LocalActorRefProvider { *; }",
  "-keep class akka.actor.CreatorFunctionConsumer { *; }",
  "-keep class akka.actor.TypedCreatorFunctionConsumer { *; }",
  "-keep class akka.dispatch.BoundedDequeBasedMessageQueueSemantics { *; }",
  "-keep class akka.dispatch.UnboundedMessageQueueSemantics { *; }",
  "-keep class akka.dispatch.UnboundedDequeBasedMessageQueueSemantics { *; }",
  "-keep class akka.dispatch.DequeBasedMessageQueueSemantics { *; }",
  "-keep class akka.actor.LocalActorRefProvider$Guardian { *; }",
  "-keep class akka.actor.LocalActorRefProvider$SystemGuardian { *; }",
  "-keep class akka.dispatch.UnboundedMailbox { *; }",
  "-keep class akka.actor.DefaultSupervisorStrategy { *; }",
  "-keep class akka.event.Logging$LogExt { *; }",
  //  "-keepattributes InnerClasses",
  "-keep class com.typesafe.**",
  "-keep class akka.**",
  "-keep class scala.collection.immutable.StringLike {*;}",
  "-keepclasseswithmembers class * { public <init>(java.lang.String, akka.actor.ActorSystem$Settings, akka.event.EventStream, akka.actor.Scheduler, akka.actor.DynamicAccess);}",
  "-keepclasseswithmembers class * {public <init>(akka.actor.ExtendedActorSystem);}",
  "-keep class scala.collection.SeqLike {public protected *;}",
  "-dontwarn sun.misc.Unsafe"
)

libraryDependencies ++= Seq(
  "com.android.support" % "support-v4" % "19.1.0",
  "com.android.support" % "appcompat-v7" % "22.0.0",
  "com.typesafe.akka" %% "akka-actor" % "2.3.14")


run <<= run in Android
install <<= install in Android

// Tests //////////////////////////////

//libraryDependencies ++= Seq(
//  "org.apache.maven" % "maven-ant-tasks" % "2.1.3" % "test",
//  "org.robolectric" % "robolectric" % "3.0" % "test",
//  "junit" % "junit" % "4.12" % "test",
//  "com.novocode" % "junit-interface" % "0.11" % "test",
//  "org.scalatest" %% "scalatest" % "2.1.5" % "test",
//  "org.scalacheck" %% "scalacheck" % "1.13.0" % "test",
//  "org.specs2" %% "specs2-core" % "3.7" % "test"
//)

// without this, @Config throws an exception,
unmanagedClasspath in Test ++= (bootClasspath in Android).value