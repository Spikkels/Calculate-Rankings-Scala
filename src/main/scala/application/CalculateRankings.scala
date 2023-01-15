package application

import models.{Game, LeagueScoringSystem, LeagueStanding, TeamLeagueScore}
import scala.util.{Failure, Success, Try}
import scala.annotation.tailrec
import zio.Console._
import zio._

class CalculateRankings(scoreRules: LeagueScoringSystem = LeagueScoringSystem(3, 0, 1)) {

  /**
   * Extracts game teams and scores with a regexp
   *
   * @param rawLine The raw line that contains team names and scores
   * @return Some(game) results or None
   */
  private def extractGame(rawLine: String): ZIO[Any, Any, Option[Game]] = {
    Try {
      val pattern = "^([A-Za-z0-9 ]+) ([0-9]+), ([A-Za-z0-9 ]+) ([0-9]+)$".r
      val pattern(country1, score1, country2, score2) = rawLine.strip()

      Game(country1.strip(), score1.toInt, country2.strip(), score2.toInt)
    } match {
      case Success(game) if game.team1 == game.team2 =>
        printLine(s"ERROR: team1 and team2 cannot be the same: $rawLine") *> ZIO.succeed(None)
      case Success(game) =>
        ZIO.succeed(Some(game))
      case Failure(_) =>
        printLine(s"ERROR: Line is not written in the correct format: $rawLine") *> ZIO.succeed(None)
    }
  }

  /**
   * Calculates the Winner and Loser of a game
   * Makes a list of TeamLeagueScore which contains the team and the league score that the team received
   * Allocated amount of points can be changed with class initiation
   *
   * @param game game results which include 2 team names and 2 two scores
   * @return TeamLeagueScore
   */
  private def calculateGameLeagueScore(game: Game): ZIO[Any, Nothing, List[TeamLeagueScore]] = {
    (game.score1, game.score2) match {
      case (score1, score2) if score1 == score2 =>
        val team1 = TeamLeagueScore(game.team1, scoreRules.gameTieScore)
        val team2 = TeamLeagueScore(game.team2, scoreRules.gameTieScore)
        ZIO.succeed(List(team1, team2))
      case (score1, score2) if score1 > score2 =>
        val team1 = TeamLeagueScore(game.team1, scoreRules.gameWinScore)
        val team2 = TeamLeagueScore(game.team2, scoreRules.gameLoseScore)
        ZIO.succeed(List(team1, team2))
      case (score1, score2) if score1 < score2 =>
        val team1 = TeamLeagueScore(game.team1, scoreRules.gameLoseScore)
        val team2 = TeamLeagueScore(game.team2, scoreRules.gameWinScore)
        ZIO.succeed(List(team1, team2))
    }
  }

  /**
   * Handles The Option of game and calculates leauge score for the game
   *
   * @param game game results which include 2 team names and 2 two scores
   * @return List[TeamLeagueScore]
   */
  private def getTeamLeagueScore(game: Option[Game]): ZIO[Any, Nothing, List[TeamLeagueScore]] = {
    game match {
      case Some(game) => calculateGameLeagueScore(game)
      case None       => ZIO.succeed(List.empty[TeamLeagueScore])
    }
  }

  /**
   * Groups all the teams with the same names in a list.
   * Then sums the league score for each Team
   * The result is a list with Teams and their total league points
   *
   * @param team Contains team name and league score
   * @return List[TeamLeagueScore]
   */
  private def groupTeamLeague(team: List[TeamLeagueScore]) : List[TeamLeagueScore] = {
    team.groupBy(_.teamName)
      .toList
      .map { case (name, team) =>
        TeamLeagueScore(name,team.map(_.score).sum)
      }
  }

  /**
   * Sorts teams by their score and then by their name.
   *
   * Note: This sorting can be easily done using the build in scala methods but I wanted to demonstrate
   * that I can do this sorting in recursion with tailrec functions.
   *
   * @param list contains team name and team league score
   * @return List[TeamLeagueScore]
   */
  private def sortRankings(list: List[TeamLeagueScore]): List[TeamLeagueScore] = {
    @tailrec
    def insertSort(team: TeamLeagueScore,
                      sortedList: List[TeamLeagueScore],
                      accumulator: List[TeamLeagueScore]): List[TeamLeagueScore] = {
      if (sortedList.isEmpty || team.score > sortedList.head.score) {
        accumulator.reverse ++ (team :: sortedList)

      } else if (team.score == sortedList.head.score) {
        if (team.teamName < sortedList.head.teamName) {
          accumulator.reverse ++ (team :: sortedList)
        } else {
          insertSort(team, sortedList.tail, sortedList.head :: accumulator)
        }

      } else {
        insertSort(team, sortedList.tail, sortedList.head :: accumulator)
      }
    }

    @tailrec
    def sort(list: List[TeamLeagueScore], accumulator: List[TeamLeagueScore]): List[TeamLeagueScore] = {
      if (list.isEmpty) accumulator
      else sort(list.tail, insertSort(list.head, accumulator, Nil))
    }
    sort(list, Nil)
  }

  /**
   * Sorts teams by their score and then by their name.
   *
   * Note: This sorting can be easily done using the build in scala methods but I wanted to demonstrate
   * that I can do this sorting in recursion with tailrec functions.
   *
   * @param list Contains team name and team league score
   * @return List[TeamLeagueScore]
   */
  private def numberRankings(list: List[TeamLeagueScore]): List[LeagueStanding] = {

    def sameScore(list: List[TeamLeagueScore],
                  accumulator: List[LeagueStanding],
                  currentRank: Int,
                  counter: Int): List[LeagueStanding] = {
      if (accumulator.last.teamScore == list.head.score) {
        adder(list.tail, accumulator ++ List(LeagueStanding(currentRank, list.head.teamName, list.head.score)), currentRank, counter + 1)
      } else {
        adder(list.tail, accumulator ++ List(LeagueStanding(counter, list.head.teamName, list.head.score)), counter, counter + 1)
      }
    }

    @tailrec
    def adder(list: List[TeamLeagueScore],
              accumulator: List[LeagueStanding],
              currentRank: Int = 1,
              counter: Int = 1): List[LeagueStanding] = {

      (list, accumulator) match {
        case (list, _) if list.isEmpty =>
          accumulator
        case (list, acc) if list.nonEmpty && acc.isEmpty =>
          adder(list.tail, accumulator ++ List(LeagueStanding(1, list.head.teamName, list.head.score)), currentRank, counter + 1)
        case (list, acc) if list.nonEmpty && acc.nonEmpty =>
          sameScore(list, accumulator, currentRank, counter)
      }
    }

    adder(list, List.empty)
  }

  /**
   * 1. Takes a list af strings and extract Game teams and scores.  This is done in parallel processing.
   * 2. Calculates the league score for every game that was played.  This is done in parallel processing.
   * 3. Groups all the Games data bu their team names and sums the scores
   * 4. Sort according to Winners and loser
   * 5. Add rankings to each Team.
   *
   */
  def calculateRankings(rawLines: List[String]): ZIO[Any, Any, List[LeagueStanding]] = {
    for {
      gameExtract    <- ZIO.foreachPar(rawLines)(extractGame)
      leagueScore    <- ZIO.foreachPar(gameExtract.map(game => game))(getTeamLeagueScore)
      sumLeagueScore  = groupTeamLeague(leagueScore.flatten)
      sorted          = sortRankings(sumLeagueScore)
      leagueRankings  = numberRankings(sorted)
    } yield leagueRankings
  }


  def printLeagueRankings(rankings: List[LeagueStanding]): List[String] = {
    val lines = rankings.map { row =>
      if (row.teamScore != 1) {
        s"${row.rank}. ${row.teamName}, ${row.teamScore} pts"
      } else {
        s"${row.rank}. ${row.teamName}, ${row.teamScore} pt"
      }
    }
    println(lines.mkString(sys.props("line.separator")))
    lines
  }

}




