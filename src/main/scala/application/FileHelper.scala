package application

import zio.ZIO
import java.io.File
import scala.io.Source

trait FileHelper {

  implicit class RichFile(file: File) {
    def read() = Source.fromFile(file).getLines()
  }

  private def open(path: String) = new File(path)

  def readLinesFromFile(file: String): ZIO[Any, Any, List[String]] = {
    if (open(file).isFile) {
      ZIO.succeed(open(file).read().toList)
    } else {
      println("File does not Exist.")
      ZIO.succeed(List.empty[String])
    }
  }
}
