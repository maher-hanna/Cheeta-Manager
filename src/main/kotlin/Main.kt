package org.example

import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.*

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val jarLoader = JarLoader()
    jarLoader.loadEngineJar("C:\\Users\\User\\android_dev\\projects\\Cheeta\\app\\core\\build\\libs\\core.jar")
    val userInput = Scanner(System.`in`)
    try {
        while (true) {

            val input = userInput.nextLine()
            if (!input.isEmpty()) {
                System.out.println(jarLoader.commandEngine(input))

            }
        }
    } catch (e: IllegalStateException) {
        System.out.println("System.in was closed; exiting");
    } catch (e: NoSuchElementException) {
        System.out.println("System.in was closed; exiting");

    }

}

