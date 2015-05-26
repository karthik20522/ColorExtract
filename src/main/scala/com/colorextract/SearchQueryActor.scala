package com.colorextract

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import scala.concurrent._
import scala.concurrent.duration._
import akka.actor._
import java.io._
import javax.imageio.ImageIO;
import scala.collection.JavaConverters._
import org.json4s.native.Serialization.{read, write}
import com.github.nscala_time.time._
import com.github.nscala_time.time.Imports.DateTime
import java.net.URL
import org.jsoup.Jsoup
import scala.collection.JavaConversions._
import spray.client.pipelining._
import spray.http.HttpResponse
import scala.concurrent.duration._

case class ColorSearch (masterId: String, fileURL: String, views: Integer, rank: Integer, color: List[Color])
case class Color(r: Integer, g: Integer, b: Integer)
case class GETNEXT(query: String)

class SearchQueryActor() extends Actor {

  import context.dispatcher
  implicit val formats: org.json4s.Formats = DefaultFormats

  val pipeline = sendReceive ~> unmarshal[HttpResponse]

  var iterationCount: Integer = 0
  var count: Integer = 0
  val timeout = 60000
  val viewsRandom = new scala.util.Random
  val rankRandom = new scala.util.Random

  def receive = {
    case "init" => iterateData(getNextDirectory)
    case GETNEXT(query) => iterateData(query)
  }

  def getNextDirectory = s"/webdirectory/prod/170Thumb/${getDate(iterationCount)}"

  def iterateData(request: String) = {
    println(request)

    val url = new URL(request);
    val imgTags = Jsoup.parse(url, timeout).select("a[href]").toList
    val imgSources:List[String] = imgTags.map(_.attributes.get("href"))

    imgSources.par.map { r =>
      if(r.contains("_SEA.jpg")) {
        val masterId = r.replace("_SEA.jpg", "")
        count = count + 1
        println(s"${count}:${masterId}")

        val colorPalette = getPalette(masterId, s"${request}/${r}")
        val cpJValue = write(colorPalette)
        val esResponse: HttpResponse = Await.result(pipeline(Post(s"/colorsearch/color/${masterId}", cpJValue)), 60.seconds)
      }
    }
    iterationCount = iterationCount + 1
    self ! GETNEXT(getNextDirectory)
  }

  def getPalette(masterId: String, fileURI: String): ColorSearch = {    
    val url = new URL(fileURI);
    val img = ImageIO.read(url)
    val result = DominantColor.compute(img, 10).asScala.toList

    val rgbColorList: List[Color] = result map { r => Color(r(0), r(1), r(2)) }    
    val viewCount = viewsRandom.nextInt(200) match {
      case x: Int if x == 0 => viewsRandom.nextInt(200)
      case x: Int => x
    }

    val imageRank = rankRandom.nextInt(4) match {
      case x: Int if x == 0 => 1
      case x: Int => x
    }

    ColorSearch(masterId, fileURI, viewsRandom.nextInt(200), rankRandom.nextInt(4), rgbColorList)
  }

  def getDate(subtractDate: Integer) = DateTime.now.minusDays(subtractDate).toString(StaticDateTimeFormat.forPattern("yyyyMMdd"))  
}