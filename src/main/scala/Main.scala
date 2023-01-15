import zio.ZIOAppDefault
import zio.Console._
import application.{CalculateRankings, FileHelper}

//  /Users/hendrikmaritz/Documents/git-branch-checker/ScoreKeeper/src/main/scala/example.txt
object MyApp extends ZIOAppDefault with FileHelper {

  private val rankings = new CalculateRankings()
  def run = myAppLogic

  private val myAppLogic =
    for {
      _              <- printLine("Welcome to Calculate Rankings")
      _              <- printLine("Insert Full file path to raw rankings file for import:")
      filePath       <- readLine("")
      rawLines       <- readLinesFromFile(filePath)
      leagueRankings <- rankings.calculateRankings(rawLines)
      _               = rankings.printLeagueRankings(leagueRankings)
    } yield ()
}