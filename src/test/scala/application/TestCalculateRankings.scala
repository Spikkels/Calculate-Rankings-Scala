package application

import models.LeagueStanding
import zio.test._

object TestCalculateRankings extends ZIOSpecDefault {
  def spec = suite("Calculate Rankings") (
    test("Winning team is appointed 3 points with rank 1 and losing team appointed 0 points with rank 2") {
      val rawInput = List("England 2, Brazil 1")
      val calculateRankings = new CalculateRankings()
      for {
        rankings <- calculateRankings.calculateRankings(rawInput)
      } yield assertTrue(rankings == List(LeagueStanding(1,"England",3),LeagueStanding(2,"Brazil",0)))
    },

    test("Teams with a tie is awarded 1 point each both team will have the rank of 1") {
      val rawInput = List("England 1, Brazil 1")
      val calculateRankings = new CalculateRankings()
      for {
        rankings <- calculateRankings.calculateRankings(rawInput)
      } yield assertTrue(rankings == List(LeagueStanding(1, "Brazil", 1), LeagueStanding(1, "England", 1)))
    },

    test("Multple teams are awarded correct league points") {
      val rawInput = List("England 1, Brazil 1", "Spain 2, England 3", "Spain 0, Brazil 3")
      val calculateRankings = new CalculateRankings()
      for {
        rankings <- calculateRankings.calculateRankings(rawInput)
      } yield assertTrue(rankings == List(
        LeagueStanding(rank = 1, teamName = "Brazil",  teamScore = 4),
        LeagueStanding(rank = 1, teamName = "England", teamScore = 4),
        LeagueStanding(rank = 3, teamName = "Spain",   teamScore = 0))
      )
    },

    test("Multple teams are awarded correct league points and redundant spaces are removed from player name") {
      val rawInput = List(
        "England 1, Brazil 1",
        "Spain 2, England 3",
        "Spain  0, Brazil  3",
        "Germany 5,  England 3",
        "Spain   2,  Germany 3",
        "Brazil 0, Germany 2"
      )
      val calculateRankings = new CalculateRankings()
      for {
        rankings <- calculateRankings.calculateRankings(rawInput)
      } yield assertTrue(rankings == List(
        LeagueStanding(rank = 1, teamName = "Germany", teamScore = 9),
        LeagueStanding(rank = 2, teamName = "Brazil", teamScore = 4),
        LeagueStanding(rank = 2, teamName = "England", teamScore = 4),
        LeagueStanding(rank = 4, teamName = "Spain", teamScore = 0))
      )
    },

    test("Incorrect name format single line") {
      val rawInput = List(
        "England 1, Brazil 1",
        "Spain 2, England 3",
        "Spain 0, Brazil 3",
        "Germany 5, England 3",
        "Spain,   2,  Germany 3",
        "Brazil 0, Germany 2")
      val calculateRankings = new CalculateRankings()
      for {
        _     <- calculateRankings.calculateRankings(rawInput)
        output <- TestConsole.output
      } yield assertTrue(output == Vector("ERROR: Line is not written in the correct format: Spain,   2,  Germany 3\n"))
    },

    test ("Teams cannot have the same name") {
      val rawInput = List("Brazil 1, Brazil 1")
      val calculateRankings = new CalculateRankings()
      for {
        _ <- calculateRankings.calculateRankings(rawInput)
        output <- TestConsole.output
      } yield assertTrue(output == Vector("ERROR: team1 and team2 cannot be the same: Brazil 1, Brazil 1\n"))
    },


    test ("Output from, Winning team is appointed 3 points with rank 1 and losing team appointed 0 points with rank 2") {
      val rawInput = List("Brazil 1, England 1")
      val calculateRankings = new CalculateRankings()
      for {
        league <- calculateRankings.calculateRankings(rawInput)
        output  = calculateRankings.printLeagueRankings(league)

      } yield assertTrue(output == List("1. Brazil, 1 pt", "1. England, 1 pt"))
    },

    test("Output from, Teams with a tie is awarded 1 point each both team will have the rank of 1") {
      val rawInput = List("England 1, Brazil 1")
      val calculateRankings = new CalculateRankings()
      for {
        rankings <- calculateRankings.calculateRankings(rawInput)
        output    = calculateRankings.printLeagueRankings(rankings)
      } yield assertTrue(output == List("1. Brazil, 1 pt", "1. England, 1 pt"))
    },


    test("Output from, Multiple teams are awarded correct league points") {
      val rawInput = List("England 1, Brazil 1", "Spain 2, England 3", "Spain 0, Brazil 3")
      val calculateRankings = new CalculateRankings()
      for {
        rankings <- calculateRankings.calculateRankings(rawInput)
        output    = calculateRankings.printLeagueRankings(rankings)
      } yield assertTrue(output == List("1. Brazil, 4 pts", "1. England, 4 pts", "3. Spain, 0 pts")
      )
    }
  )

}


