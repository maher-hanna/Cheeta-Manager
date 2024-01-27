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
            compareEngines(args[0],args[1])
        }

        else -> {
            println("Wrong arguments correct usage:")
            println("cheeta_manager PATH_TO_ENGINE_JAR")
            println("or for comparing tow versions of the engine")
            println("cheeta_manager PATH_TO_FIRST_ENGINE_JAR PATH_TO_SECOND_ENGINE_JAR")
        }
    }

}

fun compareEngines(firstEnginePath: String, secondEnginePath: String){
    val firstJarLoader = JarLoader()
    firstJarLoader.loadEngineJar(firstEnginePath)
    val secondJarLoader = JarLoader()
    secondJarLoader.loadEngineJar(secondEnginePath)
    val numberOfGames = 20
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
            if(currentPlayerIndex == 0){
                firstJarLoader.commandEngine("position startpos")
                val move = firstJarLoader.commandEngine("go infinite")
                val splits = move.trim().split("\\s+".toRegex())
                if(splits.size > 1){
                    movesList += splits[1] + " "
                }
            } else {
                secondJarLoader.commandEngine("position startpos")
                val move = secondJarLoader.commandEngine("go infinite")
                val splits = move.trim().split("\\s+".toRegex())
                if(splits.size > 1){
                    movesList += splits[1] + " "
                }
            }
            while (!isCurrentGameFinished){
                // flip current player index from 0 to 1 and vice versa
                currentPlayerIndex = currentPlayerIndex xor 1

                if(currentPlayerIndex == 0){
                    firstJarLoader.commandEngine("position startpos moves $movesList")
                    val move = firstJarLoader.commandEngine("go infinite")
                    val splits = move.trim().split("\\s+".toRegex())
                    if(splits.size > 1){
                        movesList += splits[1] + " "
                    }
                } else{
                    secondJarLoader.commandEngine("position startpos moves $movesList")
                    val move = secondJarLoader.commandEngine("go infinite")
                    val splits = move.trim().split("\\s+".toRegex())
                    if(splits.size > 1){
                        movesList += splits[1] + " "
                    }
                }
                println(movesList)
                val gameStatusResponse = firstJarLoader.commandEngine("check_status")
                // game status : 0 not finished, 1 draw, 2 white winds, 3 black wins
                val statusCode = gameStatusResponse.toInt()
                isCurrentGameFinished = statusCode != 0
                if(isCurrentGameFinished){
                    when(statusCode){
                        1 -> firstEngineDraws += 1
                        2 -> {
                            if(randomPlayerIndex == 0){
                                firstEngineWins += 1
                            }else {
                                firstEngineLoses += 1
                            }
                        }
                        3 -> {
                            if(randomPlayerIndex == 0){
                                firstEngineLoses += 1
                            }else {
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

    }
}

