package org.example

import java.util.*

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main(args: Array<String>) {
    when (args.size) {
        1 -> {
            val jarLoader = JarLoader()
            jarLoader.loadEngineJar(args[0])
            val userInput = Scanner(System.`in`)
            try {
                while (true) {

                    val input = userInput.nextLine()
                    if (input.isNotEmpty()) {
                        println(jarLoader.commandEngine(input))

                    }
                }
            } catch (e: IllegalStateException) {
                println("System.in was closed; exiting")
            } catch (e: NoSuchElementException) {
                println("System.in was closed; exiting")

            }
        }

        2 -> {
            compareEngines(args[0], args[1])
        }

        else -> {
            println("Wrong arguments correct usage:")
            println("cheeta_manager PATH_TO_ENGINE_JAR")
            println("or for comparing tow versions of the engine")
            println("cheeta_manager PATH_TO_FIRST_ENGINE_JAR PATH_TO_SECOND_ENGINE_JAR")
        }
    }

}

fun compareEngines(firstEnginePath: String, secondEnginePath: String) {
    var carlsenGamesBufferReader =
        object {}.javaClass.getResourceAsStream("/games_database/carlsen.txt")?.bufferedReader()
    var positions = mutableListOf<String>()
    try {

        var line: String?
        while (carlsenGamesBufferReader?.readLine().also { line = it } != null) {
            // Process each line
            if (line != "\u0000") {
                line?.let { positions.add(it.trim().replace("\n".toRegex(), replacement = "")) }
            }
        }
    } catch (e: Exception) {
        println("An error occurred: ${e.message}")
    } finally {
        try {
            carlsenGamesBufferReader?.close()
        } catch (e: Exception) {
            println("An error occurred while closing the file: ${e.message}")
        }
    }
    val firstJarLoader = JarLoader()
    firstJarLoader.loadEngineJar(firstEnginePath)
    val secondJarLoader = JarLoader()
    secondJarLoader.loadEngineJar(secondEnginePath)
    val numberOfGames = 30
    var currentGameNumber = 1
    var isCurrentGameFinished: Boolean
    var movesList: String
    var firstEngineWins = 0
    var firstEngineDraws = 0
    var firstEngineLoses = 0
    try {
        while (currentGameNumber <= numberOfGames) {
            isCurrentGameFinished = false
            movesList = ""
            val random = Random(System.currentTimeMillis())
            val randomPlayerIndex = random.nextInt(2)
            var currentPlayerIndex = randomPlayerIndex
            val randomPosition =
                positions[random.nextInt(positions.size)] ?: "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 "
            if (currentPlayerIndex == 0) {
                firstJarLoader.commandEngine("position fen $randomPosition")
                val gameStatusResponse = secondJarLoader.commandEngine("check_status")
                // game status : 0 not finished, 1 draw, 2 white winds, 3 black wins
                val statusCode = gameStatusResponse.toInt()
                if (statusCode != 0) {
                    continue
                }
                val move = firstJarLoader.commandEngine("go infinite")
                val splits = move.trim().split("\\s+".toRegex())
                if (splits.size > 1) {
                    movesList += splits[1] + " "
                }
            } else {
                secondJarLoader.commandEngine("position fen $randomPosition")
                val gameStatusResponse = secondJarLoader.commandEngine("check_status")
                // game status : 0 not finished, 1 draw, 2 white winds, 3 black wins
                val statusCode = gameStatusResponse.toInt()
                if (statusCode != 0) {
                    continue
                }
                val move = secondJarLoader.commandEngine("go infinite")
                val splits = move.trim().split("\\s+".toRegex())
                if (splits.size > 1) {
                    movesList += splits[1] + " "
                }
            }
            while (!isCurrentGameFinished) {
                // flip current player index from 0 to 1 and vice versa
                currentPlayerIndex = currentPlayerIndex xor 1

                if (currentPlayerIndex == 0) {
                    firstJarLoader.commandEngine("position fen $randomPosition moves $movesList")
                    val move = firstJarLoader.commandEngine("go infinite")
                    val splits = move.trim().split("\\s+".toRegex())
                    if (splits.size > 1) {
                        movesList += splits[1] + " "
                    }
                } else {
                    secondJarLoader.commandEngine("position fen $randomPosition moves $movesList")
                    val move = secondJarLoader.commandEngine("go infinite")
                    val splits = move.trim().split("\\s+".toRegex())
                    if (splits.size > 1) {
                        movesList += splits[1] + " "
                    }
                }
                println("position fen $randomPosition moves $movesList")
                val gameStatusResponse = secondJarLoader.commandEngine("check_status")
                // game status : 0 not finished, 1 draw, 2 white winds, 3 black wins
                val statusCode = gameStatusResponse.toInt()
                isCurrentGameFinished = statusCode != 0
                if (isCurrentGameFinished) {
                    when (statusCode) {
                        1 -> firstEngineDraws += 1
                        2 -> {
                            if (randomPlayerIndex == 0) {
                                firstEngineWins += 1
                            } else {
                                firstEngineLoses += 1
                            }
                        }

                        3 -> {
                            if (randomPlayerIndex == 0) {
                                firstEngineLoses += 1
                            } else {
                                firstEngineWins += 1
                            }
                        }
                    }
                }
            }
            currentGameNumber += 1
        }
        println("First engine results compared to second engine are:")
        println("wins: $firstEngineWins draws: $firstEngineDraws loses: $firstEngineLoses")
    } catch (e: IllegalStateException) {
        println("System.in was closed; exiting")
    } catch (e: NoSuchElementException) {
        println("System.in was closed; exiting")
    }catch (e: Exception){
        println("Error occurred:")
        println("First engine results compared to second engine are:")
        println("wins: $firstEngineWins draws: $firstEngineDraws loses: $firstEngineLoses")
    }
}

