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
import com.github.nscala_time.time.Imports._
import java.net.URL
import org.jsoup.Jsoup
import scala.collection.JavaConversions._

case class Color (masterId: String, dominantColor: RGB, colorPalette: List[RGB])
case class RGB(r: Integer, g: Integer, b: Integer)
case class GETNEXT(query: String)

class SearchQueryActor() extends Actor {

  import context.dispatcher
  implicit val formats: org.json4s.Formats = DefaultFormats

  var iterationCount: Integer = 0
  val timeout = 60000

  def receive = {
    case "init" => iterateData(getNextDirectory)
    case GETNEXT(query) => iterateData(query)
  }

  def getNextDirectory = s""

  def iterateData(request: String) = {
    println(request)
    val url = new URL(request);
    val imgTags = Jsoup.parse(url, timeout).select("a[href]").toList
    val imgSources:List[String] = imgTags.map(_.attributes.get("href"))

    imgSources map { r =>
      if(r.contains("_SEA.jpg")) {
        val colorPalette = getPalette(r.replace("_SEA.jpg", ""), s"${request}/${r}")
      }
    }
    iterationCount = iterationCount + 1
    self ! GETNEXT(getNextDirectory)
  }

  def getPalette(masterId: String, fileURI: String): Color = {    
    val url = new URL(fileURI);
    val img = ImageIO.read(url)
    val result = DominantColor.compute(img, 10).asScala.toList

    val rgbColorList: List[RGB] = result map { r => RGB(r(0), r(1), r(2)) }
    val dominantColor = RGB(result.head(0), result.head(1), result.head(2))

    Color(masterId, dominantColor, rgbColorList)
  }

  def getDate(subtractDate: Integer) = DateTime.now.minusDays(subtractDate).toString(StaticDateTimeFormat.forPattern("yyyyMMdd"))  
}