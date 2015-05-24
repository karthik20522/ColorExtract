package com.colorextract

import akka.actor._
import com.colorextract.ColorExtractConfig._

object Main {
  def main(args: Array[String]) {
  	val system = ActorSystem("colorextractAS")

  	val esQueryActor = system.actorOf(Props(new SearchQueryActor()), name = "imageQuery")
  	
  	esQueryActor ! "init"
  }
}
